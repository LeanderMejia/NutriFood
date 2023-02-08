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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.dpio.nutrifood.R;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;

public class HomeFragment extends Fragment {

    private long backPressedValue;
    private final int TIME_INTERVAL = 2000;
    private BottomNavigationView bottomNav;
    private AutoCompleteTextView searchInput;
    private ImageView babies, toddlers, strategies, community;
    private String[] babiesArray, toddlerArray;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedValue + TIME_INTERVAL > System.currentTimeMillis()) {
                    System.exit(0);
                    return;
                }
                Toast.makeText(getActivity(), "Press again to exit", Toast.LENGTH_SHORT).show();
                backPressedValue = System.currentTimeMillis();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        babies = viewGroup.findViewById(R.id.babies);
        toddlers = viewGroup.findViewById(R.id.toddlers);
        strategies = viewGroup.findViewById(R.id.strategies);
        community = viewGroup.findViewById(R.id.community);
        searchInput = viewGroup.findViewById(R.id.searchInput);

        babiesArray = new String[]{"Mango Puree", "Ripe Papaya Puree", "Dragon Fruit Puree", "Pear Puree", "Apple Puree", "Banana Puree", "Avocado Banana Puree", "Avocado Puree", "Potato Puree", "Green Beans Puree", "Cucumber Puree", "Squash Puree", "Sweet Potato Puree", "Cauliflower Puree", "Green Peas Puree", "Broccoli Puree", "Broccoli Potato Puree", "Mashed potato", "Carrot Puree", "Carrot Potato Puree"};
        toddlerArray = new String[]{"Vege with Egg and Banana", "Pumpkin Meal with Banana and Cucumber", "Saute Green Beans with Orange and Egg", "Sweet Potato Broccoli Meal with Apple", "Vegetable Nuggets", "Vegetable Chicken Meal with Apple", "Vegetable Scrambled Egg", "Vegetable Shanghai", "Fried Cauliflower", "Fried Rice with Green Beans", "Egg fried rice with Orange and Cucumber", "Fried Broccoli", "Homemade Chicken Nuggets", "Potato Carrot Meal with Apple", "Banana Pancake", "Cauliflower Nuggets with Orange and Egg", "Colorful Fries with Banana and Egg", "Fluffy Scrambled Egg with Orange and Cucumber", "Cheese Pasta with Egg and Cucumber", "Chicken Fried Rice with Orange"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ArrayUtils.concat(babiesArray, toddlerArray));
        searchInput.setAdapter(adapter);

        performSearch();

        babies.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new BabiesMealListFragment()).addToBackStack(null).commit();
        });
        toddlers.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ToddlerMealListFragment()).addToBackStack(null).commit();
        });
        strategies.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new StrategiesFragment()).addToBackStack(null).commit();
        });
        community.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CommunityFragment()).addToBackStack(null).commit();
            bottomNav = getActivity().findViewById(R.id.bottomNav);
            bottomNav.setSelectedItemId(R.id.community);
            bottomNav.setActivated(true);
        });

        return viewGroup;
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
        boolean existInToddlers = Arrays.asList(toddlerArray).contains(searchInput.getText().toString());

        if (existInBabies) {
            String mealIndex = String.valueOf(Arrays.asList(babiesArray).indexOf(searchInput.getText().toString()));
            Fragment fragment = new BabiesMealFragment();
            openMealScreen(mealIndex, fragment);
            return;
        }
        if (existInToddlers) {
            String mealIndex = String.valueOf(Arrays.asList(toddlerArray).indexOf(searchInput.getText().toString()));
            Fragment fragment = new ToddlerMealFragment();
            openMealScreen(mealIndex, fragment);
            return;
        }
        Toast.makeText(getActivity(), searchInput.getText().toString() + " is not exist.", Toast.LENGTH_SHORT).show();
    }

    private void openMealScreen(String mealIndex, Fragment fragment) {
        Bundle bundle = new Bundle();
        bundle.putString("key", mealIndex);
        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }
}