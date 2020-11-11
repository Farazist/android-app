package project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperString;

public class FragmentSystemDetails extends Fragment {

    private ViewGroup loader;
    private ViewGroup layoutDetails;
    private int id;
    private TextView txtAddress;
    private TextView txtName;
    private TextView txtDate;
    private ImageView imgCover;
    private Button btnRegister;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_system_details, container, false);

        layoutDetails = root.findViewById(R.id.layoutDetails);
        loader = root.findViewById(R.id.loader);
        txtAddress = (TextView) root.findViewById(R.id.txtAddress);
        txtName = (TextView) root.findViewById(R.id.txtTitle);
        txtDate = (TextView) root.findViewById(R.id.txtDate);
        imgCover = (ImageView) root.findViewById(R.id.img);
        btnRegister = (Button) root.findViewById(R.id.btnRegister);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);

        id = getArguments().getInt("ID");
        getSystemDetails();

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
        return root;
    }

    private void getSystemDetails() {
        loader.setVisibility(View.VISIBLE);
        layoutDetails.setVisibility(View.GONE);
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    loader.setVisibility(View.GONE);
                    layoutDetails.setVisibility(View.VISIBLE);
                    JSONObject system = new JSONObject(data);
                    String name = system.getString("name");
                    txtName.setText(name);
                    String coverUrl = App.SERVER_ADDRESS + system.getString("image");
                    Picasso.with(App.getContext()).load(coverUrl).into(imgCover);
                    String address = system.getString("address");
                    txtAddress.setText(address);
                    String date = system.getString("updated_at");

                    if ( date!= null && !date.equals("null") ) {
                        date = system.getString("updated_at").split(" ")[0];
                        txtDate.setText(HelperString.getTransformedDate(date));
                    }

                } catch (Exception e) {
                    CustomToast.showToast(getString(R.string.oldVersionError));
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(getString(R.string.connectionError));
            }
        }).getSystemDetails(id);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    App.canBackPress = false;
                    btnRegister.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                        @Override
                        public void onComplete(String data) {
                            getUserInfo();
                        }

                        @Override
                        public void onFail(String error) {
                            CustomToast.showToast(getString(R.string.connectionError));
                            btnRegister.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            App.canBackPress = true;
                        }
                    }).editUserSystem(id);
                }catch (Exception e){
                    App.canBackPress = true;
                    btnRegister.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                }

            }
        });
    }


    private void getUserInfo() {
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                App.canBackPress = true;
                CustomToast.showToast(getString(R.string.accountConnectedToSystem));
                ActivityMain.fragmentManager.popBackStack();
                ActivityMain.fragmentManager.popBackStack();
                ActivityMain.fragmentManager.popBackStack();
            }

            @Override
            public void onFail(String error) {
                App.canBackPress = true;
                CustomToast.showToast(getString(R.string.connectionError));
                btnRegister.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }).getUserInfo();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            ActivityMain.fragmentManager.popBackStack();
            ActivityMain.fragmentManager.popBackStack();
        }catch (Exception e){

        }

    }
}
