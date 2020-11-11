package project.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import project.connection.Commands;
import project.custom.CustomToast;

public class FragPackageRequestStepThree extends Fragment {

    private int amount = 0;
    private int id = 0;
    private String dateTime = "";
    private String invoice;
    private String items;
    private String customItems;
    private int cityID;
    private int provinceID;
    private String address;
    private int deliverySystemID;
    private ViewGroup layoutRetry;
    private ViewGroup layoutSending;
    String undefinedItemName = "";
    int undefinedItemWeight = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_package_request_step_three, container, false);
        App.canBackPress = false;
        FragPackageRequestStepTwo.nextEnable = true;
        amount = Integer.parseInt(getArguments().getString("AMOUNT"));
        invoice = getArguments().getString("INVOICE");
        items = getArguments().getString("ITEMS");
        customItems = getArguments().getString("CUSTOM_ITEMS");
        address = getArguments().getString("ADDRESS");
        deliverySystemID = getArguments().getInt("SYSTEM_ID");
        provinceID = getArguments().getInt("PROVINCE_ID");
        cityID = getArguments().getInt("CITY_ID");
        undefinedItemName = getArguments().getString("UNDEFINED_ITEM_NAME");
        undefinedItemWeight = getArguments().getInt("UNDEFINED_ITEM_WEIGHT");
        Button btnRetry = (Button) root.findViewById(R.id.btnRetry);

        layoutRetry = (ViewGroup) root.findViewById(R.id.layoutRetry);
        layoutSending = (ViewGroup) root.findViewById(R.id.layoutSending);

        layoutRetry.setVisibility(View.GONE);


        sendData();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layoutRetry.setVisibility(View.GONE);
                layoutSending.setVisibility(View.VISIBLE);
                sendData();
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

    private void sendData() {
        App.canBackPress = false;
        try {
            new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                @Override
                public void onComplete(String data) {
                    try {
                        JSONObject result = new JSONObject(data);
                        dateTime = result.getString("created_at");
                        id = result.getInt("id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pop();
                }

                @Override
                public void onFail(String error) {
                    App.canBackPress = true;
                    CustomToast.showToast(App.getContext().getString(R.string.unsuccessfulAct));
                    layoutRetry.setVisibility(View.VISIBLE);
                    layoutSending.setVisibility(View.GONE);

                }
            }).addNewDelivery(App.account.id,
                    deliverySystemID,
                    new JSONArray(items),
                    new JSONArray(customItems),
                    undefinedItemName,
                    undefinedItemWeight,
                    provinceID,
                    cityID,
                    address,
                    App.waiting);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void pop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    App.getHandler().post(new Runnable() {
                        @Override
                        public void run() {

                            Fragment fragStepFour = new FragPackageRequestStepFour();
                            Bundle extras = new Bundle();
                            extras.putString("DATE_TIME", dateTime);
                            extras.putInt("ID", id);
                            fragStepFour.setArguments(extras);
                            PagerFragNewPackageRequest.step = 4;

                            FragmentTransaction fragmentTransaction = PagerFragNewPackageRequest.childFragmentManager.beginTransaction();
                            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                            fragmentTransaction.add(R.id.fragmentDeliveryContainer, fragStepFour, App.FRAGMENT_PACKAGE_DELIVERY_STEP4_ID);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                            App.canBackPress = true;

                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragPackageRequestStepTwo.nextEnable = true;
    }
}
