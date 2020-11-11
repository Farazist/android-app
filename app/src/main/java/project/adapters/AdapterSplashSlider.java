package project.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;
import ir.farazist.farazist_app.R;

public class AdapterSplashSlider extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    private ArrayList<Integer> images;
    private ArrayList<String> titles;
    private ArrayList<String> descs;

    public AdapterSplashSlider(Context context){
        this.context = context;
    }

    public AdapterSplashSlider images(@LayoutRes ArrayList<Integer> imagesId){
        this.images = imagesId;
        return this;
    }

    public AdapterSplashSlider titles(ArrayList<String> titles){
        this.titles = titles;
        return this;
    }

    public AdapterSplashSlider descs(ArrayList<String> descs){
        this.descs = descs;
        return this;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (LinearLayout)object;
    }

    @Override
    public int getCount() {
        if(images.size() == titles.size() && titles.size() == descs.size()){
            return images.size();
        }
        return 0;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View root = layoutInflater.inflate(R.layout.struct_splash,container,false);

        ImageView imgSlide = (ImageView)root.findViewById(R.id.imgSlide);
        TextView txtTitle = (TextView)root.findViewById(R.id.txtTitle);
        TextView txtDesc = (TextView)root.findViewById(R.id.txtDesc);

        imgSlide.setImageResource(images.get(position));
        txtTitle.setText(titles.get(position)+"");
        txtDesc.setText(descs.get(position)+"");

        container.addView(root);

        return root;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ViewGroup)object);
    }
}
