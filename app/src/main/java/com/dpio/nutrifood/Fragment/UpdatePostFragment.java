package com.dpio.nutrifood.Fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dpio.nutrifood.Miscellaneous.IngrEditText;
import com.dpio.nutrifood.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdatePostFragment extends Fragment {

    private static final String TAG = "TAG";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private LinearProgressIndicator progressIndicator;
    private TextInputLayout mealNameLayout, mealIngredientsLayout, mealProceduresLayout, mealImageLayout;
    private TextInputEditText mealNameInput, mealProceduresInput;
    private IngrEditText mealIngredientsInput;
    private ImageView mealImage;
    private Button updateBtn;
    private ScrollView postScrollView;
    private String postId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        postId = bundle.getString("postId");

        displayPostData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_update_post, container, false);

        mAuth = FirebaseAuth.getInstance();
        progressIndicator = viewGroup.findViewById(R.id.progressIndicator);
        mealNameLayout = viewGroup.findViewById(R.id.mealNameLayout);
        mealIngredientsLayout = viewGroup.findViewById(R.id.mealIngredientsLayout);
        mealProceduresLayout = viewGroup.findViewById(R.id.mealProceduresLayout);
        mealImageLayout = viewGroup.findViewById(R.id.mealImageLayout);
        mealNameInput = viewGroup.findViewById(R.id.mealNameInput);
        mealIngredientsInput = viewGroup.findViewById(R.id.mealIngredientsInput);
        mealProceduresInput = viewGroup.findViewById(R.id.mealProceduresInput);
        mealImage = viewGroup.findViewById(R.id.mealImage);
        updateBtn = viewGroup.findViewById(R.id.updateBtn);
        postScrollView = viewGroup.findViewById(R.id.postScrollView);

        updateBtn.setOnClickListener(view -> {
            updatePost(view);
        });

        return viewGroup;
    }

    private void updatePost(View view) {
        boolean mealNameField = checkForEmptyField(mealNameInput.getText().toString(), mealNameLayout, "Enter a meal name");
        boolean mealIngredientsField = checkForEmptyField(mealIngredientsInput.getText().toString(), mealIngredientsLayout, "Enter a meal ingredients");
        boolean mealProceduresField = checkForEmptyField(mealProceduresInput.getText().toString(), mealProceduresLayout, "Enter a meal procedures");
        boolean haveEmptyField = mealNameField || mealIngredientsField || mealProceduresField;

        hideErrorMsg(mealNameInput, null, mealNameLayout);
        hideErrorMsg(null, mealIngredientsInput, mealIngredientsLayout);
        hideErrorMsg(mealProceduresInput, null, mealProceduresLayout);

        if (haveEmptyField) return;

        String mealName = mealNameInput.getText().toString();
        String mealIngredients = mealIngredientsInput.getText().toString().replaceAll(getString(R.string.special_characters), " ");
        String mealProcedures = mealProceduresInput.getText().toString().replaceAll(getString(R.string.special_characters), " ");
        List<String> mealIngrArray = Arrays.asList(mealIngredients.split("\n"));
        List<String> mealProcArray = Arrays.asList(mealProcedures.split("\n"));

        Map<String, Object> updateData = new HashMap<>();
        updateData.put("mealName", mealName);
        updateData.put("mealIngrArray", mealIngrArray);
        updateData.put("mealProcArray", mealProcArray);

        DocumentReference updatePostRef = db.collection("Post").document(postId);
        updatePostRef.update(updateData).addOnCompleteListener(task -> {
            Snackbar.make(getActivity().findViewById(R.id.frameLayout), "Update Successful", Snackbar.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CommunityFragment()).addToBackStack(null).commit();

            progressIndicator.setVisibility(View.VISIBLE);
            updateBtn.setBackgroundColor(getContext().getResources().getColor(R.color.disableSubmit));
            updateBtn.setEnabled(false);
        });
    }

    private void displayPostData() {
        DocumentReference postRef = db.collection("Post").document(postId);
        postRef.get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, "Task unsuccessful " + task.getException().getMessage());
                return;
            }
            if (!task.getResult().exists()) {
                Log.d(TAG, "Post Ref is empty");
                return;
            }

            String mealName = task.getResult().getString("mealName");
            String mealIngredients = String.valueOf(task.getResult().get("mealIngrArray")).replace("[", " ").replace("]", " ").replace(", ", "\n ").replace("  ", " ");
            String mealProcedures = String.valueOf(task.getResult().get("mealProcArray")).replace("[", " ").replace("]", " ").replace(", ", "\n ").replace("  ", " ");
            String mealImageUrl = task.getResult().getString("mealImage");

            mealNameInput.setText(mealName);
            mealIngredientsInput.setText(mealIngredients);
            mealProceduresInput.setText(mealProcedures);
            Glide.with(getContext()).load(mealImageUrl).into(mealImage);
        });
    }


    private boolean checkForEmptyField(String inputValue, TextInputLayout textInputLayout, String errorMsg) {
        if (inputValue.trim().isEmpty()) {
            textInputLayout.setHelperText("");
            textInputLayout.setError(errorMsg);

            postScrollView.requestFocus(View.FOCUS_UP);
            postScrollView.smoothScrollTo(0, 0);
            mealNameInput.clearFocus();
            mealIngredientsInput.clearFocus();
            mealProceduresInput.clearFocus();
            return true;
        }
        return false;
    }

    private void hideErrorMsg(TextInputEditText editText, IngrEditText ingrEditText, TextInputLayout textInputLayout) {
        if (ingrEditText != null) {
            ingrEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    textInputLayout.setError("");
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        } else {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    textInputLayout.setError("");
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }
}