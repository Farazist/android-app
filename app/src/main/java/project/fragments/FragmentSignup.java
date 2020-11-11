package project.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityRules;
import project.activities.Login;
import project.adapters.AdapterCityRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.custom.LinearProgressBar;
import project.custom.ProgressButton;
import project.helper.HelperView;
import project.structures.StructCity;


public class FragmentSignup extends Fragment {

    public static OnAddressSelectListener addressSelectListener;

    private String name = "";
    private String mobile = "";
    private String province = "";
    private String city = "";
    private int provinceID = -1;
    private int cityID = -1;
    private String address = "";
    private String password = "";
    private boolean signUpEnable = true;
    private boolean getItemEnable = true;

    private CheckBox chkAcceptRules;
    private LinearProgressBar progressBar;
    private RecyclerView lstItems;
    private ProgressButton btnSignUp;

    private int mode = 0;
    private static final int PROVINCE_MODE = 0;
    private static final int CITY_MODE = 1;

    private ArrayList<StructCity> provinces;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_signup,container,false);


        FragmentEditAccount.addressSelectListener = null;
        App.ownerSystems.clear();
        provinces = new ArrayList<>();

        mobile = getArguments().getString("MOBILE");

        final TextView txtTitle = (TextView)root.findViewById(R.id.txtTitle);
        TextView txtSignupRules = (TextView)root.findViewById(R.id.txtSignupRules);
        chkAcceptRules = (CheckBox)root.findViewById(R.id.chkAcceptRules);
        final EditText edtName = (EditText)root.findViewById(R.id.edtName);
        final EditText edtAddress = (EditText)root.findViewById(R.id.edtAddress);
        final EditText edtPassword = (EditText)root.findViewById(R.id.edtPassword);
        final Button btnProvince = (Button)root.findViewById(R.id.btnProvince);
        final Button btnCity = (Button)root.findViewById(R.id.btnCity);
        btnSignUp = (ProgressButton)root.findViewById(R.id.btnSignUp);


        edtName.requestFocus();
        HelperView.hideKeyboard(App.getCurrentActivity());
        btnSignUp.setNormalText(App.getContext().getString(R.string.signUp));
        btnSignUp.setWorkText(App.getContext().getString(R.string.pleaseWait));
        btnSignUp.setDoneText(App.getContext().getString(R.string.done));

        final Dialog dialogSelection = HelperView.configDialog(R.layout.dialog_select_address,App.getCurrentActivity(),true);
        final TextView txtDialogTitle = (TextView) dialogSelection.findViewById(R.id.txtTitle);
        lstItems = (RecyclerView) dialogSelection.findViewById(R.id.lstItems);
        progressBar = (LinearProgressBar) dialogSelection.findViewById(R.id.progressBar);

        lstItems.setLayoutManager(new LinearLayoutManager(App.getContext()));


        addressSelectListener = new OnAddressSelectListener() {
            @Override
            public void onItemSelect(int id, String name) {
                if (mode == PROVINCE_MODE) {
                    provinceID = id;
                    if(id<0){
                        cityID = -1;
                        btnCity.setText(App.getContext().getString(R.string.city));
                        btnProvince.setText(App.getContext().getString(R.string.province));
                    }
                    province = name;
                    btnProvince.setText(name);
                } else {
                    cityID = id;
                    city = name;
                    btnCity.setText(name);
                    if(id<0){
                        btnCity.setText(App.getContext().getString(R.string.city));
                    }
                }
                dialogSelection.dismiss();
            }
        };


        txtSignupRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.getCurrentActivity(), ActivityRules.class);
                App.getCurrentActivity().startActivity(intent);
                App.getCurrentActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = PROVINCE_MODE;
                txtDialogTitle.setText(App.getContext().getString(R.string.provinceSelection));
                dialogSelection.show();
                lstItems.setVisibility(View.INVISIBLE);
                getProvinces();


            }
        });

        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (provinceID < 0) {
                    return;
                }
                mode = CITY_MODE;
                txtDialogTitle.setText(App.getContext().getString(R.string.selectCityFromProvince) + " " + province);
                dialogSelection.show();
                lstItems.setVisibility(View.INVISIBLE);
                getCities();


            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edtName.getText().toString();
                address = edtAddress.getText().toString();
                password = edtPassword.getText().toString();

                if (name.length() < 1 || mobile.length() < 1) {
                    CustomToast.showToast(getString(R.string.fillAllRequiredFields));
                    signUpEnable = true;
                    return;
                }
                if (password.length() < 4) {
                    CustomToast.showToast(getString(R.string.minimumPasswordLength));
                    signUpEnable = true;
                    return;
                }
                if (password.contains(" ")) {
                    CustomToast.showToast(getString(R.string.invalidPassword));
                    signUpEnable = true;
                    return;
                }

                if(!chkAcceptRules.isChecked()){
                    CustomToast.showToast(App.getContext().getString(R.string.rulesIsNecessary));
                    signUpEnable = true;
                    return;
                }
                prepareVerificationCode(mobile);

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

    private void prepareVerificationCode(final String mobileNumber) {
        btnSignUp.loading();
        int min = 1000;
        int max = 9999;
        final int code = new Random().nextInt((max - min) + 1) + min;
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                btnSignUp.loaded();
                FragmentEnterSignupCode fragmentEnterSignupCode = new FragmentEnterSignupCode();
                Bundle extras = new Bundle();
                extras.putString("NAME",name);
                extras.putString("MOBILE", mobileNumber);
                extras.putInt("PROVINCE_ID", provinceID);
                extras.putInt("CITY_ID", cityID);
                extras.putString("ADDRESS", address);
                extras.putString("PASSWORD", password);
                extras.putInt("CODE", code);
                fragmentEnterSignupCode.setArguments(extras);
                FragmentTransaction fragmentTransaction = Login.fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.add(R.id.fragmentLoginSteps, fragmentEnterSignupCode, App.FRAGMENT_ENTER_SIGNUP_CODE);
                fragmentTransaction.addToBackStack(App.FRAGMENT_ENTER_SIGNUP_CODE);
                fragmentTransaction.commit();
            }

            @Override
            public void onFail(String error) {
                btnSignUp.reset();
                signUpEnable = true;
                CustomToast.showToast(getString(R.string.connectionError));
            }
        }).sendVerificationSMS(mobileNumber, code + "");


    }

    private void getProvinces() {
        if (provinces.size() > 0) {
            AdapterCityRecycler adapter = new AdapterCityRecycler(provinces);
            lstItems.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressBar.stop();
            lstItems.setVisibility(View.VISIBLE);
            progressBar.stop();
            return;
        }
        progressBar.start();
        if(!getItemEnable){
            return;
        }
        getItemEnable = false;
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    getItemEnable = true;
                    JSONArray provinceArray = new JSONArray(data);
                    ArrayList<StructCity> items = new ArrayList<>();
                    StructCity p = new StructCity();
                    p.name = App.getContext().getString(R.string.select);
                    p.id = -1;
                    items.add(p);
                    for (int i = 0; i < provinceArray.length(); i++) {
                        JSONObject provinceObject = provinceArray.getJSONObject(i);
                        p = new StructCity();
                        p.name = provinceObject.getString("name");
                        p.id = provinceObject.getInt("id");
                        items.add(p);
                    }

                    AdapterCityRecycler adapter = new AdapterCityRecycler(items);
                    lstItems.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.stop();
                    lstItems.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                getItemEnable = true;
            }
        }).getProvinces();
    }

    private void getCities() {
        progressBar.start();
        if (provinceID < 0) {
            return;
        }
        if(!getItemEnable){
            return;
        }
        getItemEnable = false;
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    ArrayList<StructCity> cities = new ArrayList<>();
                    JSONArray cityArray = new JSONArray(data);
                    StructCity c = new StructCity();
                    c.name = App.getContext().getString(R.string.select);
                    c.id = -1;
                    cities.add(c);
                    for(int i=0;i<cityArray.length();i++){
                        JSONObject cityObject = cityArray.getJSONObject(i);
                        c = new StructCity();
                        c.id = cityObject.getInt("id");
                        c.name = cityObject.getString("name");
                        cities.add(c);
                    }
                    AdapterCityRecycler adapter = new AdapterCityRecycler(cities);
                    lstItems.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.stop();
                    lstItems.setVisibility(View.VISIBLE);
                    getItemEnable = true;
                } catch (JSONException e) {
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                getItemEnable = true;
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).getProvinceCities(provinceID);

    }


    public interface OnAddressSelectListener {
        void onItemSelect(int id, String name);
    }
}
