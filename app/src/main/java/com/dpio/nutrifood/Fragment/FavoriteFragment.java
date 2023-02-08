package com.dpio.nutrifood.Fragment;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dpio.nutrifood.Adapter.FavoriteAdapter;
import com.dpio.nutrifood.Class.FavoriteClass;
import com.dpio.nutrifood.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoriteFragment extends Fragment {

    private BottomNavigationView bottomNav;
    private RecyclerView recyclerView;
    private ArrayList<String> mealNameList, mealImageList;
    private TextView emptyFavoriteMsg, noAccountMsg;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mealNameList = new ArrayList<>();
        mealImageList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_favorite, container, false);
        recyclerView = viewGroup.findViewById(R.id.recyclerView);
        emptyFavoriteMsg = viewGroup.findViewById(R.id.emptyFavoriteMsg);
        noAccountMsg = viewGroup.findViewById(R.id.noAccountMsg);

        if (mAuth.getCurrentUser() == null) {
            noAccountMsg.setVisibility(View.VISIBLE);
        }

        //Clear the arraylist to fix displaying multiple duplicate item
        mealNameList.clear();
        mealImageList.clear();

        return viewGroup;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FavoriteClass favoriteClass = new FavoriteClass();
        favoriteClass.validateUser();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        FavoriteAdapter favoriteAdapter = new FavoriteAdapter(mealNameList, mealImageList, getActivity());
        recyclerView.setAdapter(favoriteAdapter);

        DatabaseReference userFavoriteRef = database.getReference("FavoriteMeal").child(favoriteClass.UID);
        userFavoriteRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    emptyFavoriteMsg.setVisibility(View.VISIBLE);
                }

                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    mealNameList.add(dataSnapshot.child("mealName").getValue().toString());
                    mealImageList.add(dataSnapshot.child("mealImage").getValue().toString());
                }
                favoriteAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.i("tag", error.getMessage());
            }
        });
    }
}