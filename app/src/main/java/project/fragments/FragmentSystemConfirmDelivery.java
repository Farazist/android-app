package project.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.adapters.AdapterSystemDeliveriesRecycler;
import project.adapters.AdapterWasteRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperFragment;
import project.helper.HelperView;
import project.structures.StructCustomWaste;
import project.structures.StructWaste;

public class FragmentSystemConfirmDelivery extends Fragment {

    private static int price = 0;


    private int oldUndefinedPrice = 0;
    private ProgressBar progressBar;
    private Button btnChargeWallet;
    private Button btnDoneDelivery;
    private int position;
    private int deliveryId;
    private int userId;
    private String items;
    private String customItems;
    private ViewGroup loader;
    private ViewGroup layoutContainer;
    private TextView txtWallet;
    private RecyclerView lstWastes;

    private static int customPrice = 0;
    private static int previousCustomPrice = 0;
    private static TextView txtSum;
    private static CheckBox chkAddCustomItems;
    private static TextView txtAddCustomItems;
    private static TextView txtCustomsCount;
    private static ViewGroup layoutSuccessfulAct;


    JSONArray itemsArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_system_confirm_delivery, container, false);
        price = 0;
        previousCustomPrice = 0;
        customPrice = 0;
        FragmentPackageRequestCustom.clearList();
        loader = (ViewGroup) root.findViewById(R.id.loader);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        layoutContainer = (ViewGroup) root.findViewById(R.id.layoutContainer);
        lstWastes = (RecyclerView) root.findViewById(R.id.lstWastes);
        btnChargeWallet = (Button) root.findViewById(R.id.btnChargeWallet);
        btnDoneDelivery = (Button) root.findViewById(R.id.btnDoneDelivery);
        txtSum = (TextView) root.findViewById(R.id.txtSum);
        txtWallet = (TextView) root.findViewById(R.id.txtWallet);
        txtCustomsCount = (TextView) root.findViewById(R.id.txtCustomsCount);
        chkAddCustomItems = (CheckBox) root.findViewById(R.id.chkAddCustomItems);
        txtAddCustomItems = (TextView) root.findViewById(R.id.txtAddCustomItems);
        layoutSuccessfulAct = (ViewGroup) root.findViewById(R.id.layoutSuccessfulAct);

        Button btnOK = (Button)root.findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityMain.fragmentManager.popBackStack();
                ActivityMain.fragmentManager.popBackStack();
            }
        });

        layoutSuccessfulAct.setVisibility(View.GONE);
        chkAddCustomItems.setVisibility(View.GONE);
        chkAddCustomItems.setChecked(false);
        txtAddCustomItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousCustomPrice = customPrice;
                FragmentPackageRequestCustom fragment = new FragmentPackageRequestCustom();
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_PACKAGE_DELIVERY_CUSTOM)
                        .addFragment(R.id.fragmentContainer, fragment, true)
                        .commit();
            }
        });

        chkAddCustomItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (chkAddCustomItems.isChecked()) {
                    customPrice = FragmentPackageRequestCustom.getSum();
                    changeSum(customPrice);
                    txtCustomsCount.setText("(" + FragmentPackageRequestCustom.getCustomItemsCount() + " " + App.getContext().getString(R.string.itemsAdded) + ")");
                } else {
                    customPrice = 0;
                    changeSum(-FragmentPackageRequestCustom.getSum());
                    txtCustomsCount.setText("");
                }
            }
        });

        position = getArguments().getInt("ID");
        deliveryId = getArguments().getInt("DELIVERY_ID");
        userId = getArguments().getInt("USER_ID");
        items = getArguments().getString("ITEMS");
        customItems = getArguments().getString("CUSTOM_ITEMS");

        try {
            itemsArray = new JSONArray(items);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.GONE);
        layoutContainer.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                lstWastes.setLayoutManager(new LinearLayoutManager(App.getCurrentActivity()));
                try {
                    price = 0;
                    for (int i = 0; i < itemsArray.length(); i++) {
                        boolean founded = false;
                        for (int j = 0; j < App.wastes.size(); j++) {
                            if (App.wastes.get(j).id == itemsArray.getJSONObject(i).getInt("id")){
                                App.wastes.get(j).count = itemsArray.getJSONObject(i).getJSONObject("pivot").getInt("count");
                                price += (App.wastes.get(j).count*itemsArray.getJSONObject(i).getInt("price"));
                                founded = true;
                                break;
                            }
                        }
                        if(founded){
                            continue;
                        }
                    }
                    changeSum(0);
                }catch (Exception e){
                }
                AdapterWasteRecycler adapter = new AdapterWasteRecycler(App.wastes);
                adapter.setOnSumChangedListener(new AdapterWasteRecycler.OnSumChangedListener() {
                    @Override
                    public void onChanged(int value, boolean add) {
                        if (!add) {
                            value = -value;
                        }
                       changeSum(value);
                    }
                });
                lstWastes.setAdapter(adapter);
                getInfo();
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(getString(R.string.connectionError));
                loader.setVisibility(View.GONE);
            }
        }).getWastesTypes();


        btnDoneDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneDelivery(false);
            }
        });

        btnChargeWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneDelivery(true);
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

    private void getInfo() {
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                txtWallet.setText(getString(R.string.balance) + " : " + App.account.wallet + " " + getString(R.string.tooman));
                layoutContainer.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(getString(R.string.connectionError));
                loader.setVisibility(View.GONE);
            }
        }).getUserInfo();
    }

    private void doneDelivery(final boolean chargeWallet) {
        HelperView.hideKeyboard(App.getCurrentActivity());
        if (price <= 0 && !chkAddCustomItems.isChecked()) {
            CustomToast.showToast(App.getContext().getString(R.string.noWasteToSend));
            return;
        }

        if (chargeWallet && price > App.account.wallet) {
            CustomToast.showToast(getString(R.string.notEnoughWallet));
            return;
        }

        try {
            App.canBackPress = false;
            btnChargeWallet.setVisibility(View.GONE);
            btnDoneDelivery.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            JSONObject delivery = new JSONObject();
            delivery.put("id", deliveryId);
            delivery.put("state", App.done);
            JSONArray items = getItems();
            JSONArray customItems = getCustomItems();
            delivery.put("items", items);
            delivery.put("custom_items", customItems);
            new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                @Override
                public void onComplete(String data) {
                    if (chargeWallet) {
                        chargeWallet();
                        return;
                    } else {
                        layoutSuccessfulAct.setVisibility(View.VISIBLE);
                        FragmentMainOwner.getDeliveriesCount(true);
                        new Commands().getUserInfo();
                        App.canBackPress = true;
                        AdapterSystemDeliveriesRecycler.onStateChangeListener.onChanged(position, App.done);
                    }
                }

                @Override
                public void onFail(String error) {
                    App.canBackPress = true;
                    CustomToast.showToast(getString(R.string.connectionError));
                    btnChargeWallet.setVisibility(View.VISIBLE);
                    btnDoneDelivery.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }).editDelivery(delivery);

        } catch (JSONException e) {
            CustomToast.showToast(getString(R.string.oldVersionError));
            btnChargeWallet.setVisibility(View.VISIBLE);
            btnDoneDelivery.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            e.printStackTrace();
        }


    }

    private void chargeWallet() {
        btnChargeWallet.setVisibility(View.GONE);
        btnDoneDelivery.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        if (price > App.account.wallet) {
            CustomToast.showToast(getString(R.string.notEnoughWallet));
            return;
        }
        App.canBackPress = false;
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                layoutSuccessfulAct.setVisibility(View.VISIBLE);
                FragmentMainOwner.getDeliveriesCount(true);
                new Commands().getUserInfo();
                App.canBackPress = true;
                AdapterSystemDeliveriesRecycler.onStateChangeListener.onChanged(position, App.done);
            }

            @Override
            public void onFail(String error) {
                App.canBackPress = true;
                CustomToast.showToast(getString(R.string.connectionError));
                btnChargeWallet.setVisibility(View.VISIBLE);
                btnDoneDelivery.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }
        }).transfer(userId, price, getString(R.string.packageDelivery));
    }


    private JSONArray getItems() {
        JSONArray items = new JSONArray();
        for (int i = 0; i < App.wastes.size(); i++) {
            StructWaste waste = App.wastes.get(i);
            if (waste.count > 0) {
                JSONObject item = new JSONObject();
                try {
                    item.put("id", waste.id);
                    item.put("count", waste.count);
                    items.put(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return items;
    }
    private JSONArray getCustomItems() {
        JSONArray customItems = new JSONArray();
        ArrayList<StructCustomWaste> customWastes = FragmentPackageRequestCustom.getCustomItems();
        if(customWastes == null){
            return customItems;
        }
        for(int i=0;i<customWastes.size();i++) {
            JSONObject customItem = new JSONObject();
            try {
                customItem.put("name", customWastes.get(i).name);
                customItem.put("count", customWastes.get(i).count);
                customItem.put("weight", customWastes.get(i).weight);
                customItem.put("price", customWastes.get(i).price);
                customItems.put(customItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return customItems;
    }

    public static void updateCustomItemsSetting(int count) {
        if (count == 0) {
            chkAddCustomItems.setChecked(false);
            chkAddCustomItems.setVisibility(View.INVISIBLE);
            txtCustomsCount.setText("");
        } else {
            chkAddCustomItems.setChecked(true);
            chkAddCustomItems.setVisibility(View.VISIBLE);
            txtCustomsCount.setText("(" + FragmentPackageRequestCustom.getCustomItemsCount() + " " + App.getContext().getString(R.string.itemsAdded) + ")");
        }
        int newSum = FragmentPackageRequestCustom.getSum();
        changeSum(-previousCustomPrice);
        changeSum(newSum);
        customPrice = newSum;
    }

    private static void changeSum(int value){
        price += value;
        txtSum.setTextColor(Color.parseColor("#000000"));
        txtSum.setText(App.getContext().getString(R.string.sum) + " : " + price + " " + App.getContext().getString(R.string.tooman));
        if (App.account.wallet < price) {
            txtSum.setTextColor(Color.parseColor("#a62626"));
        }
    }

    @Override
    public void onDestroyView() {
        FragmentSystemDeliveryDetails.nextEnable = true;
        super.onDestroy();
    }
}
