package com.dpio.nutrifood.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dpio.nutrifood.Adapter.ToddlersListAdapter;
import com.dpio.nutrifood.Class.ClassJSON;
import com.dpio.nutrifood.R;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ToddlerMealListFragment extends Fragment {

    private BottomNavigationView bottomNav;
    private RecyclerView recyclerView;
    private Button backBtn;
    private AutoCompleteTextView searchInput;
    ArrayList<String> mealNameList = new ArrayList<>();
    ArrayList<String> mealImageList = new ArrayList<>();
    ArrayList<Integer> mealIndexList = new ArrayList<>();
    private String[] toddlersArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayToddlersListInfo();

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
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_toddler_meal_list, container, false);
        backBtn = viewGroup.findViewById(R.id.backBtn);
        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        searchInput = viewGroup.findViewById(R.id.searchInput);

        toddlersArray = new String[]{"Vege with Egg and Banana", "Pumpkin Meal with Banana and Cucumber", "Saute Green Beans with Orange and Egg", "Sweet Potato Broccoli Meal with Apple", "Vegetable Nuggets", "Vegetable Chicken Meal with Apple", "Vegetable Scrambled Egg", "Vegetable Shanghai", "Fried Cauliflower", "Fried Rice with Green Beans", "Egg fried rice with Orange and Cucumber", "Fried Broccoli", "Homemade Chicken Nuggets", "Potato Carrot Meal with Apple", "Banana Pancake", "Cauliflower Nuggets with Orange and Egg", "Colorful Fries with Banana and Egg", "Fluffy Scrambled Egg with Orange and Cucumber", "Cheese Pasta with Egg and Cucumber", "Chicken Fried Rice with Orange"};

        backBtn.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ArrayUtils.concat(toddlersArray));
        searchInput.setAdapter(adapter);

        performSearch();

        return viewGroup;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        ToddlersListAdapter toddlersListAdapter = new ToddlersListAdapter(mealNameList, mealImageList, mealIndexList, getActivity());
        recyclerView.setAdapter(toddlersListAdapter);
    }

    private void displayToddlersListInfo() {
        try {
            ClassJSON classJSON = new ClassJSON();
            JSONObject jsonObject = new JSONObject(classJSON.getJSONFile(getActivity()));
            JSONArray jsonArray = jsonObject.getJSONArray("toddlersMeal");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject mealData = jsonArray.getJSONObject(i);
                mealNameList.add(mealData.getString("meal"));
                mealImageList.add(mealData.getString("imageURL"));
                mealIndexList.add(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void performSearch() {
        searchInput.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                getSearchResult();
                return true;
            }
            return false;
        });
    }

    private void getSearchResult() {
        View view = getActivity().getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        searchInput.clearFocus();

        boolean existInToddlers = Arrays.asList(toddlersArray).contains(searchInput.getText().toString());

        if (existInToddlers) {
            String mealIndex = String.valueOf(Arrays.asList(toddlersArray).indexOf(searchInput.getText().toString()));
            Fragment fragment = new ToddlerMealFragment();
            openToddlersMealScreen(mealIndex, fragment);
            return;
        }
        Toast.makeText(getActivity(), searchInput.getText().toString() + " is not exist.", Toast.LENGTH_SHORT).show();
    }

    private void openToddlersMealScreen(String mealIndex, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("key", mealIndex);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }
}