package com.dpio.nutrifood.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dpio.nutrifood.Class.FavoriteClass;
import com.dpio.nutrifood.Fragment.BabiesMealFragment;
import com.dpio.nutrifood.Fragment.ToddlerMealFragment;
import com.dpio.nutrifood.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.MyViewHolder> {

    private BottomNavigationView bottomNav;
    private Context context;
    ArrayList<String> mealNameList;
    ArrayList<String> mealImageList;

    public FavoriteAdapter(ArrayList<String> mealNameList, ArrayList<String> mealImageList, Context context) {
        this.mealNameList = mealNameList;
        this.mealImageList = mealImageList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView mealImage;
        private TextView mealName;
        private ImageButton deleteBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mealImage = itemView.findViewById(R.id.mealImage);
            mealName = itemView.findViewById(R.id.mealName);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_layout, parent, false);
        FavoriteAdapter.MyViewHolder holder = new FavoriteAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String drawableStr = mealImageList.get(position);
        int id = context.getResources().getIdentifier(drawableStr, "drawable", context.getPackageName());
        Drawable drawable = context.getResources().getDrawable(id);

        Glide.with(context).load(drawable).into(holder.mealImage);
        holder.mealName.setText(mealNameList.get(position));

        holder.itemView.setOnClickListener(view -> {
            String mealName = mealNameList.get(position);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FavoriteClass favoriteClass = new FavoriteClass();
            favoriteClass.validateUser();

            DatabaseReference userFavoriteRef = database.getReference("FavoriteMeal").child(favoriteClass.UID);
            userFavoriteRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String mealCategoryData = dataSnapshot.child("mealCategory").getValue().toString();
                        String mealNameData = dataSnapshot.child("mealName").getValue().toString();

                        if (mealCategoryData.equals("Babies") && mealNameData.equals(mealName)) {
                            String mealIndex = dataSnapshot.child("mealIndex").getValue().toString();
                            openBabiesMealFragment(mealIndex, holder.itemView);
                        }

                        if (mealCategoryData.equals("Toddlers") && mealNameData.equals(mealName)) {
                            String mealIndex = dataSnapshot.child("mealIndex").getValue().toString();
                            openToddlersMealFragment(mealIndex, holder.itemView);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.i("tag", error.getMessage());
                }
            });
        });

        holder.deleteBtn.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            String mealName = mealNameList.get(position);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FavoriteClass favoriteClass = new FavoriteClass();
            favoriteClass.validateUser();

            MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme);
            dialog.setIcon(R.drawable.ic_baseline_warning_24);
            dialog.setTitle("Are you sure?");
            dialog.setMessage("Do you really want to remove this meal? This action cannot be undone.");

            dialog.setPositiveButton("REMOVE", (dialogInterface, i) -> {
                DatabaseReference userFavoriteRef = database.getReference().child("FavoriteMeal").child(favoriteClass.UID);
                userFavoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String mealNameData = dataSnapshot.child("mealName").getValue().toString();

                                if (mealNameData.equals(mealName)){
                                    Snackbar.make(activity.findViewById(R.id.drawerLayout), mealName + " removed from favorites", Snackbar.LENGTH_SHORT).show();
                                    String pushKey = dataSnapshot.getKey();
                                    userFavoriteRef.child(pushKey).removeValue();
                                    notifyDataSetChanged();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("tag", error.getMessage());
                    }
                });
                mealNameList.clear();
                mealImageList.clear();
            });

            dialog.setNegativeButton("CANCEL", null);

            dialog.show();
        });


    }

    @Override
    public int getItemCount() {
        return mealNameList.size();
    }

    public void openBabiesMealFragment(String mealIndex, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("key", mealIndex);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fragment = new BabiesMealFragment();
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }

    public void openToddlersMealFragment(String mealIndex, View view) {
        Bundle bundle = new Bundle();
        bundle.putString("key", mealIndex);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        Fragment fragment = new ToddlerMealFragment();
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }
}
