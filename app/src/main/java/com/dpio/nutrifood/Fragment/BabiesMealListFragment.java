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

import com.dpio.nutrifood.Adapter.BabiesListAdapter;
import com.dpio.nutrifood.Class.ClassJSON;
import com.dpio.nutrifood.R;
import com.google.android.gms.common.util.ArrayUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


public class BabiesMealListFragment extends Fragment {

    private RecyclerView recyclerView;
    private Button backBtn;
    private AutoCompleteTextView searchInput;
    ArrayList<String> mealNameList = new ArrayList<>();
    ArrayList<String> mealImageList = new ArrayList<>();
    ArrayList<Integer> mealIndexList = new ArrayList<>();
    private String[] babiesArray;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        displayBabiesListInfo();

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_babies_meal_list, container, false);
        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        backBtn = viewGroup.findViewById(R.id.backBtn);
        searchInput = viewGroup.findViewById(R.id.searchInput);

        babiesArray = new String[]{"Mango Puree", "Ripe Papaya Puree", "Dragon Fruit Puree", "Pear Puree", "Apple Puree", "Banana Puree", "Avocado Banana Puree", "Avocado Puree", "Potato Puree", "Green Beans Puree", "Cucumber Puree", "Squash Puree", "Sweet Potato Puree", "Cauliflower Puree", "Green Peas Puree", "Broccoli Puree", "Broccoli Potato Puree", "Mashed potato", "Carrot Puree", "Carrot Potato Puree"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ArrayUtils.concat(babiesArray));
        searchInput.setAdapter(adapter);

        performSearch();

        backBtn.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
        });

        return viewGroup;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        BabiesListAdapter babiesListAdapter = new BabiesListAdapter(mealNameList, mealImageList, mealIndexList, getActivity());
        recyclerView.setAdapter(babiesListAdapter);
    }

    private void displayBabiesListInfo() {
        try {
            ClassJSON classJSON = new ClassJSON();
            JSONObject jsonObject = new JSONObject(classJSON.getJSONFile(getActivity()));
            JSONArray jsonArray = jsonObject.getJSONArray("babiesMeal");
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

        boolean existInBabies = Arrays.asList(babiesArray).contains(searchInput.getText().toString());

        if (existInBabies) {
            String mealIndex = String.valueOf(Arrays.asList(babiesArray).indexOf(searchInput.getText().toString()));
            Fragment fragment = new BabiesMealFragment();
            openBabiesMealScreen(mealIndex, fragment);
            return;
        }
        Toast.makeText(getActivity(), searchInput.getText().toString() + " is not exist.", Toast.LENGTH_SHORT).show();
    }

    private void openBabiesMealScreen(String mealIndex, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("key", mealIndex);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }
}