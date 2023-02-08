package com.dpio.nutrifood.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dpio.nutrifood.R;

import java.util.ArrayList;

public class MealProcAdapter extends RecyclerView.Adapter<MealProcAdapter.MyViewHolder> {

    public ArrayList<String> mealProcList;

    public MealProcAdapter(ArrayList<String> mealProcList) {
        this.mealProcList = mealProcList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mealProcedure;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mealProcedure = itemView.findViewById(R.id.text);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingr_proc_list_layout, parent, false);
        MealProcAdapter.MyViewHolder holder = new MealProcAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mealProcedure.setText(mealProcList.get(position));
    }

    @Override
    public int getItemCount() {
        return mealProcList.size();
    }
}
