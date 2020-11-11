package project.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.adapters.AdapterUserDeliveriesRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperString;
import project.structures.StructUserDelivery;


public class PagerFragOldPackageRequest extends Fragment {

    public static OnDeliveriesChangedListener listener;

    private RecyclerView lstRequests;
    private ViewGroup loader;
    private TextView txtNoRequest;
    private ArrayList<StructUserDelivery> list;
    private AdapterUserDeliveriesRecycler adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.pager_frag_old_package_request, container, false);

        list = new ArrayList<>();

        lstRequests = (RecyclerView) root.findViewById(R.id.lstRequests);
        loader = (ViewGroup) root.findViewById(R.id.loader);
        txtNoRequest = (TextView) root.findViewById(R.id.txtNoRequest);

        lstRequests.setVisibility(View.GONE);
        txtNoRequest.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
        lstRequests.setLayoutManager(new LinearLayoutManager(App.getContext()));
        adapter = new AdapterUserDeliveriesRecycler(list);
        lstRequests.setAdapter(adapter);

        listener = new OnDeliveriesChangedListener() {
            @Override
            public void onChanged() {
                getDeliveries();
            }
        };

        getDeliveries();
        return root;
    }

    private void getDeliveries(){
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    list.clear();
                    JSONArray requests = new JSONArray(data);
                    for (int i = 0; i < requests.length(); i++) {
                        JSONObject request = requests.getJSONObject(i);
                        StructUserDelivery item = new StructUserDelivery();
                        String date = request.getString("created_at").split(" ")[0];
                        String time = request.getString("created_at").split(" ")[1];
                        item.state = request.getString("state");
                        if(item.state.equals(App.deleted) || item.state.equals(App.waiting)){
                            continue;
                        }
                        item.deliveryID = request.getInt("id");
                        item.date = HelperString.getTransformedDate(date);
                        item.time = HelperString.getTransformedTime(time);
                        item.desc = request.getString("description");
                        JSONArray itemsArray = request.getJSONArray("items");
                        JSONArray customItemsArray = request.getJSONArray("custom_items");
                        String invoice = HelperString.computeInvoice(itemsArray);
                        String customInvoice = HelperString.computeCustomInvoice(customItemsArray);

                        String fullInvoice = "";
                        if(invoice.length()>0){
                            fullInvoice+=invoice;
                        }
                        if(invoice.length()>0 && customInvoice.length()>0){
                            fullInvoice+="\n";
                            fullInvoice+=customInvoice;
                        }
                        if(invoice.length()==0 && customInvoice.length()>0){
                            fullInvoice+=customInvoice;
                        }

                        item.invoice = fullInvoice;
                        int definedSum = HelperString.getSum(itemsArray);
                        int customSum = HelperString.getCustomSum(customItemsArray);
                        item.price = definedSum + customSum;
                        item.userCity = request.getJSONObject("city").getString("name");
                        item.userProvince = request.getJSONObject("city").getJSONObject("province").getString("name");
                        item.userAddress = request.getString("address");
                        String coverUrl = request.getJSONObject("system").getString("image");
                        if(coverUrl!=null && !coverUrl.equals("null")&&coverUrl.length()>1){
                            item.systemCoverUrl = App.SERVER_ADDRESS+coverUrl;
                        }else{
                            item.systemCoverUrl = "";
                        }
                        item.systemID = request.getJSONObject("system").getInt("id");
                        item.systemCity = request.getJSONObject("system").getJSONObject("city").getString("name");
                        item.systemProvince = request.getJSONObject("system").getJSONObject("city").getJSONObject("province").getString("name");
                        item.systemAddress = request.getJSONObject("system").getString("address");
                        item.systemName = request.getJSONObject("system").getString("name");
                        list.add(item);
                    }
                    if(list.size()>0) {
                        adapter.notifyDataSetChanged();
                        loader.setVisibility(View.GONE);
                        lstRequests.setVisibility(View.VISIBLE);
                    }else{
                        txtNoRequest.setVisibility(View.VISIBLE);
                        loader.setVisibility(View.GONE);
                        lstRequests.setVisibility(View.GONE);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    CustomToast.showToast(App.getContext().getString(R.string.oldVersionError));
                }
            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(getString(R.string.connectionError));
                loader.setVisibility(View.GONE);
            }
        }).getUserDeliveries();
    }

    public interface OnDeliveriesChangedListener{
        void onChanged();
    }

}
