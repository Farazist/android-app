package project.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.viewpager.widget.ViewPager;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.adapters.AdapterSplashSlider;

public class ActivitySplash extends CAppCompatActivity {

    private ViewPager slider ;
    private LinearLayout bottomBar;
    private TextView[] dots;

    ArrayList<Integer> headerImages ;
    ArrayList<String> titles ;
    ArrayList<String> descs ;

    private Button btnNext;
    private Button btnPrevious;
    private int currentPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        slider = (ViewPager)findViewById(R.id.slider);
        bottomBar = (LinearLayout)findViewById(R.id.bottomBar);

        btnNext = (Button)findViewById(R.id.btnNext);
        btnPrevious = (Button)findViewById(R.id.btnPrevious);

        headerImages = new ArrayList<>();
        titles = new ArrayList<>();
        descs = new ArrayList<>();

        btnPrevious.setEnabled(false);

        headerImages.add(R.drawable.fara);
        headerImages.add(R.drawable.bottle);
        headerImages.add(R.drawable.box);
        headerImages.add(R.drawable.wallet);
        headerImages.add(R.drawable.recycle);

        titles.add(App.getContext().getString(R.string.splashTitle_1));
        titles.add(App.getContext().getString(R.string.splashTitle_2));
        titles.add(App.getContext().getString(R.string.splashTitle_3));
        titles.add(App.getContext().getString(R.string.splashTitle_4));
        titles.add(App.getContext().getString(R.string.splashTitle_5));

        descs.add(App.getContext().getString(R.string.splashDesc_1));
        descs.add(App.getContext().getString(R.string.splashDesc_2));
        descs.add(App.getContext().getString(R.string.splashDesc_3));
        descs.add(App.getContext().getString(R.string.splashDesc_4));
        descs.add(App.getContext().getString(R.string.splashDesc_5));

        AdapterSplashSlider adapterSlider =
                new AdapterSplashSlider(App.getContext())
                        .images(headerImages).titles(titles).descs(descs);


        slider.addOnPageChangeListener(listener);
        addDotIndicator(0);
        slider.setAdapter(adapterSlider);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btnNext.getText().toString().equals(App.getContext().getString(R.string.start))) {
                    SharedPreferences.Editor editor = App.getPreferences().edit();
                    editor.putBoolean("WELCOME",false);
                    editor.commit();
                    Intent intent = new Intent(App.getCurrentActivity(),ActivityStart.class);
                    startActivity(intent);
                    finish();
                } else {
                    slider.setCurrentItem(currentPosition + 1, true);
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slider.setCurrentItem(currentPosition-1,true);
            }
        });

    }

    private void addDotIndicator(int position){
        dots = new TextView[headerImages.size()];
        bottomBar.removeAllViews();
        for(int i=0;i<dots.length;i++){
            dots[i] = new TextView(App.getContext());
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(40);
            dots[i].setTextColor(Color.parseColor("#3328A746"));
            bottomBar.addView(dots[i]);
        }
        if(dots.length>0){
            dots[position].setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            currentPosition = i;
            addDotIndicator(i);
            if(currentPosition == 0){
                btnNext.setEnabled(true);
                btnPrevious.setEnabled(false);
                btnNext.setText(App.getCurrentActivity().getString(R.string.next));
            }else if(currentPosition == headerImages.size()-1){
                btnNext.setText(App.getCurrentActivity().getString(R.string.start));
                btnPrevious.setEnabled(true);
            }else{
                btnNext.setEnabled(true);
                btnPrevious.setEnabled(true);
                btnNext.setText(App.getCurrentActivity().getString(R.string.next));
                btnNext.setVisibility(View.VISIBLE);
                btnPrevious.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };


}
