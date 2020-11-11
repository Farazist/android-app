package project.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.fragments.FragmentTicketMessages;
import project.structures.StructTicketMessage;

public class AdapterTicketMessageRecycler extends RecyclerView.Adapter<AdapterTicketMessageRecycler.ViewHolder> {

    private static final int SEND = 0;
    private static final int RECEIVE = 1;

    private ArrayList<StructTicketMessage> list;

    public AdapterTicketMessageRecycler(ArrayList<StructTicketMessage> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_ticket_message, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final StructTicketMessage item = list.get(position);

        holder.txtDateTime.setText(item.dateTime);
        holder.txtMessage.setText(item.message);

        if(item.imgUrl==null || item.imgUrl.length()==0){
            holder.txtAttach.setVisibility(View.GONE);
        }else{
            holder.txtAttach.setVisibility(View.VISIBLE);
        }

        if(item.message.length()==0){
            holder.txtMessage.setVisibility(View.GONE);
        }else{
            holder.txtMessage.setVisibility(View.VISIBLE);
        }

        if(item.type.toLowerCase().contains("send")){
            holder.header.setBackgroundColor(Color.parseColor("#009687"));
            holder.txtName.setText(App.account.name);
        }else{
            holder.header.setBackgroundColor(Color.parseColor("#db4537"));
            holder.txtName.setText(App.getContext().getString(R.string.farazistSupport));
        }

        holder.txtAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTicketMessages.showImageDialog(item.imgUrl);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView root;
        public ViewGroup header;
        public TextView txtDateTime;
        public TextView txtName;
        public TextView txtMessage;
        public TextView txtAttach;
        public ImageView imgAvatar;

        public ViewHolder(View view) {
            super(view);
            root = (CardView) view.findViewById(R.id.root);
            header = (ViewGroup) view.findViewById(R.id.header);
            txtDateTime = (TextView) view.findViewById(R.id.txtDateTime);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtMessage = (TextView) view.findViewById(R.id.txtMessage);
            txtAttach = (TextView) view.findViewById(R.id.txtAttach);
            imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        }
    }

}
