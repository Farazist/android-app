package project.helper;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ir.farazist.farazist_app.R;
import project.activities.ActivityStart;

public class HelperView {
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public static Dialog configDialog(int dialogLayoutID, Activity activity, boolean clickManagment){
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogLayoutID);
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        window.setGravity(Gravity.CENTER);

        if(clickManagment) {
            try{
                ViewGroup main = dialog.findViewById(R.id.main);
                ViewGroup root = dialog.findViewById(R.id.root);

                main.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        return;
                    }
                });

                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }catch (Exception e){
                Log.i("LOGGG","undefined view groups ID");
            }
        }
        return dialog;
    }

}

