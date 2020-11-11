package project.helper;

import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.RawRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.activities.Login;
import project.custom.CustomToast;
import project.fragments.FragmentTransactions;
import project.fragments.FragmentWallet;

public class HelperFragment {

    private FragmentTransaction fragmentTransaction;
    private Fragment fragment;
    private View.OnKeyListener listener;
    private String tag = "";
    private int containerId = R.id.fragmentContainer;

    public HelperFragment addFragment(@LayoutRes int id, Fragment fragment , boolean withAnimation) {
        if(fragmentTransaction == null){
            fragmentTransaction = ActivityMain.fragmentManager.beginTransaction();
        }
        this.fragment = fragment;
        if(tag!=null && tag.length()>0) {
            fragmentTransaction.addToBackStack(tag);
        }
        if(withAnimation){
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
        }
        fragmentTransaction.add(containerId, fragment, tag);
        return this;

    }

    public HelperFragment replaceFragment(@LayoutRes int id, Fragment fragment , boolean withAnimation) {
        if(fragmentTransaction == null){
            fragmentTransaction = ActivityMain.fragmentManager.beginTransaction();
        }
        this.fragment = fragment;
        if(tag!=null && tag.length()>0) {
            fragmentTransaction.addToBackStack(tag);
        }
        if(withAnimation){
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
        }
        fragmentTransaction.replace(containerId, fragment, tag);

        return this;

    }

    public HelperFragment addToBackStack(String tag) {
        this.tag = tag;
        return this;

    }


    public void pop(String tag) {
        ActivityMain.fragmentManager.popBackStack(tag, ActivityMain.fragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public HelperFragment setContainer(@LayoutRes int containerId){
        this.containerId = containerId;
        return this;
    }

    public HelperFragment setFragmentTransaction(FragmentTransaction fragmentTransaction){
        this.fragmentTransaction = fragmentTransaction;
        return this;
    }
    public void commit() {
        fragmentTransaction.commit();
    }


}
