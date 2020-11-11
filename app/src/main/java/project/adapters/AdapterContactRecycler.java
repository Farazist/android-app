package project.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.structures.StructContactUs;

public class AdapterContactRecycler extends RecyclerView.Adapter<AdapterContactRecycler.ViewHolder> {

    private ArrayList<StructContactUs> list;

    public AdapterContactRecycler(ArrayList<StructContactUs> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {



        View view = LayoutInflater.from(App.getContext()).inflate(R.layout.struct_contact_us, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final StructContactUs item = list.get(position);
        holder.txtName.setText(item.name);
        holder.txtValue.setText(item.value);

        holder.txtValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (item.type.toLowerCase().contains("number")) {
                        String number = item.value;
                        number.replace(" ", "");
                        number.replace("-", "");
                        number.replace("_", "");
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + number));
                        App.getCurrentActivity().startActivity(intent);
                    } else if (item.type.toLowerCase().contains("site")) {
                        String webSite = item.value;
                        if (!webSite.startsWith("http")) {
                            webSite = "https://" + webSite;
                        }
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(webSite));
                        App.getCurrentActivity().startActivity(browserIntent);
                    } else if (item.type.toLowerCase().contains("mail")) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{item.value});
//                    intent.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
//                    intent.putExtra(Intent.EXTRA_TEXT   , "body of email");
                        App.getCurrentActivity().startActivity(Intent.createChooser(intent, App.getContext().getString(R.string.sendEMail)));
                    }
                }catch (Exception e){

                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtName;
        public TextView txtValue;

        public ViewHolder(View view) {
            super(view);
            txtName = (TextView) view.findViewById(R.id.txtTitle);
            txtValue = (TextView) view.findViewById(R.id.txtValue);
        }
    }

}
