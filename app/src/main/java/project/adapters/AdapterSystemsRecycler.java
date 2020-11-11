package project.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.connection.Commands;
import project.custom.CustomToast;
import project.fragments.FragmentMainClient;
import project.structures.StructSystem;

public class AdapterSystemsRecycler extends RecyclerView.Adapter<AdapterSystemsRecycler.ViewHolder> implements Filterable {

    private ArrayList<StructSystem> list;
    private ArrayList<StructSystem> fullList;
    private int previousPosition = -1;
    private StructSystem previousItem;

    public AdapterSystemsRecycler(ArrayList<StructSystem> list) {
        this.list = list;
        fullList = new ArrayList<StructSystem>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_system, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final StructSystem item = list.get(position);
        String state = item.state;

        if(item.isExpanded){
            holder.layoutDetails.setVisibility(View.VISIBLE);
        }else{
            holder.layoutDetails.setVisibility(View.GONE);
        }

        if(state.equals(App.active)){
            holder.txtState.setText(App.getContext().getString(R.string.online));
            holder.txtState.setTextColor(App.getContext().getResources().getColor(R.color.colorAccent));
        }else{
            holder.txtState.setText(App.getContext().getString(R.string.offline));
            holder.txtState.setTextColor(Color.parseColor("#a62626"));
        }

        if(item.id == App.clientSystem.id){
            holder.btnConnect.setEnabled(false);
            holder.btnConnect.setText(App.getContext().getString(R.string.youAreConnected));
        }else{
            holder.btnConnect.setEnabled(true);
            holder.btnConnect.setText(App.getContext().getString(R.string.connect));
        }

        if(item.processing){
            holder.btnConnect.setVisibility(View.GONE);
            holder.loader.setVisibility(View.VISIBLE);
        }else{
            holder.btnConnect.setVisibility(View.VISIBLE);
            holder.loader.setVisibility(View.GONE);
        }
        holder.txtCity.setText(item.cityName);
        holder.txtName.setText(item.name);
        holder.txtAddress.setText(item.address);
        if (item.coverUrl != null && !item.coverUrl.toLowerCase().equals("null")&& item.coverUrl.length() > 0) {
            Picasso.with(App.getContext()).load(App.SERVER_ADDRESS+item.coverUrl).resize(100,100).into(holder.imgCover);
        } else {
            holder.imgCover.setImageResource(R.drawable.fara_padded);
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (previousPosition >= 0) {
                    previousItem.isExpanded = !previousItem.isExpanded;
                    notifyItemChanged(previousPosition);
                }

                item.isExpanded = !item.isExpanded;
                notifyItemChanged(position);

                previousPosition = position;
                previousItem = item;
            }
        };

        holder.imgCover.setOnClickListener(listener);
        holder.layoutRoot.setOnClickListener(listener);


        holder.btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.id == App.clientSystem.id){
                    CustomToast.showToast(App.getContext().getString(R.string.youAreConnected));
                    return;
                }else{
                    App.canBackPress = false;
                    item.processing = true;
                    holder.btnConnect.setVisibility(View.GONE);
                    holder.loader.setVisibility(View.VISIBLE);
                    new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
                        @Override
                        public void onComplete(String data) {
                            getUserInfo(holder,item);
                        }

                        @Override
                        public void onFail(String error) {
                            App.canBackPress = true;
                            holder.btnConnect.setVisibility(View.GONE);
                            holder.loader.setVisibility(View.VISIBLE);
                            item.processing = false;
                            CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                        }
                    }).editUserSystem(item.id);
                }
            }
        });
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<StructSystem> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(fullList);

            } else {
                String filterWord = charSequence.toString();
                for (StructSystem system : fullList) {
                    if (system.cityName.contains(filterWord) || system.address.contains(filterWord) || system.name.contains(filterWord)) {
                        filteredList.add(system);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }


        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            list.clear();
            list.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public CardView layoutRoot;
        public TextView txtName;
        public TextView txtCity;
        public TextView txtAddress;
        public TextView txtState;
        public Button btnConnect;
        public ImageView imgCover;
        public LinearLayout layoutDetails;
        public ProgressBar loader;

        public ViewHolder(View view) {
            super(view);
            layoutRoot = (CardView) view.findViewById(R.id.layoutRoot);
            imgCover = (ImageView) view.findViewById(R.id.img);
            txtName = (TextView) view.findViewById(R.id.txtTitle);
            txtCity = (TextView) view.findViewById(R.id.txtCity);
            txtAddress = (TextView) view.findViewById(R.id.txtAddress);
            txtState = (TextView) view.findViewById(R.id.txtState);
            btnConnect = (Button) view.findViewById(R.id.btnConnect);
            layoutDetails = (LinearLayout) view.findViewById(R.id.layoutDetails);
            loader = (ProgressBar) view.findViewById(R.id.loader);
        }
    }
    private void getUserInfo(final ViewHolder holder , final StructSystem item) {
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                FragmentMainClient.getSlideImages();
                CustomToast.showToast(App.getContext().getString(R.string.accountConnectedToSystem));
                ActivityMain.fragmentManager.popBackStack();
                ActivityMain.fragmentManager.popBackStack();
                App.canBackPress = true;
                holder.btnConnect.setVisibility(View.GONE);
                holder.loader.setVisibility(View.VISIBLE);
                item.processing = false;
            }

            @Override
            public void onFail(String error) {
                App.canBackPress = true;
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
                holder.btnConnect.setVisibility(View.GONE);
                holder.loader.setVisibility(View.VISIBLE);
                item.processing = false;
            }
        }).getUserInfo();
    }

}
