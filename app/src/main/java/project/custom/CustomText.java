package project.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;


import java.util.ArrayList;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

public class CustomText extends LinearLayout {
    float size = 0;

    float width = getWidth();
    float height = getHeight();
    Paint paint;
    private Typeface font;
    private String text;
    private int color_1 = Color.BLACK;
    private int color_2 = Color.BLACK;


    public CustomText(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public CustomText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public CustomText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    public CustomText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        width = getWidth();
        height = getHeight();
        paint = new Paint();
        paint.setTextSize(size);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(size);
        paint.setTextAlign(Paint.Align.CENTER);
        if(font!=null) {
            paint.setTypeface(font);
        }
            Shader shader = new LinearGradient(0, 0, width, height, color_2, color_1, Shader.TileMode.CLAMP);
            paint.setShader(shader);

        canvas.drawText(text, width / 2, height / 2, paint);
    }




    public CustomText setFont(Typeface font) {
        this.font = font;
        return this;
    }

    public CustomText size(float size) {
        this.size = dpToPx(size);
        return this;
    }

    public CustomText text(String text) {
        this.text = text;
        return this;
    }

    public CustomText color(@ColorInt int color_1 , @ColorInt int color_2) {
        this.color_1 = color_1;
        this.color_2 = color_2;
        return this;
    }

    public void commit(){
        postInvalidate();
    }

    private float dpToPx(float dp) {
        float px = dp * getResources().getDisplayMetrics().density;
        return px;
    }
}
