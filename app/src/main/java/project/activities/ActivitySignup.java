package project.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import project.adapters.AdapterCityRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.custom.LinearProgressBar;
import project.fragments.FragmentEditAccount;
import project.helper.HelperView;
import project.structures.StructCity;


public class ActivitySignup extends CAppCompatActivity {

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

    private Toast toast;
    private TextView txtBirthday;
    private CheckBox chkAcceptRules;
    private ProgressBar loader;
    private LinearProgressBar progressBar;
    private RecyclerView lstItems;


    private int mode = 0;
    private static final int PROVINCE_MODE = 0;
    private static final int CITY_MODE = 1;

    private ArrayList<StructCity> provinces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeActionBar();
        setContentView(R.layout.activity_signup);

        FragmentEditAccount.addressSelectListener = null;
        App.ownerSystems.clear();
        provinces = new ArrayList<>();

        loader = (ProgressBar) findViewById(R.id.loader);
        final TextView txtTitle = (TextView) findViewById(R.id.txtTitle);
        TextView txtSignupRules = (TextView) findViewById(R.id.txtSignupRules);
        txtBirthday = (TextView) findViewById(R.id.txtBirthday);
        chkAcceptRules = (CheckBox) findViewById(R.id.chkAcceptRules);
        final EditText edtName = (EditText) findViewById(R.id.edtName);
        final EditText edtAddress = (EditText) findViewById(R.id.edtAddress);
        final EditText edtMobileNumber = (EditText) findViewById(R.id.edtMobileNumber);
        final EditText edtPassword = (EditText) findViewById(R.id.edtPassword);
        final Button btnSignUp = (Button) findViewById(R.id.btnSignUp);
        final Button btnBirthday = (Button) findViewById(R.id.btnBirthday);
        final Button btnProvince = (Button) findViewById(R.id.btnProvince);
        final Button btnCity = (Button) findViewById(R.id.btnCity);


        edtName.requestFocus();
        HelperView.hideKeyboard(App.getCurrentActivity());
        loader.setVisibility(View.INVISIBLE);
        loader.setIndeterminate(true);
        loader.getIndeterminateDrawable().setColorFilter(Color.parseColor("#a62626"), PorterDuff.Mode.SRC_IN);


        final Dialog dialogSelection = new Dialog(ActivitySignup.this);
        dialogSelection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSelection.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogSelection.setContentView(R.layout.dialog_select_address);
        Window window = dialogSelection.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
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
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
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
                HelperView.hideKeyboard(App.getCurrentActivity());
                if (!signUpEnable) {
                    CustomToast.showToast(getString(R.string.pleaseWait));
                    SharedPreferences.Editor editor = App.getPreferences().edit();
                    editor.putString("TOCKEN", App.accessToken);
                    editor.commit();
                    return;
                }
                signUpEnable = false;
                name = edtName.getText().toString();
                address = edtAddress.getText().toString();
                mobile = edtMobileNumber.getText().toString();
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
                if (mobile.length() != 11 || !mobile.substring(0, 2).equals("09")) {
                    CustomToast.showToast(getString(R.string.invalidMobileNumber));
                    signUpEnable = true;
                    return;
                }

                if(!chkAcceptRules.isChecked()){
                    CustomToast.showToast(App.getContext().getString(R.string.rulesIsNecessary));
                    signUpEnable = true;
                    return;
                }

                loader.setVisibility(View.VISIBLE);
                new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                    @Override
                    public void onComplete(String data) {
                        if (data.equals("1")) {
                            loader.setVisibility(View.INVISIBLE);
                            CustomToast.showToast(getString(R.string.duplicateMobileNumber));
                            signUpEnable = true;
                        } else if (data.equals("0")) {
                            prepareVerificationCode(mobile);
                        } else {
                            signUpEnable = true;
                            CustomToast.showToast(getString(R.string.connectionError));
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        loader.setVisibility(View.INVISIBLE);
                        CustomToast.showToast(getString(R.string.connectionError));
                        signUpEnable = true;
                    }
                }).checkMobileNumber(mobile);

            }

        });


        btnBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        showCalendar();
                    }
                }).start();
            }
        });


    }


    private void showCalendar() {
        DatePickerDialog dialog = new DatePickerDialog();
        dialog.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                NumberFormat numberFormat = new DecimalFormat("00");
                String birthDay = String.valueOf(year) + " / " +
                        numberFormat.format(monthOfYear + 1) + " / " +
                        numberFormat.format(dayOfMonth);

                txtBirthday.setText(birthDay);
            }
        });
        dialog.show(getFragmentManager(), "");
    }

    private void prepareVerificationCode(final String mobileNumber) {
        int min = 1000;
        int max = 9999;
        final int code = new Random().nextInt((max - min) + 1) + min;
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                loader.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(App.getCurrentActivity(), ActivityVerificationCode.class);
                intent.putExtra("MODE", "SignUp");
                intent.putExtra("NAME", name);
                intent.putExtra("MOBILE", mobile);
                intent.putExtra("PROVINCE_ID", provinceID);
                intent.putExtra("CITY_ID", cityID);
                intent.putExtra("ADDRESS", address);
                intent.putExtra("PASSWORD", password);
                intent.putExtra("CODE", code);
                startActivity(intent);
                App.getCurrentActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }

            @Override
            public void onFail(String error) {
                loader.setVisibility(View.INVISIBLE);
                signUpEnable = true;
                CustomToast.showToast(getString(R.string.connectionError));
            }
        }).sendVerificationSMS(mobileNumber, code + "");


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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
