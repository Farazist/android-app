package project.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.custom.CustomToast;
import project.structures.StructQuestion;
import project.structures.StructWorkTime;

public class AdapterQuestionRecycler extends RecyclerView.Adapter<AdapterQuestionRecycler.ViewHolder> {

    private ArrayList<StructQuestion> list;
    private StructQuestion previousItem = null;
    private int previousPosition = -1;

    public AdapterQuestionRecycler(ArrayList<StructQuestion> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_question, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final StructQuestion item = list.get(position);
        holder.txtQuestion.setText(item.question);
        holder.txtAnswer.setText(item.answer);
        holder.layoutAnswer.setVisibility(item.isExpanded ? View.VISIBLE : View.GONE);

        holder.layoutQuestion.setOnClickListener(new View.OnClickListener() {
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
        });
        holder.layoutAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                previousPosition = -1;
                item.isExpanded = !item.isExpanded;
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtQuestion;
        public TextView txtAnswer;
        public LinearLayout layoutQuestion;
        public ConstraintLayout layoutAnswer;

        public ViewHolder(View view) {
            super(view);
            txtQuestion = (TextView) view.findViewById(R.id.txtQuestion);
            txtAnswer = (TextView) view.findViewById(R.id.txtAnswer);
            layoutQuestion = (LinearLayout) view.findViewById(R.id.layoutQuestion);
            layoutAnswer = (ConstraintLayout) view.findViewById(R.id.layoutAnswer);
        }
    }

}
