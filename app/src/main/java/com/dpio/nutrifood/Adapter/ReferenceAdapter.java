package com.dpio.nutrifood.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dpio.nutrifood.R;

import java.util.ArrayList;

public class ReferenceAdapter extends RecyclerView.Adapter<ReferenceAdapter.MyViewHolder> {

    private ArrayList<String> nameList, locationList;

    public ReferenceAdapter(ArrayList<String> nameList, ArrayList<String> locationList) {
        this.nameList = nameList;
        this.locationList = locationList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView name, location;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            location = itemView.findViewById(R.id.location);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reference_layout, parent, false);
        ReferenceAdapter.MyViewHolder holder = new ReferenceAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(nameList.get(position));
        holder.location.setText(locationList.get(position));
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }
}
