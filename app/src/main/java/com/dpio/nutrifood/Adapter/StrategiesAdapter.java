package com.dpio.nutrifood.Adapter;

import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.dpio.nutrifood.R;

import java.util.ArrayList;

public class StrategiesAdapter extends RecyclerView.Adapter<StrategiesAdapter.MyViewHolder> {

    private ArrayList<String> titleList, descriptionList;

    public StrategiesAdapter(ArrayList<String> titleList, ArrayList<String> descriptionList){
        this.titleList = titleList;
        this.descriptionList = descriptionList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description;
        private CardView card;
        private RelativeLayout cardContent;
        private ImageButton arrowBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            card = itemView.findViewById(R.id.card);
            cardContent = itemView.findViewById(R.id.cardContent);
            arrowBtn = itemView.findViewById(R.id.arrowBtn);
        }
    }

    @NonNull
    @Override
    public StrategiesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.strategies_layout, parent, false);
        StrategiesAdapter.MyViewHolder holder = new StrategiesAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StrategiesAdapter.MyViewHolder holder, int position) {
        holder.title.setText(titleList.get(position));
        holder.description.setText(descriptionList.get(position));

        holder.card.setOnClickListener(view -> {
            displayStrategiesDescription(holder);
        });

        holder.arrowBtn.setOnClickListener(view -> {
            displayStrategiesDescription(holder);
        });
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    private void displayStrategiesDescription(@NonNull MyViewHolder holder) {
        if (holder.cardContent.getVisibility() == View.GONE) {
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(300);
            TransitionManager.beginDelayedTransition(holder.card, autoTransition);
            holder.cardContent.setVisibility(View.VISIBLE);
            holder.arrowBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);
        } else {
            Transition transition = new Fade();
            transition.setDuration(600);
            TransitionManager.beginDelayedTransition(holder.card, transition);
            holder.cardContent.setVisibility(View.GONE);
            holder.arrowBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
        }
    }


}
