package project.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import project.activities.ActivityMain;
import project.helper.HelperString;

public class FragPackageRequestStepFour extends Fragment {


    private int id = 0;
    private String date ;
    private String time ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_package_request_step_four,container,false);
        id = getArguments().getInt("ID");
        String dateTime = getArguments().getString("DATE_TIME");
        date = HelperString.getTransformedDate(dateTime.split(" ")[0]);
        time = HelperString.getTransformedTime(dateTime.split(" ")[1]);

        Button btnOk = (Button)root.findViewById(R.id.btnOk);
        TextView txtTime = (TextView)root.findViewById(R.id.txtTime);
        TextView txtDate = (TextView)root.findViewById(R.id.txtDate);
        TextView txtId = (TextView)root.findViewById(R.id.txtId);

        App.wastes.clear();
        FragmentPackageRequestCustom.clearList();

        txtDate.setText(date);
        txtTime.setText(time);
        txtId.setText(id+"");

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PagerFragNewPackageRequest.step = 1;
                ActivityMain.fragmentManager.popBackStack();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PagerFragNewPackageRequest.step = 1;
    }
}
