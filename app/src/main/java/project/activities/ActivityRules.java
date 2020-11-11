package project.activities;

import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.helper.HelperFile;

import android.os.Bundle;
import android.widget.TextView;

public class ActivityRules extends CAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        removeActionBar();
        setContentView(R.layout.activity_rules);

        TextView txtRules = (TextView)findViewById(R.id.txtRules);

        String rules = HelperFile.readTextFile(App.getContext(),R.raw.rules);

        txtRules.setText(rules);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
