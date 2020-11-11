package project.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

import ir.farazist.farazist_app.App;

import ir.farazist.farazist_app.R;
import project.connection.Commands;
import project.custom.CustomText;
import project.custom.CustomToast;
import project.custom.ProgressButton;
import project.helper.HelperApplication;
import project.helper.HelperView;

public class ActivityLogin extends CAppCompatActivity {

    public static ActivityLogin activityLogin;
    private static boolean canGetNewPassword = true;
    private boolean confirmEnable;
    private boolean resetEnable;
    private EditText edtMobileNumber;
    private EditText edtPassword;
    private ProgressBar loader;
    private CheckBox chkRemember;


    @Override
    protected void onResume() {
        super.onResume();
        App.getLayoutInflater().inflate(R.layout.custom_toast, (ViewGroup) App.getCurrentActivity().findViewById(R.id.root));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeActionBar();
        setContentView(R.layout.activity_login);
        activityLogin = this;
        confirmEnable = true;
        resetEnable = true;
        canGetNewPassword = true;
        App.ownerSystems.clear();
        HelperView.hideKeyboard(App.getCurrentActivity());

        loader = (ProgressBar) findViewById(R.id.loader);
        final Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSignUp = (Button) findViewById(R.id.btnSignUp);
        Button btnForgetPassword = (Button) findViewById(R.id.btnForgetPassword);
        final CustomText txtName = (CustomText) findViewById(R.id.txtTitle);
        edtMobileNumber = (EditText) findViewById(R.id.edtMobileNumber);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        chkRemember = (CheckBox) findViewById(R.id.chkRemember);
        chkRemember.setChecked(false);

        Animation alert = AnimationUtils.loadAnimation(App.getContext(),R.anim.signup_alert);

        btnSignUp.startAnimation(alert);

        final Dialog dialogRecovery = new Dialog(ActivityLogin.this);
        dialogRecovery.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogRecovery.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogRecovery.setContentView(R.layout.dialog_reset_passwrod);
        Window window = dialogRecovery.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        final EditText edtResetMobileNumber = (EditText)dialogRecovery.findViewById(R.id.edtResetMobileNumber);
        final ProgressButton btnReset = (ProgressButton)dialogRecovery.findViewById(R.id.btnReset);
        btnReset.setNormalText(App.getContext().getString(R.string.reset));
        btnReset.setDoneText(App.getContext().getString(R.string.done));


        txtName
                .text(ActivityLogin.this.getString(R.string.farazist))
                .color(this.getResources().getColor(R.color.colorPrimary), this.getResources().getColor(R.color.colorAccent))
                .setFont(App.lalehzar)
                .size(50)
                .commit();

        loader.setVisibility(View.INVISIBLE);
        loader.setIndeterminate(true);
        loader.getIndeterminateDrawable().setColorFilter(Color.parseColor("#a62626"), PorterDuff.Mode.SRC_IN);


        String savedPassword = App.getPreferences().getString("PASSWORD", "");
        if (savedPassword.equals("")) {
            edtPassword.setText("");
            edtMobileNumber.setText("");
            chkRemember.setChecked(false);
        } else {
            edtPassword.setText(savedPassword);
            edtMobileNumber.setText(App.getPreferences().getString("MOBILE_NUMBER", ""));
            chkRemember.setChecked(true);
        }

        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelperView.hideKeyboard(App.getCurrentActivity());
                btnReset.reset();
                edtResetMobileNumber.setText("");
                dialogRecovery.show();
                edtResetMobileNumber.clearFocus();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtMobileNumber.clearFocus();
                edtPassword.clearFocus();
                HelperView.hideKeyboard(App.getCurrentActivity());
                btnReset.reset();
                if(!confirmEnable || !resetEnable){
                    CustomToast.showToast(getString(R.string.pleaseWait));
                    return;
                }
                final String mobileNumber = edtResetMobileNumber.getText().toString();
                if (mobileNumber.length() != 11 || !mobileNumber.substring(0, 2).equals("09")) {
                    CustomToast.showToast(getString(R.string.invalidMobileNumber));
                    return;
                } else {
                    btnReset.loading();
                    confirmEnable = false;
                    resetEnable = false;
                    new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                        @Override
                        public void onComplete(String data) {
                            confirmEnable = true;
                            resetEnable = true;
                            if (data.equals("0")) {
                                btnReset.reset();
                                CustomToast.showToast(getString(R.string.unregisteredUser));

                            } else {
                                btnReset.loaded();
                                dialogRecovery.dismiss();
                                btnReset.reset();
                                CustomToast.showToast(App.getContext().getString(R.string.newPasswordSMSSent));
                            }
                        }

                        @Override
                        public void onFail(String error) {
                            confirmEnable = true;
                            resetEnable = true;
                            CustomToast.showToast(getString(R.string.connectionError));
                            btnReset.reset();
                        }
                    }).changeUserPassword(mobileNumber);
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(App.getCurrentActivity(), ActivitySignup.class);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        startActivity(intent);
                    }
                }).start();

            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!HelperApplication.appEnable){
                    CustomToast.showToast(App.getContext().getString(R.string.serverIsUpdating));
                    finish();
                    return;
                }
                HelperView.hideKeyboard(App.getCurrentActivity());
                login();
            }
        });


        edtMobileNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent event) {
                if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    txtName.setVisibility(View.GONE);
                } else {
                    txtName.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });

    }

    private void login() {
        if (!confirmEnable) {
            CustomToast.showToast(getString(R.string.pleaseWait));
            return;
        }
        final String mobileNumber = edtMobileNumber.getText().toString();
        final String password = edtPassword.getText().toString();

        if (password.length() < 1) {
            CustomToast.showToast(getString(R.string.emptyPassword));
            return;
        }
        if (password.contains(" ")) {
            CustomToast.showToast(getString(R.string.invalidPassword));
            return;
        }

        if (password.length() < 4) {
            CustomToast.showToast(getString(R.string.shortPassword));
            return;
        }

        if (mobileNumber.length() != 11 || !mobileNumber.substring(0, 2).equals("09")) {
            CustomToast.showToast(getString(R.string.invalidMobileNumber));
            return;
        } else {

            confirmEnable = false;
            loader.setVisibility(View.VISIBLE);
            new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                @Override
                public void onComplete(String data) {
                    confirmEnable = true;
                    if (data.equals("0")) {
                        CustomToast.showToast(getString(R.string.wrongUsernameOrPassword));
                        confirmEnable = true;
                        loader.setVisibility(View.INVISIBLE);

                    } else {
                        prepareVerificationCode(mobileNumber, password);
                    }
                }

                @Override
                public void onFail(String error) {
                    confirmEnable = true;
                    CustomToast.showToast(getString(R.string.connectionError));
                    loader.setVisibility(View.INVISIBLE);
                }
            }).checkUser(mobileNumber, password);
        }
    }

    private void prepareVerificationCode(final String mobileNumber, final String password) {
        int min = 1000;
        int max = 9999;
        final int code = new Random().nextInt((max - min) + 1) + min;

//        Intent intent = new Intent(App.getCurrentActivity(), ActivityVerificationCode.class);
//        App.account.mobileNumber = mobileNumber;
//        App.account.password = password;
//        intent.putExtra("MODE", "Login");
//        intent.putExtra("CODE", code);
//        intent.putExtra("MOBILE", mobileNumber);
//        startActivity(intent);
//        finish();

        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {

                loader.setVisibility(View.INVISIBLE);
                App.account.mobileNumber = mobileNumber;
                App.account.password = password;
                if(chkRemember.isChecked()){
                    SharedPreferences.Editor editor = App.getPreferences().edit();
                    editor.putString("PASSWORD",password);
                    editor.putString("MOBILE_NUMBER",mobileNumber);
                    editor.commit();
                }else{
                    SharedPreferences.Editor editor = App.getPreferences().edit();
                    editor.putString("PASSWORD","");
                    editor.putString("MOBILE_NUMBER","");
                    editor.commit();
                }
                Intent intent = new Intent(App.getCurrentActivity(), ActivityVerificationCode.class);
                intent.putExtra("MODE", "Login");
                intent.putExtra("CODE", code);
                intent.putExtra("MOBILE", mobileNumber);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).sendVerificationSMS(mobileNumber, code + "");


    }
}
