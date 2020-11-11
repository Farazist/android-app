package project.adapters;

import android.graphics.Color;
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
import project.fragments.FragmentUserDeliveryDetails;
import project.structures.StructUserDelivery;

public class AdapterUserDeliveriesRecycler extends RecyclerView.Adapter<AdapterUserDeliveriesRecycler.ViewHolder> {

    private ArrayList<StructUserDelivery> list;

    public AdapterUserDeliveriesRecycler(ArrayList<StructUserDelivery> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_client_package_request, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final StructUserDelivery item = list.get(position);
        holder.txtDate.setText(item.date);
        holder.txtTime.setText(item.time);
        holder.txtSystemName.setText(item.systemName);
        holder.txtDeliveryID.setText(item.deliveryID + "");
        if (item.state.equals(App.verification_rejected) || item.state.equals(App.delivery_rejected)) {
            holder.txtState.setTextColor(Color.parseColor("#a62626"));
            holder.txtState.setText(App.getContext().getString(R.string.reject));
        } else if (item.state.equals(App.waiting)) {
            holder.txtState.setTextColor(Color.parseColor(App.getContext().getString(R.color.colorAccent)));
            holder.txtState.setText(App.getContext().getString(R.string.register));
        }else if(item.state.equals(App.accepted)){
            holder.txtState.setTextColor(Color.parseColor(App.getContext().getString(R.color.colorAccent)));
            holder.txtState.setText(App.getContext().getString(R.string.verify));
        }else if(item.state.equals(App.done)){
            holder.txtState.setTextColor(Color.parseColor(App.getContext().getString(R.color.colorAccent)));
            holder.txtState.setText(App.getContext().getString(R.string.delivery));
        }else{
            holder.txtState.setTextColor(Color.parseColor("#888888"));
            holder.txtState.setText(App.getContext().getString(R.string.unknown));
        }
        holder.imgCover.setImageResource(R.drawable.cycle);
        if (item.systemCoverUrl.length() > 1) {
            Picasso.with(App.getContext()).load(item.systemCoverUrl).resize(100,100).into(holder.imgCover);
        }


        holder.layoutRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragDetails = new FragmentUserDeliveryDetails();
                Bundle extras = new Bundle();
                extras.putString("COVER_URL", item.systemCoverUrl);
                extras.putString("USER_PROVINCE", item.userProvince);
                extras.putString("USER_CITY", item.userCity);
                extras.putString("USER_ADDRESS", item.userAddress);
                extras.putString("SYSTEM_PROVINCE", item.systemProvince);
                extras.putString("SYSTEM_CITY", item.systemCity);
                extras.putString("SYSTEM_ADDRESS", item.systemAddress);
                extras.putString("INVOICE", item.invoice);
                extras.putString("DATE", item.date);
                extras.putString("TIME", item.time);
                extras.putString("SUM", item.price + "");
                extras.putInt("DELIVERY_ID", item.deliveryID);
                extras.putInt("SYSTEM_ID", item.systemID);
                extras.putString("STATE", item.state);
                extras.putString("DESC", item.desc);
                extras.putString("SYSTEM_NAME", item.systemName);
                fragDetails.setArguments(extras);
                FragmentTransaction fragmentTransaction = App.getCurrentActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_right, R.anim.slide_out_left);
                fragmentTransaction.add(R.id.fragmentContainer, fragDetails, App.FRAGMENT_USER_DELIVERIES_DETAILS_ID);
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
        public TextView txtSystemName;
        public TextView txtDate;
        public TextView txtTime;
        public TextView txtDeliveryID;
        public TextView txtState;

        public ViewHolder(View view) {
            super(view);
            txtSystemName = (TextView) view.findViewById(R.id.txtSystemName);
            layoutRoot = (CardView) view.findViewById(R.id.layoutRoot);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            txtDeliveryID = (TextView) view.findViewById(R.id.txtDeliveryID);
            txtState = (TextView) view.findViewById(R.id.txtState);
            imgCover = (ImageView) view.findViewById(R.id.img);
        }
    }


}
