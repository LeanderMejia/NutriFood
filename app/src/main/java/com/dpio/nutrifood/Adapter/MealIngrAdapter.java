package com.dpio.nutrifood.Adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.dpio.nutrifood.R;

import java.util.ArrayList;

public class MealIngrAdapter extends RecyclerView.Adapter<MealIngrAdapter.MyViewHolder> {

    public ArrayList<String> mealIngrList;

    public MealIngrAdapter(ArrayList<String> mealIngrList) {
        this.mealIngrList = mealIngrList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mealIngredient;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mealIngredient = itemView.findViewById(R.id.text);
        }
    }

    @NonNull
    @Override
    public MealIngrAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingr_proc_list_layout, parent, false);
        MealIngrAdapter.MyViewHolder holder = new MealIngrAdapter.MyViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull MealIngrAdapter.MyViewHolder holder, int position) {
        holder.mealIngredient.setText(mealIngrList.get(position));
    }

    @Override
    public int getItemCount() {
        return mealIngrList.size();
    }

}
