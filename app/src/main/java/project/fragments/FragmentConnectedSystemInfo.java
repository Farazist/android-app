package project.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import pl.droidsonroids.gif.GifImageView;
import project.activities.ActivityMain;
import project.activities.ActivityScanner;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperFragment;

public class FragmentConnectedSystemInfo extends Fragment {
    private boolean isOwner;
    private ViewGroup layoutInfo;
    private LinearLayout layoutState;
    private ViewGroup loader;
    private TextView txtDesc;
    private TextView txtStateWord;
    private TextView txtStateIcon;
    private ImageView imgLogo;
    private GifImageView supportLoader;
    private LinearLayout layoutSupport ;
    private Button btnMobile ;

    private String online = "\uf00c";
    private String offline = "\uf00d";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_connected_system_info, container, false);

        layoutInfo = (ViewGroup) root.findViewById(R.id.layoutInfo);
        layoutSupport = (LinearLayout) root.findViewById(R.id.layoutSupport);
        supportLoader = (GifImageView) root.findViewById(R.id.supportLoader);
        layoutState = (LinearLayout) root.findViewById(R.id.layoutState);
        loader = (ViewGroup) root.findViewById(R.id.loader);
        txtDesc = (TextView) root.findViewById(R.id.txtDesc);
        txtStateIcon = (TextView) root.findViewById(R.id.txtStateIcon);
        txtStateWord = (TextView) root.findViewById(R.id.txtStateWord);
        ConstraintLayout btnScan = (ConstraintLayout) root.findViewById(R.id.btnScan);
        ConstraintLayout btnSearch = (ConstraintLayout) root.findViewById(R.id.btnSearch);
        btnMobile = (Button)root.findViewById(R.id.btnMobile);
        imgLogo = (ImageView) root.findViewById(R.id.imgLogo);

        layoutSupport.setVisibility(View.GONE);
        supportLoader.setVisibility(View.GONE);
        getUserInfo();

        btnMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + btnMobile.getText().toString()));
                    startActivity(intent);
                }catch (Exception e){
                    CustomToast.showToast(getString(R.string.unsuccessfulAct));
                }
            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOwner) {
                    CustomToast.showToast(getString(R.string.clientPanel));
                    return;
                }
                Intent intent = new Intent(App.getCurrentActivity(), ActivityScanner.class);
                intent.putExtra("MODE",ActivityScanner.SYSTEM_CONNECTION);
                App.getContext().startActivity(intent);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isOwner) {
                    CustomToast.showToast(getString(R.string.clientPanel));
                    return;
                }
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_SYSTEMS_ID)
                        .addFragment(R.id.layoutContainer, new FragmentSystemsList(), false)
                        .commit();

            }
        });

        return root;
    }

    private void getOwnerInfo(int ownerID){
        if (App.account.systemID == 0 && App.ownerSystems.size() == 0) {//client not registered
            return;
        }
        supportLoader.setVisibility(View.VISIBLE);
        layoutSupport.setVisibility(View.GONE);
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONObject targetObject = new JSONObject(data);
                    btnMobile.setText(targetObject.getString("mobile_number"));
                    layoutSupport.setVisibility(View.VISIBLE);
                    supportLoader.setVisibility(View.GONE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                supportLoader.setVisibility(View.GONE);
                layoutSupport.setVisibility(View.GONE);
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).getTargetUserInfo(ownerID+"");
    }

    private void getUserInfo() {
        loading();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONObject userObject = new JSONObject(data);
                    int ownerID = userObject.getJSONObject("system").getInt("owner_id");
                    getOwnerInfo(ownerID);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loaded();
                fillInfo();
            }

            @Override
            public void onFail(String error) {
                loaded();
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).getUserInfo();
    }

    private void setState(String state) {
        if (state.equals(App.active)) {
            txtStateWord.setText(getString(R.string.online));
            txtStateIcon.setText(online);
            txtStateIcon.setTextColor(App.getContext().getResources().getColor(R.color.colorAccent));
            txtStateWord.setTextColor(App.getContext().getResources().getColor(R.color.colorAccent));
        } else {
            txtStateWord.setText(getString(R.string.offline));
            txtStateIcon.setText(offline);
            txtStateIcon.setTextColor(Color.parseColor("#a62626"));
            txtStateWord.setTextColor(Color.parseColor("#a62626"));
        }
    }

    private void loaded() {
        loader.setVisibility(View.GONE);
        layoutInfo.setVisibility(View.VISIBLE);
    }

    private void loading() {
        loader.setVisibility(View.VISIBLE);
        layoutInfo.setVisibility(View.GONE);
    }

    private void fillInfo() {
        layoutState.setVisibility(View.GONE);
        if (App.account.systemID == 0 && App.ownerSystems.size() == 0) { // client not registered
            isOwner = false;
            String desc = getString(R.string.notRegisteredInSystem);
            imgLogo.setImageResource(R.drawable.fara_padded);
            txtDesc.setText(desc);

        } else {
            layoutState.setVisibility(View.VISIBLE);
            if (App.account.systemID == 0 && App.ownerSystems.size() > 0) { //owner
                if (App.ownerSystems.get(0).state.equals(App.active)) {
                    setState(App.active);
                } else {
                    setState(App.deactive);
                }
                String coverUrl = App.ownerSystems.get(0).coverUrl;
                if (coverUrl != null && !coverUrl.equals("null") && coverUrl.length() > 0) {
                    Picasso.with(App.getContext()).load(coverUrl).into(imgLogo);
                }
                isOwner = true;
                String desc = "شما مالک دستگاه با مشخصات زیر هستید :";
                desc += "\n";
                desc += "نام دستگاه : ";
                desc += App.ownerSystems.get(0).name;
                desc += "\n";
                desc += "آدرس : ";
                desc += App.ownerSystems.get(0).cityName + "، " + App.ownerSystems.get(0).address;
                txtDesc.setText(desc);
            } else { // client registered
                if (App.clientSystem.state.equals(App.active)) {
                    setState(App.active);
                } else {
                    setState(App.deactive);
                }

                String coverUrl = App.clientSystem.coverUrl;
                if (coverUrl != null && !coverUrl.equals("null") && coverUrl.length() > 0) {
                    Picasso.with(App.getContext()).load(coverUrl).into(imgLogo);
                }

                isOwner = false;
                String desc = "حساب کاربری شما به دستگاهی با مشخصات زیر متصل است :";
                desc += "\n";
                desc += "نام دستگاه : ";
                desc += App.clientSystem.name;
                desc += "\n";
                desc += "آدرس : ";
                desc += App.clientSystem.cityName + "، " + App.clientSystem.address;
                txtDesc.setText(desc);
            }
        }
    }

   public static void ApplyChanges(){

   }
}
