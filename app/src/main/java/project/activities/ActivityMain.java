package project.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.BuildConfig;
import ir.farazist.farazist_app.R;

import project.adapters.AdapterMenuRecycler;
import project.connection.Commands;
import project.connection.FileUploader;
import project.custom.CustomToast;
import project.fragments.FragmentEditAccount;
import project.fragments.FragmentMainClient;
import project.fragments.FragmentMainOwner;
import project.fragments.FragmentSystemsList;
import project.fragments.FragmentTicketMessages;
import project.helper.HelperFragment;
import project.helper.HelperString;
import project.helper.PermissionManager;
import project.interfaces.ActivityResult;
import project.interfaces.PermissionListener;
import project.services.ServiceCheckWallet;
import project.structures.StructMenu;


public class ActivityMain extends CAppCompatActivity {

    public static ActivityResult activityResultListener;

    public static ImageView imgAvatar;
    public static String inviteText;
    public static FragmentManager fragmentManager;
    //public static OnSetMainToolbarListener listener;
    public static DrawerLayout drawerLayout;
    public static Toolbar mainToolbar;

    private static boolean exitAccount;
    private static TextView txtWalletBalance;
    private static TextView txtMyClientNumbers;
    private FragmentTransaction fragmentTransaction;
    private static Dialog dialogExit;
    private static TextView dialogExitTitle;
    private static PermissionManager permissionManager;


    private static ArrayList<StructMenu> menuItems;
    private static AdapterMenuRecycler adapter;
    private RecyclerView lstMenu;

    private static Thread clientNumberThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        removeActionBar();
        setContentView(R.layout.root);

        App.enableThreads = true;
        menuItems = new ArrayList<>();


        imgAvatar = (ImageView) findViewById(R.id.imgAvatar);
        final LinearLayout layoutAdmin = (LinearLayout) findViewById(R.id.layoutAdmin);
        final LinearLayout layoutBalance = (LinearLayout) findViewById(R.id.layoutBalance);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        TextView txtFarazistId = (TextView) findViewById(R.id.txtFarazistId);
        TextView txtFullName = (TextView) findViewById(R.id.txtFullName);
        txtWalletBalance = (TextView) findViewById(R.id.txtWalletBalance);
        ImageView imgInstagram = (ImageView) findViewById(R.id.imgInstagram);
        ImageView imgAparat = (ImageView) findViewById(R.id.imgAparat);
        ImageView imgTelegram = (ImageView) findViewById(R.id.imgTelegram);
        TextView txtAppVersion = (TextView)findViewById(R.id.txtAppVersion);

        txtAppVersion.setText(App.getContext().getString(R.string.version)+" "+ BuildConfig.VERSION_NAME);

        if (App.account.role == 2) {  // OWNER
            layoutAdmin.setVisibility(View.VISIBLE);
            layoutBalance.setVisibility(View.GONE);
            txtMyClientNumbers = (TextView) findViewById(R.id.txtMyClientNumbers);
            new HelperFragment()
                    .addToBackStack(App.FRAGMENT_MAIN_OWNER_ID)
                    .addFragment(R.id.fragmentContainer, new FragmentMainOwner(), false)
                    .commit();
            startGetClientNumbers();
        } else {
            layoutAdmin.setVisibility(View.GONE);
            layoutBalance.setVisibility(View.VISIBLE);
            new HelperFragment()
                    .addToBackStack(App.FRAGMENT_MAIN_CLIENT_ID)
                    .addFragment(R.id.fragmentContainer, new FragmentMainClient(), false)
                    .commit();
        }


        initializeMenuList();
        lstMenu = (RecyclerView) findViewById(R.id.lstMenu);
        lstMenu.setLayoutManager(new LinearLayoutManager(App.getContext()));
        lstMenu.setNestedScrollingEnabled(false);
        adapter = new AdapterMenuRecycler(menuItems);
        lstMenu.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        inviteText = "";
        getInviteText();

        mainToolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView imgMenu = (ImageView) findViewById(R.id.imgMenu);
        ImageView imgQRcode = (ImageView) findViewById(R.id.imgQRcode);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imgMenu.setBackgroundResource(R.drawable.item_circle_ripple);
            imgQRcode.setBackgroundResource(R.drawable.item_circle_ripple);
        }else {
            imgMenu.setBackgroundResource(R.drawable.item_circle_selection);
            imgQRcode.setBackgroundResource(R.drawable.item_circle_selection);
        }

        if (App.account.avatarUrl != null && !App.account.avatarUrl.equals("null") && App.account.avatarUrl.length() > 0) {
//            App.getHandler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Picasso.with(App.getContext()).load(App.account.avatarUrl).resize(imgAvatar.getWidth(), imgAvatar.getHeight()).into(imgAvatar);
//                }
//            }, 500);
            Picasso.with(App.getContext()).load(App.account.avatarUrl).into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.avatar);
        }
        txtFullName.setText(App.account.name);
        txtFarazistId.setText(App.getContext().getString(R.string.farazist_id) + " : " + App.account.id);
        setupConfiguration();

        dialogExit = new Dialog(ActivityMain.this);
        dialogExit.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogExit.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogExit.setContentView(R.layout.dialog_exit);
        Window window = dialogExit.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        dialogExitTitle = (TextView) dialogExit.findViewById(R.id.txtTitle);
        Button btnYes = (Button) dialogExit.findViewById(R.id.btnYes);
        Button btnNo = (Button) dialogExit.findViewById(R.id.btnNo);

        final Dialog dialogRegister = new Dialog(ActivityMain.this);
        dialogRegister.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogRegister.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogRegister.setContentView(R.layout.dialog_register);
        window = dialogRegister.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        Button btnUnderstand = (Button) dialogRegister.findViewById(R.id.btnUnderstand);


        if (!isCheckServiceRunning()) {
            App.checkIntent = new Intent(ActivityMain.this, ServiceCheckWallet.class);
            App.getCurrentActivity().startService(App.checkIntent);
        }

        if (App.account.systemID == 0 && App.ownerSystems.size() == 0) {
            dialogRegister.show();
        }

        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(Gravity.RIGHT);
                FragmentEditAccount frag = (FragmentEditAccount) ActivityMain.fragmentManager.findFragmentByTag(App.FRAGMENT_EDIT_ACCOUNT_ID);
                if (frag != null && frag.isVisible()) {
                    return;
                }
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_EDIT_ACCOUNT_ID)
                        .addFragment(R.id.fragmentContainer, new FragmentEditAccount(), true)
                        .commit();
            }
        });
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });

        imgQRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(App.getCurrentActivity(), ActivityScanner.class);
                intent.putExtra("MODE", ActivityScanner.LOGIN_SYSTEM);
                startActivity(intent);
            }
        });

        imgInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri = Uri.parse("https://instagram.com/_u/farazist.ir_/");
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                    likeIng.setPackage("com.instagram.android");

                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.instagram.com/farazist.ir_/")));
                    }catch (Exception e2){

                    }
                }
            }
        });

        imgAparat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.aparat.com/faramarz.co"));
                    startActivity(browserIntent);
                }catch (Exception e){

                }
            }
        });

        imgTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/Eco_Farazist"));
                    startActivity(telegram);
                }catch (Exception e){

                }
            }
        });

        btnUnderstand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogRegister.dismiss();
            }
        });

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.enableThreads = false;
                try {
                    FragmentMainOwner.ticketThread.interrupt();
                    FragmentMainOwner.deliveryThread.interrupt();
                    clientNumberThread.interrupt();
                } catch (Exception e) {
                }
                ;
                if (exitAccount) {
                    SharedPreferences.Editor editor = App.getPreferences().edit();
                    editor.putString("TOCKEN", "");
                    editor.commit();

                    Intent intent = new Intent(App.getCurrentActivity(), Login.class);
                    startActivity(intent);
                }
                finish();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogExit.dismiss();
            }
        });

    }

    private boolean isCheckServiceRunning() {
        ActivityManager manager = (ActivityManager) App.getContext().getSystemService(App.getContext().ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ServiceCheckWallet.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
            drawerLayout.closeDrawer(Gravity.RIGHT);
            return;
        }
        if (!App.canBackPress) {
            return;
        } else {
            super.onBackPressed();
        }

    }

    public interface OnSetMainToolbarListener {
        void onChange();
    }

    public static void setupConfiguration() {
        if (ActivityMain.txtWalletBalance != null) {
            txtWalletBalance.setText(App.getContext().getString(R.string.myWallet) + " :   " + HelperString.convertToNumberFormat(App.account.wallet + "") + " " + App.getContext().getString(R.string.tooman));
        }

    }


    public void requestAccessCoarseLocation() {
        permissionManager = new PermissionManager(App.getCurrentActivity());
        permissionManager.setOnPermissionGranted(new PermissionManager.OnPermissionGranted() {
            @Override
            public void onGranted() {
                FragmentSystemsList.permissionListener.onAccess(true);

            }
        }).setOnPermissionDenied(new PermissionManager.OnPermissionDenied() {
            @Override
            public void onDenied() {
                new AlertDialog.Builder(App.getCurrentActivity())
                        .setTitle(App.getContext().getString(R.string.permission_required))
                        .setMessage(App.getContext().getString(R.string.get_location_required_text))
                        .setPositiveButton(App.getContext().getString(R.string.ask_again), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestAccessCoarseLocation();
                            }
                        })
                        .setNegativeButton(App.getContext().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                                FragmentSystemsList.permissionListener.onAccess(false);
                            }
                        }).create().show();
            }
        }).request(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        for (int i = 0; i < permissions.length; i++) {
//            if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                if (FragmentSystemsList.permissionListener != null) {
//                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
//                        FragmentSystemsList.permissionListener.onAccess(true);
//                    } else {
//                        FragmentSystemsList.permissionListener.onAccess(false);
//                    }
//                }
//            }
//        }
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void showExitDialog(String title) {
        if (title.equals(App.getContext().getString(R.string.doYouWantToExitFromAccount))) {
            exitAccount = true;
        } else {
            exitAccount = false;
        }
        dialogExitTitle.setText(title);
        dialogExit.show();
    }

    private void getInviteText() {

        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONArray connections = new JSONArray(data);
                    for (int i = 0; i < connections.length(); i++) {
                        JSONObject connection = connections.getJSONObject(i);
                        if (connection.getString("type").contains("share_text")) {
                            inviteText = connection.getString("value");
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CustomToast.showToast(getString(R.string.oldVersionError));
                }
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(getString(R.string.connectionError));
                return;
            }
        }).getInformations();
    }

    private void initializeMenuList() {
        ArrayList<String> titles = new ArrayList<>();
        ArrayList<String> icons = new ArrayList<>();
        if (App.account.role == 2) { // OWNER
            titles.add(App.getContext().getString(R.string.accountEdit));
            titles.add(App.getContext().getString(R.string.setting));
            titles.add(App.getContext().getString(R.string.contactUs));
            titles.add(App.getContext().getString(R.string.invideFriends));
            titles.add(App.getContext().getString(R.string.contactManagement));
            titles.add(App.getContext().getString(R.string.appGuide));
            titles.add(App.getContext().getString(R.string.exit));

            icons.add("\uf2bd");
            icons.add("\uf013");
            icons.add("\uf098");
            icons.add("\uF234");
            icons.add("\uf509");
            icons.add("\uf05a");
            icons.add("\uf2f5");
        } else {
            titles.add(App.getContext().getString(R.string.systemInfo));
            titles.add(App.getContext().getString(R.string.accountEdit));
            titles.add(App.getContext().getString(R.string.commonQuestions));
            titles.add(App.getContext().getString(R.string.appGuide));
            titles.add(App.getContext().getString(R.string.contactUs));
            titles.add(App.getContext().getString(R.string.invideFriends));
            titles.add(App.getContext().getString(R.string.exit));

            icons.add("\uF1EB");
            icons.add("\uf2bd");
            icons.add("\uf059");
            icons.add("\uf05a");
            icons.add("\uf098");
            icons.add("\uF234");
            icons.add("\uf2f5");
        }
        for (int i = 0; i < titles.size(); i++) {
            StructMenu item = new StructMenu();
            item.title = titles.get(i);
            item.icon = icons.get(i);
            item.count = 0;
            menuItems.add(item);
        }
    }

    private void startGetClientNumbers() {
        final int min = 2; // 2 minute
        final int max = 3; // 3 minute
        if (clientNumberThread != null) {
            getMyClientNumbers();
            return;
        }
        clientNumberThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (App.enableThreads) {
                    try {
                        getMyClientNumbers();
                        Random random = new Random();
                        int time = random.nextInt((max - min) + 1) + min;
                        time = time * 60000; // convert to millis
                        Thread.sleep(time);
                        if (App.account.role != 2) {
                            break;
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                clientNumberThread = null;
            }
        });
        clientNumberThread.start();
    }

    private void getMyClientNumbers() {
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(final String data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        txtMyClientNumbers.setText(data);
                    }
                });

            }

            @Override
            public void onFail(String error) {
            }
        }).getMyCitizensCount(App.ownerSystems.get(0).id);
    }

    public static void updateTicketCount(int count) {
        try {
            int i = 0;
            for (i = 0; i < menuItems.size(); i++) {
                if (menuItems.get(i).title.contains(App.getContext().getString(R.string.contactManagement))) {
                    break;
                }
            }
            menuItems.get(i).count = count;
            adapter.notifyDataSetChanged();
        } catch (Exception e) {

        }

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            if (activityResultListener != null) {
//                activityResultListener.onGranted(requestCode, resultCode, data);
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            if (activityResultListener != null) {
//                activityResultListener.onGranted(requestCode, resultCode, data);
//            }
//        }
        if (resultCode == Activity.RESULT_OK && data!=null) {
            if (activityResultListener != null) {
                activityResultListener.onGranted(requestCode, resultCode, data);
            }
        }
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 280);
            cropIntent.putExtra("outputY", 280);

            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 203);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            CustomToast.showToast(errorMessage);
//            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
//            toast.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (App.connectedSystemChanged) {
            App.connectedSystemChanged = false;
            FragmentMainClient.getSlideImages();
            ActivityMain.fragmentManager.popBackStack();
        }
    }


}

