package project.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
import project.adapters.AdapterTicketRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperCalendar;
import project.helper.HelperFragment;
import project.helper.HelperString;
import project.structures.StructTicket;

public class FragmentContactManagement extends Fragment {

    private ViewGroup layoutNothing;
    private RecyclerView lstTickets;
    private SwipeRefreshLayout swipe;
    private static ArrayList<StructTicket> tickets;
    private static AdapterTicketRecycler adapter;

    private static int newTicketCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_contact_management, container, false);

        tickets = new ArrayList<>();
        lstTickets = (RecyclerView)root.findViewById(R.id.lstTickets);
        Button btnSendTicket = (Button) root.findViewById(R.id.btnSendTicket);
        layoutNothing = (ViewGroup) root.findViewById(R.id.layoutNothing);
        swipe = (SwipeRefreshLayout) root.findViewById(R.id.swipe);

        swipe.setColorSchemeColors(App.getContext().getResources().getColor(R.color.colorAccent),App.getContext().getResources().getColor(R.color.colorPrimary));
        adapter = new AdapterTicketRecycler(tickets);
        lstTickets.setLayoutManager(new LinearLayoutManager(App.getContext()));
        lstTickets.setAdapter(adapter);

        getTickets();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTickets();
            }
        });

        btnSendTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new HelperFragment()
                        .addToBackStack(App.FRAGMENT_SEND_TICKET_ID)
                        .addFragment(R.id.fragmentContainer, new FragmentNewTicket(), true)
                        .commit();
            }
        });


        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });

        return root;
    }

    private void getTickets(){
        newTicketCount = 0;
        tickets.clear();
        loading();

        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONArray ticketArray = new JSONArray(data);

                    for(int i=0;i<ticketArray.length();i++){
                        JSONObject ticketObject = ticketArray.getJSONObject(i);
                        StructTicket ticket = new StructTicket();
                        ticket.id = ticketObject.getInt("id");
                        String state = ticketObject.getString("state");
                        if(state.toLowerCase().contains("open")){
                            ticket.state = App.getContext().getString(R.string.open);
                        }else{
                            ticket.state = App.getContext().getString(R.string.closed);
                        }
                        ticket.date = HelperString.getTransformedDate(ticketObject.getString("created_at").split(" ")[0]);
                        ticket.time = HelperString.getTransformedTime(ticketObject.getString("created_at").split(" ")[1]);
                        JSONObject titleObject = ticketObject.getJSONObject("group");
                        ticket.title = titleObject.getString("title");
                        ticket.subject = ticketObject.getString("subject");
                        ticket.count = ticketObject.getInt("new_ticket_messages_count");
                        newTicketCount +=ticket.count;
                        tickets.add(ticket);
                    }
                    adapter.notifyDataSetChanged();
                    loaded();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {
                swipe.setRefreshing(false);
                CustomToast.showToast(App.getContext().getString(R.string.connectionError));
            }
        }).getTickets();


    }

    private void loading(){
        swipe.setRefreshing(true);
        lstTickets.setVisibility(View.GONE);
        layoutNothing.setVisibility(View.GONE);
    }

    private void loaded(){
        swipe.setRefreshing(false);
        FragmentMainOwner.updateNewTicketCount(newTicketCount);
        if(tickets.size()==0){
            layoutNothing.setVisibility(View.VISIBLE);
            lstTickets.setVisibility(View.GONE);
        }else{
            lstTickets.setVisibility(View.VISIBLE);
            layoutNothing.setVisibility(View.GONE);
        }
    }

    public static void updateList(int position){
        newTicketCount -= tickets.get(position).count;
        tickets.get(position).count = 0;
        FragmentMainOwner.updateNewTicketCount(newTicketCount);
        adapter.notifyDataSetChanged();
    }
}
