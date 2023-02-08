package com.dpio.nutrifood.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.dpio.nutrifood.Fragment.BabiesMealFragment;
import com.dpio.nutrifood.Fragment.ToddlerMealFragment;
import com.dpio.nutrifood.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

public class SuggestedMealAdapter extends RecyclerView.Adapter<SuggestedMealAdapter.MyViewHolder> {

    private BottomSheetDialog suggestedMealDialog;
    private Context context;
    ArrayList<String> mealIndexList;
    ArrayList<String> mealNameList;
    String mealCategory;

    public SuggestedMealAdapter(Context context, BottomSheetDialog suggestedMealDialog, ArrayList<String> mealIndexList, ArrayList<String> mealNameList, String mealCategory) {
        this.context = context;
        this.suggestedMealDialog = suggestedMealDialog;
        this.mealIndexList = mealIndexList;
        this.mealNameList = mealNameList;
        this.mealCategory = mealCategory;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mealContainer;
        private TextView mealName;
        private ImageButton openBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mealContainer = itemView.findViewById(R.id.mealContainer);
            mealName = itemView.findViewById(R.id.mealName);
            openBtn = itemView.findViewById(R.id.openBtn);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggested_meal_layout, parent, false);
        SuggestedMealAdapter.MyViewHolder holder = new SuggestedMealAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mealName.setText(mealNameList.get(position));

        holder.mealContainer.setOnClickListener(view -> openMealScreen(position, view));
        holder.openBtn.setOnClickListener(view -> openMealScreen(position, view));
    }

    @Override
    public int getItemCount() {
        return mealIndexList.size();
    }

    private void openMealScreen(int position, View view) {
        suggestedMealDialog.dismiss();
        if (mealCategory.equals("BabiesMeal")) {
            chooseFragmentScreen(new BabiesMealFragment(), position, view);
            return;
        }
        chooseFragmentScreen(new ToddlerMealFragment(), position, view);
    }

    private void chooseFragmentScreen(Fragment fragment, int position, View view) {

        Bundle bundle = new Bundle();
        String mealIndex = String.valueOf(mealIndexList.get(position));
        bundle.putString("key", mealIndex);

        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        fragment.setArguments(bundle);
        activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
    }
}
