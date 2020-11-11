package project.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import project.activities.ActivityMain;
import project.adapters.AdapterWasteRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperFragment;

public class FragPackageRequestStepOne extends Fragment {

    private int sum = 0;
    public static boolean nextEnable = true;
    private ViewGroup loader;
    private ViewGroup layoutContainer;
    private RecyclerView lstWastes;
    private TextView txtSum;
    private int systemID;
    private int cityID;
    private int provinceID;
    private String name;

    private static CheckBox chkAddCustomItems;
    private static TextView txtAddCustomItems;
    private static TextView txtCustomsCount;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_package_request_step_one, container, false);
        loader = (ViewGroup) root.findViewById(R.id.loader);
        layoutContainer = (ViewGroup) root.findViewById(R.id.layoutContainer);
        lstWastes = (RecyclerView) root.findViewById(R.id.lstWastes);
        Button btnGoNextStep = (Button) root.findViewById(R.id.btnGoNextStep);
        txtSum = (TextView) root.findViewById(R.id.txtSum);
        txtCustomsCount = (TextView) root.findViewById(R.id.txtCustomsCount);
        chkAddCustomItems = (CheckBox) root.findViewById(R.id.chkAddCustomItems);
        txtAddCustomItems = (TextView)root.findViewById(R.id.txtAddCustomItems);

        FragmentPackageRequestCustom.clearList();
        chkAddCustomItems.setChecked(false);
        chkAddCustomItems.setVisibility(View.INVISIBLE);
        nextEnable = true;
        systemID = App.clientSystem.id;
        cityID = App.account.cityID;
        provinceID = App.account.provinceID;
        name = App.account.name;
        getSystemWastesList();

        txtAddCustomItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_PACKAGE_DELIVERY_CUSTOM)
                        .addFragment(R.id.fragmentContainer,new FragmentPackageRequestCustom(),true)
                        .commit();
            }
        });

        chkAddCustomItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chkAddCustomItems.isChecked()){
                    txtCustomsCount.setText("("+ FragmentPackageRequestCustom.getCustomItemsCount()+" "+App.getContext().getString(R.string.itemsAdded)+")");
                }else{
                    txtCustomsCount.setText("");
                }
            }
        });

        btnGoNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!name.equals(App.account.name) || systemID != App.clientSystem.id || cityID != App.account.cityID || provinceID != App.account.provinceID) {
                    CustomToast.showToast(App.getContext().getString(R.string.informationChanged));
                    ActivityMain.fragmentManager.popBackStack();
                    return;
                }
                if(App.account.cityID<1 || App.account.provinceID<1){
                    CustomToast.showToast(getString(R.string.setCityAndProvinceForDelivery));
                    return;
                }
                if (App.account.address == null || App.account.address.equals("null") || App.account.address.length()<1) {
                    CustomToast.showToast(getString(R.string.setAddressForDelivery));
                    return;
                }
                if (App.clientSystem.id <= 0) {
                    CustomToast.showToast(getString(R.string.connectToSystemForPackageRequest));
                    return;
                }
                if (App.account.address.length() < 1) {
                    CustomToast.showToast(App.getContext().getString(R.string.setAddressForDelivery));
                    return;
                }
                if (sum <= 0 && !chkAddCustomItems.isChecked()) {
                    CustomToast.showToast(App.getContext().getString(R.string.noWasteToSend));
                    return;
                }
                if (!nextEnable) {
                    return;
                }
                nextEnable = false;

                Fragment fragStepTwo = new FragPackageRequestStepTwo();
                Bundle extras = new Bundle();
                extras.putString("AMOUNT", sum + "");
                extras.putInt("SYSTEM_ID", systemID);
                extras.putInt("CITY_ID", App.account.cityID);
                extras.putInt("PROVINCE_ID", App.account.provinceID);
                extras.putString("PROVINCE", App.account.province);
                extras.putString("CITY", App.account.city);
                extras.putBoolean("HAS_CUSTOM_ITEMS", chkAddCustomItems.isChecked());
                fragStepTwo.setArguments(extras);
                PagerFragNewPackageRequest.step = 2;
                FragmentTransaction fragmentTransaction = PagerFragNewPackageRequest.childFragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.add(R.id.fragmentDeliveryContainer, fragStepTwo, App.FRAGMENT_PACKAGE_DELIVERY_STEP2_ID);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
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

    private void getSystemWastesList() {
        App.wastes.clear();
        systemID = App.clientSystem.id;
        sum = 0;
        txtSum.setText(getString(R.string.totalAmount) + " : " + sum + " " + getString(R.string.tooman));
        loader.setVisibility(View.VISIBLE);
        layoutContainer.setVisibility(View.GONE);
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                lstWastes.setLayoutManager(new LinearLayoutManager(App.getCurrentActivity()));
                layoutContainer.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);

                AdapterWasteRecycler adapter = new AdapterWasteRecycler(App.wastes);

                adapter.setOnSumChangedListener(new AdapterWasteRecycler.OnSumChangedListener() {
                    @Override
                    public void onChanged(int value, boolean add) {
                        if (App.clientSystem.id != systemID) {
                            CustomToast.showToast(App.getContext().getString(R.string.yourConnectedSystemChanged));
                            getSystemWastesList();
                            return;
                        }
                        if (add) {
                            sum += value;
                        } else {
                            sum -= value;
                        }
                        txtSum.setText(App.getContext().getString(R.string.totalAmount) + " : " + sum + " " + App.getContext().getString(R.string.tooman));
                    }
                });
                lstWastes.setAdapter(adapter);
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                loader.setVisibility(View.GONE);
            }
        }).getWastesTypes();
    }

    public static void updateCustomItemsSetting(int count){
        if(count==0){
            chkAddCustomItems.setChecked(false);
            chkAddCustomItems.setVisibility(View.INVISIBLE);
            txtCustomsCount.setText("");
        }else{
            chkAddCustomItems.setChecked(true);
            chkAddCustomItems.setVisibility(View.VISIBLE);
            txtCustomsCount.setText("("+ FragmentPackageRequestCustom.getCustomItemsCount()+" "+App.getContext().getString(R.string.itemsAdded)+")");
        }
    }
}
