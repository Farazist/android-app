package project.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.adapters.AdapterPagerSlider;
import project.connection.Commands;
import project.custom.CustomToast;

public class FragmentDeliveryInfo extends Fragment {
    private ViewPager slidingPager;
    private TabLayout tabLayout;
    private AdapterPagerSlider adapter;
    private TextView txtLoading;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_delivery_info,container,false);
        slidingPager = (ViewPager) root.findViewById(R.id.slidingPager);
        txtLoading = (TextView)root.findViewById(R.id.txtLoading);
        slidingPager.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) root.findViewById(R.id.tabLayout);
        adapter = new AdapterPagerSlider(getChildFragmentManager());

        getLastRequestSeen();
       // adapter.addFragment(new PagerFragCurrentPackageRequest(), getString(R.string.currentPackageRequeste));



        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
        return root;
    }

    private void getLastRequestSeen(){
        txtLoading.setVisibility(View.VISIBLE);
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONObject request = new JSONObject(data);
                    int seen = request.getInt("seen");

                    adapter.addFragment(new PagerFragOldPackageRequest(), App.getContext().getString(R.string.oldPackageRequestes));
                    if(seen == 1){
                        adapter.addFragment(new PagerFragNewPackageRequest(), App.getContext().getString(R.string.requestNewPackge));
                    }else{
                        adapter.addFragment(new PagerFragCurrentPackageRequest(), App.getContext().getString(R.string.requestNewPackge));
                    }
                } catch (Exception e) {
                    adapter.addFragment(new PagerFragOldPackageRequest(), App.getContext().getString(R.string.oldPackageRequestes));
                    adapter.addFragment(new PagerFragNewPackageRequest(), App.getContext().getString(R.string.requestNewPackge));
                    e.printStackTrace();
                }finally {
                    txtLoading.setVisibility(View.GONE);
                    slidingPager.setAdapter(adapter);
                    slidingPager.setCurrentItem(adapter.getNumberOfItems() - 1);
                    tabLayout.setupWithViewPager(slidingPager, true);
                }

            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                ActivityMain.fragmentManager.popBackStack();
            }
        }).getLastUserDelivery();
    }
}
