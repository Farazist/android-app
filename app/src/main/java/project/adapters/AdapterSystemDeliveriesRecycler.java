package project.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.fragments.FragmentSystemDeliveriesList;
import project.fragments.FragmentSystemDeliveryDetails;
import project.structures.StructSystemDelivery;

public class AdapterSystemDeliveriesRecycler extends RecyclerView.Adapter<AdapterSystemDeliveriesRecycler.ViewHolder> {

    private ArrayList<StructSystemDelivery> list;

    public static OnStateChangeListener onStateChangeListener = null;

    public interface OnStateChangeListener {
        void onChanged(int position, String state);
    }

    public AdapterSystemDeliveriesRecycler(ArrayList<StructSystemDelivery> list) {
        this.list = list;
        onStateChangeListener = null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_owner_package_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final StructSystemDelivery item = list.get(position);

        if (onStateChangeListener == null) {
            onStateChangeListener = new OnStateChangeListener() {
                @Override
                public void onChanged(int position, String state) {
                    list.get(position).state = state;
                    notifyItemChanged(position, holder.txtState);
                    notifyDataSetChanged();
                    if (state.equals(App.done) || state.equals(App.verification_rejected) || state.equals(App.delivery_rejected)) {
                        list.remove(position);
                        FragmentSystemDeliveriesList.itemDeletedListener.onDeleted();
                        notifyDataSetChanged();
                    }
                }
            };
        }

        if(item.userAvatar.length()>0){
            Picasso.with(App.getContext()).load(item.userAvatar).resize(150,150).into(holder.imgCover);
        }else{
            holder.imgCover.setImageResource(R.drawable.avatar);
        }

        holder.txtDate.setText(item.date);
        holder.txtTime.setText(item.time);
        holder.txtUserName.setText(item.userName);
        if (item.state.equals(App.done)) {
            holder.txtState.setText(App.getContext().getString(R.string.delivery));
        } else if (item.state.equals(App.delivery_rejected) || item.state.equals(App.verification_rejected)) {
            holder.txtState.setText(App.getContext().getString(R.string.reject));
        } else if (item.state.equals(App.accepted)) {
            holder.txtState.setText(App.getContext().getString(R.string.verify));
        } else if (item.state.equals(App.waiting)) {
            holder.txtState.setText(App.getContext().getString(R.string.register));
        } else {
            holder.txtState.setText(App.getContext().getString(R.string.unknown));
        }



        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragDetails = new FragmentSystemDeliveryDetails();
                Bundle extras = new Bundle();
                extras.putInt("ID", position);
                extras.putInt("DELIVERY_ID", item.id);
                extras.putInt("USER_ID", item.userId);
                extras.putString("AVATAR", item.userAvatar);
                extras.putString("NAME", item.userName);
                extras.putString("MOBILE", item.userMobile);
                extras.putString("USER_PROVINCE", item.userProvince);
                extras.putString("USER_CITY", item.userCity);
                extras.putString("USER_ADDRESS", item.userAddress);
                extras.putString("INVOICE", item.invoice);
                extras.putString("ITEMS", item.items.toString());
                extras.putString("CUSTOM_ITEMS", item.customItems.toString());
                extras.putString("DATE", item.date);
                extras.putString("TIME", item.time);
                extras.putInt("PRICE", item.definedItemsPrice);
                extras.putString("STATE", item.state);
                fragDetails.setArguments(extras);
                FragmentTransaction fragmentTransaction = App.getCurrentActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.add(R.id.fragmentContainer, fragDetails, App.FRAGMENT_SYSTEM_DELIVERIES_DETAILS_ID);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView layoutRoot;
        public ImageView imgCover;
        public TextView txtUserName;
        public TextView txtDate;
        public TextView txtTime;
        public TextView txtState;

        public ViewHolder(View view) {
            super(view);
            layoutRoot = (CardView) view.findViewById(R.id.layoutRoot);
            imgCover = (ImageView) view.findViewById(R.id.img);
            txtUserName = (TextView) view.findViewById(R.id.txtUserName);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            txtState = (TextView) view.findViewById(R.id.txtState);
        }
    }


}
