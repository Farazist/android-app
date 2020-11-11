package project.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.adapters.AdapterSystemDeliveriesRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperView;

public class FragmentSystemDeliveryDetails extends Fragment {

    public static boolean nextEnable;

    private int position;
    private String state = "";
    private int deliveryId = -1;
    private TextView txtState ;
    private Button btnReject ;
    private  Button btnNext   ;
    private  ProgressBar loader   ;
    private  Dialog dialogRejectReason   ;

    private ArrayList<RadioButton> reasonButtons;
    private ArrayList<String> reasons;
    private EditText edtReason;
    private String reason;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_system_deliveriy_details, container, false);
        nextEnable = true;
        TextView txtName = (TextView) root.findViewById(R.id.txtTitle);
        TextView txtMobile = (TextView) root.findViewById(R.id.txtMobile);
        ImageView imgCover = (ImageView) root.findViewById(R.id.img);
        TextView txtUserAddress = (TextView) root.findViewById(R.id.txtUserAddress);
        TextView txtInvoice = (TextView) root.findViewById(R.id.txtInvoice);
        TextView txtDate = (TextView) root.findViewById(R.id.txtDate);
        TextView txtTime = (TextView) root.findViewById(R.id.txtTime);
        TextView txtSum = (TextView) root.findViewById(R.id.txtSum);
        txtState = (TextView) root.findViewById(R.id.txtState);

        reasons = new ArrayList<>();
        reasons.add(App.getContext().getString(R.string.limitedTime));
        reasons.add(App.getContext().getString(R.string.longDistance));
        reasons.add(App.getContext().getString(R.string.lowDeliveryAmount));
        reasons.add(App.getContext().getString(R.string.offlineSystem));
        reasons.add(App.getContext().getString(R.string.problemInSystem));
        reasons.add(App.getContext().getString(R.string.others));

        btnReject = root.findViewById(R.id.btnReject);
        btnNext    = root.findViewById(R.id.btnNext);
        loader = root.findViewById(R.id.loader);
        loader.setVisibility(View.GONE);

        position = getArguments().getInt("POSITION");
        deliveryId = getArguments().getInt("DELIVERY_ID");
        final int userId = getArguments().getInt("USER_ID");
        String coverUrl = getArguments().getString("AVATAR");
        String name = getArguments().getString("NAME");
        final String mobile = getArguments().getString("MOBILE");
        String userProvince = getArguments().getString("USER_PROVINCE");
        String userCity = getArguments().getString("USER_CITY");
        String userAddress = getArguments().getString("USER_ADDRESS");
        String invoice = getArguments().getString("INVOICE");
        final String items = getArguments().getString("ITEMS");
        final String customItems = getArguments().getString("CUSTOM_ITEMS");
        String date = getArguments().getString("DATE");
        String time = getArguments().getString("TIME");
        int definedPrice = getArguments().getInt("PRICE");
        state = getArguments().getString("STATE");

        if(coverUrl.length()>0){
            Picasso.with(App.getContext()).load(coverUrl).resize(300,300).into(imgCover);
        }else{
            imgCover.setImageResource(R.drawable.avatar);
        }

        String totalPrice = "";
        try {
            if(new JSONArray(items).length()>0){
                totalPrice+= definedPrice +" "+App.getContext().getString(R.string.tooman);
            }
            if(new JSONArray(items).length()>0 && new JSONArray(customItems).length()>0){
                totalPrice+= " + ("+App.getContext().getString(R.string.agreedPriceForCustomItems)+")";
            }
            if(new JSONArray(items).length()==0 && new JSONArray(customItems).length()>0){
                totalPrice+= App.getContext().getString(R.string.agreedPrice);
            }
            txtSum.setText(totalPrice);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txtName.setText(name);
        txtMobile.setText(mobile);

        dialogRejectReason = new Dialog(App.getCurrentActivity());
        dialogRejectReason.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        dialogRejectReason.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogRejectReason.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogRejectReason.setContentView(R.layout.dialog_reject_reason);
        Window window = dialogRejectReason.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        Button btnRejectConfirm = (Button)dialogRejectReason.findViewById(R.id.btnRejectConfirm);
        final Button btnCancel = (Button)dialogRejectReason.findViewById(R.id.btnCancel);


        reasonButtons = new ArrayList<>();
        RadioButton r1 = (RadioButton)dialogRejectReason.findViewById(R.id.r1);
        RadioButton r2 = (RadioButton)dialogRejectReason.findViewById(R.id.r2);
        RadioButton r3 = (RadioButton)dialogRejectReason.findViewById(R.id.r3);
        RadioButton r4 = (RadioButton)dialogRejectReason.findViewById(R.id.r4);
        RadioButton r5 = (RadioButton)dialogRejectReason.findViewById(R.id.r5);
        RadioButton r6 = (RadioButton)dialogRejectReason.findViewById(R.id.r6);
        edtReason  = (EditText) dialogRejectReason.findViewById(R.id.edtReason);
        reasonButtons.add(r1);
        reasonButtons.add(r2);
        reasonButtons.add(r3);
        reasonButtons.add(r4);
        reasonButtons.add(r5);
        reasonButtons.add(r6);

        View.OnClickListener reasonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeReason((RadioButton)view);
            }
        };

        r1.setOnClickListener(reasonListener);
        r2.setOnClickListener(reasonListener);
        r3.setOnClickListener(reasonListener);
        r4.setOnClickListener(reasonListener);
        r5.setOnClickListener(reasonListener);
        r6.setOnClickListener(reasonListener);



        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogRejectReason.dismiss();
            }
        });

        btnRejectConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(reason.equals(App.getContext().getString(R.string.others))){
                    reason = edtReason.getText().toString();
                }
                if(reason.equals("")){
                    changeReason(reasonButtons.get(0));
                    return;
                }else{
                    reject();
                }
            }
        });

        txtMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + mobile));
                    startActivity(intent);
                }catch (Exception e){
                    CustomToast.showToast(getString(R.string.unsuccessfulAct));
                }
            }
        });

        if (coverUrl != null && coverUrl.length()> 0) {
            Picasso.with(App.getContext()).load(coverUrl).into(imgCover);
        }

        if (state.equals(App.waiting)) {
            txtState.setText(App.getContext().getString(R.string.register));
            btnNext.setText(getString(R.string.verify));
        } else if (state.equals(App.delivery_rejected) || state.equals(App.verification_rejected)) {
            txtState.setText(App.getContext().getString(R.string.reject));
        } else if (state.equals(App.accepted)) {
            txtState.setText(App.getContext().getString(R.string.verify));
            btnNext.setText(getString(R.string.delivery));
        } else if (state.equals(App.done)) {
            txtState.setText(App.getContext().getString(R.string.delivery));
        } else {
            txtState.setText(App.getContext().getString(R.string.unknown));
        }

        txtUserAddress.setText(userProvince+ " - "+userCity+" - "+userAddress);
        txtInvoice.setText(invoice);
        txtDate.setText(date);
        txtTime.setText(time);


        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeReason(reasonButtons.get(0));
                edtReason.setText("");
                edtReason.setVisibility(View.GONE);
                dialogRejectReason.show();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!nextEnable){
                    return;
                }
                try {
                    if (state.equals(App.waiting)) {
                        loader.setVisibility(View.VISIBLE);
                        btnNext.setVisibility(View.GONE);
                        btnReject.setVisibility(View.GONE);
                        JSONObject delivery = new JSONObject();
                        delivery.put("id", deliveryId);
                        delivery.put("state", App.accepted);
                        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                            @Override
                            public void onComplete(String data) {
                                state = App.accepted;
                                CustomToast.showToast(getString(R.string.deliveryVerified));
                                loader.setVisibility(View.GONE);
                                btnNext.setVisibility(View.VISIBLE);
                                btnReject.setVisibility(View.VISIBLE);
                                txtState.setText(getString(R.string.verify));
                                btnNext.setText(getString(R.string.delivery));
                                AdapterSystemDeliveriesRecycler.onStateChangeListener.onChanged(position, App.accepted);

                            }

                            @Override
                            public void onFail(String error) {
                                CustomToast.showToast(getString(R.string.connectionError));
                                loader.setVisibility(View.GONE);
                                btnNext.setVisibility(View.VISIBLE);
                                btnReject.setVisibility(View.VISIBLE);
                            }
                        }).editDelivery(delivery);
                    }else if(state.equals(App.accepted)){
                        nextEnable = false;
                        Fragment fragmentPackageDelivery = new FragmentSystemConfirmDelivery();
                        Bundle extras = new Bundle();
                        extras.putInt("DELIVERY_ID", deliveryId);
                        extras.putInt("USER_ID", userId);
                        extras.putString("ITEMS", items);
                        extras.putString("CUSTOM_ITEMS", customItems);
                        fragmentPackageDelivery.setArguments(extras);
                        FragmentTransaction fragmentTransaction =  ActivityMain.fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left,R.anim.slide_out_right,R.anim.slide_in_right,R.anim.slide_out_left);
                        fragmentTransaction.add(R.id.fragmentContainer, fragmentPackageDelivery, App.FRAGMENT_PACKAGE_DELIVERY_ID);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }

                } catch (JSONException e) {
                    CustomToast.showToast(getString(R.string.oldVersionError));
                    e.printStackTrace();
                    loader.setVisibility(View.GONE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.VISIBLE);
                }

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

    private void reject(){
        try {
            dialogRejectReason.dismiss();
            loader.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            btnReject.setVisibility(View.GONE);
            JSONObject delivery = new JSONObject();
            delivery.put("id", deliveryId);
            delivery.put("description", reason);
            String newState = "";
            if (state.equals(App.waiting)) {
                newState = App.verification_rejected;
            } else {
                newState = App.delivery_rejected;
            }
            delivery.put("state", newState);
            final String finalNewState = newState;
            new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                @Override
                public void onComplete(String data) {
                    FragmentMainOwner.getDeliveriesCount(true);
                    CustomToast.showToast(getString(R.string.deliveryRejected));
                    loader.setVisibility(View.GONE);
                    txtState.setText(getString(R.string.reject));
                    AdapterSystemDeliveriesRecycler.onStateChangeListener.onChanged(position, finalNewState);
                    ActivityMain.fragmentManager.popBackStack();
                }

                @Override
                public void onFail(String error) {
                    CustomToast.showToast(getString(R.string.connectionError));
                    loader.setVisibility(View.GONE);
                    btnNext.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.VISIBLE);
                }
            }).editDelivery(delivery);

        } catch (JSONException e) {
            CustomToast.showToast(getString(R.string.oldVersionError));
            e.printStackTrace();
            loader.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);
            btnReject.setVisibility(View.VISIBLE);
        }
    }

    private void changeReason(RadioButton radioButton){
        edtReason.setText("");
        for(int i = 0; i< reasonButtons.size(); i++){
            reasonButtons.get(i).setChecked(false);
        }
        radioButton.setChecked(true);
        reason = reasons.get(reasonButtons.indexOf(radioButton));
        if(reason.equals(App.getContext().getString(R.string.others))){
            edtReason.setVisibility(View.VISIBLE);
        }else{
            edtReason.setVisibility(View.GONE);
            dialogRejectReason.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            HelperView.hideKeyboard(App.getCurrentActivity());
        }
    }

}
