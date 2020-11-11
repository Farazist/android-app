package project.adapters;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.util.ArrayList;

import androidx.viewpager.widget.PagerAdapter;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;


public class AdapterSlider extends PagerAdapter {


    private ArrayList<String> urls;
    private LayoutInflater inflater;
    private Context context;


    public AdapterSlider(Context context, ArrayList<String> urls) {
        this.context = context;
        this.urls = urls;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        View imageLayout = inflater.inflate(R.layout.struct_slider, view, false);

        final ImageView imageView = (ImageView) imageLayout.findViewById(R.id.image);

        imageView.setImageResource(0);

      Picasso.with(App.getCurrentActivity()).load(urls.get(position)).into(imageView, new Callback() {
          @Override
          public void onSuccess() {
          }

          @Override
          public void onError() {
          }
      });

//        Glide.with(context)
//                .load(urls[position])
//                .into(imageView);

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}