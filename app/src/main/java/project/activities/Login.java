package project.activities;

import androidx.fragment.app.FragmentManager;
import ir.farazist.farazist_app.R;
import project.fragments.FragmentEnterPhoneNumber;
import project.helper.HelperFragment;

import android.os.Bundle;

public class Login extends CAppCompatActivity {
    public static FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        fragmentManager = getSupportFragmentManager();
        new HelperFragment()
                .setFragmentTransaction(fragmentManager.beginTransaction())
                .setContainer(R.id.fragmentLoginSteps)
                .addFragment(R.id.fragmentLoginSteps, new FragmentEnterPhoneNumber(), false)
                .commit();
    }
}
