package project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.fragments.FragmentNewTicket;
import project.structures.StructTicketTitle;

public class AdapterTicketTitleRecycler extends RecyclerView.Adapter<AdapterTicketTitleRecycler.ViewHolder> {

    private ArrayList<StructTicketTitle> list;

    public AdapterTicketTitleRecycler(ArrayList<StructTicketTitle> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_ticket_title, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final StructTicketTitle item = list.get(position);
        holder.txtTitle.setText(item.text);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentNewTicket.listener.onItemSelected(item.id,item.text);
            }
        };

        holder.txtTitle.setOnClickListener(clickListener);
        holder.root.setOnClickListener(clickListener);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup root;
        public TextView txtTitle;

        public ViewHolder(View view) {
            super(view);
            root = (ViewGroup) view.findViewById(R.id.root);
            txtTitle = (TextView) view.findViewById(R.id.txtTitle);
        }
    }


}
