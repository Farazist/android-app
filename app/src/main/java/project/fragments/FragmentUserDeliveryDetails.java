package project.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

public class FragmentUserDeliveryDetails extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user_delivery_details,container,false);

        ImageView imgCover = (ImageView)root.findViewById(R.id.img);
        TextView txtSystemAddress = (TextView)root.findViewById(R.id.txtSystemAddress);
        TextView txtID = (TextView)root.findViewById(R.id.txtID);
        TextView txtName = (TextView)root.findViewById(R.id.txtName);
        TextView txtUserAddress = (TextView)root.findViewById(R.id.txtUserAddress);
        TextView txtInvoice = (TextView)root.findViewById(R.id.txtInvoice);
        TextView txtDate = (TextView)root.findViewById(R.id.txtDate);
        TextView txtTime = (TextView)root.findViewById(R.id.txtTime);
        TextView txtSum = (TextView)root.findViewById(R.id.txtSum);
        TextView txtDeliveryID = (TextView)root.findViewById(R.id.txtDeliveryID);
        TextView txtState = (TextView)root.findViewById(R.id.txtState);
        TextView txtRejectDesc = (TextView)root.findViewById(R.id.txtRejectDesc);
        ViewGroup layoutRejectDesc = (ViewGroup)root.findViewById(R.id.layoutRejectDesc);

        int deliveryID = getArguments().getInt("DELIVERY_ID");
        int systemID = getArguments().getInt("SYSTEM_ID");
        String coverUrl = getArguments().getString("COVER_URL");
        String systemProvince = getArguments().getString("SYSTEM_PROVINCE");
        String systemCity = getArguments().getString("SYSTEM_CITY");
        String systemAddress = getArguments().getString("SYSTEM_ADDRESS");
        String userProvince = getArguments().getString("USER_PROVINCE");
        String userCity = getArguments().getString("USER_CITY");
        String userAddress = getArguments().getString("USER_ADDRESS");
        String invoice = getArguments().getString("INVOICE");
        String date = getArguments().getString("DATE");
        String time = getArguments().getString("TIME");
        String sum = getArguments().getString("SUM") +" "+getString(R.string.tooman);
        String state = getArguments().getString("STATE");
        String day = getArguments().getString("DAY");
        String desc = getArguments().getString("DESC");
        String systemName = getArguments().getString("SYSTEM_NAME");

        txtID.setText(App.getContext().getString(R.string.systemID_)+ " "+systemID);
        txtName.setText(systemName);

        layoutRejectDesc.setVisibility(View.GONE);
        txtState.setVisibility(View.VISIBLE);
        if (state.equals(App.verification_rejected) || state.equals(App.delivery_rejected)) {
            layoutRejectDesc.setVisibility(View.VISIBLE);
            txtState.setVisibility(View.GONE);
            txtState.setTextColor(Color.parseColor("#a62626"));
            txtState.setText(App.getContext().getString(R.string.reject));
            if(desc!=null && !desc.equals("null")&& desc.length()>0) {
                txtRejectDesc.setText(desc + " "+App.getContext().getString(R.string.canceled));

            }else{
                txtRejectDesc.setText(" - "+App.getContext().getString(R.string.canceled));
            }
        } else if (state.equals(App.waiting)) {
            txtState.setTextColor(Color.parseColor(App.getContext().getString(R.color.colorAccent)));
            txtState.setText(App.getContext().getString(R.string.register));
        }else if(state.equals(App.accepted)){
            txtState.setTextColor(Color.parseColor(App.getContext().getString(R.color.colorAccent)));
            txtState.setText(App.getContext().getString(R.string.verify));
        }else if(state.equals(App.done)){
            txtState.setTextColor(Color.parseColor(App.getContext().getString(R.color.colorAccent)));
            txtState.setText(App.getContext().getString(R.string.delivery));
        }else{
            txtState.setTextColor(Color.parseColor("#888888"));
            txtState.setText(App.getContext().getString(R.string.unknown));
        }

        if(coverUrl.length()>1){
            Picasso.with(App.getContext()).load(coverUrl).into(imgCover);
        }else{
            imgCover.setImageResource(R.drawable.cycle);
        }

        txtDeliveryID.setText(deliveryID+"");

        txtSystemAddress.setText(systemProvince + " - "+systemCity + " - "+systemAddress);
        txtUserAddress.setText(userProvince+ " - "+userCity+" - "+userAddress);
        txtInvoice.setText(invoice);
        txtDate.setText(date);
        txtTime.setText(time);
        txtSum.setText(sum);

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
        return root;
    }


}
