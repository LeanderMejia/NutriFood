package com.dpio.nutrifood.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dpio.nutrifood.Adapter.OurTeamAdapter;
import com.dpio.nutrifood.Class.ClassJSON;
import com.dpio.nutrifood.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OurTeamFragment extends Fragment {

    private RecyclerView recyclerView;
    private ArrayList<String> profileList = new ArrayList<>();
    private ArrayList<String> nameList = new ArrayList<>();
    private ArrayList<String> roleList = new ArrayList<>();
    private ArrayList<String> emailList = new ArrayList<>();
    private ArrayList<String> locationList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayOurTeamInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_our_team, container, false);

        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        return viewGroup;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        OurTeamAdapter ourTeamAdapter = new OurTeamAdapter(profileList, nameList, roleList, emailList, locationList, getActivity());
        recyclerView.setAdapter(ourTeamAdapter);
    }

    private void displayOurTeamInfo() {
        try {
            ClassJSON classJSON = new ClassJSON();
            JSONObject jsonObject = new JSONObject(classJSON.getJSONFile(getActivity()));
            JSONArray jsonArray = jsonObject.getJSONArray("ourTeam");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject ourTeamData = jsonArray.getJSONObject(i);
                profileList.add(ourTeamData.getString("profile"));
                nameList.add(ourTeamData.getString("name"));
                roleList.add(ourTeamData.getString("role"));
                emailList.add(ourTeamData.getString("email"));
                locationList.add(ourTeamData.getString("location"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}