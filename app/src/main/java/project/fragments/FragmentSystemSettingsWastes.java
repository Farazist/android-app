package project.fragments;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import project.adapters.AdapterWasteEditRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.custom.ProgressButton;
import project.structures.StructWasteEdit;

public class FragmentSystemSettingsWastes extends Fragment {

    private static ArrayList<StructWasteEdit> wastes;
    private static AdapterWasteEditRecycler adapter;
    private RecyclerView lstWastes;

    private ViewGroup loader;
    private ViewGroup layoutContainer;
    private boolean verifyEnable;
    private Dialog dialogTicketGuidance;
    private static Dialog dialogEditPrice;
    private boolean showDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_system_settings_wastes, container, false);
        layoutContainer = root.findViewById(R.id.layoutContainer);
        loader = root.findViewById(R.id.loader);
        lstWastes = root.findViewById(R.id.lstWastes);
        final ProgressButton btnVerify = (ProgressButton) root.findViewById(R.id.btnVerify);
        btnVerify.setNormalText(App.getContext().getString(R.string.applyChanges));
        btnVerify.setDoneText(App.getContext().getString(R.string.done));
        verifyEnable = true;
        getMyWastes();

        dialogTicketGuidance = new Dialog(App.getCurrentActivity());
        dialogTicketGuidance.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogTicketGuidance.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogTicketGuidance.setContentView(R.layout.dialog_send_ticket);
        Window window = dialogTicketGuidance.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        Button btnUnderstand = (Button)dialogTicketGuidance.findViewById(R.id.btnUnderstand);

        dialogEditPrice = new Dialog(App.getCurrentActivity());
        dialogEditPrice.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogEditPrice.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogEditPrice.setContentView(R.layout.dialog_edit_waste_price);
        window = dialogEditPrice.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);



        showDialog = App.getPreferences().getBoolean("ADVICE_FOR_NEW_WASTE",true);
        SharedPreferences.Editor editor = App.getPreferences().edit();
        editor.putBoolean("ADVICE_FOR_NEW_WASTE",false);
        editor.commit();

        btnUnderstand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTicketGuidance.dismiss();
            }
        });


        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (!verifyEnable) {
                        CustomToast.showToast(App.getContext().getString(R.string.pleaseWait));
                        return;
                    }
                    btnVerify.loading();
                    verifyEnable = false;
                    boolean canSend = true;
                    JSONObject itemsSetting = new JSONObject();
                    JSONArray wastesArray = new JSONArray();
                    for (int i = 0; i < wastes.size(); i++) {
                        if (wastes.get(i).ownerID == 0) {
                            continue;
                        }
                        JSONObject wastesObject = new JSONObject();
                        int id = wastes.get(i).id;
                        String name = wastes.get(i).name;
                        int price = wastes.get(i).unit;
                        int enable = wastes.get(i).enable?1:0;
                        if (price == 0) {
                            CustomToast.showToast(App.getContext().getString(R.string.invalidWastePrice));
                            canSend = false;
                            break;
                        }
                        wastesObject.put("id", id);
                        wastesObject.put("price", price);
                        wastesObject.put("enable", enable);
                        wastesArray.put(wastesObject);
                    }
                    itemsSetting.put("items",wastesArray);
                    if(canSend){
                        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                            @Override
                            public void onComplete(String data) {
                                btnVerify.loaded();
                                CustomToast.showToast(App.getContext().getString(R.string.successfulAct));
                                ActivityMain.fragmentManager.popBackStack();
                            }

                            @Override
                            public void onFail(String error) {
                                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                                btnVerify.reset();
                                verifyEnable = true;
                            }
                        }).editSystemItems(itemsSetting);
                    }else{
                        verifyEnable = true;
                        btnVerify.reset();
                    }
                } catch (JSONException e) {
                    btnVerify.reset();
                    verifyEnable = true;
                    e.printStackTrace();
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


    private void getMyWastes() {

        loader.setVisibility(View.VISIBLE);
        layoutContainer.setVisibility(View.GONE);
        wastes = new ArrayList<>();
        wastes.clear();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONArray items = new JSONArray(data);
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        StructWasteEdit waste = new StructWasteEdit();
                        waste.id = item.getInt("id");
                        waste.unit = item.getInt("price");
                        waste.name = item.getString("name");
                        waste.enable = item.getInt("enable") == 1;
                        waste.ownerID = item.getInt("owner_id");
                        waste.imgUrl = "";
                        if (item.has("image")) {
                            String url = item.getString("image");
                            if (url != null && url.length() > 0 && !url.toLowerCase().equals("null")) {
                                waste.imgUrl = App.SERVER_ADDRESS + item.getString("image");
                            }
                        }
                        wastes.add(waste);
                    }

                    adapter = new AdapterWasteEditRecycler(wastes);
                    lstWastes.setLayoutManager(new LinearLayoutManager(App.getContext()));
                    lstWastes.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    layoutContainer.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);
                    if(showDialog){
                        dialogTicketGuidance.show();
                    }
                } catch (JSONException e) {
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                ActivityMain.fragmentManager.popBackStack();
            }
        }).getWastesTypes();
    }

    public static void showEditDialog(final int position){
        TextView txtName = (TextView)dialogEditPrice.findViewById(R.id.txtName);
        final EditText edtPrice = (EditText) dialogEditPrice.findViewById(R.id.edtPrice);
        txtName.setText(wastes.get(position).name);
        edtPrice.setText(wastes.get(position).unit+"");
        Button btnCancel = (Button) dialogEditPrice.findViewById(R.id.btnCancel);
        Button btnEdit = (Button) dialogEditPrice.findViewById(R.id.btnEdit);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEditPrice.dismiss();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String price_s = edtPrice.getText().toString();
                int price = price_s.length()>0?Integer.valueOf(price_s):0;
                if(price==0){
                    return;
                }
                wastes.get(position).unit = price;
                adapter.notifyDataSetChanged();
                dialogEditPrice.dismiss();
            }
        });
        dialogEditPrice.show();
    }

}
