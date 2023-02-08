package com.dpio.nutrifood.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.AutoTransition;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dpio.nutrifood.Fragment.UpdatePostFragment;
import com.dpio.nutrifood.Model.CommentModelClass;
import com.dpio.nutrifood.Model.RatingModelClass;
import com.dpio.nutrifood.Model.ReportModelClass;
import com.dpio.nutrifood.R;
import com.facebook.Profile;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommunityAdapter extends RecyclerView.Adapter<CommunityAdapter.MyViewHolder> {

    ArrayList<String> userNameList, userProfileList, mealNameList, mealImageList;
    ArrayList<String> uidList = new ArrayList<>();
    ArrayList<String> nameList = new ArrayList<>();
    ArrayList<String> profileList = new ArrayList<>();
    ArrayList<String> commentList = new ArrayList<>();
    List<String> mealIngredientsList, mealProceduresList;
    private static final String TAG = "TAG";
    private CommentAdapter commentAdapter;
    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String UID, userName, userProfile;

    public CommunityAdapter(ArrayList<String> userNameList, ArrayList<String> userProfileList, ArrayList<String> mealNameList, List<String> mealIngredientsList, List<String> mealProceduresList, ArrayList<String> mealImageList) {
        this.userNameList = userNameList;
        this.userProfileList = userProfileList;
        this.mealNameList = mealNameList;
        this.mealIngredientsList = mealIngredientsList;
        this.mealProceduresList = mealProceduresList;
        this.mealImageList = mealImageList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CircularProgressIndicator progressIndicator;
        private CardView card, cardComment;
        private RelativeLayout cardTitle;
        private RelativeLayout cardContent;
        private ImageButton arrowBtn, commentBtn, addCommentBtn, reportBtn, menu;
        private CheckBox heartCheckBox;
        private TextView userName, mealName, mealIngredients, mealProcedures, commentCounter, heartCounter;
        private ImageView mealImage;
        private CircleImageView userProfile;
        private EditText commentInput;
        private RecyclerView recyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            progressIndicator = itemView.findViewById(R.id.progressIndicator);
            card = itemView.findViewById(R.id.card);
            cardComment = itemView.findViewById(R.id.cardComment);
            cardTitle = itemView.findViewById(R.id.cardTitle);
            cardContent = itemView.findViewById(R.id.cardContent);
            arrowBtn = itemView.findViewById(R.id.arrowBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            addCommentBtn = itemView.findViewById(R.id.addCommentBtn);
            userName = itemView.findViewById(R.id.userName);
            userProfile = itemView.findViewById(R.id.userProfile);
            mealName = itemView.findViewById(R.id.mealName);
            mealIngredients = itemView.findViewById(R.id.mealIngredients);
            mealProcedures = itemView.findViewById(R.id.mealProcedures);
            mealImage = itemView.findViewById(R.id.mealImage);
            commentInput = itemView.findViewById(R.id.commentInput);
            commentCounter = itemView.findViewById(R.id.commentCounter);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            heartCheckBox = itemView.findViewById(R.id.heartCheckBox);
            heartCounter = itemView.findViewById(R.id.heartCounter);
            reportBtn = itemView.findViewById(R.id.reportBtn);
            menu = itemView.findViewById(R.id.menu);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_meal_layout, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.cardTitle.setOnClickListener(view -> hideMealData(holder));

        holder.arrowBtn.setOnClickListener(view -> hideMealData(holder));

        holder.commentBtn.setOnClickListener(view -> displayCommentSection(holder, view));

        holder.addCommentBtn.setOnClickListener(view -> createComment(holder, view));

        holder.heartCheckBox.setOnClickListener(view -> createHeartRating(holder, view));

        holder.reportBtn.setOnClickListener(view -> displayReportBottomSheet(holder, view));

        context = holder.itemView.getContext();
        holder.userName.setText(userNameList.get(position));
        Glide.with(context).load(userProfileList.get(position)).into(holder.userProfile);
        holder.mealName.setText(mealNameList.get(position));

        getHeartRating(holder);
        countNumberOfHearts(holder);
        displayMealData(holder);
        countNumberOfComments(holder);

        switch (getSharedPrefFilter()) {
            case "ratingFilter":
            case "newestFilter":
                holder.arrowBtn.setVisibility(View.VISIBLE);
                holder.menu.setVisibility(View.GONE);
                break;
            case "ownPostFilter":
                holder.arrowBtn.setVisibility(View.GONE);
                holder.menu.setVisibility(View.VISIBLE);
                ownPostMenu(holder);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return userNameList.size();
    }



    private void displayMealData(MyViewHolder holder) {
        AutoTransition autoTransition = new AutoTransition();
        autoTransition.setDuration(300);
        TransitionManager.beginDelayedTransition(holder.card, autoTransition);
        holder.cardContent.setVisibility(View.VISIBLE);
        holder.arrowBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_up_24);

        String finalIngredients = mealIngredientsList.get(holder.getAdapterPosition()).replace("[", "• ").replace("]", " ").replace(", ", "\n• ").replace("  ", " ");
        holder.mealIngredients.setText(finalIngredients);

        String mealProcItem = mealProceduresList.get(holder.getAdapterPosition()).replace("[", " ").replace("]", " ").replace(", ", "\n ").replace("  ", " ");
        holder.mealProcedures.setText(mealProcItem);

        Glide.with(context).load(mealImageList.get(holder.getAdapterPosition())).into(holder.mealImage);
    }

    private void hideMealData(@NonNull MyViewHolder holder) {
        if (holder.cardContent.getVisibility() == View.VISIBLE) {
            holder.cardContent.setVisibility(View.GONE);
            holder.arrowBtn.setImageResource(R.drawable.ic_baseline_keyboard_arrow_down_24);
            return;
        }
        displayMealData(holder);
    }



    private void displayCommentSection(MyViewHolder holder, View view) {
        if (holder.cardComment.getVisibility() == View.GONE) {
            AutoTransition autoTransition = new AutoTransition();
            autoTransition.setDuration(300);
            TransitionManager.beginDelayedTransition(holder.cardComment, autoTransition);
            holder.cardComment.setVisibility(View.VISIBLE);
            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.commentBtn.setImageResource(R.drawable.ic_baseline_mode_comment_24);
            getComment(holder);

            SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit().putInt("mealPostIndex", holder.getAdapterPosition());
            editor.commit();
        } else {
            Transition transition = new Fade();
            transition.setDuration(600);
            TransitionManager.beginDelayedTransition(holder.cardComment, transition);
            TransitionManager.beginDelayedTransition(holder.recyclerView, transition);
            holder.cardComment.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.GONE);
            holder.progressIndicator.setVisibility(View.GONE);
            holder.commentBtn.setImageResource(R.drawable.ic_outline_mode_comment_24);

            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            holder.commentInput.clearFocus();

            clearCommentArrayList();
        }
    }

    private void createComment(MyViewHolder holder, View view) {
        String comment = holder.commentInput.getText().toString();
        if (comment.isEmpty()) return;

        UID = mAuth.getCurrentUser().getUid();
        userName = mAuth.getCurrentUser().getDisplayName();
        List<? extends UserInfo> provider = mAuth.getCurrentUser().getProviderData();
        for (UserInfo signInProvider : provider) {
            if (signInProvider.getProviderId().equals("facebook.com")) {
                Uri profileUrl = Profile.getCurrentProfile().getProfilePictureUri(500, 500);
                userProfile = profileUrl.toString();
            } else {
                userProfile = mAuth.getCurrentUser().getPhotoUrl().toString();
            }
        }

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

        filterPostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (queryDocumentSnapshots.isEmpty()) {
                Log.d(TAG, "Snapshot is empty: " + queryDocumentSnapshots.isEmpty());
                return;
            }

            int i = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (i == holder.getAdapterPosition()) {
                    String postId = documentSnapshot.getId();
                    Date postDate = Calendar.getInstance().getTime();
                    CommentModelClass commentData = new CommentModelClass(UID, userName, userProfile, comment, postId, postDate);
                    Task<DocumentReference> commentRef = db.collection("CommentPost").add(commentData);
                    break;
                }
                i++;
            }
        });

        getComment(holder);
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        holder.commentInput.clearFocus();
        holder.commentInput.getText().clear();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void getComment(MyViewHolder holder) {
        holder.progressIndicator.setVisibility(View.VISIBLE);
        countNumberOfComments(holder);

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
                holder.progressIndicator.setVisibility(View.GONE);
                return;
            }
            if (task.getResult().isEmpty()) {
                Log.d(TAG, "Empty result");
                holder.progressIndicator.setVisibility(View.GONE);
                return;
            }

            int count = 0;
            for (DocumentSnapshot documentSnapshot : task.getResult()) {
                if (count == holder.getAdapterPosition()) {
                    String postId = documentSnapshot.getId();

                    CollectionReference commentRef = db.collection("CommentPost");
                    Query commentQuery = commentRef.whereEqualTo("postId", postId).orderBy("postDate", Query.Direction.DESCENDING);
                    commentQuery.get().addOnCompleteListener(task1 -> {
                        if (!task1.isSuccessful()) {
                            Log.d(TAG, "Task Unsuccessful: " + task1.getException().getMessage());
                            holder.progressIndicator.setVisibility(View.GONE);
                            return;
                        }
                        if (task1.getResult().isEmpty()) {
                            Log.d(TAG, "Empty commentRef");
                            holder.progressIndicator.setVisibility(View.GONE);
                            return;
                        }

                        for (DocumentSnapshot documentSnapshot1 : task1.getResult()) {
                            uidList.add(documentSnapshot1.getString("uid"));
                            nameList.add(documentSnapshot1.getString("userName"));
                            profileList.add(documentSnapshot1.getString("userProfile"));
                            commentList.add(documentSnapshot1.getString("comment"));

                            RecyclerView.LayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            holder.recyclerView.setLayoutManager(linearLayoutManager);
                            commentAdapter = new CommentAdapter(uidList, nameList, profileList, commentList);
                            holder.recyclerView.setAdapter(commentAdapter);
                            holder.progressIndicator.setVisibility(View.GONE);
                        }
                        commentAdapter.notifyDataSetChanged();
                    });
                    clearCommentArrayList();
                    break;
                }
                holder.progressIndicator.setVisibility(View.GONE);
                count++;
            }
        });
    }

    private void countNumberOfComments(MyViewHolder holder) {
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

        filterPostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int i = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (i == holder.getAdapterPosition()) {
                    String postId = documentSnapshot.getId();

                    CollectionReference commentRef = db.collection("CommentPost");
                    Query commentQuery = commentRef.whereEqualTo("postId", postId);

                    commentQuery.addSnapshotListener((value, error) -> {
                        if (value.isEmpty()) {
                            Log.d(TAG, "countNumberOfComments: Rating Query is empty");
                            holder.commentCounter.setText("0");
                            return;
                        }
                        if (error != null) {
                            Log.d(TAG, "Listen failed " + error.getMessage());
                            return;
                        }

                        int commentCount = value.size();
                        holder.commentCounter.setText(String.valueOf(commentCount));
                    });
                    break;
                }
                i++;
            }
        });
    }

    private void clearCommentArrayList() {
        uidList.clear();
        nameList.clear();
        profileList.clear();
        commentList.clear();
    }



    private void getHeartRating(MyViewHolder holder) {
        UID = mAuth.getCurrentUser().getUid();

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

        filterPostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int i = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (i == holder.getAdapterPosition()) {
                    String postId = documentSnapshot.getId();

                    CollectionReference ratingRef = db.collection("RatingPost");
                    Query ratingQuery = ratingRef.whereEqualTo("postId", postId).whereEqualTo("uid", UID);
                    ratingQuery.get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Task Unsuccessful " + task.getException().getMessage());
                            return;
                        }
                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, "displayHeart: Rating Query is empty");
                            holder.heartCheckBox.setChecked(false);
                            return;
                        }

                        holder.heartCheckBox.setChecked(true);
                    });
                    break;
                }
                i++;
            }
        });
    }

    private void createHeartRating(MyViewHolder holder, View view) {
        UID = mAuth.getCurrentUser().getUid();
        CheckBox heartCheck = (CheckBox) view;

        if (heartCheck.isChecked()) {
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

            filterPostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
                int i = 0;
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (i == holder.getAdapterPosition()) {
                        String postId = documentSnapshot.getId();

                        RatingModelClass ratingData = new RatingModelClass(UID, postId, true);
                        Task<DocumentReference> ratingRef = db.collection("RatingPost").add(ratingData);
                        break;
                    }
                    i++;
                }
            });
            return;
        }

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

        filterPostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int i = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (i == holder.getAdapterPosition()) {
                    String postId = documentSnapshot.getId();

                    CollectionReference ratingRef = db.collection("RatingPost");
                    Query ratingQuery = ratingRef.whereEqualTo("postId", postId).whereEqualTo("uid", UID);

                    ratingQuery.get().addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, "Task unsuccessful " + task.getException().getMessage());
                            return;
                        }
                        if (task.getResult().isEmpty()) {
                            Log.d(TAG, "heartRating: Rating Query is empty");
                            return;
                        }

                        for (DocumentSnapshot documentSnapshot1 : task.getResult()) {
                            String ratingId = documentSnapshot1.getId();

                            Task<Void> deleteRatingRef = db.collection("RatingPost").document(ratingId).delete();
                        }
                    });
                    break;
                }
                i++;
            }
        });
    }

    private void countNumberOfHearts(MyViewHolder holder) {
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

        filterPostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int i = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (i == holder.getAdapterPosition()) {
                    String postId = documentSnapshot.getId();

                    CollectionReference ratingRef = db.collection("RatingPost");
                    Query ratingQuery = ratingRef.whereEqualTo("postId", postId).whereEqualTo("heartRating", true);

                    ratingQuery.addSnapshotListener((value, error) -> {
                        int heartCount = value.size();

                        if (value.isEmpty()) {
                            Log.d(TAG, "countNumberOfHearts: Rating Query is empty");
                            postRef.document(postId).update("heartCount", heartCount);
                            holder.heartCounter.setText(String.valueOf(heartCount));
                            return;
                        }
                        if (error != null) {
                            Log.d(TAG, "Listen failed " + error.getMessage());
                            return;
                        }

                        postRef.document(postId).update("heartCount", heartCount);
                        holder.heartCounter.setText(String.valueOf(heartCount));
                    });
                    break;
                }
                i++;
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void displayReportBottomSheet(MyViewHolder holder, View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.report_layout);

        RadioGroup reportGroup = bottomSheetDialog.findViewById(R.id.radioGroup);
        RadioButton report1 = bottomSheetDialog.findViewById(R.id.report1);
        TextInputLayout otherReportLayout = bottomSheetDialog.findViewById(R.id.otherReportLayout);
        TextInputEditText otherReportInput = bottomSheetDialog.findViewById(R.id.otherReportInput);
        Button submitBtn = bottomSheetDialog.findViewById(R.id.submitBtn);

        final String[] reportMessage = new String[1];

        if (report1.isChecked()) {
            reportMessage[0] = "Irrelevant information";
        }

        reportGroup.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            otherReportLayout.setVisibility(View.GONE);
            switch (checkedId) {
                case R.id.report1:
                    reportMessage[0] = "Irrelevant information";
                    break;
                case R.id.report2:
                    reportMessage[0] = "Spam";
                    break;
                case R.id.report3:
                    reportMessage[0] = "Harassment";
                    break;
                case R.id.report4:
                    reportMessage[0] = "Sharing inappropriate things";
                    break;
                case R.id.report5:
                    otherReportLayout.setVisibility(View.VISIBLE);
                    reportMessage[0] = otherReportInput.getText().toString();
                    break;
            }
        });

        otherReportInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence == null) {
                    otherReportLayout.setError("Enter a report here");
                    otherReportLayout.setHelperText("");
                    return;
                }
                otherReportLayout.setError("");
                otherReportLayout.setHelperText("Tell us the problem here");
                reportMessage[0] = otherReportInput.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        submitBtn.setOnClickListener(view1 -> {
            if (reportMessage[0].isEmpty()) {
                otherReportLayout.setVisibility(View.VISIBLE);
                otherReportLayout.setError("Describe the problem here");
                otherReportLayout.setHelperText("");
                return;
            }

            createReport(holder, Arrays.toString(reportMessage), view);
            bottomSheetDialog.hide();
        });

        bottomSheetDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createReport(MyViewHolder holder, String reportMessage, View view) {
        UID = mAuth.getCurrentUser().getUid();

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

        filterPostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int i = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (i == holder.getAdapterPosition()) {
                    String postId = documentSnapshot.getId();

                    CollectionReference reportRef = db.collection("ReportPost");
                    Query reportQuery = reportRef.whereEqualTo("uid", UID).whereEqualTo("postId", postId);

                    reportQuery.get().addOnCompleteListener(task -> {
                        AppCompatActivity activity = (AppCompatActivity) view.getContext();
                        if (!task.getResult().isEmpty()) {
                            Snackbar.make(activity.findViewById(R.id.frameLayout), "Post has been reported", Snackbar.LENGTH_SHORT).show();
                            return;
                        }

                        String mealName = String.valueOf(documentSnapshot.get("mealName"));
                        String mealIngr = String.valueOf(documentSnapshot.get("mealIngrArray"));
                        String mealProc = String.valueOf(documentSnapshot.get("mealProcArray"));
                        String mealImage = String.valueOf(documentSnapshot.get("mealImage"));
                        Date postDate = Calendar.getInstance().getTime();

                        ReportModelClass reportData = new ReportModelClass(UID, reportMessage, postId, postDate, mealName, mealIngr, mealProc, mealImage, false);
                        Task<DocumentReference> addReportRef = db.collection("ReportPost").add(reportData);
                        Snackbar.make(activity.findViewById(R.id.drawerLayout), "Report successful", Snackbar.LENGTH_SHORT).show();
                    });
                    break;
                }
                i++;
            }
        });
    }


    @SuppressLint("RestrictedApi")
    private void ownPostMenu(MyViewHolder holder) {
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
                        updatePost(holder, view);
                        return true;
                    }
                    if (item.getItemId() == R.id.delete) {
                        deletePost(holder);
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

    private void updatePost(MyViewHolder holder, View view) {
        UID = mAuth.getCurrentUser().getUid();

        CollectionReference postRef = db.collection("Post");
        Query ownPostQuery = postRef.whereEqualTo("uid", UID).orderBy("postDate", Query.Direction.DESCENDING);
        ;

        ownPostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int i = 0;
            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                if (i == holder.getAdapterPosition()) {
                    String postId = documentSnapshot.getId();

                    Bundle bundle = new Bundle();
                    bundle.putString("postId", postId);

                    AppCompatActivity activity = (AppCompatActivity) view.getContext();
                    Fragment fragment = new UpdatePostFragment();
                    fragment.setArguments(bundle);
                    activity.getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
                    break;
                }
                i++;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deletePost(MyViewHolder holder) {
        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme);
        dialog.setIcon(R.drawable.ic_baseline_warning_24);
        dialog.setTitle("Are you sure?");
        dialog.setMessage("Do you really want to delete this post.");

        dialog.setPositiveButton("DELETE", (dialogInterface, i) -> {
            UID = mAuth.getCurrentUser().getUid();

            CollectionReference postRef = db.collection("Post");
            Query ownPostQuery = postRef.whereEqualTo("uid", UID).orderBy("postDate", Query.Direction.DESCENDING);

            ownPostQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
                int position = 0;
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (position == holder.getAdapterPosition()) {
                        String postId = documentSnapshot.getId();

                        // Delete data in Post collection and image Storage
                        String mealImage = documentSnapshot.getString("mealImage");
                        Task<Void> imagesRef = FirebaseStorage.getInstance().getReferenceFromUrl(mealImage).delete();
                        postRef.document(postId).delete();

                        // Delete data in CommentPost collection
                        CollectionReference commentRef = db.collection("CommentPost");
                        Query deleteCommentQuery = commentRef.whereEqualTo("postId", postId);

                        deleteCommentQuery.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                            if (queryDocumentSnapshots1.isEmpty()) {
                                Log.d(TAG, "Deleted CommentPost Query is  empty");
                                return;
                            }

                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots1) {
                                String commentDocumentId = documentSnapshot1.getId();
                                commentRef.document(commentDocumentId).delete();
                            }
                        });

                        // Delete data in RatingPost collection
                        CollectionReference ratingRef = db.collection("RatingPost");
                        Query deleteRatingQuery = ratingRef.whereEqualTo("postId", postId);

                        deleteRatingQuery.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots1) {
                                if (queryDocumentSnapshots1.isEmpty()) {
                                    Log.d(TAG, "Deleted RatingPost Query is  empty");
                                    return;
                                }

                                String ratingDocumentId = documentSnapshot1.getId();
                                ratingRef.document(ratingDocumentId).delete();
                            }
                        });

                        // Delete data in ReportPost collection
                        CollectionReference reportRef = db.collection("ReportPost");
                        Query deleteReportQuery = reportRef.whereEqualTo("postId", postId);

                        deleteReportQuery.get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                            if (queryDocumentSnapshots1.isEmpty()) {
                                Log.d(TAG, "Deleted ReportPost Query is  empty");
                                return;
                            }

                            for (DocumentSnapshot documentSnapshot1 : queryDocumentSnapshots1) {
                                String reportDocumentId = documentSnapshot1.getId();
                                reportRef.document(reportDocumentId).delete();
                            }
                        });

                        userNameList.remove(position);
                        userProfileList.remove(position);
                        mealNameList.remove(position);
                        mealIngredientsList.remove(position);
                        mealProceduresList.remove(position);
                        mealImageList.remove(position);

                        notifyDataSetChanged();
                        break;
                    }
                    position++;
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