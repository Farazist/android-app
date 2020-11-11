package ir.farazist.farazist_app;


import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.pm.PackageInfoCompat;
import androidx.core.content.res.ResourcesCompat;
import ir.farazist.farazist_app.R;

import java.util.ArrayList;
import java.util.Timer;

import project.activities.CAppCompatActivity;
import project.structures.StructAccount;
import project.structures.StructSystem;
import project.structures.StructTransaction;
import project.structures.StructWaste;


public class App extends Application {

    public static boolean enableThreads;

    private static Context context;
    private static CAppCompatActivity currentActivity;
    private static SharedPreferences preferences;
    private static LayoutInflater layoutInflater;
    public static NotificationManagerCompat notificationManager;
    private static Handler handler;
    private static App base;

    public static final String MANUAL = "manual";
    public static final String AUTO = "auto";
    public static final String waiting = "waiting";
    public static final String accepted = "accepted";
    public static final String delivery_rejected = "delivery_rejected";
    public static final String verification_rejected = "verification_rejected";
    public static final String deleted = "deleted";
    public static final String done = "done";
    public static final String active = "active";
    public static final String deactive = "deactive";

    public static final String DIR_SDCARD = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DIR_APP = DIR_SDCARD + "/Farazist";
    public static final String DIR_GALLERY = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES).getAbsolutePath();
    public static final String DIR_GALLERY_FARAZIST = DIR_GALLERY + "/Farazist";

    public static StructAccount account = new StructAccount();
    public static ArrayList<StructSystem> ownerSystems = new ArrayList<StructSystem>();
    public static StructSystem clientSystem = new StructSystem();

    public static final String SERVER_ADDRESS = "https://farazist.ir/";

    public static Typeface persianFont;
    public static Typeface fontawesome;
    public static Typeface lalehzar;
    //public static Typeface numberFont;
    public static String packageName;

    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

    public static final String NOT_FOUND_ERROR = "404";
    public static final String LESS_MONEY_ERROR = "303";

    public static String accessToken = "";

    public static final String FRAGMENT_MAIN_CLIENT_ID = "FRAGMENT_MAIN_CLIENT";
    public static final String FRAGMENT_MAIN_OWNER_ID = "FRAGMENT_MAIN_OWNER";
    public static final String FRAGMENT_WALLET_ID = "FRAGMENT_WALLET";
    public static final String FRAGMENT_TRANSACTION_ID = "FRAGMENT_TRANSACTION_ID";
    public static final String FRAGMENT_SYSTEM_ADD_DELIVERY = "FRAGMENT_SYSTEM_ADD_DELIVERY";
    public static final String FRAGMENT_TRANSACTIONS_ID = "FRAGMENT_TRANSACTIONS";
    public static final String FRAGMENT_REQUEST_MONEY_ID = "FRAGMENT_REQUEST_MONEY_ID";
    public static final String FRAGMENT_ADD_TRANSACTION_VERIFICATION_ID = "FRAGMENT_ADD_TRANSACTION_VERIFICATION";
    public static final String FRAGMENT_PACKAGE_DELIVERY_STEP1_ID = "FRAGMENT_PACKAGE_DEVIVERY_STEP1";
    public static final String FRAGMENT_PACKAGE_DELIVERY_CUSTOM = "FRAGMENT_PACKAGE_DELIVERY_CUSTOM";
    public static final String FRAGMENT_PACKAGE_DELIVERY_STEP2_ID = "FRAGMENT_PACKAGE_DEVIVERY_STEP2";
    public static final String FRAGMENT_PACKAGE_DELIVERY_STEP3_ID = "FRAGMENT_PACKAGE_DEVIVERY_STEP3";
    public static final String FRAGMENT_PACKAGE_DELIVERY_STEP4_ID = "FRAGMENT_PACKAGE_DEVIVERY_STEP4";
    public static final String FRAGMENT_PAY_CASH_STEP1_ID = "FRAGMENT_PAY_CASH_STEP1_ID";
    public static final String FRAGMENT_PAY_CASH_STEP2_ID = "FRAGMENT_PAY_CASH_STEP2_ID";
    public static final String FRAGMENT_PAY_CASH_STEP3_ID = "FRAGMENT_PAY_CASH_STEP3_ID";
    public static final String FRAGMENT_SYSTEM_INFO_ID = "FRAGMENT_SYSTEM_INFO_ID";
    public static final String FRAGMENT_SYSTEMS_ID = "FRAGMENT_SYSTEMS_ID";
    public static final String FRAGMENT_SYSTEM_DETAILS = "FRAGMENT_SYSTEM_DETAILS_ID";
    public static final String FRAGMENT_PAY_CASH = "FRAGMENT_PAY_CASH_ID";
    public static final String FRAGMENT_USER_DELIVERIES_DETAILS_ID = "FRAGMENT_USER_DELIVERIES_DETAILS_ID";
    public static final String FRAGMENT_SYSTEM_DELIVERIES_DETAILS_ID = "FRAGMENT_SYSTEM_DELIVERIES_DETAILS_ID";
    public static final String FRAGMENT_SYSTEM_SETTINGS_ID = "FRAGMENT_SYSTEM_SETTINGS_ID";
    public static final String FRAGMENT_SYSTEM_DELIVERIES_LIST_ID = "FRAGMENT_SYSTEM_DELIVERIES_LIST_ID";
    public static final String FRAGMENT_COMMON_QUESTIONS_ID = "FRAGMENT_COMMON_QUESTIONS_ID";
    public static final String FRAGMENT_PACKAGE_DELIVERY_ID = "FRAGMENT_PACKAGE_DELIVERY_ID";
    public static final String FRAGMENT_EDIT_ACCOUNT_ID = "FRAGMENT_EDIT_ACCOUNT_ID";
    public static final String FRAGMENT_CONTACT_US_ID = "FRAGMENT_CONTACT_US_ID";
    public static final String FRAGMENT_DELIVERY_INFO_ID = "FRAGMENT_DELIVERY_INFO_ID";
    public static final String FRAGMENT_CONTACT_MANAGEMENT_ID = "FRAGMENT_CONTACT_MANAGEMENT_ID";
    public static final String FRAGMENT_SEND_TICKET_ID = "FRAGMENT_SEND_TICKET_ID";
    public static final String FRAGMENT_TICKET_MESSAGES_ID = "FRAGMENT_TICKET_MESSAGES_ID";
    public static final String FRAGMENT_CHARGING_CITIZEN_WALLET_ID = "FRAGMENT_CHARGING_CITIZEN_WALLET_ID";
    public static final String FRAGMENT_SETTING_ID = "FRAGMENT_SETTING_ID";
    public static final String FRAGMENT_SETTING_WASTES_ID = "FRAGMENT_SETTING_WASTES_ID";
    public static final String FRAGMENT_ENTER_PASSWORD = "FRAGMENT_ENTER_PASSWORD";
    public static final String FRAGMENT_ENTER_LOGIN_CODE = "FRAGMENT_ENTER_LOGIN_CODE";
    public static final String FRAGMENT_ENTER_SIGNUP_CODE = "FRAGMENT_ENTER_SIGNUP_CODE";
    public static final String FRAGMENT_SINGUP = "FRAGMENT_SINGUP";

    public static final String CHECK_TEXT = "\uf00c";
    public static final String REJECT_TEXT = "\uf068";
    public static final String UNKNOWN_TEXT = "\uf128";

    public static boolean canBackPress = true;
    public static boolean connectedSystemChanged = false;
    public static Intent checkIntent;


    public static ArrayList<StructWaste> wastes = new ArrayList<StructWaste>();


    @Override
    public void onCreate() {
        super.onCreate();
        base = this;
        context = getApplicationContext();
        handler = new Handler();
        layoutInflater = LayoutInflater.from(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        packageName = getPackageName();
        persianFont = ResourcesCompat.getFont(context, R.font.persian);
        fontawesome = ResourcesCompat.getFont(context, R.font.fontawesome);
        lalehzar = ResourcesCompat.getFont(context, R.font.lalehzar);
        // numberFont = ResourcesCompat.getFont(context, R.font.number);
        notificationManager = NotificationManagerCompat.from(context);
        createNotificationChannels();
    }


    public static App get() {
        return base;
    }

    public static Context getContext() {
        if (currentActivity != null) {
            return currentActivity;
        }

        return context;
    }

    public static void setCurrentActivity(CAppCompatActivity activity) {
        currentActivity = activity;
    }

    public static CAppCompatActivity getCurrentActivity() {
        return currentActivity;
    }

    public static LayoutInflater getLayoutInflater() {
        return layoutInflater;
    }

    public static SharedPreferences getPreferences() {
        return preferences;
    }


    public static View inflateLayout(@LayoutRes int res) {
        return layoutInflater.inflate(res, null);
    }

    public static View inflateLayout(@LayoutRes int res, @Nullable ViewGroup root) {
        return layoutInflater.inflate(res, root);
    }

    public static Handler getHandler() {
        return handler;
    }


    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.enableLights(true);
            channel1.setLightColor(Color.parseColor(getResources().getString(R.color.colorAccent)));

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "Channel 2",
                    NotificationManager.IMPORTANCE_LOW);


            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
        }
    }

    public long getApplicationVersionCode() {
        long versionCode = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String version = pInfo.versionName;
            versionCode = PackageInfoCompat.getLongVersionCode(pInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

//    public static void showCurrentDelivery(boolean showing){
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putBoolean("SHOW_CURRENT_DELIVERY",showing);
//        editor.commit();
//    }
//
//    public static boolean mustShowCurrentDelivery(){
//        boolean mustShow = preferences.getBoolean("SHOW_CURRENT_DELIVERY",false);
//        return mustShow;
//    }
}
