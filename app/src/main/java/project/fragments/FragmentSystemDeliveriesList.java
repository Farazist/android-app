package project.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;
import project.activities.ActivityMain;
import project.adapters.AdapterSystemDeliveriesRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperString;
import project.structures.StructSystemDelivery;

public class FragmentSystemDeliveriesList extends Fragment {

    public static OnItemDeletedListener itemDeletedListener;
    private SwipeRefreshLayout swipe;
    private RecyclerView lst;
    private ArrayList<StructSystemDelivery> list;
    private LinearLayout layoutNothing;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_system_deliveries_list,container,false);

        root.setFocusableInTouchMode(true);
        root.requestFocus();

        swipe = (SwipeRefreshLayout)root.findViewById(R.id.swipe);
        layoutNothing = (LinearLayout)root.findViewById(R.id.layoutNothing);
        lst = (RecyclerView)root.findViewById(R.id.lst);
        swipe.setColorSchemeColors(App.getContext().getResources().getColor(R.color.colorAccent),App.getContext().getResources().getColor(R.color.colorPrimary));

        itemDeletedListener = new OnItemDeletedListener() {
            @Override
            public void onDeleted() {
                if(list.size()==0){
                    layoutNothing.setVisibility(View.VISIBLE);
                }
            }
        };

        getDeliveries();
        list = new ArrayList<>();


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDeliveries();
            }
        });




        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        root.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (ActivityMain.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    ActivityMain.drawerLayout.closeDrawer(Gravity.RIGHT);
                    return true;
                }
                Fragment currentFragment = ActivityMain.fragmentManager.findFragmentById(R.id.fragmentContainer);
                if (currentFragment instanceof FragmentSystemDeliveriesList) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentMainOwner.getDeliveriesCount(false);
                        return false;
                    }
                }
                return false;
            }
        });


        return root;
    }

    private void getDeliveries(){
        layoutNothing.setVisibility(View.GONE);
        swipe.setRefreshing(true);
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    list = new ArrayList<>();
                    list.clear();
                    JSONArray deliveries = new JSONArray(data);
                    for(int i=0;i<deliveries.length();i++) {
                        JSONObject delivery = deliveries.getJSONObject(i);
                        StructSystemDelivery item = new StructSystemDelivery();
                        item.state = delivery.getString("state");
                        if(!item.state.equals(App.waiting) && !item.state.equals(App.accepted)){
                            continue;
                        }
                        item.id = delivery.getInt("id");
                        item.date = HelperString.getTransformedDate(delivery.getString("created_at").split(" ")[0]);
                        item.time = HelperString.getTransformedTime(delivery.getString("created_at").split(" ")[1]);
                        item.items = delivery.getJSONArray("items");
                        item.customItems = delivery.getJSONArray("custom_items");
                        String invoice = HelperString.computeInvoice(item.items);
                        String customInvoice = HelperString.computeCustomInvoice(item.customItems);
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
                        item.definedItemsPrice = HelperString.getSum(item.items);
                        item.userCity = delivery.getJSONObject("city").getString("name");
                        item.userProvince = delivery.getJSONObject("city").getJSONObject("province").getString("name");
                        item.userAddress = delivery.getString("address");
                        item.userId = delivery.getJSONObject("user").getInt("id");
                        item.userMobile = delivery.getJSONObject("user").getString("mobile_number");
                        String userAvatarUrl = delivery.getJSONObject("user").getString("image");
                        if(userAvatarUrl !=null && !userAvatarUrl.toLowerCase().equals("null") && userAvatarUrl.length()>0){
                            item.userAvatar = App.SERVER_ADDRESS+userAvatarUrl;
                        }else{
                            item.userAvatar = "";
                        }

                        item.userName = delivery.getJSONObject("user").getString("name");
                        list.add(item);
                    }
                    if(list.size()==0){
                        swipe.setRefreshing(false);
                        layoutNothing.setVisibility(View.VISIBLE);
                        return;
                    }
                    AdapterSystemDeliveriesRecycler adapter = new AdapterSystemDeliveriesRecycler(list);
                    lst.setLayoutManager(new LinearLayoutManager(App.getContext()));
                    lst.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    swipe.setRefreshing(false);
                    CustomToast.showToast(getString(R.string.connectionError));
                    e.printStackTrace();
                }
                swipe.setRefreshing(false);
            }

            @Override
            public void onFail(String error) {
                swipe.setRefreshing(false);
                CustomToast.showToast(getString(R.string.connectionError));
            }
        }).getSystemDeliveries(App.ownerSystems.get(0).id);



    }
    public interface OnItemDeletedListener{
        void onDeleted();
    }
}
