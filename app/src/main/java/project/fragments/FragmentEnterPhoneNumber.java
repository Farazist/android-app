package project.fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import project.activities.Login;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperView;

public class FragmentEnterPhoneNumber extends Fragment {

    private LinearLayout layoutLoading;
    private Button btnCheck;
    private EditText edtMobileNumber;
    private String mobileNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_enter_phone_number,container,false);

        layoutLoading = (LinearLayout)root.findViewById(R.id.layoutLoading);
        btnCheck = (Button)root.findViewById(R.id.btnCheck);
        edtMobileNumber = (EditText)root.findViewById(R.id.edtMobileNumber);

        ProgressBar progressBar = (ProgressBar)root.findViewById(R.id.progressBar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable wrapDrawable = DrawableCompat.wrap(progressBar.getIndeterminateDrawable());
            DrawableCompat.setTint(wrapDrawable, ContextCompat.getColor(App.getContext(), R.color.background));
            progressBar.setIndeterminateDrawable(DrawableCompat.unwrap(wrapDrawable));
        } else {
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.background), PorterDuff.Mode.SRC_IN);
        }


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
        mobileNumber = edtMobileNumber.getText().toString();
        if(mobileNumber.length() != 11 || !mobileNumber.substring(0, 2).equals("09")){
            CustomToast.showToast(App.getContext().getString(R.string.invalidMobileNumber));
            return;
        }
        HelperView.hideKeyboard(App.getCurrentActivity());
        checking();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                if(data.equals("1")){ //user exists
                    prepareVerificationCode(mobileNumber);
                }else{
                    // registration
                    stopChecking();
                    FragmentSignup fragmentSignup = new FragmentSignup();
                    FragmentTransaction fragmentTransaction = Login.fragmentManager.beginTransaction();
                    Bundle extras = new Bundle();
                    extras.putString("MOBILE", mobileNumber);
                    fragmentSignup.setArguments(extras);
                    fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                    fragmentTransaction.add(R.id.fragmentLoginSteps,fragmentSignup, App.FRAGMENT_SINGUP);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onFail(String error) {
                stopChecking();
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).checkUser(mobileNumber);
    }

    private void prepareVerificationCode(final String mobileNumber) {
        int min = 1000;
        int max = 9999;
        final int code = new Random().nextInt((max - min) + 1) + min;

        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                stopChecking();
                Fragment fragmentEnterLoginCode = new FragmentEnterLoginCode();
                Bundle extras = new Bundle();
                extras.putString("MOBILE", mobileNumber);
                extras.putInt("CODE", code);
                fragmentEnterLoginCode.setArguments(extras);
                FragmentTransaction fragmentTransaction = Login.fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.replace(R.id.fragmentLoginSteps, fragmentEnterLoginCode, App.FRAGMENT_ENTER_LOGIN_CODE);
                fragmentTransaction.commit();
            }

            @Override
            public void onFail(String error) {
                stopChecking();
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).sendVerificationSMS(mobileNumber, code + "");


    }

    private void checking(){
        layoutLoading.setVisibility(View.VISIBLE);
        btnCheck.setVisibility(View.GONE);
    }

    private void stopChecking(){
        layoutLoading.setVisibility(View.GONE);
        btnCheck.setVisibility(View.VISIBLE);
    }
}
