package project.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.adapters.AdapterCustomWasteRecycler;
import project.custom.CustomToast;
import project.structures.StructCustomWaste;

public class FragmentPackageRequestCustom extends Fragment {

    private RecyclerView lstWastes;
    private static AdapterCustomWasteRecycler adapter;
    private static ArrayList<StructCustomWaste> customWastes;
    private int spinnerPosition;
    private Vibrator vibrator;

    private boolean backPressed;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_package_request_custom, container, false);
        root.setFocusableInTouchMode(true);
        root.requestFocus();
        vibrator = (Vibrator) App.getContext().getSystemService(Context.VIBRATOR_SERVICE);


        backPressed = false;
        lstWastes = (RecyclerView) root.findViewById(R.id.lstWastes);
        Button btnAdd = (Button) root.findViewById(R.id.btnAdd);
        Button btnEnd = (Button) root.findViewById(R.id.btnEnd);

        if (customWastes == null) {
            customWastes = new ArrayList<>();
        }
        adapter = new AdapterCustomWasteRecycler(customWastes);
        lstWastes.setLayoutManager(new LinearLayoutManager(App.getContext()));
        lstWastes.setAdapter(adapter);

        final Dialog dialogNewWaste = new Dialog(App.getCurrentActivity());
        dialogNewWaste.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogNewWaste.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogNewWaste.setContentView(R.layout.dialog_custom_waste);
        Window window = dialogNewWaste.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        final ConstraintLayout dialogRoot = (ConstraintLayout) dialogNewWaste.findViewById(R.id.root);
        final ViewGroup dialogLayout = (ViewGroup) dialogNewWaste.findViewById(R.id.dialogLayout);
        Button btnRecord = (Button) dialogNewWaste.findViewById(R.id.btnRecord);
        final EditText edtName = (EditText) dialogNewWaste.findViewById(R.id.edtName);
        final EditText edtCount = (EditText) dialogNewWaste.findViewById(R.id.edtCount);
        final EditText edtPrice = (EditText) dialogNewWaste.findViewById(R.id.edtPrice);
        final AppCompatSpinner spinnerType = (AppCompatSpinner) dialogNewWaste.findViewById(R.id.spinnerType);
        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(App.getContext(), R.array.spinner_custom_wastes, R.layout.struct_spinner_custom_waste_type);
        adapterSpinner.setDropDownViewResource(R.layout.struct_spinner_custom_waste_type);
        spinnerType.setAdapter(adapterSpinner);

        final Dialog dialogGuide = new Dialog(App.getCurrentActivity());
        dialogGuide.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogGuide.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialogGuide.setContentView(R.layout.dialog_custom_waste_addition_guide);
        window = dialogGuide.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);
        Button btnOK = (Button)dialogGuide.findViewById(R.id.btnOK);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogGuide.dismiss();
            }
        });

        if(App.getPreferences().getBoolean("SHOW_CUSTOM_WASTE_GUIDE",true)){
            dialogGuide.show();
            SharedPreferences.Editor editor = App.getPreferences().edit();
            editor.putBoolean("SHOW_CUSTOM_WASTE_GUIDE",false);
            editor.commit();
        }

        dialogRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogNewWaste.dismiss();
            }
        });

        dialogLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                spinnerPosition = position;
                if (position == 0) {
                    edtCount.setHint(App.getContext().getString(R.string.count));
                } else {
                    edtCount.setHint(App.getContext().getString(R.string.kiloWeight));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyChanges();
                ActivityMain.fragmentManager.popBackStack();
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customWastes.size() == 20) {
                    CustomToast.showToast(App.getContext().getString(R.string.maximumListOverflow));
                    return;
                }
                if (App.account.role == 2) {
                    edtPrice.setVisibility(View.VISIBLE);
                    edtPrice.setText("");
                } else {
                    edtPrice.setVisibility(View.GONE);
                    edtPrice.setText("1");
                }
                edtName.setText("");
                edtCount.setText("");
                edtCount.setHint(App.getContext().getString(R.string.count));
                spinnerType.setSelection(0);
                dialogNewWaste.show();
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString();
                String count_s = edtCount.getText().toString();
                String price_s = edtPrice.getText().toString();
                int count = count_s.length() == 0 ? 0 : Integer.valueOf(count_s);
                int price = price_s.length() == 0 ? 0 : Integer.valueOf(price_s);
                if (count == 0 || name.length() == 0 || price == 0) {
                    if(name.length() == 0) {
                        edtName.setError(App.getContext().getString(R.string.thisFieldCannotBeEmpty));
                    }else if(count ==0){
                        edtCount.setError(App.getContext().getString(R.string.thisFieldCannotBeEmpty));
                    }else if(price==0){
                        edtPrice.setError(App.getContext().getString(R.string.thisFieldCannotBeEmpty));
                    }
                    vibrateDevice();
                    return;
                }
                StructCustomWaste customWaste = new StructCustomWaste();
                customWaste.name = name;

                if (spinnerPosition == 0) {
                    customWaste.count = count;
                    customWaste.weight = 0;
                } else {
                    customWaste.weight = count;
                    customWaste.count = 0;
                }
                customWaste.price = price;
                customWastes.add(customWaste);
                adapter.notifyDataSetChanged();
                dialogNewWaste.dismiss();

            }
        });

        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (ActivityMain.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    ActivityMain.drawerLayout.closeDrawer(Gravity.RIGHT);
                    return true;
                }
                Fragment currentFragment = ActivityMain.fragmentManager.findFragmentById(R.id.fragmentContainer);
                if (currentFragment instanceof FragmentPackageRequestCustom) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (!backPressed) {
                            backPressed = true;
                            notifyChanges();
                        }

                        return false;
                    }
                }
                return false;
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

    public static int getCustomItemsCount() {
        if (customWastes == null) {
            return 0;
        }
        return customWastes.size();
    }

    public static ArrayList<StructCustomWaste> getCustomItems() {
        return customWastes;
    }

    public static void clearList() {
        if (customWastes != null) {
            customWastes.clear();
            customWastes = null;
        }

    }

    public static int getSum() {
        if (customWastes == null) {
            return 0;
        }
        int sum = 0;
        for (int i = 0; i < customWastes.size(); i++) {
            sum += customWastes.get(i).price;
        }
        return sum;
    }

    private void notifyChanges() {
        int count = 0;
        if (customWastes != null) {
            count = customWastes.size();
        }
        if (App.account.role == 2) {
            FragmentSystemConfirmDelivery.updateCustomItemsSetting(count);
        } else {
            FragPackageRequestStepOne.updateCustomItemsSetting(count);
        }
    }

    private void vibrateDevice(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(500);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
