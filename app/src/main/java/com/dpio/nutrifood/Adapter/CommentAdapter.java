package com.dpio.nutrifood.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dpio.nutrifood.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UID;
    private static final String TAG = "TAG";
    private Context context;
    ArrayList<String> uidList, nameList, profileList, commentList;

    public CommentAdapter(ArrayList<String> uidList, ArrayList<String> nameList, ArrayList<String> profileList, ArrayList<String> commentList) {
        this.uidList = uidList;
        this.nameList = nameList;
        this.profileList = profileList;
        this.commentList = commentList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView profile;
        private TextView name, comment;
        private ImageButton menu;
        private LinearLayout editCommentContainer;
        private TextInputEditText commentInput;
        private Button updateBtn, cancelBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            profile = itemView.findViewById(R.id.profile);
            name = itemView.findViewById(R.id.name);
            comment = itemView.findViewById(R.id.comment);
            menu = itemView.findViewById(R.id.menu);
            editCommentContainer = itemView.findViewById(R.id.editCommentContainer);
            commentInput = itemView.findViewById(R.id.commentInput);
            updateBtn = itemView.findViewById(R.id.updateBtn);
            cancelBtn = itemView.findViewById(R.id.cancelBtn);
        }
    }

    @NonNull
    @Override
    public CommentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_layout, parent, false);
        CommentAdapter.MyViewHolder holder = new CommentAdapter.MyViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyViewHolder holder, int position) {
        context = holder.itemView.getContext();
        holder.name.setText(nameList.get(position));
        Glide.with(context).load(profileList.get(position)).into(holder.profile);
        holder.comment.setText(commentList.get(position));

        displayMenu(holder);
    }

    @Override
    public int getItemCount() {
        return nameList.size();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void displayMenu(MyViewHolder holder) {
        UID = mAuth.getCurrentUser().getUid();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        int mealPostIndex = sharedPreferences.getInt("mealPostIndex", 0);

        CollectionReference postRef = db.collection("Post");
        Query filterPostQuery = null;
        switch (getSharedPrefFilter()) {
            case "ratingFilter":
                filterPostQuery = postRef.orderBy("heartCount", Query.Direction.DESCENDING);
                break;
            case "ownPostFilter":
                filterPostQuery = postRef.whereEqualTo("uid", UID).orderBy("postDate", Query.Direction.DESCENDING);
                break;
            case "newestFilter":
                filterPostQuery = postRef.orderBy("postDate", Query.Direction.DESCENDING);
                break;
        }

        filterPostQuery.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, task.getException().getMessage());
                return;
            }
            if (task.getResult().isEmpty()) {
                Log.d(TAG, "Empty result");
                return;
            }

            int count = 0;
            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                if (count == mealPostIndex) {
                    String postId = documentSnapshot.getId();
                    String UID = mAuth.getCurrentUser().getUid();

                    CollectionReference commentRef = db.collection("CommentPost");
                    Query commentQuery = commentRef.whereEqualTo("postId", postId).whereEqualTo("uid", UID).orderBy("postDate", Query.Direction.DESCENDING);

                    commentQuery.get().addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            Log.d(TAG, task1.getException().getMessage());
                            return;
                        }
                        if (task1.getResult().isEmpty()) {
                            Log.d(TAG, "Empty result");
                            return;
                        }

                        for (DocumentSnapshot documentSnapshot1 : task1.getResult()) {
                            String uidData = documentSnapshot1.getString("uid");

                            for (int i = 0; i < uidList.size(); i++) {
                                if (uidList.get(i).contains(uidData) && i == holder.getAdapterPosition()) {
                                    holder.menu.setVisibility(View.VISIBLE);
                                    commentMenu(holder);
                                }
                            }
                            break;
                        }
                    });
                    break;
                }
                count++;
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void commentMenu(MyViewHolder holder) {
        holder.menu.setOnClickListener(view -> {
            @SuppressLint("RestrictedApi")
            MenuBuilder menuBuilder = new MenuBuilder(context);
            MenuInflater menuInflater = new MenuInflater(context);
            menuInflater.inflate(R.menu.comment_menu, menuBuilder);

            @SuppressLint("RestrictedApi")
            MenuPopupHelper menuPopupHelper = new MenuPopupHelper(context, menuBuilder, view);
            menuPopupHelper.setForceShowIcon(true);
            menuBuilder.setCallback(new MenuBuilder.Callback() {
                @Override
                public boolean onMenuItemSelected(@NonNull MenuBuilder menu, @NonNull MenuItem item) {
                    if (item.getItemId() == R.id.edit) {
                        updateComment(holder);
                        return true;
                    }
                    if (item.getItemId() == R.id.delete) {
                        deleteComment(holder, view);
                        return true;
                    }
                    return false;
                }

                @Override
                public void onMenuModeChange(@NonNull MenuBuilder menu) {

                }
            });
            menuPopupHelper.show();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateComment(MyViewHolder holder) {
        holder.comment.setVisibility(View.GONE);
        holder.menu.setVisibility(View.GONE);
        holder.editCommentContainer.setVisibility(View.VISIBLE);

        String comment = holder.comment.getText().toString();
        holder.commentInput.setText(comment);

        holder.commentInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    holder.updateBtn.setBackgroundColor(context.getResources().getColor(R.color.disableUpdate));
                    holder.updateBtn.setEnabled(false);
                    return;
                }
                holder.updateBtn.setBackgroundColor(context.getResources().getColor(R.color.enableUpdate));
                holder.updateBtn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        holder.updateBtn.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
            int mealPostIndex = sharedPreferences.getInt("mealPostIndex", 0);

            CollectionReference postRef = db.collection("Post");
            Query filterPostQuery = null;
            switch (getSharedPrefFilter()) {
                case "ratingFilter":
                    filterPostQuery = postRef.orderBy("heartCount", Query.Direction.DESCENDING);
                    break;
                case "ownPostFilter":
                    UID = mAuth.getCurrentUser().getUid();
                    filterPostQuery = postRef.whereEqualTo("uid", UID).orderBy("postDate", Query.Direction.DESCENDING);
                    break;
                case "newestFilter":
                    filterPostQuery = postRef.orderBy("postDate", Query.Direction.DESCENDING);
                    break;
            }

            filterPostQuery.get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d(TAG, task.getException().getMessage());
                    return;
                }
                if (task.getResult().isEmpty()) {
                    Log.d(TAG, "Empty result");
                    return;
                }

                int count = 0;
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    if (count == mealPostIndex) {
                        String postId = documentSnapshot.getId();

                        CollectionReference commentRef = db.collection("CommentPost");
                        Query commentQuery = commentRef.whereEqualTo("postId", postId).orderBy("postDate", Query.Direction.DESCENDING);

                        commentQuery.get().addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                Log.d(TAG, task1.getException().getMessage());
                                return;
                            }
                            if (task1.getResult().isEmpty()) {
                                Log.d(TAG, "Empty result");
                                return;
                            }

                            int index = 0;
                            for (DocumentSnapshot documentSnapshot1 : task1.getResult()) {
                                if (holder.getAdapterPosition() == index) {
                                    String updatedComment = holder.commentInput.getText().toString();
                                    String commentKey = documentSnapshot1.getId();

                                    Task<Void> commentUpdateRef = db.collection("CommentPost").document(commentKey).update("comment", updatedComment);
                                    commentList.set(index, updatedComment);

                                    holder.comment.setVisibility(View.VISIBLE);
                                    holder.menu.setVisibility(View.VISIBLE);
                                    holder.editCommentContainer.setVisibility(View.GONE);

                                    notifyDataSetChanged();
                                    break;
                                }
                                index++;
                            }
                        });
                        break;
                    }
                    count++;
                }
            });
        });

        holder.cancelBtn.setOnClickListener(view -> {
            holder.comment.setVisibility(View.VISIBLE);
            holder.menu.setVisibility(View.VISIBLE);
            holder.editCommentContainer.setVisibility(View.GONE);
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteComment(MyViewHolder holder, View view) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme);
        dialog.setIcon(R.drawable.ic_baseline_warning_24);
        dialog.setTitle("Are you sure?");
        dialog.setMessage("Do you really want to delete this comment.");

        dialog.setPositiveButton("DELETE", (dialogInterface, i) -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
            int mealPostIndex = sharedPreferences.getInt("mealPostIndex", 0);

            CollectionReference postRef = db.collection("Post");
            Query filterPostQuery = null;
            switch (getSharedPrefFilter()) {
                case "ratingFilter":
                    filterPostQuery = postRef.orderBy("heartCount", Query.Direction.DESCENDING);
                    break;
                case "ownPostFilter":
                    UID = mAuth.getCurrentUser().getUid();
                    filterPostQuery = postRef.whereEqualTo("uid", UID).orderBy("postDate", Query.Direction.DESCENDING);
                    break;
                case "newestFilter":
                    filterPostQuery = postRef.orderBy("postDate", Query.Direction.DESCENDING);
                    break;
            }

            filterPostQuery.get().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.d(TAG, task.getException().getMessage());
                    return;
                }
                if (task.getResult().isEmpty()) {
                    Log.d(TAG, "Empty result");
                    return;
                }

                int count = 0;
                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                    if (count == mealPostIndex) {
                        String postId = documentSnapshot.getId();

                        CollectionReference commentRef = db.collection("CommentPost");
                        Query commentQuery = commentRef.whereEqualTo("postId", postId).orderBy("postDate", Query.Direction.DESCENDING);

                        commentQuery.get().addOnCompleteListener(task1 -> {
                            if (!task1.isSuccessful()) {
                                Log.d(TAG, task1.getException().getMessage());
                                return;
                            }
                            if (task1.getResult().isEmpty()) {
                                Log.d(TAG, "Empty result");
                                return;
                            }

                            int index = 0;
                            for (DocumentSnapshot documentSnapshot1 : task1.getResult()) {
                                if (holder.getAdapterPosition() == index) {
                                    String commentKey = documentSnapshot1.getId();
                                    Task<Void> commentDeleteRef = db.collection("CommentPost").document(commentKey).delete();

                                    uidList.remove(index);
                                    nameList.remove(index);
                                    profileList.remove(index);
                                    commentList.remove(index);
                                    notifyDataSetChanged();
                                    break;
                                }

                                index++;
                            }
                        });
                        break;
                    }
                    count++;
                }
            });
        });

        dialog.setNegativeButton("CANCEL", null);

        dialog.show();
    }

    private String getSharedPrefFilter() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
        return sharedPreferences.getString("currentFilter", null);
    }
}
