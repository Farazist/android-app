/*
*
*
Copyright © 2020 Seyed Amir mohammad Rouholamini Inc. All Rights Reserved
*
*
*/


/*

Seyed Amir mohammad Rouholamini , Kerman , Iran

 */



package project.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.BuildConfig;
import ir.farazist.farazist_app.R;
import org.json.JSONException;
import org.json.JSONObject;
import project.connection.Commands;
import project.custom.CustomToast;
import project.custom.LinearProgressBar;
import project.helper.HelperApplication;
import project.structures.StructSystem;

public class ActivityStart extends CAppCompatActivity {

    private Button btnRetry;
    private String mobileNumber;
    //    private CustomText txtTitle;
    private ImageView imgStart;
    private LinearProgressBar loader;
    private Animation anim_loading;
    private Animation anim_loaded;
    private Dialog dialogUpdateInfo;
    private TextView txtDesc;

    private int minimumRequiredVersion;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isWelcome = App.getPreferences().getBoolean("WELCOME", true);
        if (isWelcome) {
            Intent intent = new Intent(ActivityStart.this, ActivitySplash.class);
            startActivity(intent);
            finish();
            return;
        }
        removeActionBar();
        setContentView(R.layout.activity_start);

        HelperApplication.checkActivation(8);

        dialogUpdateInfo = new Dialog(ActivityStart.this);
        dialogUpdateInfo.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogUpdateInfo.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogUpdateInfo.setContentView(R.layout.dialog_application_version);
        Window window = dialogUpdateInfo.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        txtDesc = (TextView) dialogUpdateInfo.findViewById(R.id.txtDesc);
        Button btnBazaarDownload = (Button)dialogUpdateInfo.findViewById(R.id.btnBazaarDownload);
        Button btnPlayStoreDownload = (Button)dialogUpdateInfo.findViewById(R.id.btnPlayStoreDownload);


        btnPlayStoreDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = App.getContext().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });

        btnBazaarDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String appPackageName = App.getContext().getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("bazaar://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://cafebazaar.ir/app/" + appPackageName)));
                }
            }
        });

        dialogUpdateInfo.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (minimumRequiredVersion > BuildConfig.VERSION_CODE) {
                    finish();
                } else {
                    tryLogin();
                }
            }
        });

        imgStart = (ImageView) findViewById(R.id.imgStart);
        loader = (LinearProgressBar) findViewById(R.id.loader);

        anim_loading = AnimationUtils.loadAnimation(ActivityStart.this, R.anim.app_loading);
        anim_loaded = AnimationUtils.loadAnimation(ActivityStart.this, R.anim.app_loaded);
        imgStart.startAnimation(anim_loading);
        App.ownerSystems.clear();
        App.clientSystem = new StructSystem();

        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnRetry.setVisibility(View.INVISIBLE);

        App.accessToken = App.getPreferences().getString("TOCKEN", "");
        boolean isLogin = App.accessToken.length() < 1 ? false : true;
        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAppVersion();
            }
        });

        checkAppVersion();

    }

    private void checkAppVersion() {
        btnRetry.setVisibility(View.INVISIBLE);
        loader.start();
        loader.setVisibility(View.VISIBLE);
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONObject object = new JSONObject(data);
                    txtDesc.setText(object.getString("descriptions"));
                    minimumRequiredVersion = object.getInt("minimum_required_version_code");
                    int newVersion = object.getInt("code");
                    int currentVersion = BuildConfig.VERSION_CODE;
                    if (minimumRequiredVersion > currentVersion) {
                        String desc = App.getContext().getString(R.string.newAppVersion);
                        desc += " " + App.getContext().getString(R.string.updateYourApp);
                        txtDesc.setText(desc);
                        dialogUpdateInfo.show();
                    } else if (newVersion > currentVersion) {
                        String desc = App.getContext().getString(R.string.newAppVersion);
                        txtDesc.setText(desc);
                        dialogUpdateInfo.show();
                    } else {
                        tryLogin();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomToast.showToast(App.getContext().getString(R.string.updateYourApp));
                    ActivityStart.this.finish();

                }
            }

            @Override
            public void onFail(String error) {
                loader.setVisibility(View.GONE);
                loader.stop();
                btnRetry.setVisibility(View.VISIBLE);
                CustomToast.showToast(getString(R.string.connectionError));
            }
        }).getAppVersionsInfo();
    }

    private void getUserInfo() {
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                loader.setVisibility(View.GONE);
                loader.stop();
                if (data.equals(App.NOT_FOUND_ERROR)) {
                    CustomToast.showToast(getString(R.string.unregisteredUser));
                } else {
                    if(!HelperApplication.appEnable){
                        CustomToast.showToast(App.getContext().getString(R.string.serverIsUpdating));
                        finish();
                        return;
                    }
                    imgStart.startAnimation(anim_loaded);
                    App.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(App.getCurrentActivity(), ActivityMain.class);
                            startActivity(intent);
                            finish();
                        }
                    }, 800);


                }
            }

            @Override
            public void onFail(String error) {
                loader.stop();
                if (error.toLowerCase().contains("json")) {
                    Intent intent = new Intent(App.getCurrentActivity(), Login.class);
                    startActivity(intent);
                    finish();
                } else {
                    loader.setVisibility(View.GONE);
                    btnRetry.setVisibility(View.VISIBLE);
                    CustomToast.showToast(getString(R.string.connectionError));
                }
            }
        }).getUserInfo();
    }

    private void tryLogin() {
        boolean isLogin = App.accessToken.length() < 1 ? false : true;
        if (isLogin) {
            getUserInfo();

        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        App.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                imgStart.startAnimation(anim_loaded);
                            }
                        });
                        loader.stop();
                        Thread.sleep(1000);
                        App.getHandler().post(new Runnable() {
                            @Override
                            public void run() {

                                Intent intent = new Intent(App.getCurrentActivity(), Login.class);
                                startActivity(intent);
                                finish();
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
}
