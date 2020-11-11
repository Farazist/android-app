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
import project.helper.HelperString;


public class FragmentMainOwner extends Fragment {

    public static TextView txtRequestCount;
    public static TextView txtFarazistBalance;

    private static ViewPager viewPager;
    private static CirclePageIndicator indicator;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private static Timer swipeTimer;

    private static ArrayList<String> sliderUrls;
    private boolean canUpdateSlider;

    public static Thread deliveryThread;
    public static Thread ticketThread;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_owner, container, false);
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        viewPager = (ViewPager) rootView.findViewById(R.id.imagePager);
        indicator = (CirclePageIndicator) rootView.findViewById(R.id.imageIndicator);
        TextView txtWallet = (TextView) rootView.findViewById(R.id.txtWallet);
        ImageView imgDelivery = (ImageView) rootView.findViewById(R.id.imgDelivery);
        txtRequestCount = (TextView) rootView.findViewById(R.id.txtRequestCount);


        txtRequestCount.setText("0");
        startGetDeliveryCount();
        startGetTicketsCount();
        getSlideImages(true);

        final CardView itemWallet = (CardView) rootView.findViewById(R.id.itemWallet);
        txtFarazistBalance = (TextView) rootView.findViewById(R.id.txtFarazistBalance);
        final CardView itemDelivery = (CardView) rootView.findViewById(R.id.itemDelivery);
        final CardView itemChargingCitizenWallet = (CardView) rootView.findViewById(R.id.itemChargingCitizenWallet);
        final CardView item5 = (CardView) rootView.findViewById(R.id.item5);
        final CardView item6 = (CardView) rootView.findViewById(R.id.item6);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            itemWallet.setBackgroundResource(R.drawable.items_ripple);
            itemDelivery.setBackgroundResource(R.drawable.items_ripple);
            itemChargingCitizenWallet.setBackgroundResource(R.drawable.items_ripple);
            item5.setBackgroundResource(R.drawable.items_ripple);
            item6.setBackgroundResource(R.drawable.items_ripple);
        } else {
            itemWallet.setBackgroundResource(R.drawable.items_selection);
            itemDelivery.setBackgroundResource(R.drawable.items_selection);
            itemChargingCitizenWallet.setBackgroundResource(R.drawable.items_selection);
            item5.setBackgroundResource(R.drawable.items_selection);
            item6.setBackgroundResource(R.drawable.items_selection);
        }

        txtFarazistBalance.setText(HelperString.convertToNumberFormat(App.account.wallet + "") + "  " + App.getContext().getString(R.string.tooman));


        View.OnClickListener soonListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CustomToast.showToast(getString(R.string.comingSoon));
            }
        };


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
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_SYSTEM_DELIVERIES_LIST_ID)
                        .addFragment(R.id.layoutContainer, new FragmentSystemDeliveriesList(), true)
                        .commit();
            }
        });

        itemChargingCitizenWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_CHARGING_CITIZEN_WALLET_ID)
                        .addFragment(R.id.layoutContainer, new FragmentChargingCitizenWallet(), true)
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
                if (currentFragment instanceof FragmentMainOwner) {
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

    private void init() {
        viewPager.setAdapter(new AdapterSlider(App.getCurrentActivity(), sliderUrls));
        indicator.setViewPager(viewPager);
        indicator.setFillColor(Color.parseColor(App.getContext().getString(R.color.colorPrimary)));
        final float density = getResources().getDisplayMetrics().density;

        indicator.setRadius(3 * density);
        NUM_PAGES = sliderUrls.size();
        final Runnable Update = new Runnable() {
            public void run() {
                if (!canUpdateSlider) {
                    return;
                }
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };
        if (swipeTimer == null) {
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

    public static void updateRequestsCount(int count) {
        txtRequestCount.setText(count + "");
    }

    private void startGetTicketsCount() {
        if (ticketThread != null) {
            getNewTicketsCount();
            return;
        }
        ticketThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (App.enableThreads) {
                    try {
                        App.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                App.getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        getNewTicketsCount();
                                    }
                                });

                            }
                        });

                        Thread.sleep(1 * 60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ticketThread = null;
            }
        });
        ticketThread.start();

    }

    public static void updateNewTicketCount(int count) {
        ActivityMain.updateTicketCount(count);
    }

    private void startGetDeliveryCount() {
        if (deliveryThread != null) {
            getDeliveriesCount(true);
            return;
        }
        deliveryThread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean isFirstTime = true;
                while (App.enableThreads) {
                    try {
                        final boolean finalIsFirstTime = isFirstTime;
                        App.getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                getDeliveriesCount(finalIsFirstTime);
                            }
                        });

                        Thread.sleep(1 * 60 * 1000);
                        isFirstTime = false;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                deliveryThread = null;
            }
        });
        deliveryThread.start();
    }


    private void getNewTicketsCount() {
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String count) {
                ActivityMain.updateTicketCount(Integer.valueOf(count));
            }

            @Override
            public void onFail(String error) {
            }
        }).checkNewTicketsCount();
    }

    public static void getDeliveriesCount(boolean isFirstTime) {
        if (isFirstTime) {
            txtRequestCount.setVisibility(View.GONE);
        }
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String count) {
                txtRequestCount.setVisibility(View.VISIBLE);
                txtRequestCount.setText(count);

            }

            @Override
            public void onFail(String error) {
            }
        }).getSystemDeliveriesCount(App.ownerSystems.get(0).id);
    }


    private void getSlideImages(final boolean init) {
        canUpdateSlider = false;
        sliderUrls = new ArrayList<>();
        sliderUrls.clear();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONArray slideArray = new JSONArray(data);
                    for (int i = 0; i < slideArray.length(); i++) {
                        JSONObject slideObject = slideArray.getJSONObject(i);
                        String url = slideObject.getString("image");
                        url = App.SERVER_ADDRESS + url;
                        sliderUrls.add(url);
                    }
                    canUpdateSlider = true;
                    if (init) {
                        init();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String error) {
            }
        }).getSystemSliders(App.ownerSystems.get(0).id);
    }
}
