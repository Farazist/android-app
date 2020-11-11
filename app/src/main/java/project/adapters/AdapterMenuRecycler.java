package project.adapters;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.custom.CustomToast;
import project.fragments.FragmentConnectedSystemInfo;
import project.fragments.FragmentContactManagement;
import project.fragments.FragmentContactUs;
import project.fragments.FragmentEditAccount;
import project.fragments.FragmentFAQs;
import project.fragments.FragmentSystemSettings;
import project.helper.HelperFragment;
import project.structures.StructMenu;

public class AdapterMenuRecycler extends RecyclerView.Adapter<AdapterMenuRecycler.ViewHolder> {

    private ArrayList<StructMenu> list;

    public AdapterMenuRecycler(ArrayList<StructMenu> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_menu, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final StructMenu item = list.get(position);
        holder.txtTitle.setText(item.title);
        holder.txtIcon.setText(item.icon);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.root.setBackgroundResource(R.drawable.menu_button_ripple);
        }else{
            holder.root.setBackgroundResource(R.drawable.menu_button_selection);
        }

        if(item.title.equals(App.getContext().getString(R.string.contactManagement))){
            if(item.count>0) {
                holder.txtCount.setVisibility(View.VISIBLE);
                holder.txtCount.setText(""+item.count);
            }else{
                holder.txtCount.setVisibility(View.GONE);
            }
        }else{
            holder.txtCount.setVisibility(View.GONE);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityMain.drawerLayout.closeDrawer(Gravity.RIGHT);
                String title = item.title;
                if(title.equals(App.getContext().getString(R.string.invideFriends))){
                    share();
                }

                else if(title.equals(App.getContext().getString(R.string.systemInfo))){
                    FragmentConnectedSystemInfo frag = (FragmentConnectedSystemInfo) ActivityMain.fragmentManager.findFragmentByTag(App.FRAGMENT_SYSTEM_INFO_ID);
                    if (frag != null && frag.isVisible()) {
                        return;
                    }
                    new HelperFragment()
                            .addToBackStack(App.FRAGMENT_SYSTEM_INFO_ID)
                            .addFragment(R.id.fragmentContainer, new FragmentConnectedSystemInfo(), true)
                            .commit();
                }

                else if(title.equals(App.getContext().getString(R.string.contactUs))){
                    FragmentContactUs frag = (FragmentContactUs) ActivityMain.fragmentManager.findFragmentByTag(App.FRAGMENT_CONTACT_US_ID);
                    if (frag != null && frag.isVisible()) {
                        return;
                    }
                    new HelperFragment()
                            .addToBackStack(App.FRAGMENT_CONTACT_US_ID)
                            .addFragment(R.id.fragmentContainer, new FragmentContactUs(), true)
                            .commit();
                }

                else if(title.equals(App.getContext().getString(R.string.commonQuestions))){
                    FragmentFAQs frag = (FragmentFAQs) ActivityMain.fragmentManager.findFragmentByTag(App.FRAGMENT_COMMON_QUESTIONS_ID);
                    if (frag != null && frag.isVisible()) {
                        return;
                    }
                    new HelperFragment()
                            .addToBackStack(App.FRAGMENT_COMMON_QUESTIONS_ID)
                            .addFragment(R.id.fragmentContainer, new FragmentFAQs(), true)
                            .commit();
                }

                else if(title.equals(App.getContext().getString(R.string.accountEdit))){
                    FragmentEditAccount frag = (FragmentEditAccount) ActivityMain.fragmentManager.findFragmentByTag(App.FRAGMENT_EDIT_ACCOUNT_ID);
                    if (frag != null && frag.isVisible()) {
                        return;
                    }
                    new HelperFragment()
                            .addToBackStack(App.FRAGMENT_EDIT_ACCOUNT_ID)
                            .addFragment(R.id.fragmentContainer, new FragmentEditAccount(), true)
                            .commit();
                }

                else if(title.equals(App.getContext().getString(R.string.exit))){
                    ActivityMain.showExitDialog(App.getContext().getString(R.string.doYouWantToExitFromAccount));
                }

                else if(title.equals(App.getContext().getString(R.string.contactManagement))){
                    FragmentContactManagement frag = (FragmentContactManagement) ActivityMain.fragmentManager.findFragmentByTag(App.FRAGMENT_CONTACT_MANAGEMENT_ID);
                    if (frag != null && frag.isVisible()) {
                        return;
                    }
                    new HelperFragment()
                            .addToBackStack(App.FRAGMENT_CONTACT_MANAGEMENT_ID)
                            .addFragment(R.id.fragmentContainer, new FragmentContactManagement(), true)
                            .commit();
                }

                else if(title.equals(App.getContext().getString(R.string.setting))){
                    FragmentSystemSettings frag = (FragmentSystemSettings) ActivityMain.fragmentManager.findFragmentByTag(App.FRAGMENT_SETTING_ID);
                    if (frag != null && frag.isVisible()) {
                        return;
                    }
                    new HelperFragment()
                            .addToBackStack(App.FRAGMENT_SETTING_ID)
                            .addFragment(R.id.fragmentContainer, new FragmentSystemSettings(), true)
                            .commit();
                }
                else if(title.equals(App.getContext().getString(R.string.appGuide))){
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://farazist.ir/help/"));
                        App.getCurrentActivity().startActivity(browserIntent);
                    }catch (Exception e){

                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup root;
        public TextView txtTitle;
        public TextView txtIcon;
        public TextView txtSeperator;
        public TextView txtCount;

        public ViewHolder(View view) {
            super(view);
            root = (ViewGroup) view.findViewById(R.id.root);
            txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            txtIcon = (TextView) view.findViewById(R.id.txtIcon);
            txtCount = (TextView) view.findViewById(R.id.txtCount);
            txtSeperator = (TextView) view.findViewById(R.id.txtSeperator);
        }
    }

    private void share() {
        try {
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setType("text/plain");
            share.putExtra(Intent.EXTRA_TEXT, ActivityMain.inviteText);
            App.getCurrentActivity().startActivity(Intent.createChooser(share, "Share to"));
        } catch (Exception e) {
            CustomToast.showToast(App.getContext().getString(R.string.unsuccessfulAct));
        }
    }

}
