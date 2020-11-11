package project.custom;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import ir.farazist.farazist_app.R;

public class ProgressButton extends LinearLayout {

    private final Context context;
    private ConstraintLayout root;
    private ProgressBar progressBar;
    private TextView txtTitle;

    private String normalText;
    private String workText;
    private String doneText;

    Animation fadeIn;

    public ProgressButton(Context context) {
        super(context);
        this.context = context;
        init(context);

    }

    public ProgressButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public ProgressButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init(context);
    }

    private void init(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.progress_button,this,true);

        root = (ConstraintLayout)view.findViewById(R.id.root);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        txtTitle = (TextView)view.findViewById(R.id.txtTitle);

        normalText = context.getString(R.string.send);
        workText = context.getString(R.string.pleaseWait);
        doneText = context.getString(R.string.sent);
        reset();

    }

    public void reset(){
        root.setBackground(context.getDrawable(R.drawable.button_login_selection));
        txtTitle.setText(normalText);
        progressBar.setVisibility(View.GONE);
    }

    public void loading(){
        root.setBackground(context.getDrawable(R.drawable.button_login_selection));
        txtTitle.setText(workText);
        progressBar.setVisibility(View.VISIBLE);
    }

    public void loaded(){
        root.setBackground(context.getDrawable(R.drawable.done_shape));
        progressBar.setVisibility(View.GONE);
        txtTitle.setText(doneText);
    }

    public ProgressButton setNormalText(String normalText){
        this.normalText = normalText;
        reset();
        return this;
    }

    public ProgressButton setWorkText(String workText){
        this.workText = workText;
        return this;
    }

    public ProgressButton setDoneText(String doneText){
        this.doneText = doneText;
        return this;
    }


//    ProgressButton(Context context, View view){
//
//        this.context = context;
//        cardView = (CardView)view.findViewById(R.id.cardView);
//        root = (ConstraintLayout)view.findViewById(R.id.root);
//        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
//        txtTitle = (TextView)view.findViewById(R.id.txtTitle);
//    }
//
//    void buttonActivated(){
//        progressBar.setVisibility(View.VISIBLE);
//        txtTitle.setText(context.getString(R.string.pleaseWait));
//    }
//
//    void buttonFinished(){
//        root.setBackgroundColor(cardView.getResources().getColor(R.color.colorAccent));
//        progressBar.setVisibility(View.GONE);
//        txtTitle.setText(context.getString(R.string.send));
//    }

}
