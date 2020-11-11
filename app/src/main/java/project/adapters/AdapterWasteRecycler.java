package project.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

import java.util.ArrayList;

import project.structures.StructWaste;

public class AdapterWasteRecycler extends RecyclerView.Adapter<AdapterWasteRecycler.ViewHolder> {

    private ArrayList<StructWaste> list;
    private OnSumChangedListener listener;

    public interface OnSumChangedListener{
        void onChanged(int value , boolean add);
    }

    private int previousPosition = -1;
    private StructWaste previousItem;
    public AdapterWasteRecycler(ArrayList<StructWaste> list) {
        this.list = list;
    }
    
    public void setOnSumChangedListener(OnSumChangedListener listener){
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_waste, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final StructWaste item = list.get(position);
        holder.txtId.setText((position+1)+"");
        holder.txtName.setText(item.name);
        holder.txtUnit.setText(item.unit+" "+App.getContext().getString(R.string.tooman));
        holder.txtCount.setText(item.count+"");

        if(item.count>0){
            holder.txtName.setTextColor(Color.parseColor(App.getContext().getString(R.color.colorAccent)));
        }else{
            holder.txtName.setTextColor(Color.BLACK);
        }

        if(item.isExpanded){
            holder.layoutAdd.setVisibility(View.VISIBLE);
        }else{

            holder.layoutAdd.setVisibility(View.GONE);
        }

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (previousPosition >= 0 && position!=previousPosition) {
                    previousItem.isExpanded = false;
                    notifyItemChanged(previousPosition);
                }

                item.isExpanded = !item.isExpanded;
                notifyItemChanged(position);

                previousPosition = position;
                previousItem = item;
            }
        });


        holder.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringCount = holder.txtCount.getText().toString();
                int count = Integer.parseInt(stringCount);
                count++;
                item.count = count;
                holder.txtCount.setText(count+"");
                App.wastes.get(position).count = count;
                if(listener !=null){
                    listener.onChanged(item.unit,true);
                }
                if(item.count>0){
                    holder.txtName.setTextColor(Color.parseColor(App.getContext().getString(R.color.colorAccent)));
                }else{
                    holder.txtName.setTextColor(Color.BLACK);
                }

            }
        });

        holder.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringCount = holder.txtCount.getText().toString();
                int count = Integer.parseInt(stringCount);
                if(count==0){
                    return;
                }else {
                    count--;
                    item.count = count;
                    App.wastes.get(position).count = count;
                    holder.txtCount.setText(count + "");
                    if(listener!=null){
                        listener.onChanged(item.unit,false);
                    }
                    if(item.count>0){
                        holder.txtName.setTextColor(Color.parseColor(App.getContext().getString(R.color.colorAccent)));
                    }else{
                        holder.txtName.setTextColor(Color.BLACK);
                    }

                }
            }
        });

        if(item.imgUrl!=null && item.imgUrl.length()>0){
            Picasso.with(App.getContext()).load(item.imgUrl).resize(100,100).into(holder.imgAvatar);
        }else{
            holder.imgAvatar.setImageResource(R.drawable.cycle);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout root;
        public LinearLayout layoutAdd;
        public Button btnPlus;
        public Button btnMinus;
        public TextView txtId;
        public TextView txtName;
        public TextView txtUnit;
        public TextView txtCount;
        public ImageView imgAvatar;

        public ViewHolder(View view) {
            super(view);
            root = (LinearLayout) view.findViewById(R.id.root);
            layoutAdd = (LinearLayout) view.findViewById(R.id.layoutAdd);
            btnPlus = (Button) view.findViewById(R.id.btnPlus);
            btnMinus = (Button) view.findViewById(R.id.btnMinus);
            txtId = (TextView) view.findViewById(R.id.txtId);
            txtName = (TextView) view.findViewById(R.id.txtTitle);
            txtUnit = (TextView) view.findViewById(R.id.txtUnit);
            txtCount = (TextView) view.findViewById(R.id.txtCount);
            imgAvatar = (ImageView) view.findViewById(R.id.imgAvatar);
        }
    }



}
