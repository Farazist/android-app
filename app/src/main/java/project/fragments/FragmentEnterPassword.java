package project.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import project.activities.ActivityVerificationCode;
import project.activities.Login;
import project.connection.Commands;
import project.custom.CustomToast;
import project.custom.ProgressButton;
import project.helper.HelperFragment;
import project.helper.HelperView;

public class FragmentEnterPassword extends Fragment {

    private LinearLayout layoutLoading;
    private Button btnCheck;
    private EditText edtPassword;
    private String mobile;
    private String password;
    private ProgressBar progressResetPassword;
    private Button btnForgetPassword;

    private boolean confirmEnable;
    private static boolean resetEnable = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_enter_password, container, false);

        HelperView.hideKeyboard(App.getCurrentActivity());
        confirmEnable = true;
        layoutLoading = (LinearLayout) root.findViewById(R.id.layoutLoading);
        btnCheck = (Button) root.findViewById(R.id.btnCheck);
        edtPassword = (EditText) root.findViewById(R.id.edtPassword);
        btnForgetPassword = (Button) root.findViewById(R.id.btnForgetPassword);
        progressResetPassword = (ProgressBar)root.findViewById(R.id.progressResetPassword);

        ProgressBar progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(App.getContext(), R.color.background));
            progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));

            wrapDrawable = DrawableCompat.wrap(progressResetPassword.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(App.getContext(), R.color.colorAccent));
            progressResetPassword.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));

        } else {
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.background), PorterDuff.Mode.SRC_IN);
            progressResetPassword.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }

        mobile = getArguments().getString("MOBILE");


        btnForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              if(!resetEnable){
                  CustomToast.showToast(App.getContext().getString(R.string.yourNewPasswordSentPleaseWait));
                  return;
              }
              resetPassword();
            }
        });


        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUser();
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

    private void checkUser() {
        if (!confirmEnable) {
            CustomToast.showToast(getString(R.string.pleaseWait));
            return;
        }
        password = edtPassword.getText().toString();
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
        confirmEnable = false;
        checking();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                if (data.equals("0")) {
                    confirmEnable = true;
                    stopChecking();
                    CustomToast.showToast(getString(R.string.wrongPassword));

                } else {
                    login();
                }
            }

            @Override
            public void onFail(String error) {
                confirmEnable = true;
                stopChecking();
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).checkUser(mobile, password);
    }

    private void resetPassword(){
        edtPassword.clearFocus();
        HelperView.hideKeyboard(App.getCurrentActivity());
        if (!confirmEnable) {
            CustomToast.showToast(getString(R.string.pleaseWait));
            return;
        }
        confirmEnable = false;
        reseting();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                startThread();
                confirmEnable = true;
                stopReset();
                CustomToast.showToast(App.getContext().getString(R.string.newPasswordSMSSent));
            }

            @Override
            public void onFail(String error) {
                stopReset();
                confirmEnable = true;
                CustomToast.showToast(getString(R.string.connectionError));
            }
        }).changeUserPassword(mobile);
    }

    private void startThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    resetEnable = false;
                    Thread.sleep(2*60*1000);
                    resetEnable = true;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void login(){
        HelperView.hideKeyboard(App.getCurrentActivity());
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                confirmEnable = true;
                stopChecking();
                if (data.equals(App.NOT_FOUND_ERROR)) {
                    CustomToast.showToast(getString(R.string.unregisteredUser));
                } else {
                    Intent intent = new Intent(App.getCurrentActivity(), ActivityMain.class);
                    App.getCurrentActivity().startActivity(intent);
                    App.getCurrentActivity().finish();
                }
            }

            @Override
            public void onFail(String error) {
                confirmEnable = true;
                stopChecking();
                CustomToast.showToast(getString(R.string.connectionError));
            }
        }).login(mobile, password);
    }

    private void checking() {
        layoutLoading.setVisibility(View.VISIBLE);
        btnCheck.setVisibility(View.GONE);
    }

    private void stopChecking() {
        layoutLoading.setVisibility(View.GONE);
        btnCheck.setVisibility(View.VISIBLE);
    }

    private void reseting(){
        progressResetPassword.setVisibility(View.VISIBLE);
        btnForgetPassword.setVisibility(View.GONE);
    }
    private void stopReset(){
        progressResetPassword.setVisibility(View.GONE);
        btnForgetPassword.setVisibility(View.VISIBLE);
    }
}
