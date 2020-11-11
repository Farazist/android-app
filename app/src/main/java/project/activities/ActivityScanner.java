package project.activities;

import androidx.annotation.NonNull;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import project.adapters.AdapterSystemsRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.fragments.FragmentMainClient;
import project.helper.PermissionManager;
import project.structures.StructSystem;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.google.zxing.Result;

public class ActivityScanner extends CAppCompatActivity {

    public static final String SYSTEM_CONNECTION = "CONNECT_TO_SYSTEM";
    public static final String LOGIN_SYSTEM = "LOGIN_SYSTEM";

    private ZXingScannerView scannerView;
    private PermissionManager permissionManager;
    private Dialog dialogWait;

    private String mode = "";

    private boolean backEnable;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeActionBar();
//        scannerView = new ZXingScannerView(App.getContext());
//        setContentView(scannerView);
        final Dialog dialogMode = new Dialog(ActivityScanner.this);
        dialogMode.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogMode.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogMode.setContentView(R.layout.dialog_scanner_action);
        Window window = dialogMode.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        Button btnLogin = (Button) dialogMode.findViewById(R.id.btnLogin);
        Button btnConnect = (Button) dialogMode.findViewById(R.id.btnConnect);
        dialogMode.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (mode.equals("")) {
                    finish();
                }
            }
        });
//        dialogMode.setCancelable(false);
//        dialogMode.setCancelable(false);


        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = LOGIN_SYSTEM;
                dialogMode.dismiss();
                requestCameraPermission();

            }
        });

        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = SYSTEM_CONNECTION;
                dialogMode.dismiss();
                requestCameraPermission();
            }
        });


        Intent intent = getIntent();
        if (intent.hasExtra("MODE")) {
            Bundle extras = intent.getExtras();
            mode = extras.getString("MODE");
        }

        if (mode.equals(LOGIN_SYSTEM) && App.account.role != 2) {
            mode = "";
            dialogMode.show();
        } else {
            requestCameraPermission();
        }

        backEnable = true;
        dialogWait = new Dialog(ActivityScanner.this);
        dialogWait.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogWait.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogWait.setContentView(R.layout.dialog_wait);
        dialogWait.setCancelable(false);
        window = dialogWait.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

    }


    private void requestCameraPermission() {
        permissionManager = new PermissionManager(ActivityScanner.this);
        permissionManager.setOnPermissionGranted(new PermissionManager.OnPermissionGranted() {
            @Override
            public void onGranted() {
                if (scannerView == null) {
                    scannerView = new ZXingScannerView(App.getContext());
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
                    @Override
                    public void handleResult(Result result) {
                        if (!backEnable) {
                            return;
                        }
                        backEnable = false;
                        String scanResult = result.getText();
//                        CustomToast.showToast(scanResult);
                        if (mode.equals(LOGIN_SYSTEM)) {
                            loginSystem(scanResult);
                        } else if (mode.equals(SYSTEM_CONNECTION)) {
                            connectToSystem(scanResult);
                        }

                    }
                });
                scannerView.startCamera();
            }
        }).setOnPermissionDenied(new PermissionManager.OnPermissionDenied() {
            @Override
            public void onDenied() {
                new AlertDialog.Builder(ActivityScanner.this)
                        .setTitle(getString(R.string.permission_required))
                        .setMessage(getString(R.string.write_sdcard_required_text))
                        .setPositiveButton(getString(R.string.ask_again), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                requestCameraPermission();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        }).create().show();
            }
        }).request(Manifest.permission.CAMERA);
    }

    private void connectToSystem(String scanResult) {
        int systemID = 0;
        try {
            systemID = Integer.valueOf(scanResult);
        } catch (Exception e) {
            dialogWait.dismiss();
            finish();
        }
        if (systemID == 0) {
            finish();
            return;
        }
        if (systemID == App.clientSystem.id) {
            dialogWait.dismiss();
            CustomToast.showToast(App.getContext().getString(R.string.youAreConnected));
            finish();
            return;
        } else {
            dialogWait.show();
            new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                @Override
                public void onComplete(String data) {
                    if (data.equals("0")) {
                        CustomToast.showToast(App.getContext().getString(R.string.unsuccessfulAct));
                        finish();
                        return;
                    }
                    getUserInfo();
                }

                @Override
                public void onFail(String error) {
                    dialogWait.dismiss();
                    CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                    finish();
                }
            }).editUserSystem(systemID);
        }
    }

    private void getUserInfo() {
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                dialogWait.dismiss();
                CustomToast.showToast(App.getContext().getString(R.string.accountConnectedToSystem));
                App.connectedSystemChanged = true;
                App.getCurrentActivity().finish();
                App.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            FragmentMainClient.getSlideImages();
                        }catch (Exception e){}
                    }
                },1000);

            }

            @Override
            public void onFail(String error) {
                dialogWait.dismiss();
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                finish();
            }
        }).getUserInfo();
    }

    private void loginSystem(String scanResult) {
        dialogWait.show();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                dialogWait.dismiss();
                backEnable = true;
                CustomToast.showToast(App.getContext().getString(R.string.sentData));
//                CustomToast.showToast(data + "");
                finish();
            }

            @Override
            public void onFail(String error) {
                dialogWait.dismiss();
                backEnable = true;
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                finish();
            }
        }).sendQRCode(scanResult);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (scannerView != null) {
                scannerView.stopCamera();
            }
        } catch (Exception e) {
        }
        ;
    }

    @Override
    public void onBackPressed() {
        if (!backEnable) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
