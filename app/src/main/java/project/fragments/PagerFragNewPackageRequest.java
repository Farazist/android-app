package project.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PagerFragNewPackageRequest extends Fragment {
    public static int step = 1;
    public static FragmentManager childFragmentManager;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pager_frag_new_package_request,container,false);
        FrameLayout fragmentContainer = (FrameLayout)root.findViewById(R.id.fragmentDeliveryContainer);
        final Button btn1 = (Button)root.findViewById(R.id.btn1);
        final Button btn2 = (Button)root.findViewById(R.id.btn2);
        final Button btn3 = (Button)root.findViewById(R.id.btn3);
        final Button btn4 = (Button)root.findViewById(R.id.btn4);

        childFragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.fragmentDeliveryContainer,new FragPackageRequestStepOne(), App.FRAGMENT_PACKAGE_DELIVERY_STEP1_ID);
        fragmentTransaction.commit();

        fragmentContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                btn1.setBackgroundResource(R.drawable.circle_empty);
                btn2.setBackgroundResource(R.drawable.circle_empty);
                btn3.setBackgroundResource(R.drawable.circle_empty);
                btn4.setBackgroundResource(R.drawable.circle_empty);

                btn1.setTextColor(Color.parseColor("#000000"));
                btn2.setTextColor(Color.parseColor("#000000"));
                btn3.setTextColor(Color.parseColor("#000000"));
                btn4.setTextColor(Color.parseColor("#000000"));
                if(step == 1){
                    btn1.setBackgroundResource(R.drawable.circle_fill);
                    btn1.setTextColor(Color.parseColor("#ffffff"));
                }else if(step == 2){
                    btn2.setBackgroundResource(R.drawable.circle_fill);
                    btn2.setTextColor(Color.parseColor("#ffffff"));
                }else if(step == 3){
                    btn3.setBackgroundResource(R.drawable.circle_fill);
                    btn3.setTextColor(Color.parseColor("#ffffff"));
                }else if(step == 4){
                    btn4.setBackgroundResource(R.drawable.circle_fill);
                    btn4.setTextColor(Color.parseColor("#ffffff"));
                }
            }
        });

        return root;
    }
}
