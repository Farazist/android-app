package project.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.connection.Commands;
import project.custom.CustomToast;
import project.custom.ProgressButton;
import project.helper.HelperString;
import project.helper.HelperView;

public class FragmentChargingCitizenWallet extends Fragment {

    private ViewGroup rootLayout;
    private ViewGroup loader;
    private EditText edtAmount;
    private EditText edtID;
    private TextView txtName;
    private Button btnCheck;

    private boolean validDestination;
    private boolean verifyEnable;

    private Handler handler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_charging_citizen_wallet, container, false);

        validDestination = false;
        verifyEnable = true;
        handler = new Handler();

        rootLayout = (ViewGroup) root.findViewById(R.id.rootLayout);
        loader = (ViewGroup) root.findViewById(R.id.loader);
        edtAmount = (EditText) root.findViewById(R.id.edtAmount);
        edtID = (EditText) root.findViewById(R.id.edtID);
        final EditText edtTransferAmount = (EditText) root.findViewById(R.id.edtTransferAmount);
        final ProgressButton btnVerify = (ProgressButton) root.findViewById(R.id.btnVerify);
        btnCheck = (Button) root.findViewById(R.id.btnCheck);
        txtName = (TextView) root.findViewById(R.id.txtName);

        edtAmount.setFocusable(false);
        btnVerify.setNormalText(App.getContext().getString(R.string.transfer))
                .setDoneText(App.getContext().getString(R.string.done));

        getUserInfo(false);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperView.hideKeyboard(App.getCurrentActivity());
                validDestination = false;
                if(verifyEnable){
                   getTargetInfo();
                }else{
                    CustomToast.showToast(App.getContext().getString(R.string.pleaseWait));
                }
            }
        });

        edtID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                btnCheck.setClickable(true);
                validDestination = false;
                txtName.setText("");
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!verifyEnable){
                    return;
                }
                String targetID_s = edtID.getText().toString();
                int targetID = targetID_s.length()>0?Integer.valueOf(targetID_s):-1;
                int amount = Integer.valueOf(edtAmount.getText().toString());
                int transferAmount = 0;
                if (edtTransferAmount.getText().toString().length() > 0) {
                    transferAmount = Integer.valueOf(edtTransferAmount.getText().toString());
                }

                HelperView.hideKeyboard(App.getCurrentActivity());
                if (!validDestination) {
                    CustomToast.showToast(getString(R.string.invalidDestination));
                    return;
                }
                if(transferAmount==0){
                    return;
                }
                if (amount < transferAmount) {
                    CustomToast.showToast(getString(R.string.notEnoughWallet));
                    return;
                }


                if(targetID == App.account.id){
                    CustomToast.showToast(App.getContext().getString(R.string.equalOriginAndDestinationID));
                    return;
                }
                verifyEnable = false;
                btnVerify.loading();
                new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                    @Override
                    public void onComplete(String data) {
                        loaded();
                        CustomToast.showToast(App.getContext().getString(R.string.successfullyTransferred));
                        getUserInfo(true);
                    }

                    @Override
                    public void onFail(String error) {
                        verifyEnable = true;
                        loaded();
                        CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                        return;
                    }
                }).transfer(targetID,transferAmount , App.getContext().getString(R.string.transfer));
            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        return root;
    }

    private void loading() {
        rootLayout.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }

    private void loaded() {
        rootLayout.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }

    private void getUserInfo(final boolean finish) {
        if(!finish) {
            loading();
        }
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                if(finish){
                    if(FragmentWallet.txtMoney!=null) {
                        FragmentWallet.txtMoney.setText(HelperString.convertToNumberFormat(App.account.wallet + "") + "  " + App.getContext().getString(R.string.tooman));
                    }
                    ActivityMain.fragmentManager.popBackStack();
                    return;
                }
                try {
                    JSONObject userObject = new JSONObject(data);
                    edtAmount.setText(userObject.getString("wallet"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loaded();
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                loaded();
            }
        }).getUserInfo();
    }

    private void getTargetInfo(){
        validDestination = false;
        verifyEnable = false;
        txtName.setText(App.getContext().getString(R.string.pleaseWait));
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONObject userObject = new JSONObject(data);
                    if(userObject.has("name")){
                        txtName.setText(userObject.getString("name"));
                        validDestination = true;
                        verifyEnable = true;
                    }else{
                        txtName.setText(App.getContext().getString(R.string.notFound));
                        validDestination = false;
                        verifyEnable = true;
                    }
                } catch (JSONException e) {
                    verifyEnable = true;
                    validDestination = false;
                    txtName.setText("");
                    CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                verifyEnable = true;
                validDestination = false;
                txtName.setText("");
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).getTargetUserInfo(edtID.getText().toString());
    }

}
