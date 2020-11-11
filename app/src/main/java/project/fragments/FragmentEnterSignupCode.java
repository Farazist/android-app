package project.fragments;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityLogin;
import project.activities.ActivityMain;
import project.activities.Login;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperView;

public class FragmentEnterSignupCode extends Fragment {

    private LinearLayout layoutLoading;
    private Button btnCheck;
    private TextView txtTime;
    private int mode = -1; //SignUp = 1 , Login = 2
    private Button btnResend;
    private int totalTime = 60;
    private int time;
    private boolean invalidCode = true;
    private boolean confirmEnable = true;
    private Bundle extras;

    private ArrayList<EditText> codes;
    private int correctCode;
    String mobile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_enter_signup_code, container, false);

        time = totalTime;
        layoutLoading = (LinearLayout) root.findViewById(R.id.layoutLoading);
        txtTime = (TextView) root.findViewById(R.id.txtTime);
        TextView txtDesc = (TextView) root.findViewById(R.id.txtDesc);
        final EditText edt1 = (EditText) root.findViewById(R.id.edt1);
        final EditText edt2 = (EditText) root.findViewById(R.id.edt2);
        final EditText edt3 = (EditText) root.findViewById(R.id.edt3);
        final EditText edt4 = (EditText) root.findViewById(R.id.edt4);
        btnCheck = (Button) root.findViewById(R.id.btnCheck);
        btnResend = (Button) root.findViewById(R.id.btnResend);

        ProgressBar progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(App.getContext(), R.color.background));
            progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.background), PorterDuff.Mode.SRC_IN);
        }

        btnResend.setVisibility(View.INVISIBLE);
        codes = new ArrayList<EditText>();
        codes.add(edt1);
        codes.add(edt2);
        codes.add(edt3);
        codes.add(edt4);

        edt1.requestFocus();
        InputMethodManager imm = (InputMethodManager) App.getContext().getSystemService(App.getContext().INPUT_METHOD_SERVICE);
        imm.showSoftInput(edt1, InputMethodManager.SHOW_IMPLICIT);


        mobile = getArguments().getString("MOBILE");
        txtDesc.setText(getString(R.string.verificationCodeDesc1) + "\n" + mobile + " " + getString(R.string.verificationCodeDesc2));
        correctCode = getArguments().getInt("CODE");

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
                if (edt1.isFocused()) {
                    edt = edt1;
                } else if (edt2.isFocused()) {
                    edt = edt2;
                } else if (edt3.isFocused()) {
                    edt = edt3;
                } else {
                    edt = edt4;
                }
                String code = edt.getText().toString();
                for (int i = 0; i < codes.size(); i++) {
                    if (codes.get(i) == edt) {
                        if (code.length() > 0 && i < 3) {
                            codes.get(i + 1).requestFocus();
                        } else if (code.length() == 0 && i > 0) {
                            codes.get(i - 1).requestFocus();
                        }
                    }
                }
            }
        };
        edt1.addTextChangedListener(textChangeListener);
        edt2.addTextChangedListener(textChangeListener);
        edt3.addTextChangedListener(textChangeListener);
        edt4.addTextChangedListener(textChangeListener);

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String totalCode = "";
                for (int i = 0; i < 4; i++) {
                    if (codes.get(i).getText().toString().length() > 0) {
                        totalCode += codes.get(i).getText().toString();
                    }
                }
                if (totalCode.equals(correctCode + "") && confirmEnable && time>0) {
                    signUp();
                }else if(totalCode.equals(correctCode + "") && confirmEnable && time<=0){
                    CustomToast.showToast(App.getContext().getString(R.string.timeOut));
                }
            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!confirmEnable){
                    CustomToast.showToast(App.getContext().getString(R.string.pleaseWait));
                    return;
                }
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
                    }).sendVerificationSMS(mobile, correctCode + "");
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

    private void resetInput() {
        for (EditText edtCode : codes) {
            edtCode.setText("");
            codes.get(0).requestFocus();
        }
    }

    private void goNextPage() {
        invalidCode = false;
        checking();
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopChecking();
                        Fragment FragmentEnterPassword = new FragmentEnterPassword();
                        Bundle extras = new Bundle();
                        extras.putString("MOBILE", mobile);
                        FragmentEnterPassword.setArguments(extras);
                        FragmentTransaction fragmentTransaction = Login.fragmentManager.beginTransaction();
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                        fragmentTransaction.replace(R.id.fragmentLoginSteps, FragmentEnterPassword, App.FRAGMENT_ENTER_PASSWORD);
                        fragmentTransaction.commit();
                    }
                }, 1000);

            }
        }).start();
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
                        App.getCurrentActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                txtTime.setText(time+"");
                            }
                        });
                        Thread.sleep(1000);
                        time--;

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                App.getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if(time<=0) {
                            btnResend.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();
    }

    private void signUp(){
        HelperView.hideKeyboard(App.getCurrentActivity());
        confirmEnable = false;
        checking();
        final String name = getArguments().getString("NAME");
        final int provinceID = getArguments().getInt("PROVINCE_ID");
        final int cityID = getArguments().getInt("CITY_ID");
        final String address = getArguments().getString("ADDRESS");
        final String password = getArguments().getString("PASSWORD");
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                stopChecking();
                    confirmEnable = true;
                    if (data.equals("0")) {
                        CustomToast.showToast(App.getContext().getString(R.string.unsuccessfulAct));
                        return;
                    }else {
                        Intent intent = new Intent(App.getCurrentActivity(), ActivityMain.class);
                        App.getCurrentActivity().startActivity(intent);
                        App.getCurrentActivity().finish();
                    }
            }

            @Override
            public void onFail(String error) {
                stopChecking();
                confirmEnable = true;
                CustomToast.showToast(getString(R.string.connectionError));
            }
        }).signUp(name, mobile, provinceID, cityID, address, password);
    }

    private void checking() {
        confirmEnable = false;
        layoutLoading.setVisibility(View.VISIBLE);
        btnCheck.setVisibility(View.GONE);
    }

    private void stopChecking() {
        confirmEnable = true;
        layoutLoading.setVisibility(View.GONE);
        btnCheck.setVisibility(View.VISIBLE);
    }
}
