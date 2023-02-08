package com.dpio.nutrifood.Fragment;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dpio.nutrifood.Adapter.ReferenceAdapter;
import com.dpio.nutrifood.Class.ClassJSON;
import com.dpio.nutrifood.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReferenceFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView strategiesLink;
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> locationList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayMealReferenceInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_reference, container, false);

        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        strategiesLink = viewGroup.findViewById(R.id.strategiesLink);

        strategiesLink.setMovementMethod(LinkMovementMethod.getInstance());
        return viewGroup;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        ReferenceAdapter referenceAdapter = new ReferenceAdapter(nameList, locationList);
        recyclerView.setAdapter(referenceAdapter);
    }

    private void displayMealReferenceInfo() {
        try {
            ClassJSON classJSON = new ClassJSON();
            JSONObject jsonObject = new JSONObject(classJSON.getJSONFile(getActivity()));
            JSONArray jsonArray = jsonObject.getJSONArray("reference");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject ourTeamData = jsonArray.getJSONObject(i);
                nameList.add(ourTeamData.getString("name"));
                locationList.add(ourTeamData.getString("location"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}