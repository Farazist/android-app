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
import project.adapters.AdapterQuestionRecycler;
import project.connection.Commands;
import project.structures.StructQuestion;

public class FragmentFAQs extends Fragment {

    private RecyclerView lstFAQs;
    private ViewGroup loader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_faqs, container, false);

        lstFAQs = root.findViewById(R.id.lstFAQs);
        loader = root.findViewById(R.id.loader);
        final ArrayList<StructQuestion> questions = new ArrayList<>();

        loading();
        new Commands().setCompleteListener(new Commands.onCommandCompleteListener() {
            @Override
            public void onComplete(String data) {
                try {
                    questions.clear();
                    JSONArray questionsArray = new JSONArray(data) ;
                    for(int i=0;i<questionsArray.length();i++){
                        JSONObject question = questionsArray.getJSONObject(i);
                        StructQuestion item = new StructQuestion();
                        item.question = question.getString("question");
                        item.answer = question.getString("answer");
                        questions.add(item);
                        AdapterQuestionRecycler adapter = new AdapterQuestionRecycler(questions);
                        lstFAQs.setLayoutManager(new LinearLayoutManager(App.getContext()));
                        lstFAQs.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        loaded();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFail(String error) {

            }
        }).getFAQs();

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                return;
            }
        });
        return root;

    }

    private void loading() {
        lstFAQs.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }

    private void loaded() {
        lstFAQs.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);
    }
}
