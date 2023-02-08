package com.dpio.nutrifood.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dpio.nutrifood.Adapter.StrategiesAdapter;
import com.dpio.nutrifood.Class.ClassJSON;
import com.dpio.nutrifood.R;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StrategiesFragment extends Fragment {

    private Button backBtn;
    private RecyclerView recyclerView;
    private TabLayout tabLayout;
    private ArrayList<String> titleList = new ArrayList<>();
    private ArrayList<String> descriptionList = new ArrayList<>();
    private StrategiesAdapter strategiesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        englishStrategiesInfo();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_strategies, container, false);
        backBtn = viewGroup.findViewById(R.id.backBtn);
        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        tabLayout = viewGroup.findViewById(R.id.tabLayout);

        backBtn.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
        });

        return viewGroup;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        displayStrategiesInfo();
    }

    private JSONArray jsonArray(String jsonArrayName) throws JSONException {
        ClassJSON classJSON = new ClassJSON();
        JSONObject jsonObject = new JSONObject(classJSON.getJSONFile(getActivity()));
        JSONArray jsonArray = jsonObject.getJSONArray(jsonArrayName);
        return jsonArray;
    }

    private void englishStrategiesInfo() {
        try {
            for (int i = 0; i < jsonArray("strategies").length(); i++) {
                JSONObject strategiesData = jsonArray("strategies").getJSONObject(i);
                titleList.add(strategiesData.getString("title"));
                descriptionList.add(strategiesData.getString("description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void filipinoStrategiesInfo() {
        try {
            for (int i = 0; i < jsonArray("strategiesFilipino").length(); i++) {
                JSONObject strategiesData = jsonArray("strategiesFilipino").getJSONObject(i);
                titleList.add(strategiesData.getString("title"));
                descriptionList.add(strategiesData.getString("description"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void displayStrategiesInfo() {
        tabLayout.getTabAt(0).select();
        if (tabLayout.getTabAt(0).isSelected()) {
            englishStrategiesInfo();
            strategiesAdapter = new StrategiesAdapter(titleList, descriptionList);
            recyclerView.setAdapter(strategiesAdapter);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    englishStrategiesInfo();
                    strategiesAdapter = new StrategiesAdapter(titleList, descriptionList);
                    recyclerView.setAdapter(strategiesAdapter);
                    return;
                }
                filipinoStrategiesInfo();
                strategiesAdapter = new StrategiesAdapter(titleList, descriptionList);
                recyclerView.setAdapter(strategiesAdapter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                titleList.clear();
                descriptionList.clear();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}