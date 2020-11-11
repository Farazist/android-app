package project.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.helper.HelperString;
import project.structures.StructCustomWaste;
import project.structures.StructWaste;

public class AdapterCustomWasteRecycler extends RecyclerView.Adapter<AdapterCustomWasteRecycler.ViewHolder> {

    private ArrayList<StructCustomWaste> list;

    public interface OnSumChangedListener{
        void onChanged(int value, boolean add);
    }


    public AdapterCustomWasteRecycler(ArrayList<StructCustomWaste> list) {
        this.list = list;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_custom_waste, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final StructCustomWaste item = list.get(position);

        if(App.account.role==2){
            holder.layoutOwner.setVisibility(View.VISIBLE);
        }else{
            holder.layoutOwner.setVisibility(View.GONE);
        }
        holder.txtName.setText(item.name);
        if(item.weight>0) {
            holder.txtUnitWord.setText(App.getContext().getString(R.string.weight));
            holder.txtCount.setText(item.weight +" "+App.getContext().getString(R.string.kilogram));
        }else{
            holder.txtUnitWord.setText(App.getContext().getString(R.string.count));
            holder.txtCount.setText(item.count +" "+App.getContext().getString(R.string.unit));
        }
        holder.txtPrice.setText(HelperString.convertToNumberFormat(""+item.price) +" "+App.getContext().getString(R.string.tooman));

        holder.txtRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewGroup layoutOwner;
        public TextView txtName;
        public TextView txtUnitWord;
        public TextView txtPrice;
        public TextView txtCount;
        public TextView txtRemove;

        public ViewHolder(View view) {
            super(view);
            layoutOwner = (ViewGroup) view.findViewById(R.id.layoutOwner);
            txtName = (TextView) view.findViewById(R.id.txtName);
            txtUnitWord = (TextView) view.findViewById(R.id.txtUnitWord);
            txtPrice = (TextView) view.findViewById(R.id.txtPrice);
            txtCount = (TextView) view.findViewById(R.id.txtCount);
            txtRemove = (TextView) view.findViewById(R.id.txtRemove);
        }
    }



}
