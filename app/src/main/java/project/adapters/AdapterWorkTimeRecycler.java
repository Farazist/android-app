package project.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.custom.CustomToast;
import project.structures.StructWorkTime;

public class AdapterWorkTimeRecycler extends RecyclerView.Adapter<AdapterWorkTimeRecycler.ViewHolder> {

    private boolean canChange = false;
    private ArrayList<StructWorkTime> list;

    public AdapterWorkTimeRecycler(ArrayList<StructWorkTime> list,boolean canChange) {
        this.list = list;
        this.canChange = canChange;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_work_time, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final StructWorkTime item = list.get(position);
        switch (item.dayNumber) {
            case 1:
                holder.txtDay.setText(App.getContext().getString(R.string.saturday));
                break;
            case 2:
                holder.txtDay.setText(App.getContext().getString(R.string.sunday));
                break;
            case 3:
                holder.txtDay.setText(App.getContext().getString(R.string.monday));
                break;
            case 4:
                holder.txtDay.setText(App.getContext().getString(R.string.tuesday));
                break;
            case 5:
                holder.txtDay.setText(App.getContext().getString(R.string.wednesday));
                break;
            case 6:
                holder.txtDay.setText(App.getContext().getString(R.string.thursday));
                break;
            case 7:
                holder.txtDay.setText(App.getContext().getString(R.string.friday));
                break;
        }

        try {

            holder.txt1.setText(item.priods.get(0));
            holder.txt2.setText(item.priods.get(1));
            holder.txt3.setText(item.priods.get(2));
            holder.txt4.setText(item.priods.get(3));
            holder.txt5.setText(item.priods.get(4));
            holder.txt6.setText(item.priods.get(5));
            holder.txt7.setText(item.priods.get(6));
            manageTexts(holder.txt1, item.activations.get(0));
            manageTexts(holder.txt2, item.activations.get(1));
            manageTexts(holder.txt3, item.activations.get(2));
            manageTexts(holder.txt4, item.activations.get(3));
            manageTexts(holder.txt5, item.activations.get(4));
            manageTexts(holder.txt6, item.activations.get(5));
            manageTexts(holder.txt7, item.activations.get(6));

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView txt = (TextView) view;
                    if (txt == holder.txt1) {
                        item.activations.set(0, !item.activations.get(0));
                        manageTexts(txt, item.activations.get(0));
                    } else if (txt == holder.txt2) {
                        item.activations.set(1, !item.activations.get(1));
                        manageTexts(txt, item.activations.get(1));
                    } else if (txt == holder.txt3) {
                        item.activations.set(2, !item.activations.get(2));
                        manageTexts(txt, item.activations.get(2));
                    } else if (txt == holder.txt4) {
                        item.activations.set(3, !item.activations.get(3));
                        manageTexts(txt, item.activations.get(3));
                    } else if (txt == holder.txt5) {
                        item.activations.set(4, !item.activations.get(4));
                        manageTexts(txt, item.activations.get(4));
                    } else if (txt == holder.txt6) {
                        item.activations.set(5, !item.activations.get(5));
                        manageTexts(txt, item.activations.get(5));
                    } else if (txt == holder.txt7) {
                        item.activations.set(6, !item.activations.get(6));
                        manageTexts(txt, item.activations.get(6));
                    }
                }
            };
            if(canChange) {
                holder.txt1.setOnClickListener(clickListener);
                holder.txt2.setOnClickListener(clickListener);
                holder.txt3.setOnClickListener(clickListener);
                holder.txt4.setOnClickListener(clickListener);
                holder.txt5.setOnClickListener(clickListener);
                holder.txt6.setOnClickListener(clickListener);
                holder.txt7.setOnClickListener(clickListener);
            }
        }catch (Exception e){
            CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
            return;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtDay;
        public TextView txt1;
        public TextView txt2;
        public TextView txt3;
        public TextView txt4;
        public TextView txt5;
        public TextView txt6;
        public TextView txt7;

        public ViewHolder(View view) {
            super(view);
            txtDay = (TextView) view.findViewById(R.id.txtDay);
            txt1 = (TextView) view.findViewById(R.id.txt1);
            txt2 = (TextView) view.findViewById(R.id.txt2);
            txt3 = (TextView) view.findViewById(R.id.txt3);
            txt4 = (TextView) view.findViewById(R.id.txt4);
            txt5 = (TextView) view.findViewById(R.id.txt5);
            txt6 = (TextView) view.findViewById(R.id.txt6);
            txt7 = (TextView) view.findViewById(R.id.txt7);
        }
    }


    private void manageTexts(TextView txt, boolean isChecked) {
        if (isChecked) {
            txt.setBackgroundResource(R.drawable.button_work_time_shape2);
            txt.setTextColor(Color.parseColor(App.getContext().getString(R.color.background)));
        } else {
            txt.setBackgroundResource(R.drawable.button_work_time_shape1);
            txt.setTextColor(Color.parseColor("#333333"));
        }
    }


}
