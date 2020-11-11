package project.adapters;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.fragments.FragPackageRequestStepTwo;
import project.fragments.FragmentTicketMessages;
import project.fragments.PagerFragNewPackageRequest;
import project.helper.HelperFragment;
import project.structures.StructTicket;

public class AdapterTicketRecycler extends RecyclerView.Adapter<AdapterTicketRecycler.ViewHolder> {

    private final String OPEN = App.getContext().getString(R.string.open);
    private final String CLOSED = App.getContext().getString(R.string.closed);

    private ArrayList<StructTicket> list;
    public AdapterTicketRecycler(ArrayList<StructTicket> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_ticket, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final StructTicket item = list.get(position);

        holder.txtTitle.setText(item.title);
        holder.txtSubject.setText(item.subject);
        holder.txtDate.setText(item.date);
        holder.txtState.setText(item.state);

        if(item.count>0){
            holder.txtCount.setVisibility(View.VISIBLE);
            holder.txtCount.setText("+"+item.count);
        }else{
            holder.txtCount.setVisibility(View.GONE);
        }

        if(item.state.equals(OPEN)){
            holder.txtState.setVisibility(View.GONE);
        }else{
            holder.txtState.setVisibility(View.VISIBLE);
        }

        if(position%2 == 0){
            holder.root.setBackgroundColor(App.getContext().getResources().getColor(R.color.background));
        }else{
            holder.root.setBackgroundColor(Color.parseColor("#ebefeb"));
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragTicket = new FragmentTicketMessages();
                Bundle extras = new Bundle();
                extras.putInt("POSITION", position);
                extras.putString("STATE", item.state);
                extras.putInt("TICKET_ID", item.id);
                fragTicket.setArguments(extras);
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_TICKET_MESSAGES_ID)
                        .addFragment(R.id.layoutContainer, fragTicket, true)
                        .commit();
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
        public TextView txtCount;
        public TextView txtSubject;
        public TextView txtDate;
        public TextView txtState;

        public ViewHolder(View view) {
            super(view);
            root = (ViewGroup) view.findViewById(R.id.root);
            txtTitle = (TextView) view.findViewById(R.id.txtTitle);
            txtCount = (TextView) view.findViewById(R.id.txtCount);
            txtSubject = (TextView) view.findViewById(R.id.txtSubject);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            txtState = (TextView) view.findViewById(R.id.txtState);
        }
    }

}
