package project.custom;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

public class CustomToast {

    private static Toast customToast = new Toast(App.getCurrentActivity());

    public static void showToast(String message){
        View toastView = App.getLayoutInflater().inflate(R.layout.custom_toast,(ViewGroup)App.getCurrentActivity().findViewById(R.id.toastRoot));
        TextView txtToast = (TextView) toastView.findViewById(R.id.txtToast);
        txtToast.setText(message);
        customToast.cancel();
        customToast = new Toast(App.getContext());
        customToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setView(toastView);
        customToast.show();
    }

    public static void hideToast(){
        customToast.cancel();
    }

}
