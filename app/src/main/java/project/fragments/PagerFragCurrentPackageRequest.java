package project.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.adapters.AdapterSystemDeliveriesRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperFragment;
import project.helper.HelperString;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import project.helper.HelperView;

public class PagerFragCurrentPackageRequest extends Fragment {

    private String state; // waiting *** accepted *** delivery_rejected *** verification_rejected *** done *** deleted

    private ArrayList<Button> btns;
    private String unknown = "\uf128";
    private String verify = "\uf00c";
    private String reject = "\uf00d";
    private String ownerNumber = "";
    private Button btnCancel;
    private Button btnOK;
    private TextView txtTime;
    private TextView txtDate;
    private SwipeRefreshLayout swipe;
    private ViewGroup layoutRequest;
    private ViewGroup layoutDetails;
    private TextView txtSystemAddress;
    private TextView txtUserAddress;
    private TextView txtCall;
    private TextView txtInvoice;
    private TextView txtSum;
    private TextView txtId;
    private TextView txtSystemID;
    private LinearLayout layoutRejectDesc;
    private TextView txtRejectDesc;

    private int deliveryId = 0;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pager_frag_current_package_request, container, false);
        btns = new ArrayList<>();
        txtTime = root.findViewById(R.id.txtTime);
        txtDate = root.findViewById(R.id.txtDate);
        swipe = root.findViewById(R.id.swipe);
        layoutRequest = root.findViewById(R.id.layoutRequest);
        layoutDetails = root.findViewById(R.id.layoutDetails);
        Button btnRegister = (Button) root.findViewById(R.id.btnRegister);
        Button btnVerify = (Button) root.findViewById(R.id.btnVerify);
        Button btnDeliver = (Button) root.findViewById(R.id.btnDeliver);
        btnCancel = (Button) root.findViewById(R.id.btnCancel);
        btnOK = (Button) root.findViewById(R.id.btnOK);

        txtSystemAddress = (TextView) root.findViewById(R.id.txtSystemAddress);
        txtUserAddress = (TextView) root.findViewById(R.id.txtUserAddress);
        txtCall = (TextView) root.findViewById(R.id.txtCall);
        txtInvoice = (TextView) root.findViewById(R.id.txtInvoice);
        txtSum = (TextView) root.findViewById(R.id.txtSum);
        txtId = (TextView) root.findViewById(R.id.txtId);
        txtSystemID = (TextView) root.findViewById(R.id.txtSystemID);
        layoutRejectDesc = (LinearLayout) root.findViewById(R.id.layoutRejectDesc);
        txtRejectDesc = (TextView) root.findViewById(R.id.txtRejectDesc);

        btns.add(btnRegister);
        btns.add(btnVerify);
        btns.add(btnDeliver);

        swipe.setColorSchemeColors(App.getContext().getResources().getColor(R.color.colorAccent),App.getContext().getResources().getColor(R.color.colorPrimary));
        swipe.setRefreshing(true);
        getLastRequest(false);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getLastRequest(true);
            }
        });


        txtCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + ownerNumber));
                    startActivity(intent);
                }catch (Exception e){
                    CustomToast.showToast(getString(R.string.unsuccessfulAct));
                }
            }
        });


        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(state.equals(App.done) || state.equals(App.verification_rejected) || state.equals(App.delivery_rejected)){
                    seenDelivery();
                }

                ActivityMain.fragmentManager.popBackStack();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swipe.setRefreshing(true);
                layoutRequest.setVisibility(View.GONE);
                getLastRequestState();
            }
        });
        return root;
    }

    private void getLastRequest(final boolean notifyDeliveriesList) {
        swipe.setRefreshing(true);
        layoutRequest.setVisibility(View.GONE);
        for (int i = 0; i < btns.size(); i++) {
            changeButtonState(btns.get(i), unknown);
        }
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {

                try {
                    if(PagerFragOldPackageRequest.listener !=null && notifyDeliveriesList) {
                        PagerFragOldPackageRequest.listener.onChanged();
                    }
                    swipe.setRefreshing(false);
                    JSONObject request = new JSONObject(data);
                    state = request.getString("state");

                    if(state.equals(App.done)){
                        btnCancel.setVisibility(View.GONE);
                    }

                    if(state.equals(App.verification_rejected) || state.equals(App.delivery_rejected)){
                        layoutRejectDesc.setVisibility(View.VISIBLE);
                        btnCancel.setVisibility(View.GONE);
                    }else{
                        layoutRejectDesc.setVisibility(View.GONE);
                    }
                    if(!request.isNull("description") && request.getString("description").length()>0){
                        txtRejectDesc.setText(request.getString("description") + " "+App.getContext().getString(R.string.canceled));
                    }
                    Log.i("LOGGG","1");
                    deliveryId = request.getInt("id");
                    txtId.setText(getString(R.string.deliveryID)+" : "+deliveryId);
                    String date = request.getString("created_at").split(" ")[0];
                    String time = request.getString("created_at").split(" ")[1];
                    request.getJSONObject("city");
                    request.getJSONObject("city").getJSONObject("province");
                    String userProvinceName = request.getJSONObject("city").getJSONObject("province").getString("name");
                    String userCityName = request.getJSONObject("city").getString("name");
                    String userAddress = request.getString("address");
                    String userFullAddress = userProvinceName+" - "+userCityName+" - "+userAddress;
                    txtUserAddress.setText(userFullAddress);
                    txtDate.setText(HelperString.getTransformedDate(date));
                    txtTime.setText(HelperString.getTransformedTime(time));
                    int systemID = request.getJSONObject("system").getInt("id");
                    txtSystemID.setText(App.getContext().getString(R.string.systemID_)+" "+systemID);
                    String systemProvinceName = request.getJSONObject("system").getJSONObject("city").getJSONObject("province").getString("name");
                    String systemCityName = request.getJSONObject("system").getJSONObject("city").getString("name");
                    String systemAddress = request.getJSONObject("system").getString("address");
                    String systemFullAddress = systemProvinceName+" - "+systemCityName+" - "+systemAddress;
                    txtSystemAddress.setText(systemFullAddress);
                    ownerNumber = request.getJSONObject("system").getJSONObject("owner").getString("mobile_number");
                    txtCall.setText(ownerNumber);
                    JSONArray items = request.getJSONArray("items");
                    JSONArray customItems = request.getJSONArray("custom_items");
                    String invoice = HelperString.computeInvoice(items);
                    String customInvoice = HelperString.computeCustomInvoice(customItems);
                    int definedPrice = HelperString.getSum(items);
                    String fullInvoice = "";
                    if(invoice.length()>0){
                        fullInvoice+=invoice;
                    }
                    if(invoice.length()>0 && customInvoice.length()>0){
                        fullInvoice+="\n";
                        fullInvoice+=customInvoice;
                    }
                    if(invoice.length()==0 && customInvoice.length()>0){
                        fullInvoice+=customInvoice;
                    }
                    txtInvoice.setText(fullInvoice);

                    String totalPrice = "";
                    if(items.length()>0){
                        totalPrice+= definedPrice +" "+App.getContext().getString(R.string.tooman);
                    }
                    if(items.length()>0 && customItems.length()>0){
                        totalPrice+= " + ("+App.getContext().getString(R.string.agreedPriceForCustomItems)+")";
                    }
                    if(items.length()==0 && customItems.length()>0){
                        totalPrice+= App.getContext().getString(R.string.agreedPrice);
                    }
                    txtSum.setText(totalPrice);

                    if(state.equals(App.waiting)){
                        changeButtonState(btns.get(0),verify);
                    }else if(state.equals(App.accepted)){
                        changeButtonState(btns.get(0),verify);
                        changeButtonState(btns.get(1),verify);
                    }else if(state.equals(App.done)){
                        changeButtonState(btns.get(0),verify);
                        changeButtonState(btns.get(1),verify);
                        changeButtonState(btns.get(2),verify);
                    }else if(state.equals(App.verification_rejected)){
                        changeButtonState(btns.get(0),verify);
                        changeButtonState(btns.get(1),reject);
                    }else if(state.equals(App.delivery_rejected)){
                        changeButtonState(btns.get(0),verify);
                        changeButtonState(btns.get(1),verify);
                        changeButtonState(btns.get(2),reject);
                    }

                    layoutRequest.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    swipe.setRefreshing(false);
                    layoutRequest.setVisibility(View.GONE);
                    Log.i("LOGGG","120");
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                    e.printStackTrace();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                swipe.setRefreshing(false);
                layoutRequest.setVisibility(View.GONE);
                CustomToast.showToast(getString(R.string.connectionError));

            }
        }).getLastUserDelivery();
    }

    private void changeButtonState(Button btn, String text) {
        btn.setText(text);
        if (text.equals(unknown)) {
            btn.setBackgroundResource(R.drawable.circle_gray);
        } else if (text.equals(reject)) {
            btn.setBackgroundResource(R.drawable.circle_red);
        } else {
            btn.setBackgroundResource(R.drawable.circle_green);
        }
    }


    private void cancelRequest(){
        try {
            JSONObject deleteRequest = new JSONObject();
            deleteRequest.put("id",deliveryId);
            deleteRequest.put("state",App.deleted);
            new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                @Override
                public void onComplete(String data) {
                    CustomToast.showToast(App.getContext().getString(R.string.requestCanceled));
                    ActivityMain.fragmentManager.popBackStack();
                    seenDelivery();
                }

                @Override
                public void onFail(String error) {
                    CustomToast.showToast(getString(R.string.connectionError));
                }
            }).editDelivery(deleteRequest);
        } catch (JSONException e) {
            CustomToast.showToast(getString(R.string.oldVersionError));
            e.printStackTrace();
        }
    }

    private void getLastRequestState(){
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONObject request = new JSONObject(data);
                    String newState = request.getString("state");
                    if(!newState.equals(state)){
                        CustomToast.showToast(App.getContext().getString(R.string.stateChanged));
                        ActivityMain.fragmentManager.popBackStack();
                        return;
                    }
                    if(newState.equals(App.deleted)||newState.equals(App.delivery_rejected)||newState.equals(App.verification_rejected)||newState.equals(App.done)){
                        CustomToast.showToast(App.getContext().getString(R.string.canNotCancelRequest));
                        swipe.setRefreshing(false);
                        layoutRequest.setVisibility(View.VISIBLE);
                        return;
                    }else{
                        cancelRequest();
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
        }).getLastUserDelivery();
    }


    private void seenDelivery(){
        try {
            JSONObject delivery = new JSONObject();
            delivery.put("id", deliveryId);
            delivery.put("seen", 1);
            new Commands().editDelivery(delivery);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
