package project.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

import project.activities.ActivityMain;
import project.adapters.AdapterSlider;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperFragment;


public class FragmentMainClient extends Fragment {

    private static ViewPager viewPager;
    private static CirclePageIndicator indicator;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static Timer swipeTimer;

    private static ArrayList<String> sliderUrls;
    private static boolean canUpdateSlider;

    private static AdapterSlider adapterSlider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_client, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        viewPager = (ViewPager) rootView.findViewById(R.id.imagePager);
        indicator = (CirclePageIndicator) rootView.findViewById(R.id.imageIndicator);
        TextView txtWallet = (TextView) rootView.findViewById(R.id.txtWallet);
        TextView txtDelivery = (TextView) rootView.findViewById(R.id.txtDelivery);
        ImageView imgDelivery = (ImageView) rootView.findViewById(R.id.imgDelivery);


        txtDelivery.setText(App.getContext().getString(R.string.packageRequest));
        txtWallet.setText(App.getContext().getString(R.string.wallet));
        imgDelivery.setImageResource(R.drawable.delivery);

        getSlideImages();


        final CardView itemWallet = (CardView) rootView.findViewById(R.id.itemWallet);
        final CardView itemDelivery = (CardView) rootView.findViewById(R.id.itemDelivery);
        final CardView item3 = (CardView) rootView.findViewById(R.id.item3);
        final CardView item4 = (CardView) rootView.findViewById(R.id.item4);
        final CardView item5 = (CardView) rootView.findViewById(R.id.item5);
        final CardView item6 = (CardView) rootView.findViewById(R.id.item6);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemWallet.setBackgroundResource(R.drawable.items_ripple);
            itemDelivery.setBackgroundResource(R.drawable.items_ripple);
            item3.setBackgroundResource(R.drawable.items_ripple);
            item4.setBackgroundResource(R.drawable.items_ripple);
            item5.setBackgroundResource(R.drawable.items_ripple);
            item6.setBackgroundResource(R.drawable.items_ripple);
        } else {
            itemWallet.setBackgroundResource(R.drawable.items_selection);
            itemDelivery.setBackgroundResource(R.drawable.items_selection);
            item3.setBackgroundResource(R.drawable.items_selection);
            item4.setBackgroundResource(R.drawable.items_selection);
            item5.setBackgroundResource(R.drawable.items_selection);
            item6.setBackgroundResource(R.drawable.items_selection);
        }


        View.OnClickListener soonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomToast.showToast(getString(R.string.comingSoon));
            }
        };

        item3.setOnClickListener(soonListener);
        item4.setOnClickListener(soonListener);
        item5.setOnClickListener(soonListener);
        item6.setOnClickListener(soonListener);

        itemWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_WALLET_ID)
                        .addFragment(R.id.layoutContainer, new FragmentWallet(), true)
                        .commit();
            }
        });

        itemDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(App.account.role !=2 && App.account.systemID==0){
                    CustomToast.showToast(App.getContext().getString(R.string.connectToSystemForPackageRequest));
                    return;
                }
                FragmentPackageRequestCustom.clearList();
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_DELIVERY_INFO_ID)
                        .addFragment(R.id.layoutContainer, new FragmentDeliveryInfo(), true)
                        .commit();
            }
        });

        rootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (ActivityMain.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    ActivityMain.drawerLayout.closeDrawer(Gravity.RIGHT);
                    return true;
                }
                Fragment currentFragment = ActivityMain.fragmentManager.findFragmentById(R.id.fragmentContainer);
                if (currentFragment instanceof FragmentMainClient) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        ActivityMain.showExitDialog(getString(R.string.doYouWantExit));
                        return true;
                    }
                }
                return false;
            }
        });

        return rootView;
    }

    private static void initSlider( ) {
        adapterSlider = new AdapterSlider(App.getCurrentActivity(), sliderUrls);
        viewPager.setAdapter(adapterSlider);
        indicator.setViewPager(viewPager);
        indicator.setFillColor(Color.parseColor(App.getContext().getString(R.color.colorPrimary)));
        final float density = App.getContext().getResources().getDisplayMetrics().density;

//Set circle indicator radius
        indicator.setRadius(3 * density);
        NUM_PAGES = sliderUrls.size();
        // Auto start of viewpager
        if (swipeTimer == null) {
            final Runnable Update = new Runnable() {
                public void run() {
                    if(!canUpdateSlider){
                        return;
                    }
                    if (currentPage == NUM_PAGES) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            };

            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    App.getHandler().post(Update);
                }
            }, 3000, 3000);
        }




        // Pager listener over indicator
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

            }

            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int pos) {

            }
        });


    }

    public static void getSlideImages() {
        canUpdateSlider = false;
        sliderUrls = new ArrayList<>();
        sliderUrls.clear();
        int systemId = 0;
        if (App.account.role == 2) {
            systemId = App.ownerSystems.get(0).id;
        } else {
            systemId = App.clientSystem.id;
        }
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONArray slideArray = new JSONArray(data);
                    for (int i = 0; i < slideArray.length(); i++) {
                        JSONObject slideObject = slideArray.getJSONObject(i);
                        String url = slideObject.getString("image");
                        url = App.SERVER_ADDRESS+url;
                        sliderUrls.add(url);
                    }
                    canUpdateSlider = true;
                    initSlider();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String error) {
            }
        }).getSystemSliders(systemId);
    }

}
