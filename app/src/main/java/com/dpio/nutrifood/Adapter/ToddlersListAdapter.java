package com.dpio.nutrifood.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dpio.nutrifood.Fragment.ToddlerMealFragment;
import com.dpio.nutrifood.R;

import java.util.ArrayList;

public class ToddlersListAdapter extends RecyclerView.Adapter<ToddlersListAdapter.MyViewHolder> {

    private Context context;
    ArrayList<String> mealNameList;
    ArrayList<String> mealImageList;
    ArrayList<Integer> mealIndexList;

    public ToddlersListAdapter(ArrayList<String> mealNameList, ArrayList<String> mealImageList, ArrayList<Integer> mealIndexList, Context context) {
        this.mealNameList = mealNameList;
        this.mealImageList = mealImageList;
        this.mealIndexList = mealIndexList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mealName;
        private ImageView mealImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mealName = itemView.findViewById(R.id.mealName);
            mealImage = itemView.findViewById(R.id.mealImage);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_list_layout, parent, false);
        ToddlersListAdapter.MyViewHolder holder = new ToddlersListAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String drawableStr = mealImageList.get(position);
        int id = context.getResources().getIdentifier(drawableStr, "drawable", context.getPackageName());
        Drawable drawable = context.getResources().getDrawable(id);

        holder.mealName.setText(mealNameList.get(position));
        Glide.with(context).load(drawable).into(holder.mealImage);

        //Pass meal index to meal screen
        Bundle bundle = new Bundle();
        String mealIndex = String.valueOf(mealIndexList.get(position));
        bundle.putString("key", mealIndex);

        //Open Meal Screen
        holder.itemView.setOnClickListener(view -> {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            Fragment fragment = new ToddlerMealFragment();
            fragment.setArguments(bundle);
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
        });
    }

    @Override
    public int getItemCount() {
        return mealNameList.size();
    }
}
