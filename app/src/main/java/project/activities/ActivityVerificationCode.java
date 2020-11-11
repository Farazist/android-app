package project.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.constraintlayout.widget.ConstraintLayout;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import project.connection.Commands;
import project.custom.CustomToast;
import project.custom.ProgressButton;

public class ActivityVerificationCode extends CAppCompatActivity {

    private int mode = -1; //SignUp = 1 , Login = 2
    private int codeLength = 4;
    private TextView txtTime;
    private Button btnResend;
    private int totalTime = 60;
    private int time;
    private boolean invalidCode = true;
    private boolean checkTime = false;
    private Bundle extras;

    private ArrayList<EditText> codes;
    private int correctCode;
    private ProgressButton btnVerify;

//    private int btnResendDefaultColor = App.getContext().getResources().getColor(R.color.colorPrimary);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeActionBar();
        setContentView(R.layout.activity_verification_code);


        time = totalTime;
        final ViewGroup root = (ViewGroup) findViewById(R.id.root);

        TextView txtDesc = (TextView) findViewById(R.id.txtDesc);
        final EditText edt1 = (EditText) findViewById(R.id.edt1);
        final EditText edt2 = (EditText) findViewById(R.id.edt2);
        final EditText edt3 = (EditText) findViewById(R.id.edt3);
        final EditText edt4 = (EditText) findViewById(R.id.edt4);
        btnVerify = (ProgressButton)root.findViewById(R.id.btnVerify);
        btnVerify.setNormalText(App.getContext().getString(R.string.verify));
        btnVerify.setWorkText(App.getContext().getString(R.string.pleaseWait));
        btnVerify.setDoneText(App.getContext().getString(R.string.welcome));
        codes = new ArrayList<EditText>();
        codes.add(edt1);
        codes.add(edt2);
        codes.add(edt3);
        codes.add(edt4);

        edt1.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(App.getContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(edt1, InputMethodManager.SHOW_IMPLICIT);

        root.setVisibility(View.VISIBLE);
        txtTime = (TextView) findViewById(R.id.txtTime);
        btnResend = (Button) findViewById(R.id.btnResend);

        btnResend.setVisibility(View.INVISIBLE);
        Intent intent = getIntent();
        extras = intent.getExtras();
        final String MOBILE = extras.getString("MOBILE");
        txtDesc.setText(getString(R.string.verificationCodeDesc1) + "\n" + MOBILE + " " + getString(R.string.verificationCodeDesc2));
        correctCode = extras.getInt("CODE");

        //CustomToast.showToast(correctCode+"");

        if (intent.hasExtra("MODE")) {
            mode = extras.getString("MODE").equals("SignUp") ? 1 : 2;
        }


        startTimer();

        TextWatcher textChangeListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                EditText edt;
                if(edt1.isFocused()){
                    edt = edt1;
                }else if(edt2.isFocused()){
                    edt = edt2;
                }else if(edt3.isFocused()){
                    edt = edt3;
                }else{
                    edt = edt4;
                }
                String code = edt.getText().toString();
                for(int i=0;i<codes.size();i++){
                    if(codes.get(i)==edt){
                        if(code.length()>0 && i<codeLength-1){
                            codes.get(i+1).requestFocus();
                        }else if(code.length()==0 && i>0){
                            codes.get(i-1).requestFocus();
                        }
                    }
                }
            }
        };
        edt1.addTextChangedListener(textChangeListener);
        edt2.addTextChangedListener(textChangeListener);
        edt3.addTextChangedListener(textChangeListener);
        edt4.addTextChangedListener(textChangeListener);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkTime) {
                    return;
                }
                String code1 = edt1.getText().toString();
                String code2 = edt2.getText().toString();
                String code3 = edt3.getText().toString();
                String code4 = edt4.getText().toString();
                String password = code1+code2+code3+code4;
                if(password.length() == codeLength){
                    check(Integer.parseInt(password));
                }

            }
        });


        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (time <= 0) {
                    btnResend.setVisibility(View.INVISIBLE);
                    int min = 1000;
                    int max = 9999;
                    correctCode = new Random().nextInt((max - min) + 1) + min;
                    resetInput();
                    time = totalTime;
                    startTimer();
                    new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                        @Override
                        public void onComplete(String data) {
                            CustomToast.showToast(App.getContext().getString(R.string.validationCodeSent));
                            btnResend.setVisibility(View.INVISIBLE);

                        }

                        @Override
                        public void onFail(String error) {
                            btnResend.setVisibility(View.VISIBLE);
                            CustomToast.showToast(getString(R.string.connectionError));
                        }
                    }).sendVerificationSMS(MOBILE, correctCode + "");
                }
            }
        });

        edt2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    edt1.requestFocus();
                }
                return false;
            }
        });
        edt3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    edt2.requestFocus();
                }
                return false;
            }
        });
        edt4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_DEL) {
                    edt3.requestFocus();
                }
                return false;
            }
        });



    }

    private String convertToTimeFormat(int time) {
        String timeForm = "0";
        int minute = (time / 60);
        timeForm += minute;
        timeForm += " : ";
        int second = (time % 60);
        if (second < 10) {
            timeForm += "0";
        }
        timeForm += second;
        return timeForm;
    }

    private void startTimer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (time >= 0 && invalidCode) {
                    try {

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtTime.setText(convertToTimeFormat(time));
                            }
                        });
                        Thread.sleep(1000);
                        time--;


                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnResend.setVisibility(View.VISIBLE);
                    }
                });

            }
        }).start();
    }

//    public void numberClick(View view) {
//        if (checkTime) {
//            return;
//        }
//        Button btn = (Button) view;
//        if (btn.getTag().toString().equals("number")) {
//            if (inputCodeLength < codeLength) {
//                codes.get(inputCodeLength).setText(btn.getText().toString());
//                changeCodeColor(inputCodeLength + 1);
//                inputCodeLength += 1;
//            }
//            if (inputCodeLength == codeLength) {
//                String password = "";
//                for (TextView txt : codes) {
//                    password += txt.getText().toString();
//                }
//                check(Integer.parseInt(password));
//                return;
//            }
//
//
//        } else {
//            if (inputCodeLength == 0) {
//                return;
//            }
//            inputCodeLength -= 1;
//            codes.get(inputCodeLength).setText("");
//            changeCodeColor(inputCodeLength);
//        }
//    }

//    private void changeCodeColor(int index) {
//        index = Math.min(codeLength - 1, index);
//        for (TextView txt : codes) {
//            txt.setBackgroundResource(R.drawable.verification_code_shape1);
//        }
//        if (inputCodeLength < codeLength) {
//            codes.get(index).setBackgroundResource(R.drawable.verification_code_shape2);
//        }
//
//    }

    private void check(int number) {
        btnVerify.loading();
        checkTime = true;
        if (number == correctCode && time > 0) {
            if (mode == 2) { // Login
                new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                    @Override
                    public void onComplete(String data) {
                        btnVerify.loaded();
                        if (data.equals(App.NOT_FOUND_ERROR)) {
                            CustomToast.showToast(getString(R.string.unregisteredUser));
                        } else {
                            Intent intent = new Intent(App.getCurrentActivity(), ActivityMain.class);
                            startActivity(intent);
                            finish();
                        }
                        checkTime = false;
                    }

                    @Override
                    public void onFail(String error) {
                        btnVerify.reset();
                        checkTime = false;
                        CustomToast.showToast(getString(R.string.connectionError));
                    }
                }).login(App.account.mobileNumber, App.account.password);

            } else {   // SignUp
                final String name = extras.getString("NAME");
                final int provinceID = extras.getInt("PROVINCE_ID");
                final int cityID = extras.getInt("CITY_ID");
                final String address = extras.getString("ADDRESS");
                final String mobile = extras.getString("MOBILE");
                final String password = extras.getString("PASSWORD");
                final String code = extras.getString("CODE");
                new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                    @Override
                    public void onComplete(String data) {
                        btnVerify.loaded();
                        try {
                            if (data.equals("0")) {
                                checkTime = false;
                                CustomToast.showToast(App.getContext().getString(R.string.unsuccessfulAct));
                                return;
                            }
                            checkTime = false;
                            //SharedPreferences.Editor editor = App.getPreferences().edit();
                            //editor.putString("PASSWORD", password);
                            //editor.commit();
                            App.account.password = password;
                            Intent intent = new Intent(App.getCurrentActivity(), ActivityMain.class);
                            startActivity(intent);
                            ActivityLogin.activityLogin.finish();
                            finish();

                        } catch (Exception e) {
                            btnVerify.reset();
                            checkTime = false;
                            CustomToast.showToast(getString(R.string.connectionError));
                            App.getCurrentActivity().finish();
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        btnVerify.reset();
                        checkTime = false;
                        CustomToast.showToast(getString(R.string.connectionError));
                    }
                }).signUp(name, mobile, provinceID, cityID, address, password);
            }
        } else if (time <= 0) {
            btnVerify.reset();
            checkTime = false;
            CustomToast.showToast(getString(R.string.timeOut));
        } else {
            btnVerify.reset();
            checkTime = false;
            CustomToast.showToast(getString(R.string.wrongCode));
        }



    }

    private void resetInput() {
        for (EditText edtCode : codes) {
            edtCode.setText("");
            codes.get(0).requestFocus();

        }
    }


}
