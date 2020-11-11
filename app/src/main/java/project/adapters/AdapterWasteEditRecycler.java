package project.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.fragments.FragmentSystemSettingsWastes;
import project.structures.StructWaste;
import project.structures.StructWasteEdit;

public class AdapterWasteEditRecycler extends RecyclerView.Adapter<AdapterWasteEditRecycler.ViewHolder> {

    private ArrayList<StructWasteEdit> list;
    private OnSumChangedListener listener;

    public interface OnSumChangedListener{
        void onChanged(int value, boolean add);
    }


    public AdapterWasteEditRecycler(ArrayList<StructWasteEdit> list) {
        this.list = list;
    }
    


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_waste_edit, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final StructWasteEdit item = list.get(position);
        holder.txtId.setText((position+1)+"");
        holder.txtName.setText(item.name);
        holder.txtUnit.setText(item.unit+" "+App.getContext().getString(R.string.tooman));
        holder.chkEnable.setChecked(item.enable);

        if(item.imgUrl!=null && item.imgUrl.length()>0 && !item.imgUrl.equals("null")){
            Picasso.with(App.getContext()).load(item.imgUrl).resize(100,100).into(holder.imgAvatar);
        }else{
            holder.imgAvatar.setImageResource(R.drawable.cycle);
        }

        holder.chkEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item.enable = holder.chkEnable.isChecked();
            }
        });

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentSystemSettingsWastes.showEditDialog(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout root;
        public TextView txtId;
        public TextView txtName;
        public TextView txtUnit;
        public Button btnEdit;
        public CheckBox chkEnable;
        public CircleImageView imgAvatar;

        public ViewHolder(View view) {
            super(view);
            root = (LinearLayout) view.findViewById(R.id.root);
            txtUnit = (TextView) view.findViewById(R.id.txtUnit);
            btnEdit = (Button) view.findViewById(R.id.btnEdit);
            txtId = (TextView) view.findViewById(R.id.txtId);
            txtName = (TextView) view.findViewById(R.id.txtName);
            chkEnable = (CheckBox) view.findViewById(R.id.chkEnable);
            imgAvatar = (CircleImageView) view.findViewById(R.id.imgAvatar);
        }
    }



}
