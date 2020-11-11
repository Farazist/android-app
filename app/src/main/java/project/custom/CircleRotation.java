package project.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import ir.farazist.farazist_app.App;

import project.helper.HelperDimensions;

public class CircleRotation extends LinearLayout {

    private int color = Color.BLACK;
    private float strokeWidth = 5;
    private boolean canRotate = false;
    private int startAngle = 10;

    public CircleRotation(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public CircleRotation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public CircleRotation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CircleRotation(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);

        float width = getWidth();
        float height = getHeight();
        float margin = strokeWidth/2;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(margin,margin,width-margin,height-margin,startAngle,170,false,paint);
            canvas.drawArc(margin,margin,width-margin,height-margin,180 + startAngle,170,false,paint);
        }else{
            canvas.drawCircle(width/2,height/2,width/2,paint);
        }


    }

    public void setColor(int color){
        this.color = color;
        postInvalidate();
    }

    public void setWidth(float width){
        this.strokeWidth = HelperDimensions.convertPxToDp(App.getContext(),width);
        postInvalidate();
    }

    public void startRotation(){
        canRotate = true;
        rotate();
    }

    public void stopRotation(){
        canRotate = false;
    }

    private void rotate(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (canRotate){
                    try {
                        Thread.sleep(20);
                        startAngle += 1;
                        startAngle = startAngle%360;
                        postInvalidate();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }


}
