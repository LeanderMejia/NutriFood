package com.dpio.nutrifood.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.dpio.nutrifood.Adapter.CommunityAdapter;
import com.dpio.nutrifood.Class.FavoriteClass;
import com.dpio.nutrifood.Model.PostModelClass;
import com.dpio.nutrifood.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private static final String TAG = "TAG";
    private boolean showDialog;
    private BottomNavigationView bottomNav;
    private CircularProgressIndicator progressIndicator;
    private TextView emptyCommunityMsg, noAccountMsg, emptyOwnPostMsg;
    private FloatingActionButton postBtn;
    private RecyclerView recyclerView;
    private ImageButton filterMenu;
    private SwipeRefreshLayout swipeLayout;
    private ArrayList<String> userNameList, userProfileList, mealNameList, mealImageList;
    private List<String> mealIngredientsList, mealProceduresList;
    private CommunityAdapter communityAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userNameList = new ArrayList<>();
        userProfileList = new ArrayList<>();
        mealNameList = new ArrayList<>();
        mealIngredientsList = new ArrayList<>();
        mealProceduresList = new ArrayList<>();
        mealImageList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            deleteCommunityMealPost();
            displayWarningMessage();

            if (getSharedPrefFilter() == null || getSharedPrefFilter().equals("newestFilter")) {
                getByNewestCommunityMeal();
            } else if (getSharedPrefFilter().equals("ratingFilter")) {
                getByRatingCommunityMeal();
            } else if (getSharedPrefFilter().equals("ownPostFilter")) {
                getByOwnPostCommunityMeal();
            }
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                bottomNav = getActivity().findViewById(R.id.bottomNav);
                bottomNav.setSelectedItemId(R.id.home);
                bottomNav.setActivated(true);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).addToBackStack(null).commit();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"ServiceCast", "NotifyDataSetChanged"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_community, container, false);

        emptyCommunityMsg = viewGroup.findViewById(R.id.emptyCommunityMsg);
        noAccountMsg = viewGroup.findViewById(R.id.noAccountMsg);
        emptyOwnPostMsg = viewGroup.findViewById(R.id.emptyOwnPostMsg);
        progressIndicator = viewGroup.findViewById(R.id.progressIndicator);
        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        postBtn = viewGroup.findViewById(R.id.postBtn);
        filterMenu = viewGroup.findViewById(R.id.menu);
        swipeLayout = viewGroup.findViewById(R.id.swipeLayout);

        if (mAuth.getCurrentUser() == null) {
            noAccountMsg.setVisibility(View.VISIBLE);
            progressIndicator.setVisibility(View.GONE);
            postBtn.setVisibility(View.GONE);
            filterMenu.setVisibility(View.GONE);
        }

        postBtn.setOnClickListener(view -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CommunityPostFragment()).addToBackStack(null).commit();
        });

        filterMenu.setOnClickListener(view -> {
            menuFilter(view);
        });

        return viewGroup;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        communityAdapter = new CommunityAdapter(userNameList, userProfileList, mealNameList, mealIngredientsList, mealProceduresList, mealImageList);
        recyclerView.setAdapter(communityAdapter);

        swipeLayout.setOnRefreshListener(() -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CommunityFragment()).addToBackStack(null).commit();
            communityAdapter.notifyDataSetChanged();
            swipeLayout.setRefreshing(false);
        });
    }


    @SuppressLint("RestrictedApi")
    private void menuFilter(View view) {
        @SuppressLint("RestrictedApi")
        MenuBuilder menuBuilder = new MenuBuilder(getContext());
        MenuInflater menuInflater = new MenuInflater(getContext());
        menuInflater.inflate(R.menu.filter_menu, menuBuilder);


        @SuppressLint("RestrictedApi")
        MenuPopupHelper menuPopupHelper = new MenuPopupHelper(getContext(), menuBuilder, view);
        menuPopupHelper.setForceShowIcon(true);
        menuBuilder.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                if (item.getItemId() == R.id.newest) {
                    progressIndicator.setVisibility(View.VISIBLE);
                    clearCommunityMealArrayList();
                    getByNewestCommunityMeal();
                    return true;
                }
                if (item.getItemId() == R.id.rating) {
                    progressIndicator.setVisibility(View.VISIBLE);
                    clearCommunityMealArrayList();
                    getByRatingCommunityMeal();
                    return true;
                }
                if (item.getItemId() == R.id.ownPost) {
                    progressIndicator.setVisibility(View.VISIBLE);
                    clearCommunityMealArrayList();
                    getByOwnPostCommunityMeal();
                    return true;
                }
                return false;
            }

            @Override
            public void onMenuModeChange(@NonNull MenuBuilder menu) {

            }
        });
        menuPopupHelper.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getByNewestCommunityMeal() {
        FavoriteClass favoriteClass = new FavoriteClass();
        favoriteClass.validateUser();
        createSharedPref("newestFilter");

        CollectionReference postRef = db.collection("Post");
        Query newestQuery = postRef.orderBy("postDate", Query.Direction.DESCENDING);
        newestQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Log.d(TAG, "Snapshot is empty: " + queryDocumentSnapshots.isEmpty());
                progressIndicator.setVisibility(View.GONE);
                emptyOwnPostMsg.setVisibility(View.GONE);
                emptyCommunityMsg.setVisibility(View.VISIBLE);
                return;
            }
            progressIndicator.setVisibility(View.GONE);
            emptyCommunityMsg.setVisibility(View.GONE);
            emptyOwnPostMsg.setVisibility(View.GONE);

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                PostModelClass communityMeal = documentSnapshot.toObject(PostModelClass.class);
                userNameList.add(communityMeal.getUserName());
                userProfileList.add(communityMeal.getUserProfile());
                mealNameList.add(communityMeal.getMealName());
                mealIngredientsList.add(String.valueOf(communityMeal.getMealIngrArray()));
                mealProceduresList.add(String.valueOf(communityMeal.getMealProcArray()));
                mealImageList.add(communityMeal.getMealImage());
            }
            communityAdapter.notifyDataSetChanged();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getByRatingCommunityMeal() {
        createSharedPref("ratingFilter");

        CollectionReference postRef = db.collection("Post");
        Query ratingQuery = postRef.orderBy("heartCount", Query.Direction.DESCENDING);

        ratingQuery.addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.d(TAG, error.getMessage());
                return;
            }
            if (value.isEmpty()) {
                Log.d(TAG, "Empty rating query");
                progressIndicator.setVisibility(View.GONE);
                emptyOwnPostMsg.setVisibility(View.GONE);
                emptyCommunityMsg.setVisibility(View.VISIBLE);
                return;
            }

            progressIndicator.setVisibility(View.GONE);
            emptyCommunityMsg.setVisibility(View.GONE);
            emptyOwnPostMsg.setVisibility(View.GONE);

            clearCommunityMealArrayList();
            for (DocumentSnapshot documentSnapshot : value) {
                PostModelClass communityMeal = documentSnapshot.toObject(PostModelClass.class);
                userNameList.add(communityMeal.getUserName());
                userProfileList.add(communityMeal.getUserProfile());
                mealNameList.add(communityMeal.getMealName());
                mealIngredientsList.add(String.valueOf(communityMeal.getMealIngrArray()));
                mealProceduresList.add(String.valueOf(communityMeal.getMealProcArray()));
                mealImageList.add(communityMeal.getMealImage());
            }
            communityAdapter.notifyDataSetChanged();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getByOwnPostCommunityMeal() {
        String UID = mAuth.getCurrentUser().getUid();
        createSharedPref("ownPostFilter");

        CollectionReference postRef = db.collection("Post");
        Query ownPostQuery = postRef.whereEqualTo("uid", UID).orderBy("postDate", Query.Direction.DESCENDING);

        ownPostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Log.d(TAG, "Snapshot is empty: " + queryDocumentSnapshots.isEmpty());
                progressIndicator.setVisibility(View.GONE);
                emptyCommunityMsg.setVisibility(View.GONE);
                emptyOwnPostMsg.setVisibility(View.VISIBLE);
                return;
            }
            progressIndicator.setVisibility(View.GONE);
            emptyCommunityMsg.setVisibility(View.GONE);

            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                PostModelClass communityMeal = documentSnapshot.toObject(PostModelClass.class);
                userNameList.add(communityMeal.getUserName());
                userProfileList.add(communityMeal.getUserProfile());
                mealNameList.add(communityMeal.getMealName());
                mealIngredientsList.add(String.valueOf(communityMeal.getMealIngrArray()));
                mealProceduresList.add(String.valueOf(communityMeal.getMealProcArray()));
                mealImageList.add(communityMeal.getMealImage());
            }
            communityAdapter.notifyDataSetChanged();
        });
    }


    private void deleteCommunityMealPost() {
        CollectionReference reportRef = db.collection("ReportPost");
        Query deletedReportQuery = reportRef.whereEqualTo("postIsDeleted", true);

        deletedReportQuery.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "Task unsuccessful " + task.getException().getMessage());
                return;
            }
            if (task.getResult().isEmpty()) {
                Log.d(TAG, "Deleted Report Query is  empty");
                return;
            }

            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                String postId = documentSnapshot.getString("postId");

                CollectionReference postRef = db.collection("Post");
                Query deletePostQuery = postRef.orderBy("postDate", Query.Direction.DESCENDING);
                deletePostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                        String postDocumentId = documentSnapshot1.getId();

                        if (postId.equals(postDocumentId)) {
                            String mealImage = documentSnapshot1.getString("mealImage");
                            Task<Void> imagesRef = FirebaseStorage.getInstance().getReferenceFromUrl(mealImage).delete();
                            postRef.document(postDocumentId).delete();

                        }
                    }
                });

                CollectionReference commentRef = db.collection("CommentPost");
                Query deleteCommentQuery = commentRef.whereEqualTo("postId", postId);
                deleteCommentQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                        String commentDocumentId = documentSnapshot1.getId();
                        commentRef.document(commentDocumentId).delete();
                    }
                });

                CollectionReference ratingRef = db.collection("RatingPost");
                Query deleteRatingQuery = ratingRef.whereEqualTo("postId", postId);
                deleteRatingQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots) {
                        String ratingDocumentId = documentSnapshot1.getId();
                        ratingRef.document(ratingDocumentId).delete();
                    }
                });

                reportRef.document(documentSnapshot.getId()).delete();
            }
        });
    }

    private void clearCommunityMealArrayList() {
        userNameList.clear();
        userProfileList.clear();
        mealNameList.clear();
        mealIngredientsList.clear();
        mealProceduresList.clear();
        mealImageList.clear();
    }


    @SuppressLint({"ResourceAsColor", "CommitPrefEdits"})
    private void displayWarningMessage() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        String uid = mAuth.getCurrentUser().getUid();

        CollectionReference reportRef = db.collection("ReportPost");
        Query reportQuery = reportRef.whereEqualTo("uid", uid).whereEqualTo("postIsDeleted", true);
        reportQuery.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "Task unsuccessful " + task.getException().getMessage());
                return;
            }
            if (task.getResult().isEmpty()) {
                Log.d(TAG, "Report Query is  empty");
                return;
            }

            int countViolation = task.getResult().size();

            if (countViolation == 3) {
                for (DocumentSnapshot documentSnapshot1 : task.getResult()) {
                    String postId = documentSnapshot1.getString("postId");
                    reportRef.whereEqualTo("postId", postId);
                    reportRef.document().delete();
                }
                mAuth.getCurrentUser().delete();
                Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Your account has been ban due to violation. " + countViolation + "/3", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Okay", null)
                        .setActionTextColor(getResources().getColor(R.color.purple_200))
                        .show();
                return;
            }

            showDialog = sharedPreferences.getInt("nextCount", 1) == countViolation;

            if (showDialog) {
                Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Your post has been deleted due to violation. " + countViolation + "/3", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Okay", view -> {
                            SharedPreferences.Editor editor = sharedPreferences.edit().putInt("nextCount", countViolation + 1);
                            editor.commit();
                        })
                        .setActionTextColor(getResources().getColor(R.color.purple_200))
                        .show();
            }
        });
    }


    private void createSharedPref(String sharedPrefMessage) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit().putString("currentFilter", sharedPrefMessage);
        editor.commit();
    }

    private String getSharedPrefFilter() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentFilter", null);
    }
}