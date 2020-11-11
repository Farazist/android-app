package project.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.connection.Commands;
import project.custom.CustomToast;
import project.custom.ProgressButton;
import project.helper.HelperFragment;
import project.helper.HelperString;
import project.helper.HelperView;

public class FragmentRequestMoney extends Fragment {

    private static boolean actionEnable = true;

    private final String PENDING = "pending";
    private final String DONE = "done";
    private final String DELETED = "deleted";
    private final String REJECTED = "rejected";

    private String state = "";

    private boolean setNewRequest;

    private int lastRequestID = 0;
    private int lastRequestAmount = 0;
    private ViewGroup loader;
    private ConstraintLayout layoutMain;
    private TextView txtDesc;
    private Button btnCancelRequest;
    private EditText edtAmount;
    private ProgressButton btnRequestMoney;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_request_money, container, false);

        LinearLayout header = (LinearLayout) root.findViewById(R.id.header);
        header.setTranslationY(-1 * App.getContext().getResources().getDimension(R.dimen._40sdp));

        loader = (ViewGroup) root.findViewById(R.id.loader);
        layoutMain = (ConstraintLayout) root.findViewById(R.id.layoutMain);
        TextView txtWallet = (TextView) root.findViewById(R.id.txtWallet);
        TextView txtCardNumber = (TextView) root.findViewById(R.id.txtCardNumber);
        txtDesc = (TextView) root.findViewById(R.id.txtDesc);
        btnRequestMoney = (ProgressButton) root.findViewById(R.id.btnRequestMoney);
        btnRequestMoney.setNormalText(App.getContext().getString(R.string.sendRequest));
        btnRequestMoney.setWorkText(App.getContext().getString(R.string.sending));
        btnRequestMoney.setDoneText(App.getContext().getString(R.string.sent));
        btnCancelRequest = (Button) root.findViewById(R.id.btnCancelRequest);
        edtAmount = (EditText) root.findViewById(R.id.edtAmount);
        HelperString.currencyFormatEditText(edtAmount);

        final Dialog dialogCancel = HelperView.configDialog(R.layout.dialog_cancel_request_money, App.getCurrentActivity(),true);
        Button btnDialogCancelRequest = dialogCancel.findViewById(R.id.btnDialogCancelRequest);
        Button btnDialogCancel = dialogCancel.findViewById(R.id.btnDialogCancel);

        final Dialog dialogSend = HelperView.configDialog(R.layout.dialog_request_money, App.getCurrentActivity(),true);
        TextView txtRules = dialogSend.findViewById(R.id.txtRules);
        final Button btnSend = dialogSend.findViewById(R.id.btnSend);
        final CheckBox chkRules = dialogSend.findViewById(R.id.chkRules);

        txtCardNumber.setText(App.account.cardNumber);
        if (App.account.cardNumber.length() < 1) {
            txtCardNumber.setText(App.getContext().getString(R.string.notRegisteredCardNumber));
        }

        String walletText = HelperString.convertToNumberFormat(App.account.wallet + "");
        walletText += " ";
        walletText += App.getContext().getString(R.string.tooman);
        txtWallet.setText(walletText);

        String rules = "1) "+ App.getContext().getString(R.string.requestMoneyRule1);
        rules += "\n";
        rules += "\n";
        rules += "2) "+ App.getContext().getString(R.string.requestMoneyRule2);
        txtRules.setText(rules);

        getLastRequest();

        chkRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkRules.isChecked()) {
                    btnSend.setEnabled(true);
                } else {
                    btnSend.setEnabled(false);
                }
            }
        });

        btnDialogCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCancel.dismiss();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSend.dismiss();
                btnRequestMoney.loading();
                int amount = Integer.valueOf(edtAmount.getText().toString().replace(",",""));
                if (setNewRequest) {
                    actionEnable = false;
                    new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                        @Override
                        public void onComplete(String data) {
                            if(data.equals("1")) {
                                actionEnable = true;
                                btnRequestMoney.reset();
                                getLastRequest();
                                CustomToast.showToast(App.getContext().getString(R.string.requestMoneySent));
                            }else{
                                CustomToast.showToast(App.getContext().getString(R.string.unsuccessfulAct));
                            }
                        }

                        @Override
                        public void onFail(String error) {
                            CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                            actionEnable = true;
                            btnRequestMoney.reset();
                        }
                    }).addMoneyRequest(amount);
                } else {
                    editRequest(lastRequestID, amount, PENDING , App.getContext().getString(R.string.requestSent));
                }
            }
        });

        txtCardNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_EDIT_ACCOUNT_ID)
                        .addFragment(R.id.fragmentContainer, new FragmentEditAccount(), true)
                        .commit();
            }
        });

        btnDialogCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCancel.dismiss();
                editRequest(lastRequestID, -1, DELETED , App.getContext().getString(R.string.requestMoneyCanceled));
            }
        });

        btnRequestMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!actionEnable) {
                    return;
                }

                String amountText = edtAmount.getText().toString().replace(",","");
                if (amountText.length() == 0) {
                    return;
                }
                if (App.account.cardNumber.length() == 0) {
                    CustomToast.showToast(App.getContext().getString(R.string.notRegisteredCardNumber));
                    return;
                }
                int amount = Integer.valueOf(amountText);
                if (amount == 0) {
                    return;
                }
                if (amount > App.account.wallet) {
                    CustomToast.showToast(App.getContext().getString(R.string.notEnoughWalletForRequestMoney));
                    return;
                }
                if (amount < 5000) {
                    CustomToast.showToast(App.getContext().getString(R.string.minimumAmountForRequestMoney));
                    return;
                }
                chkRules.setChecked(false);
                btnSend.setEnabled(false);
                dialogSend.show();
            }
        });

        btnCancelRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!actionEnable) {
                    return;
                }
                dialogCancel.show();

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

    private void editRequest(int moneyRequestID, int amount, String state , final String completeToast) {
        actionEnable = false;
        loading();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                if(data.equals("1")) {
                    actionEnable = true;
                    CustomToast.showToast(completeToast);
                    getLastRequest();
                }else{
                    CustomToast.showToast(App.getContext().getString(R.string.unsuccessfulAct));
                }
            }

            @Override
            public void onFail(String error) {
                actionEnable = true;
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                loaded();
                return;
            }
        }).editMoneyRequest(moneyRequestID, amount, state);
    }

    private void getLastRequest() {
        loading();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    actionEnable = true;
                    loaded();
                    JSONObject lastRequest = new JSONObject(data);
                    if (!lastRequest.has("id")) {
                        txtDesc.setText(App.getContext().getString(R.string.requestMoneyDesc));
                        setAddState();
                        return;
                    }
                    lastRequestID = lastRequest.getInt("id");
                    state = lastRequest.getString("state");
                    String dateTime = lastRequest.getString("updated_at");
                    String date = HelperString.getTransformedDate(dateTime.split(" ")[0]);
                    String rejectDesc = lastRequest.getString("description");
                    lastRequestAmount = lastRequest.getInt("amount");
                    if (state.equals(PENDING)) {
                        String desc = "شما درخواستی در حال بررسی به مبلغ ";
                        desc += HelperString.convertToNumberFormat(lastRequestAmount + "");
                        desc += " تومان در تاریخ ";
                        desc += date;
                        desc += " ثبت کرده اید.";
                        txtDesc.setText(desc);
                        setEditState();
                    } else if (state.equals(REJECTED)) {
                        String desc = "درخواست شما به علت";
                        desc += " ";
                        desc += "\"";
                        desc += rejectDesc;
                        desc += "\"";
                        desc += " ";
                        desc += "رد شد. پس از رفع مشکل مجددا درخواست خود را ارسال کنید.";
                        txtDesc.setText(desc);
                        setAddState();
                    } else {
                        txtDesc.setText(App.getContext().getString(R.string.requestMoneyDesc));
                        setAddState();
                    }


                } catch (JSONException e) {
                    ActivityMain.fragmentManager.popBackStack();
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                ActivityMain.fragmentManager.popBackStack();
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).getLastUserMoneyRequest();
    }

    private void loaded() {
        loader.setVisibility(View.GONE);
        layoutMain.setVisibility(View.VISIBLE);
        edtAmount.setText("");
    }

    private void loading() {
        loader.setVisibility(View.VISIBLE);
        layoutMain.setVisibility(View.GONE);
    }

    private void setEditState() {
        btnCancelRequest.setVisibility(View.VISIBLE);
        btnRequestMoney.setNormalText(App.getContext().getString(R.string.editRequestAmount));
        setNewRequest = false;
    }

    private void setAddState() {
        btnCancelRequest.setVisibility(View.GONE);
        btnRequestMoney.setNormalText(App.getContext().getString(R.string.sendRequest));
        setNewRequest = true;
    }
}
