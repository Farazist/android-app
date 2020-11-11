package project.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import pl.droidsonroids.gif.GifImageView;
import project.activities.ActivityMain;
import project.activities.ActivitySignup;
import project.adapters.AdapterCityRecycler;
import project.connection.Commands;
import project.connection.FileUploader;
import project.custom.CustomToast;
import project.custom.LinearProgressBar;
import project.helper.HelperString;
import project.interfaces.ActivityResult;
import project.structures.StructCity;

public class FragmentEditAccount extends Fragment {

    public static OnAddressSelectListener addressSelectListener;


    private ImageView imgCover;
    private TextView txtID;
    private EditText edtName;
    private EditText edtAddress;
    private EditText edtNewPassword;
    private EditText edtCardNumber;
    private CheckBox chkChangePassword;
    private Button btnApplyChanges;
    private ProgressBar loader;
    private GifImageView imageLoader;
    private String province = "";
    private String city = "";
    private int provinceID = -1;
    private int cityID = -1;
    private boolean getItemEnable = true;
    private boolean changeImageEnable = true;

    private LinearProgressBar progressBar;
    private RecyclerView lstItems;

    private int previousCardLength;

    private int mode = 0;
    private static final int PROVINCE_MODE = 0;
    private static final int CITY_MODE = 1;

    private ArrayList<StructCity> provinces;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit_account, container, false);

        ActivitySignup.addressSelectListener = null;
        provinces = new ArrayList<>();

        city = App.account.city;
        province = App.account.province;
        cityID = App.account.cityID;
        provinceID = App.account.provinceID;

        final Button btnProvince = (Button) root.findViewById(R.id.btnProvince);
        final Button btnCity = (Button) root.findViewById(R.id.btnCity);
        imgCover = (ImageView) root.findViewById(R.id.img);
        imageLoader = (GifImageView) root.findViewById(R.id.imageLoader);
        txtID = (TextView) root.findViewById(R.id.txtID);
        edtName = (EditText) root.findViewById(R.id.edtName);
        edtAddress = (EditText) root.findViewById(R.id.edtAddress);
        edtNewPassword = (EditText) root.findViewById(R.id.edtNewPassword);
        edtCardNumber = (EditText) root.findViewById(R.id.edtCardNumber);
        chkChangePassword = (CheckBox) root.findViewById(R.id.chkChangePassword);
        btnApplyChanges = (Button) root.findViewById(R.id.btnApplyChanges);
        loader = (ProgressBar) root.findViewById(R.id.loader);


        ActivityMain.activityResultListener = new ActivityResult() {
            @Override
            public void onGranted(int requestCode, int resultCode, @Nullable Intent data) {
                uploadImage(data.getData());

            }
        };

        if (provinceID > 0) {
            btnProvince.setText(province);
        }
        if (cityID > 0) {
            btnCity.setText(city);
        }
        edtName.setText(App.account.name);
        edtCardNumber.setText(App.account.cardNumber);
        txtID.setText(App.getContext().getString(R.string.ID_) + " " + App.account.id);
        chkChangePassword.setChecked(false);
        hidePassword();
        showButton();

        final Dialog dialogSelection = new Dialog(App.getCurrentActivity());
        dialogSelection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSelection.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogSelection.setContentView(R.layout.dialog_select_address);
        Window window = dialogSelection.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        final TextView txtDialogTitle = (TextView) dialogSelection.findViewById(R.id.txtTitle);
        lstItems = (RecyclerView) dialogSelection.findViewById(R.id.lstItems);
        progressBar = (LinearProgressBar) dialogSelection.findViewById(R.id.progressBar);


        final Dialog dialogImageAction = new Dialog(App.getCurrentActivity());
        dialogImageAction.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogImageAction.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogImageAction.setContentView(R.layout.dialog_image_selection_delete);
        window = dialogImageAction.getWindow();
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
                        try {
                            JSONObject userObject = new JSONObject(data);
                            String imgUrl = userObject.getString("image");
                            if (imgUrl != null && !imgUrl.toLowerCase().equals("null") && imgUrl.length() > 0) {
                                App.account.avatarUrl = App.SERVER_ADDRESS + userObject.getString("image");
                            } else {
                                App.account.avatarUrl = "";
                            }
                            imageApplied();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFail(String error) {
                        imageApplied();
                        CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                    }
                }).deleteUserImage();
            }
        });

        layoutChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogImageAction.dismiss();
                selectImage();
            }
        });


        lstItems.setLayoutManager(new LinearLayoutManager(App.getContext()));

        if (App.account.address == null || App.account.address.equals("null")) {
            edtAddress.setText("");
        } else {
            edtAddress.setText(App.account.address);
        }



        final String avatarUrl = App.account.avatarUrl;
        if (avatarUrl != null && avatarUrl.length() > 0) {
            App.getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(App.getContext()).load(avatarUrl).resize(imgCover.getWidth(), imgCover.getHeight()).into(imgCover);
                }
            }, 500);

        } else {
            imgCover.setImageResource(R.drawable.avatar);
        }

        previousCardLength = edtCardNumber.getText().toString().length();
        edtCardNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = edtCardNumber.getText().toString();
                if(previousCardLength == text.length()){
                    return;
                }
                if (previousCardLength < text.length()) {
                    text = text.replace("-", "");
                    char[] charTextArray = text.toCharArray();
                    text = "";
                    for (int i = 0; i < charTextArray.length; i++) {
                        text += charTextArray[i];
                        if(text.length()==4 || text.length()==9 || text.length()==14){
                            text +="-";
                        }
                    }
                    previousCardLength = text.length();
                    edtCardNumber.setText(text);
                }
                previousCardLength = edtCardNumber.getText().toString().length();
                edtCardNumber.setSelection(edtCardNumber.getText().length());

            }
        });


        addressSelectListener = new OnAddressSelectListener() {
            @Override
            public void onItemSelect(int id, String name) {
                if (mode == PROVINCE_MODE) {
                    provinceID = id;
                    if (id < 0) {
                        cityID = -1;
                        btnCity.setText(App.getContext().getString(R.string.city));
                        btnProvince.setText(App.getContext().getString(R.string.province));
                    }
                    province = name;
                    btnProvince.setText(name);
                } else {
                    cityID = id;
                    city = name;
                    btnCity.setText(name);
                    if (id < 0) {
                        btnCity.setText(App.getContext().getString(R.string.city));
                    }
                }
                dialogSelection.dismiss();
            }
        };


        imgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!changeImageEnable) {
                    CustomToast.showToast(App.getContext().getString(R.string.pleaseWait));
                    return;
                }
                if (App.account.avatarUrl != null && App.account.avatarUrl.length() > 0) {
                    dialogImageAction.show();
                } else {
                    selectImage();
                }


//
            }
        });


        btnProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mode = PROVINCE_MODE;
                txtDialogTitle.setText(App.getContext().getString(R.string.provinceSelection));
                dialogSelection.show();
                lstItems.setVisibility(View.INVISIBLE);
                getProvinces();


            }
        });

        btnCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (provinceID < 0) {
                    return;
                }
                mode = CITY_MODE;
                txtDialogTitle.setText(App.getContext().getString(R.string.selectCityFromProvince) + " " + province);
                dialogSelection.show();
                lstItems.setVisibility(View.INVISIBLE);
                getCities();


            }
        });


        chkChangePassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    showPassword();
                } else {
                    hidePassword();
                }
            }
        });

        btnApplyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = edtName.getText().toString();
                String address = edtAddress.getText().toString();
                String password = edtNewPassword.getText().toString();
                String cardNumber = edtCardNumber.getText().toString();

                if (name.length() < 4) {
                    CustomToast.showToast(getString(R.string.minimumNameLength));
                    return;
                } else if (chkChangePassword.isChecked() && password.length() < 4) {
                    CustomToast.showToast(getString(R.string.minimumPasswordLength));
                    return;
                } else if (password.contains(" ")) {
                    CustomToast.showToast(getString(R.string.invalidPassword));
                    return;
                }else if(cardNumber.length()!=0 && cardNumber.length()!=19){
                    CustomToast.showToast(getString(R.string.invalidCardNumber));
                    return;
                }
                else {
                    hideButton();
                    Commands editor = new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                        @Override
                        public void onComplete(String data) {
                            CustomToast.showToast(getString(R.string.loginAgain));
                            App.getCurrentActivity().finish();
                        }

                        @Override
                        public void onFail(String error) {
                            CustomToast.showToast(getString(R.string.connectionError));
                            showButton();
                            return;
                        }
                    });
                    if (chkChangePassword.isChecked()) {
                        editor.editUserSetting(name, provinceID + "", cityID + "", cardNumber,address ,password);
                    } else {
                        editor.editUserSetting(name, provinceID + "", cityID + "",cardNumber, address);
                    }
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


    private void showPassword() {
        edtNewPassword.setVisibility(View.VISIBLE);
    }

    private void hidePassword() {
        edtNewPassword.setVisibility(View.GONE);
    }

    private void showButton() {
        btnApplyChanges.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }

    private void hideButton() {
        btnApplyChanges.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }

    public interface OnAddressSelectListener {
        void onItemSelect(int id, String name);
    }

    private void getProvinces() {
        if (provinces.size() > 0) {
            AdapterCityRecycler adapter = new AdapterCityRecycler(provinces);
            lstItems.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            progressBar.stop();
            lstItems.setVisibility(View.VISIBLE);
            progressBar.stop();
            return;
        }
        progressBar.start();
        if (!getItemEnable) {
            return;
        }
        getItemEnable = false;
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    getItemEnable = true;
                    JSONArray provinceArray = new JSONArray(data);
                    ArrayList<StructCity> items = new ArrayList<>();
                    StructCity p = new StructCity();
                    p.name = App.getContext().getString(R.string.select);
                    p.id = -1;
                    items.add(p);
                    for (int i = 0; i < provinceArray.length(); i++) {
                        JSONObject provinceObject = provinceArray.getJSONObject(i);
                        p = new StructCity();
                        p.name = provinceObject.getString("name");
                        p.id = provinceObject.getInt("id");
                        items.add(p);
                    }

                    AdapterCityRecycler adapter = new AdapterCityRecycler(items);
                    lstItems.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.stop();
                    lstItems.setVisibility(View.VISIBLE);
                } catch (JSONException e) {
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                getItemEnable = true;
            }
        }).getProvinces();
    }

    private void getCities() {
        progressBar.start();
        if (provinceID < 0) {
            return;
        }
        if (!getItemEnable) {
            return;
        }
        getItemEnable = false;
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    ArrayList<StructCity> cities = new ArrayList<>();
                    JSONArray cityArray = new JSONArray(data);
                    StructCity c = new StructCity();
                    c.name = App.getContext().getString(R.string.select);
                    c.id = -1;
                    cities.add(c);
                    for (int i = 0; i < cityArray.length(); i++) {
                        JSONObject cityObject = cityArray.getJSONObject(i);
                        c = new StructCity();
                        c.id = cityObject.getInt("id");
                        c.name = cityObject.getString("name");
                        cities.add(c);
                    }
                    AdapterCityRecycler adapter = new AdapterCityRecycler(cities);
                    lstItems.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.stop();
                    lstItems.setVisibility(View.VISIBLE);
                    getItemEnable = true;
                } catch (JSONException e) {
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                getItemEnable = true;
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                Log.i("LOGGG", error);
            }
        }).getProvinceCities(provinceID);

    }

    private void selectImage() {
//        CropImage.activity()
//                .setAspectRatio(1, 1)
//                .setActivityTitle(App.getContext().getString(R.string.imageSelection))
//                .setMultiTouchEnabled(true)
//                .setAllowRotation(true)
//                .setAllowCounterRotation(true)
//                .start(App.getContext(),this);
        ImagePicker.Companion.with(this)
                .cropSquare()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start();
    }

    private void uploadImage(Uri resultUri) {
        imageChanging();
        File file = new File(resultUri.getPath());
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        String path = HelperString.getPath(App.getContext(), resultUri);
        imgCover.setImageBitmap(bitmap);
        new FileUploader().url(App.SERVER_ADDRESS + "/api/edit-user")
                .authorization("Bearer", App.accessToken)

                .setListener(new FileUploader.Listener() {
                    @Override
                    public void onComplete(String data) {
                        try {
                            JSONObject userObject = new JSONObject(data);
                            String imgUrl = userObject.getString("image");
                            if (imgUrl != null && !imgUrl.toLowerCase().equals("null") && imgUrl.length() > 0) {
                                App.account.avatarUrl = App.SERVER_ADDRESS + userObject.getString("image");
                            } else {
                                App.account.avatarUrl = "";
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
                }).setImage("image", path).maximumFileSize(1024 * 1024).upload();
    }

    private void imageChanging() {
        changeImageEnable = false;
        imageLoader.setVisibility(View.VISIBLE);
        imgCover.setAlpha(0.5f);
    }

    private void imageApplied() {
        changeImageEnable = true;
        imageLoader.setVisibility(View.GONE);
        imgCover.setAlpha(1f);
        if (App.account.avatarUrl != null && App.account.avatarUrl.length() > 0) {
            Picasso.with(App.getContext()).load(App.account.avatarUrl).resize(ActivityMain.imgAvatar.getWidth(), ActivityMain.imgAvatar.getHeight()).into(ActivityMain.imgAvatar, new Callback() {
                @Override
                public void onSuccess() {
                    Picasso.with(App.getContext()).load(App.account.avatarUrl).resize(imgCover.getWidth(), imgCover.getHeight()).into(imgCover);
                }

                @Override
                public void onError() {

                }
            });
        } else {
            ActivityMain.imgAvatar.setImageResource(R.drawable.avatar);
            imgCover.setImageResource(R.drawable.avatar);
        }
    }

}
