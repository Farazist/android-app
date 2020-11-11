package project.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.FragmentTransaction;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

import project.activities.ActivityMain;
import project.custom.CustomToast;
import project.structures.StructCustomWaste;
import project.structures.StructWaste;

public class FragPackageRequestStepTwo extends Fragment {

    public static boolean nextEnable;
    private int systemID;
    private int cityID;
    private int provinceID;
    private String name;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View root = inflater.inflate(R.layout.frag_package_request_step_two, container, false);
        root.setFocusableInTouchMode(true);
        root.requestFocus();

        systemID = App.clientSystem.id;
        cityID = App.account.cityID;
        provinceID = App.account.provinceID;
        name = App.account.name;

        TextView txtInvoice = (TextView) root.findViewById(R.id.txtInvoice);
        TextView txtAmount = (TextView) root.findViewById(R.id.txtAmount);
        TextView txtSystem = (TextView) root.findViewById(R.id.txtSystem);
        final TextView txtAddress = (TextView) root.findViewById(R.id.txtAddress);
        final TextView txtProvince = (TextView) root.findViewById(R.id.txtProvince);
        final TextView txtCity = (TextView) root.findViewById(R.id.txtCity);
        Button btnVerify = (Button) root.findViewById(R.id.btnVerify);
        Button btnEditAddress = (Button) root.findViewById(R.id.btnEditAddress);


        final Dialog dialogEditAddress = new Dialog(App.getCurrentActivity());
        dialogEditAddress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogEditAddress.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogEditAddress.setContentView(R.layout.dialog_edit_address);
        Window window = dialogEditAddress.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        final EditText edtAddress = (EditText) dialogEditAddress.findViewById(R.id.edtAddress);
        Button btnCancel = (Button) dialogEditAddress.findViewById(R.id.btnCancel);
        Button btnEdit = (Button) dialogEditAddress.findViewById(R.id.btnEdit);


        btnEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtAddress.setText(txtAddress.getText().toString());
                dialogEditAddress.show();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditAddress.dismiss();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtAddress.setText(edtAddress.getText().toString());
                dialogEditAddress.dismiss();
            }
        });


        final String definedPrice = getArguments().getString("AMOUNT");
        final int deliverySystemID = getArguments().getInt("SYSTEM_ID");
        final int cityID = getArguments().getInt("CITY_ID");
        final int provinceID = getArguments().getInt("PROVINCE_ID");
        final String city = getArguments().getString("CITY");
        final String province = getArguments().getString("PROVINCE");
        final boolean containsCustomItems = getArguments().getBoolean("HAS_CUSTOM_ITEMS");
        nextEnable = true;
        boolean containsDefinedItems = false;

        final JSONArray items = new JSONArray();
        final JSONArray customItems = new JSONArray();

        boolean first = true;
        String invoice = "";
        for (int i = 0; i < App.wastes.size(); i++) {
            StructWaste waste = App.wastes.get(i);
            if (waste.count > 0) {
                JSONObject item = new JSONObject();
                try {
                    item.put("id", waste.id);
                    item.put("count", waste.count);
                    items.put(item);
                    containsDefinedItems = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (!first) {
                    invoice += "\n";
                } else {
                    first = false;
                }
                invoice = invoice + waste.count + " " + App.getCurrentActivity().getString(R.string.unit) + " ";
                invoice += waste.name;

            }
        }
        if (invoice.length() > 0 && containsCustomItems) {
            invoice += "\n";
        }
        ArrayList<StructCustomWaste> customWastes = FragmentPackageRequestCustom.getCustomItems();
        if (containsCustomItems) {
            for (int i = 0; i < customWastes.size(); i++) {
                StructCustomWaste customWaste = customWastes.get(i);
                JSONObject customItem = new JSONObject();
                try {
                    customItem.put("name", customWaste.name);
                    customItem.put("count", customWaste.count);
                    customItem.put("weight", customWaste.weight);
                    customItems.put(customItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (customWaste.weight > 0) {
                    invoice = invoice + customWaste.weight + " " + App.getContext().getString(R.string.kilogram) + " " + customWaste.name;

                } else {
                    invoice = invoice + customWaste.count + " " + App.getContext().getString(R.string.unit) + " " + customWaste.name;
                }
               if(i<customWastes.size()-1){
                   invoice = invoice + "\n";
               }

            }
        }

        String totalPrice = "";
        if (containsDefinedItems) {
            totalPrice = definedPrice + " " + App.getContext().getString(R.string.tooman);
        }
        if (containsDefinedItems && containsCustomItems) {
            totalPrice += " + (" + App.getContext().getString(R.string.agreedPriceForCustomItems) + ")";
        }
        if (!containsDefinedItems && containsCustomItems) {
            totalPrice += App.getContext().getString(R.string.agreed);
        }
        txtAmount.setText(totalPrice);
        txtAddress.setText(App.account.address);
        txtInvoice.setText(invoice);
        edtAddress.setText(txtAddress.getText().toString());
        txtSystem.setText(App.clientSystem.cityName + " - " + App.clientSystem.name);

        if (provinceID > 0) {
            txtProvince.setText(province);
        } else {
            txtProvince.setText("-");
        }

        if (cityID > 0) {
            txtCity.setText(city);
        } else {
            txtProvince.setText("-");
        }

        final String finalInvoice = invoice;


        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!nextEnable) {
                    return;
                }
                if (!name.equals(App.account.name) || systemID != App.clientSystem.id || cityID != App.account.cityID || provinceID != App.account.provinceID) {
                    CustomToast.showToast(App.getContext().getString(R.string.informationChanged));
                    ActivityMain.fragmentManager.popBackStack();
                    return;
                }
                nextEnable = false;
                Fragment fragStepThree = new FragPackageRequestStepThree();
                Bundle extras = new Bundle();
                extras.putString("AMOUNT", definedPrice);
                extras.putString("INVOICE", finalInvoice);
                extras.putString("ITEMS", items.toString());
                extras.putString("CUSTOM_ITEMS", customItems.toString());
                extras.putString("ADDRESS", txtAddress.getText().toString());
                extras.putInt("SYSTEM_ID", deliverySystemID);
                extras.putInt("PROVINCE_ID", provinceID);
                extras.putInt("CITY_ID", cityID);
                fragStepThree.setArguments(extras);
                PagerFragNewPackageRequest.step = 3;

                FragmentTransaction fragmentTransaction = PagerFragNewPackageRequest.childFragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.add(R.id.fragmentDeliveryContainer, fragStepThree, App.FRAGMENT_PACKAGE_DELIVERY_STEP3_ID);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (ActivityMain.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    ActivityMain.drawerLayout.closeDrawer(Gravity.RIGHT);
                    return true;
                }
                Fragment currentFragment = PagerFragNewPackageRequest.childFragmentManager.findFragmentById(R.id.fragmentDeliveryContainer);
                if (currentFragment instanceof FragPackageRequestStepTwo) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        PagerFragNewPackageRequest.childFragmentManager.popBackStack();
                        return true;
                    }
                }
                return false;
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

    @SuppressLint("MissingSuperCall")
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PagerFragNewPackageRequest.step = 1;
        FragPackageRequestStepOne.nextEnable = true;

    }

}
