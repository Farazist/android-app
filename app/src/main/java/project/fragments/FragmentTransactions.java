package project.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ir.farazist.farazist_app.App;
import ir.farazist.farazist_app.R;

import project.enums.TransactionType;
import project.activities.ActivityMain;
import project.adapters.AdapterTransactionsRecycler;
import project.connection.Commands;
import project.custom.CustomToast;
import project.helper.HelperString;
import project.structures.StructTransaction;

public class FragmentTransactions extends Fragment {
    private AdapterTransactionsRecycler adapter;
    private static ArrayList<StructTransaction> transactionsArray;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_transactions, container, false);


        final ViewGroup loader = (ViewGroup) root.findViewById(R.id.loader);
        final RecyclerView lstTransactions = (RecyclerView) root.findViewById(R.id.lstTransactions);
        lstTransactions.setLayoutManager(new LinearLayoutManager(App.getContext()));


        transactionsArray = new ArrayList<StructTransaction>();
        transactionsArray.clear();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    JSONArray transactions = new JSONArray(data);
                    for (int i = 0; i < transactions.length(); i++) {
                        JSONObject transaction = transactions.getJSONObject(i);
                        StructTransaction item = new StructTransaction();
                        item.id = transaction.getInt("id");
                        String dateTime = transaction.getString("created_at");
                        if (dateTime == null || dateTime.equals("null")) {
                            item.time = App.getContext().getString(R.string.unknown);
                            item.date = App.getContext().getString(R.string.unknown);
                        } else {
                            item.date = HelperString.getTransformedDate(dateTime.split(" ")[0]);
                            item.time = HelperString.getTransformedTime(dateTime.split(" ")[1]);
                        }
                        int amount = transaction.getInt("amount");
                        item.amount = amount;
                        item.desc = transaction.getString("description");
                        if (amount < 0) {
                            item.type = TransactionType.WITHDRAW;
                        } else {
                            item.type = TransactionType.DEPOSITE;
                        }
                        item.targetName = transaction.getJSONObject("target_user").getString("name");
                        item.targetMobileNumber = transaction.getJSONObject("target_user").getString("mobile_number");
                        transactionsArray.add(item);
                    }


                    adapter = new AdapterTransactionsRecycler(transactionsArray);
                    lstTransactions.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);
                    lstTransactions.setAdapter(adapter);
                } catch (JSONException e) {
                    CustomToast.showToast(getString(R.string.oldVersionError));
                    loader.setVisibility(View.GONE);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFail(String error) {
                CustomToast.showToast(getString(R.string.connectionError));
                ActivityMain.fragmentManager.popBackStack();
            }
        }).getTransactions();


        return root;

    }
}
