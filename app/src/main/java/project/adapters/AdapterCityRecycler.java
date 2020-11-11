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
import project.activities.ActivitySignup;
import project.fragments.FragmentEditAccount;
import project.fragments.FragmentNewTicket;
import project.fragments.FragmentSignup;
import project.structures.StructCity;
import project.structures.StructTicketTitle;

public class AdapterCityRecycler extends RecyclerView.Adapter<AdapterCityRecycler.ViewHolder> {

    private ArrayList<StructCity> list;

    public AdapterCityRecycler(ArrayList<StructCity> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_city, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final StructCity item = list.get(position);
        holder.txtName.setText(item.name);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FragmentSignup.addressSelectListener != null) {
                    FragmentSignup.addressSelectListener.onItemSelect(item.id, item.name);
                }
                if (FragmentEditAccount.addressSelectListener != null) {
                    FragmentEditAccount.addressSelectListener.onItemSelect(item.id, item.name);
                }
            }
        };

        holder.txtName.setOnClickListener(clickListener);
        holder.root.setOnClickListener(clickListener);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup root;
        public TextView txtName;

        public ViewHolder(View view) {
            super(view);
            root = (ViewGroup) view.findViewById(R.id.root);
            txtName = (TextView) view.findViewById(R.id.txtName);
        }
    }


}
