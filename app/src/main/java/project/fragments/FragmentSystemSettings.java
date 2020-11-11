package project.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import pl.droidsonroids.gif.GifImageView;
import project.activities.ActivityMain;
import project.connection.Commands;
import project.connection.FileUploader;
import project.custom.CustomTimeSelection;
import project.custom.CustomToast;
import project.custom.ProgressButton;
import project.helper.HelperDimensions;
import project.helper.HelperFragment;
import project.helper.HelperString;
import project.interfaces.ActivityResult;

public class FragmentSystemSettings extends Fragment {
    private CheckBox chkAuto;
    private CheckBox chkManual;
    private TextView txtMorningActive;
    private TextView txtAfternoonActive;
    private TextView txtName;
    private TextView txtAddress;
    private TextView txtID;

    private CustomTimeSelection MSTimer;
    private CustomTimeSelection METimer;
    private CustomTimeSelection ASTimer;
    private CustomTimeSelection AETimer;

    private LinearLayout layoutAuto;
    private LinearLayout layoutManual;

    private int deactiveColor = Color.parseColor("#777777");
    private int activeColor = Color.parseColor("#000000");

    private ViewGroup layoutContainer;
    private ViewGroup layoutUnsuccessfull;
    private ViewGroup loader;
    private RadioGroup radioGroupState;
    private RadioButton radioButtonOnline;
    private RadioButton radioButtonOffline;

    private boolean actionEnable = true;
    private boolean morningActive = false;
    private boolean afternoonActive = false;
    private Handler handler;

    private ImageView imgCover;
    private GifImageView imageLoader;
    private boolean changeImageEnable = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_system_settings, container, false);
        handler = new Handler();
        layoutContainer = root.findViewById(R.id.layoutContainer);
        layoutUnsuccessfull = root.findViewById(R.id.layoutUnsuccessfull);
        loader = root.findViewById(R.id.loader);

        final ProgressButton btnVerify = (ProgressButton) root.findViewById(R.id.btnVerify);

        txtID =(TextView)root.findViewById(R.id.txtID);
        txtName =(TextView)root.findViewById(R.id.txtName);
        txtAddress =(TextView)root.findViewById(R.id.txtAddress);

        chkAuto = (CheckBox) root.findViewById(R.id.chkAuto);
        chkManual = (CheckBox) root.findViewById(R.id.chkManual);
        txtMorningActive = (TextView) root.findViewById(R.id.txtMorningActive);
        txtAfternoonActive = (TextView) root.findViewById(R.id.txtAfternoonActive);
        layoutAuto = (LinearLayout) root.findViewById(R.id.layoutAuto);
        layoutManual = (LinearLayout) root.findViewById(R.id.layoutManual);
        Button btnRetry = (Button) root.findViewById(R.id.btnRetry);
        Button btnWastesSetting = (Button) root.findViewById(R.id.btnWastesSetting);
        radioGroupState = (RadioGroup) root.findViewById(R.id.radioGroupState);
        radioButtonOnline = (RadioButton) root.findViewById(R.id.radioButtonOnline);
        radioButtonOffline = (RadioButton) root.findViewById(R.id.radioButtonOffline);

        btnVerify.setNormalText(App.getContext().getString(R.string.applyChanges));
        btnVerify.setDoneText(App.getContext().getString(R.string.done));

        MSTimer = (CustomTimeSelection) root.findViewById(R.id.MSTimer);
        METimer = (CustomTimeSelection) root.findViewById(R.id.METimer);
        ASTimer = (CustomTimeSelection) root.findViewById(R.id.ASTimer);
        AETimer = (CustomTimeSelection) root.findViewById(R.id.AETimer);

        imgCover = (ImageView) root.findViewById(R.id.img);
        imageLoader = (GifImageView) root.findViewById(R.id.imageLoader);

        ActivityMain.activityResultListener = new ActivityResult() {
            @Override
            public void onGranted(int requestCode, int resultCode, @Nullable Intent data) {
                    uploadImage(data.getData());

            }
        };


        final Dialog dialogImageAction = new Dialog(App.getCurrentActivity());
        dialogImageAction.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogImageAction.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogImageAction.setContentView(R.layout.dialog_image_selection_delete);
        Window window = dialogImageAction.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        ViewGroup layoutDelete = (ViewGroup) dialogImageAction.findViewById(R.id.layoutDelete);
        ViewGroup layoutChange = (ViewGroup) dialogImageAction.findViewById(R.id.layoutChange);


        layoutDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogImageAction.dismiss();
                imageChanging();
                new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                    @Override
                    public void onComplete(String data) {
                            App.ownerSystems.get(0).coverUrl = "";
                            imageApplied();
                    }

                    @Override
                    public void onFail(String error) {
                        imageApplied();
                        CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                    }
                }).deleteSystemImage(App.ownerSystems.get(0).id);
            }
        });

        layoutChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogImageAction.dismiss();
                selectImage();
            }
        });

        imgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!changeImageEnable){
                    CustomToast.showToast(App.getContext().getString(R.string.pleaseWait));
                    return;
                }
                if(App.ownerSystems.get(0).coverUrl!=null && App.ownerSystems.get(0).coverUrl.length()>0){
                    dialogImageAction.show();
                }else{
                    selectImage();
                }


//
            }
        });


        MSTimer.setMin(5);
        MSTimer.setMax(22);
        METimer.setMin(6);
        METimer.setMax(23);

        ASTimer.setMin(13);
        ASTimer.setMax(23);
        AETimer.setMin(14);
        AETimer.setMax(24);
        getSystemInfo();

        btnWastesSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!actionEnable) {
                    CustomToast.showToast(App.getContext().getString(R.string.pleaseWait));
                    return;
                }
                new HelperFragment().addToBackStack(App.FRAGMENT_SETTING_WASTES_ID)
                        .addFragment(R.id.layoutContainer,new FragmentSystemSettingsWastes(),true)
                        .commit();
            }
        });

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSystemInfo();
            }
        });

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!actionEnable) {
                    CustomToast.showToast(App.getContext().getString(R.string.pleaseWait));
                    return;
                }
                try {
                    actionEnable = false;
                    btnVerify.loading();
                    JSONObject systemSetting = new JSONObject();
                    systemSetting.put("id", App.ownerSystems.get(0).id);
                    if (chkManual.isChecked()) {
                        systemSetting.put("activation_type", App.MANUAL);
                        if(radioButtonOffline.isChecked()){
                            systemSetting.put("state", App.deactive);
                        }else{
                            systemSetting.put("state", App.active);
                        }
                    }else{ //AUTO
                        systemSetting.put("activation_type", App.AUTO);
                        if(morningActive){
                            systemSetting.put("morning_start_time",MSTimer.getTime());
                            systemSetting.put("morning_end_time",METimer.getTime());
                        }else{
                            systemSetting.put("morning_start_time",0);
                            systemSetting.put("morning_end_time",0);
                        }

                        if(afternoonActive){
                            systemSetting.put("afternoon_start_time",ASTimer.getTime());
                            systemSetting.put("afternoon_end_time",AETimer.getTime());
                        }else{
                            systemSetting.put("afternoon_start_time",0);
                            systemSetting.put("afternoon_end_time",0);
                        }
                    }
                    new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                        @Override
                        public void onComplete(String data) {
                            CustomToast.showToast(App.getContext().getString(R.string.successfulAct));
                            btnVerify.loaded();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    actionEnable = true;
                                    btnVerify.reset();
                                }
                            },500);

                        }

                        @Override
                        public void onFail(String error) {
                            CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                            actionEnable = true;
                            btnVerify.reset();
                        }
                    }).editSystemActivation(systemSetting);
                } catch (JSONException e) {
                    btnVerify.reset();
                    e.printStackTrace();
                }
            }
        });


        MSTimer.setOnTimeChangedListener(new CustomTimeSelection.onTimeChangedListener() {
            @Override
            public void onChanged(int time, String changes) {
                METimer.setMin(time + 1);
                ASTimer.setMin(METimer.getTime());
                AETimer.setMin(ASTimer.getMin() + 1);
            }
        });
        METimer.setOnTimeChangedListener(new CustomTimeSelection.onTimeChangedListener() {
            @Override
            public void onChanged(int time, String changes) {
                ASTimer.setMin(time);
                AETimer.setMin(ASTimer.getMin() + 1);
            }
        });

        ASTimer.setOnTimeChangedListener(new CustomTimeSelection.onTimeChangedListener() {
            @Override
            public void onChanged(int time, String changes) {
                AETimer.setMin(time + 1);
            }
        });

        txtMorningActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                morningActive = !morningActive;
                if (!morningActive) {
                    MSTimer.setTextColor(deactiveColor);
                    METimer.setTextColor(deactiveColor);
                    txtMorningActive.setAlpha(0.3f);
                } else {
                    MSTimer.setTextColor(activeColor);
                    METimer.setTextColor(activeColor);
                    txtMorningActive.setAlpha(1f);
                }
            }
        });

        txtAfternoonActive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                afternoonActive = !afternoonActive;
                if (!afternoonActive) {
                    ASTimer.setTextColor(deactiveColor);
                    AETimer.setTextColor(deactiveColor);
                    txtAfternoonActive.setAlpha(0.3f);
                } else {
                    ASTimer.setTextColor(activeColor);
                    AETimer.setTextColor(activeColor);
                    txtAfternoonActive.setAlpha(1f);
                }
            }
        });

        chkAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkAuto.isChecked()) {
                    chkManual.setChecked(false);
                    layoutAuto.setAlpha(1);
                    layoutManual.setAlpha(0.5f);
                } else {
                    chkManual.setChecked(true);
                    layoutAuto.setAlpha(0.5f);
                    layoutManual.setAlpha(1);
                }
            }
        });

        chkManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chkManual.isChecked()) {
                    chkAuto.setChecked(false);
                    layoutAuto.setAlpha(0.5f);
                    layoutManual.setAlpha(1);
                } else {
                    layoutAuto.setAlpha(1);
                    layoutManual.setAlpha(0.5f);
                    chkAuto.setChecked(true);
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

    private void getSystemInfo() {
        layoutUnsuccessfull.setVisibility(View.GONE);
        layoutContainer.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONObject userObject = new JSONObject(data);
                    JSONObject systemObject = userObject.getJSONArray("systems").getJSONObject(0);
                    final String coverUrl =  App.ownerSystems.get(0).coverUrl;

                    App.ownerSystems.get(0).state = systemObject.getString("state");
                    if (App.ownerSystems.get(0).state.equals(App.active)) {
                        radioButtonOnline.setChecked(true);
                        radioButtonOffline.setChecked(false);
                    } else {
                        radioButtonOnline.setChecked(false);
                        radioButtonOffline.setChecked(true);
                    }
                    int ms = systemObject.getInt("morning_start_time");
                    int me = systemObject.getInt("morning_end_time");
                    int as = systemObject.getInt("afternoon_start_time");
                    int ae = systemObject.getInt("afternoon_end_time");
                    METimer.setMin(ms + 1);
                    ASTimer.setMin(METimer.getTime());
                    AETimer.setMin(ASTimer.getMin() + 1);
                    MSTimer.setTime(ms);
                    METimer.setTime(me);
                    ASTimer.setTime(as);
                    AETimer.setTime(ae);

                    if (ms == 0 || me == 0) {
                        txtMorningActive.setAlpha(0.3f);
                        morningActive = false;
                        MSTimer.setTextColor(deactiveColor);
                        METimer.setTextColor(deactiveColor);
                        MSTimer.setMin(5);
                        METimer.setMin(MSTimer.getTime()+1);
                    } else {
                        txtMorningActive.setAlpha(1f);
                        morningActive = true;
                        MSTimer.setTextColor(activeColor);
                        METimer.setTextColor(activeColor);
                    }

                    if (as == 0 || ae == 0) {
                        txtAfternoonActive.setAlpha(0.3f);
                        afternoonActive = false;
                        ASTimer.setTextColor(deactiveColor);
                        AETimer.setTextColor(deactiveColor);
                        ASTimer.setMin(METimer.getTime());
                        AETimer.setMin(ASTimer.getTime()+1);
                    } else {
                        txtAfternoonActive.setAlpha(1f);
                        afternoonActive = true;
                        ASTimer.setTextColor(activeColor);
                        AETimer.setTextColor(activeColor);
                    }

                    String activationType = systemObject.getString("activation_type");
                    if (activationType.equals(App.MANUAL)) {
                        layoutManual.setAlpha(1f);
                        layoutAuto.setAlpha(0.5f);
                        chkManual.setChecked(true);
                        chkAuto.setChecked(false);
                    } else {
                        chkManual.setChecked(false);
                        chkAuto.setChecked(true);
                        layoutManual.setAlpha(0.5f);
                        layoutAuto.setAlpha(1f);
                    }
                    String name = systemObject.getString("name");
                    if(name==null || name.toLowerCase().equals("null") || name.length()==0){
                        name = "-";
                    }
                    int id = systemObject.getInt("id");
                    String city = systemObject.getJSONObject("city").getString("name");
                    String province = systemObject.getJSONObject("city").getJSONObject("province").getString("name");
                    String address = systemObject.getString("address");
                    String fullAddress = province+" - "+city+" - "+address;

                    txtID.setText(App.getContext().getString(R.string.ID_)+" "+id);
                    txtName.setText(App.getContext().getString(R.string.systemName)+" "+name);
                    txtAddress.setText(App.getContext().getString(R.string.address)+": "+fullAddress);

                    loader.setVisibility(View.GONE);
                    layoutContainer.setVisibility(View.VISIBLE);
                    App.getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (coverUrl != null && coverUrl.length() > 0) {
                                Picasso.with(App.getContext()).load(coverUrl).resize(imgCover.getWidth(),imgCover.getHeight()).into(imgCover);
                            } else {
                                imgCover.setImageResource(R.drawable.fara_padded);
                            }
                        }
                    },500);

                } catch (JSONException e) {
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                layoutUnsuccessfull.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);
            }
        }).getUserInfo();
    }


    private void uploadImage(Uri resultUri) {
        imageChanging();
        File file = new File(resultUri.getPath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        String path = HelperString.getPath(App.getContext(), resultUri);
        imgCover.setImageBitmap(bitmap);
        new FileUploader().url(App.SERVER_ADDRESS + "/api/edit-system")
                .authorization("Bearer", App.accessToken)
                .setListener(new FileUploader.Listener() {
                    @Override
                    public void onComplete(String data) {
                        try {
                            JSONObject userObject = new JSONObject(data);
                            String imgUrl = userObject.getString("image");
                            if(imgUrl !=null && !imgUrl.toLowerCase().equals("null") && imgUrl.length()>0) {
                                App.ownerSystems.get(0).coverUrl = App.SERVER_ADDRESS + userObject.getString("image");
                            }else{
                                App.ownerSystems.get(0).coverUrl = "";
                            }
                            imageApplied();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFail(String error) {
                        imageApplied();
                        CustomToast.showToast(App.getContext().getString(R.string.connectionError_checkYourConnection));
                    }

                    @Override
                    public void onProgress(long totalSize, long uploadedSize, int percent) {
                    }
                }).setImage("image", path).params("id",App.ownerSystems.get(0).id+"").maximumFileSize(1024*1024).upload();
    }

    private void imageChanging(){
        changeImageEnable = false;
        imageLoader.setVisibility(View.VISIBLE);
        imgCover.setAlpha(0.5f);
    }

    private void selectImage() {
//        CropImage.activity()
//                .setAspectRatio(1, 1)
//                .setActivityTitle(App.getContext().getString(R.string.imageSelection))
//                .setMultiTouchEnabled(true)
//                .setAllowRotation(true)
//                .setAllowCounterRotation(true)
//                .start(App.getCurrentActivity());
        ImagePicker.Companion.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    private void imageApplied(){
        changeImageEnable = true;
        imageLoader.setVisibility(View.GONE);
        imgCover.setAlpha(1f);
        String systemCoverUrl = App.ownerSystems.get(0).coverUrl;
        if(systemCoverUrl !=null && !systemCoverUrl.toLowerCase().equals("null") && systemCoverUrl.length()>0) {
            Picasso.with(App.getContext()).load(App.ownerSystems.get(0).coverUrl).into(imgCover);

        }else {
            imgCover.setImageResource(R.drawable.fara_padded);
        }
    }
}
