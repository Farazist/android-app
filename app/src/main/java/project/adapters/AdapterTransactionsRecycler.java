package project.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

import java.util.ArrayList;

import project.structures.StructTransaction;

public class AdapterTransactionsRecycler extends RecyclerView.Adapter<AdapterTransactionsRecycler.ViewHolder> {

    private ArrayList<StructTransaction> list;

    private String download = "\uf56d";
    private String upload = "\uf574";
    private String chargeWallet = "\uf555";
    private String packageDelivery = "\uf49c";

    private int previousPosition = -1;
    private StructTransaction previousItem;


    public AdapterTransactionsRecycler(ArrayList<StructTransaction> list) {
        this.list = list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_transaction, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final StructTransaction item = list.get(position);

        holder.txtDate.setText(item.date);
        holder.txtTime.setText(item.time);
        holder.txtID.setText(item.id+"");
        holder.txtAction.setText(item.type.toString());
        holder.txtDesc.setText(item.desc);
        if(item.isExpanded){

            holder.layoutDetails.setVisibility(View.VISIBLE);
        }else{

            holder.layoutDetails.setVisibility(View.GONE);
        }
        if(item.desc == null || item.desc.equals("null") || item.desc.length()<1){
            holder.txtDesc.setVisibility(View.GONE);
        }else{
            holder.txtDesc.setVisibility(View.VISIBLE);
        }

        String number = item.targetMobileNumber;
        char[] numberArray = number.toCharArray();
        numberArray[4] = '*';
        numberArray[5] = '*';
        numberArray[6] = '*';
        number = "";
        for(char c : numberArray){
            number+=c;
        }
        holder.txtNumber.setText(number);
        if(item.amount<0){
            holder.txtOriginOrDestination.setText(App.getContext().getString(R.string.destinationNumber_));
        }else{
            holder.txtOriginOrDestination.setText(App.getContext().getString(R.string.originNumber_));
        }
        holder.txtAmount.setText(Math.abs(item.amount)+" "+App.getContext().getString(R.string.tooman));

        holder.txtName.setText(item.targetName);


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

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ConstraintLayout root;
        public LinearLayout layoutDetails;
        public TextView txtDate;
        public TextView txtDesc;
        public TextView txtTime;
        public TextView txtAmount;
        public TextView txtNumber;
        public TextView txtName;
        public TextView txtAction;
        public TextView txtOriginOrDestination;
        public TextView txtID;

        public ViewHolder(View view) {
            super(view);
            root = (ConstraintLayout) view.findViewById(R.id.root);
            layoutDetails = (LinearLayout) view.findViewById(R.id.layoutDetails);
            txtDate = (TextView) view.findViewById(R.id.txtDate);
            txtDesc = (TextView) view.findViewById(R.id.txtDesc);
            txtTime = (TextView) view.findViewById(R.id.txtTime);
            txtAmount = (TextView) view.findViewById(R.id.txtAmount);
            txtNumber = (TextView) view.findViewById(R.id.txtNumber);
            txtAction = (TextView) view.findViewById(R.id.txtAction);
            txtOriginOrDestination = (TextView) view.findViewById(R.id.txtOriginOrDestination);
            txtName = (TextView) view.findViewById(R.id.txtTitle);
            txtID = (TextView) view.findViewById(R.id.txtID);


        }
    }



}
