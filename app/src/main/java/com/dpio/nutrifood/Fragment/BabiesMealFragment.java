package com.dpio.nutrifood.Fragment;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dpio.nutrifood.Adapter.MealIngrAdapter;
import com.dpio.nutrifood.Adapter.MealProcAdapter;
import com.dpio.nutrifood.Adapter.SuggestedMealAdapter;
import com.dpio.nutrifood.Class.ClassJSON;
import com.dpio.nutrifood.Class.FavoriteClass;
import com.dpio.nutrifood.Model.FavoriteModelClass;
import com.dpio.nutrifood.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BabiesMealFragment extends Fragment {

    private BottomNavigationView bottomNav;
    private TextView mealName, mealAllergen;
    private ImageView mealImage;
    private ChipGroup chipGroup;
    private Chip mealNutrientsChip;
    private RecyclerView ingrRecyclerView, procRecyclerView;
    private Button backBtn, copyMealBtn;
    private FloatingActionButton heartBtn;
    private TabLayout tabLayout;
    private BottomSheetDialog suggestedMealDialog;
    private FirebaseAuth mAuth;
    private FirebaseUser facebookAcct;
    private GoogleSignInAccount googleAcct;
    private MaterialToolbar toolbar;
    private AppBarLayout appbar;
    private CollapsingToolbarLayout collapseToolbar;
    private ArrayList<String> mealIngredientList = new ArrayList<>();
    private ArrayList<String> mealProcedureList = new ArrayList<>();
    private ArrayList<String> suggestedMealNameList = new ArrayList<>();
    private ArrayList<String> suggestedMealIndexList = new ArrayList<>();
    private String mealIndex;
    private JSONObject mealData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_meal, container, false);

        mealName = viewGroup.findViewById(R.id.mealName);
        mealImage = viewGroup.findViewById(R.id.mealImage);
        chipGroup = viewGroup.findViewById(R.id.chipGroup);
        ingrRecyclerView = viewGroup.findViewById(R.id.ingrRecyclerView);
        procRecyclerView = viewGroup.findViewById(R.id.procRecyclerView);
        mealAllergen = viewGroup.findViewById(R.id.mealAllergen);
        backBtn = viewGroup.findViewById(R.id.backBtn);
        heartBtn = viewGroup.findViewById(R.id.heartBtn);
        tabLayout = viewGroup.findViewById(R.id.tabLayout);
        suggestedMealDialog = new BottomSheetDialog(getContext());
        toolbar = getActivity().findViewById(R.id.toolbar);
        appbar = viewGroup.findViewById(R.id.appbar);
        collapseToolbar = viewGroup.findViewById(R.id.collapseToolbar);
        copyMealBtn = viewGroup.findViewById(R.id.copyMealBtn);

        googleAcct = GoogleSignIn.getLastSignedInAccount(getContext());
        mAuth = FirebaseAuth.getInstance();
        facebookAcct = mAuth.getCurrentUser();

        // Set title when appbar is collapse
        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            boolean isShow = true;
            int scrollRange = -1;

            if (scrollRange == -1) {
                scrollRange = appBarLayout.getTotalScrollRange();
            }
            if (scrollRange + verticalOffset == 0) {
                collapseToolbar.setTitle("NutriFood");
                collapseToolbar.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
                isShow = true;
            } else if (isShow) {
                collapseToolbar.setTitle(" ");
                isShow = false;
            }
        });

        Bundle bundle = getArguments();
        mealIndex = bundle.getString("key");
        displayMealInfo();

        backBtn.setOnClickListener(view -> {
            bottomNav = getActivity().findViewById(R.id.bottomNav);
            bottomNav.setSelectedItemId(R.id.home);
            bottomNav.setActivated(true);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new BabiesMealListFragment()).addToBackStack(null).commit();
        });

        heartBtn.setOnClickListener(view -> {
            storeInFavoriteMeal();
        });

        copyMealBtn.setOnClickListener(view -> {
            copyMealInfo();
        });

        return viewGroup;
    }

    @Override
    public void onResume() {
        super.onResume();
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        toolbar.setVisibility(View.VISIBLE);
    }

    private JSONArray jsonArray(String jsonArrayName) throws JSONException {
        ClassJSON classJSON = new ClassJSON();
        JSONObject jsonObject = new JSONObject(classJSON.getJSONFile(getActivity()));
        JSONArray jsonArray = jsonObject.getJSONArray(jsonArrayName);
        return jsonArray;
    }

    @SuppressLint("ResourceAsColor")
    private void displayMealInfo() {
        try {
            mealData = jsonArray("babiesMeal").getJSONObject(Integer.parseInt(String.valueOf(mealIndex)));

            mealName.setText(mealData.getString("meal"));

            int id = getContext().getResources().getIdentifier(mealData.getString("imageURL"), "drawable", getContext().getPackageName());
            Drawable drawable = getContext().getResources().getDrawable(id);
            Glide.with(getContext()).load(drawable).into(mealImage);

            int nutrientLength = mealData.getJSONArray("nutrients").length();
            for (int i = 0; i < nutrientLength; i++) {
                String mealName = mealData.getString("meal");
                String mealNutrients = mealData.getJSONArray("nutrients").get(i).toString();
                mealNutrientsChip = new Chip(getContext());
                mealNutrientsChip.setText(mealNutrients);
                chipGroup.addView(mealNutrientsChip);

                mealNutrientsChip.setOnClickListener(view -> {
                    displaySuggestedMeal(mealName, mealNutrients);
                });
            }

            tabLayout.getTabAt(0).select();
            if (tabLayout.getTabAt(0).isSelected()) {
                getLanguageMealInfo("babiesMeal");
            }

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    if (tab.getPosition() == 1) {
                        getLanguageMealInfo("babiesMealFilipino");
                        try {
                            mealData = jsonArray("babiesMeal").getJSONObject(Integer.parseInt(String.valueOf(mealIndex)));
                            int nutrientLength = mealData.getJSONArray("nutrients").length();
                            for (int i = 0; i < nutrientLength; i++) {
                                String mealName = mealData.getString("meal");
                                String mealNutrients = mealData.getJSONArray("nutrients").get(i).toString();

                                mealNutrientsChip.setOnClickListener(view -> {
                                    displaySuggestedMeal(mealName, mealNutrients);
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return;
                    }
                    getLanguageMealInfo("babiesMeal");
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    mealIngredientList.clear();
                    mealProcedureList.clear();
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }

            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getLanguageMealInfo(String jsonArrayName) {
        try {
            mealData = jsonArray(jsonArrayName).getJSONObject(Integer.parseInt(String.valueOf(mealIndex)));

            int ingredientLength = mealData.getJSONArray("ingredients").length();
            for (int i = 0; i < ingredientLength; i++) {
                mealIngredientList.add(mealData.getJSONArray("ingredients").get(i).toString());
                MealIngrAdapter adapter = new MealIngrAdapter(mealIngredientList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
                ingrRecyclerView.setNestedScrollingEnabled(false);
                ingrRecyclerView.setLayoutManager(layoutManager);
                ingrRecyclerView.setAdapter(adapter);
            }

            int procedureLength = mealData.getJSONArray("procedures").length();
            for (int i = 0; i < procedureLength; i++) {
                mealProcedureList.add(mealData.getJSONArray("procedures").get(i).toString());
                MealProcAdapter adapter = new MealProcAdapter(mealProcedureList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
                procRecyclerView.setNestedScrollingEnabled(false);
                procRecyclerView.setLayoutManager(layoutManager);
                procRecyclerView.setAdapter(adapter);
            }

            mealAllergen.setText(mealData.getString("allergen"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void storeInFavoriteMeal() {
        FavoriteClass favoriteClass = new FavoriteClass();
        favoriteClass.validateUser();

        if (favoriteClass.UID.equals("No Account")) {
            Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Need to sign in to use the favorite feature.", Snackbar.LENGTH_LONG).show();
            return;
        }

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userFavoriteRef = database.getReference("FavoriteMeal").child(favoriteClass.UID);
        userFavoriteRef.keepSynced(true);
        writeInRealtimeDB(userFavoriteRef, favoriteClass);
    }

    private void writeInRealtimeDB(DatabaseReference reference, FavoriteClass favoriteClass) {
        try {
            ClassJSON classJSON = new ClassJSON();
            JSONObject jsonObject = new JSONObject(classJSON.getJSONFile(getActivity()));
            JSONArray jsonArray = jsonObject.getJSONArray("babiesMeal");
            mealData = jsonArray.getJSONObject(Integer.parseInt(String.valueOf(mealIndex)));

            String mealName = mealData.getString("meal");
            String mealImage = mealData.getString("imageURL");
            String mealCategory = "Babies";

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String mealNameData = dataSnapshot.child("mealName").getValue().toString();

                        if (favoriteClass.mealIsExist(mealNameData, mealName)) {
                            Snackbar.make(getActivity().findViewById(R.id.drawerLayout), mealNameData + " already exist in Favorites", Snackbar.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Snackbar.make(
                            getActivity().findViewById(R.id.drawerLayout), "Add to Favorites", Snackbar.LENGTH_SHORT)
                            .setAction("View", view1 -> {
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new FavoriteFragment()).addToBackStack(null).commit();
                                bottomNav = getActivity().findViewById(R.id.bottomNav);
                                bottomNav.setSelectedItemId(R.id.favorite);
                                bottomNav.setActivated(true);
                            })
                            .setActionTextColor(getResources().getColor(R.color.purple_200))
                            .show();

                    FavoriteModelClass mealData = new FavoriteModelClass(mealName, mealImage, mealCategory, mealIndex);
                    reference.push().setValue(mealData);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d("Error", error.getMessage());
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void copyMealInfo() {
        String formattedIngredients = mealIngredientList.toString().replace(",", "").replace("[", "").replace("]", "").trim();
        String formattedProcedures = mealProcedureList.toString().replace(",", "").replace("[", "").replace("]", "").trim();
        String mealCopied = mealName.getText().toString() + "\n" + formattedIngredients + "\n" + formattedProcedures + "\n";

        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copy", mealCopied);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getActivity(), "Meal copied", Toast.LENGTH_SHORT).show();
    }

    private void displaySuggestedMeal(String mealName, String mealNutrients) {
        try {
            suggestedMealDialog.show();

            int babiesMealLength = jsonArray("babiesMeal").length();
            for (int i = 0; i < babiesMealLength; i++) {
                JSONObject mealData = jsonArray("babiesMeal").getJSONObject(i);
                String mealNameInJSON = mealData.getString("meal");
                String mealNutrientsInJSON = mealData.getString("nutrients");

                if (mealNutrientsInJSON.contains(mealNutrients) && !mealNameInJSON.contains(mealName)) {
                    suggestedMealIndexList.add(String.valueOf(i));
                    suggestedMealNameList.add(mealData.getString("meal"));

                    View view = getLayoutInflater().inflate(R.layout.bottom_sheet, null, false);
                    RecyclerView suggestedMealRecyclerView = view.findViewById(R.id.suggestedMealRecyclerView);

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
                    suggestedMealRecyclerView.setLayoutManager(linearLayoutManager);
                    SuggestedMealAdapter suggestedMealAdapter = new SuggestedMealAdapter(getContext(), suggestedMealDialog, suggestedMealIndexList, suggestedMealNameList, "BabiesMeal");
                    suggestedMealRecyclerView.setAdapter(suggestedMealAdapter);

                    suggestedMealDialog.setContentView(view);
                }
            }

            suggestedMealDialog.setOnDismissListener(dialogInterface -> {
                suggestedMealIndexList.clear();
                suggestedMealNameList.clear();
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}