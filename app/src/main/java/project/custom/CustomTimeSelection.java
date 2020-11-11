package project.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import ir.farazist.farazist_app.R;

public class CustomTimeSelection extends LinearLayout {

    public static final String INCREASED = "INCREASED";
    public static final String DECREASED = "DECREASED";
    public static final String NOT_CHANGED = "NOT_CHANGED";
    public onTimeChangedListener listener;
    private final Context context;
    private Button btnUp;
    private Button btnDown;
    private TextView txtCount;
    private int min = 1;
    private int max = 24;

    public CustomTimeSelection(Context context) {
        super(context);
        this.context = context;
        init(context);

    }

    public CustomTimeSelection(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public CustomTimeSelection(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    public CustomTimeSelection(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_time_selection, this, true);

        btnUp = (Button) view.findViewById(R.id.btnUp);
        btnDown = (Button) view.findViewById(R.id.btnDown);
        txtCount = (TextView) view.findViewById(R.id.txtCount);


        btnUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int time = Integer.valueOf(txtCount.getText().toString());
                time++;
                if (time > max) {
                    time = min;
                }
                txtCount.setText(time + "");
                if (listener != null) {
                    listener.onChanged(time, INCREASED);
                }
            }
        });

        btnDown.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int time = Integer.valueOf(txtCount.getText().toString());
                time--;
                if (time < min) {
                    time = max;
                }
                txtCount.setText(time + "");
                if (listener != null) {
                    listener.onChanged(time, DECREASED);
                }
            }
        });
    }

    public void setMin(int min) {
        this.min = Math.min(Math.max(1, min),24);
        int time = Integer.valueOf(txtCount.getText().toString());
        if (time < this.min) {
            txtCount.setText(this.min + "");
        }
    }

    public void setMax(int max) {
        this.max = Math.max(Math.min(24, max),1);
        int time = Integer.valueOf(txtCount.getText().toString());
        if (time > this.max) {
            txtCount.setText(this.max +"");
        }
    }

    public void setTime(int time) {
        int previousTime = Integer.valueOf(txtCount.getText().toString());
        int validTime = 0;
        if (time > max) {
            validTime = max;
        } else if (time < min) {
            validTime = min;
        } else {
            validTime = time;
        }
        txtCount.setText(validTime + "");
        if (listener != null) {
            if (validTime > time){
                listener.onChanged(time,INCREASED);
            }else if(validTime == time){
                listener.onChanged(time,NOT_CHANGED);
            }else{
                listener.onChanged(time,DECREASED);
            }
        }
    }

    public int getMin(){
        return min;
    }
    public int getMax(){
        return max;
    }

    public int getTime(){
        return Integer.valueOf(txtCount.getText().toString());
    }

    public void reset() {
        txtCount.setText(min + "");
    }

    public void setTextColor(@ColorInt int color){
        txtCount.setTextColor(color);
    }

    public void setOnTimeChangedListener(onTimeChangedListener listener) {
        this.listener = listener;
    }

    public interface onTimeChangedListener {
        void onChanged(int time, String changes);
    }

}
