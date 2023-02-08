package com.dpio.nutrifood.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dpio.nutrifood.R;

import java.util.ArrayList;

public class OurTeamAdapter extends RecyclerView.Adapter<OurTeamAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> profileList, nameList, roleList, emailList, locationList;

    public OurTeamAdapter(ArrayList<String> profileList, ArrayList<String> nameList, ArrayList<String> roleList, ArrayList<String> emailList, ArrayList<String> locationList, Context context) {
        this.profileList = profileList;
        this.nameList = nameList;
        this.roleList = roleList;
        this.emailList = emailList;
        this.locationList = locationList;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private ImageView profile;
        private TextView name, role, email, location;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            role = itemView.findViewById(R.id.role);
            email = itemView.findViewById(R.id.email);
            location = itemView.findViewById(R.id.location);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.our_team_layout, parent, false);
        OurTeamAdapter.MyViewHolder holder = new OurTeamAdapter.MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String drawableStr = profileList.get(position);
        int id = context.getResources().getIdentifier(drawableStr, "drawable", context.getPackageName());
        Drawable drawable = context.getResources().getDrawable(id);

        Glide.with(context).load(drawable).into(holder.profile);
        holder.name.setText(nameList.get(position));
        holder.role.setText(roleList.get(position));
        holder.email.setText(emailList.get(position));
        holder.location.setText(locationList.get(position));
    }

    @Override
    public int getItemCount() {
        return profileList.size();
    }
}
