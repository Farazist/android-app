package project.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.adapters.AdapterContactRecycler;
import project.adapters.AdapterSlider;
import project.connection.Commands;
import project.custom.CustomToast;
import project.structures.StructContactUs;

public class FragmentContactUs extends Fragment {

    private static ViewPager viewPager;
    private static CirclePageIndicator indicator;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private Timer swipeTimer;

    private ArrayList<String> sliderUrls;
    private boolean canUpdateSlider;

    private static AdapterSlider adapterSlider;
    private TextView txtDesc;


    private RecyclerView lstContact;
    private ViewGroup layoutContainer;
    private ViewGroup loader;
    private static ArrayList<StructContactUs> contacts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact_us, container, false);

        loader = root.findViewById(R.id.loader);
        lstContact = root.findViewById(R.id.lstContact);
        lstContact.setLayoutManager(new LinearLayoutManager(App.getContext()));
        lstContact.setNestedScrollingEnabled(false);
        layoutContainer = root.findViewById(R.id.layoutContainer);
        sliderUrls = new ArrayList<>();
        viewPager = (ViewPager) root.findViewById(R.id.imagePager);
        indicator = (CirclePageIndicator) root.findViewById(R.id.imageIndicator);

        txtDesc = root.findViewById(R.id.txtDesc);
        ImageView imgInstagram = (ImageView) root.findViewById(R.id.imgInstagram);
        ImageView imgAparat = (ImageView) root.findViewById(R.id.imgAparat);
        ImageView imgTelegram = (ImageView) root.findViewById(R.id.imgTelegram);
        loading();
        contacts = new ArrayList<>();
        final AdapterContactRecycler adapter = new AdapterContactRecycler(contacts);
        lstContact.setAdapter(adapter);
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    contacts.clear();
                    JSONArray connections = new JSONArray(data);
                    for (int i = 0; i < connections.length(); i++) {
                        JSONObject connection = connections.getJSONObject(i);
                        StructContactUs item = new StructContactUs();
                        item.id = connection.getInt("id");
                        item.type = connection.getString("type");
                        item.name = connection.getString("name");
                        item.value = connection.getString("value");
                        if (item.type.contains("share_text")) {
                            continue;
                        } else if (item.type.toLowerCase().contains("slide")) {
                            String imgUrl = App.SERVER_ADDRESS + item.value;
                            sliderUrls.add(imgUrl);
                        } else if (item.type.toLowerCase().contains("desc")) {
                            txtDesc.setText(item.value);
                        } else {
                            contacts.add(item);
                        }

                    }
                    initSlider();
                    adapter.notifyDataSetChanged();
                    loaded();
                } catch (Exception e) {
                    loaded();
                    e.printStackTrace();
                    CustomToast.showToast(getString(R.string.oldVersionError));
                }
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(getString(R.string.connectionError));
                return;
            }
        }).getInformations();


        imgInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Uri uri = Uri.parse("https://instagram.com/_u/farazist.ir_/");
                    Intent likeIng = new Intent(Intent.ACTION_VIEW, uri);
                    likeIng.setPackage("com.instagram.android");

                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.instagram.com/farazist.ir_/")));
                    }catch (Exception e2){

                    }
                }
            }
        });

        imgAparat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.aparat.com/faramarz.co"));
                    startActivity(browserIntent);
                }catch (Exception e){

                }
            }
        });

        imgTelegram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/Eco_Farazist"));
                    startActivity(telegram);
                }catch (Exception e){

                }
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

    private void loading() {
        loader.setVisibility(View.VISIBLE);
        layoutContainer.setVisibility(View.GONE);
    }

    private void loaded() {
        loader.setVisibility(View.GONE);
        layoutContainer.setVisibility(View.VISIBLE);
    }

    private void initSlider() {
        adapterSlider = new AdapterSlider(App.getCurrentActivity(), sliderUrls);
        viewPager.setAdapter(adapterSlider);
        indicator.setViewPager(viewPager);
        indicator.setFillColor(Color.parseColor(App.getContext().getString(R.color.colorPrimary)));
        final float density = App.getContext().getResources().getDisplayMetrics().density;

        indicator.setRadius(3 * density);
        NUM_PAGES = sliderUrls.size();
        // Auto start of viewpager
        if (swipeTimer == null) {
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

            swipeTimer = new Timer();
            swipeTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    App.getHandler().post(Update);
                }
            }, 3000, 3000);
        }

    }
}
