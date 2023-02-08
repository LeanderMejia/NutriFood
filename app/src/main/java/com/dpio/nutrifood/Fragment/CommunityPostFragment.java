package com.dpio.nutrifood.Fragment;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;

import com.dpio.nutrifood.Miscellaneous.IngrEditText;
import com.dpio.nutrifood.Model.PostModelClass;
import com.dpio.nutrifood.R;
import com.facebook.Profile;
import com.github.drjacky.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class CommunityPostFragment extends Fragment {

    private static final String TAG = "TAG";
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private LinearProgressIndicator progressIndicator;
    private TextInputLayout mealNameLayout, mealIngredientsLayout, mealProceduresLayout, mealImageLayout;
    private TextInputEditText mealNameInput, mealProceduresInput;
    private IngrEditText mealIngredientsInput;
    private LinearLayout mealImageContainer;
    private ImageView mealImage, imageIcon;
    private TextView imageTitle, imageSubTitle;
    private Button browseBtn, submitBtn;
    private ScrollView postScrollView;
    private String UID, userName, userEmail, userProfile;
    private Uri selectedImageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CommunityFragment()).addToBackStack(null).commit();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_community_post, container, false);

        mAuth = FirebaseAuth.getInstance();
        progressIndicator = viewGroup.findViewById(R.id.progressIndicator);
        mealNameLayout = viewGroup.findViewById(R.id.mealNameLayout);
        mealIngredientsLayout = viewGroup.findViewById(R.id.mealIngredientsLayout);
        mealProceduresLayout = viewGroup.findViewById(R.id.mealProceduresLayout);
        mealImageLayout = viewGroup.findViewById(R.id.mealImageLayout);
        mealNameInput = viewGroup.findViewById(R.id.mealNameInput);
        mealIngredientsInput = viewGroup.findViewById(R.id.mealIngredientsInput);
        mealProceduresInput = viewGroup.findViewById(R.id.mealProceduresInput);
        mealImageContainer = viewGroup.findViewById(R.id.mealImageContainer);
        mealImage = viewGroup.findViewById(R.id.mealImage);
        imageIcon = viewGroup.findViewById(R.id.imageIcon);
        imageTitle = viewGroup.findViewById(R.id.imageTitle);
        imageSubTitle = viewGroup.findViewById(R.id.imageSubTitle);
        browseBtn = viewGroup.findViewById(R.id.browseBtn);
        submitBtn = viewGroup.findViewById(R.id.submitBtn);
        postScrollView = viewGroup.findViewById(R.id.postScrollView);

        submitBtn.setOnClickListener(view -> {
            createPost();
        });

        browseBtn.setOnClickListener(view -> {
            uploadMealImage();
        });

        return viewGroup;
    }

    private void uploadMealImage() {
        ImagePicker.Companion.with(this).crop(3f, 2f).maxResultSize(1080, 194).start(101);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data.getData();
            mealImage.setImageURI(selectedImageUri);
            mealImage.setVisibility(View.VISIBLE);
            mealImageContainer.setVisibility(View.GONE);
            imageIcon.setVisibility(View.GONE);
            imageTitle.setVisibility(View.GONE);
            imageSubTitle.setVisibility(View.GONE);
            mealImageLayout.setError("");
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void createPost() {
        UID = mAuth.getCurrentUser().getUid();
        userName = mAuth.getCurrentUser().getDisplayName();
        userEmail = mAuth.getCurrentUser().getEmail();
        List<? extends UserInfo> provider = mAuth.getCurrentUser().getProviderData();
        for (UserInfo signInProvider : provider) {
            if (signInProvider.getProviderId().equals("facebook.com")) {
                Uri profileUrl = Profile.getCurrentProfile().getProfilePictureUri(500, 500);
                userProfile = profileUrl.toString();
            } else {
                userProfile = mAuth.getCurrentUser().getPhotoUrl().toString();
            }
        }

        String mealName = mealNameInput.getText().toString();
        String mealIngredients = mealIngredientsInput.getText().toString();
        String mealProcedures = mealProceduresInput.getText().toString();
        mealIngredients = mealIngredients.replaceAll(getString(R.string.special_characters), " ");
        mealProcedures = mealProcedures.replaceAll(getString(R.string.special_characters), " ");
        List<String> mealIngrArray = Arrays.asList(mealIngredients.split("\n"));
        List<String> mealProcArray = Arrays.asList(mealProcedures.split("\n"));
        Date postDate = Calendar.getInstance().getTime();

        boolean mealNameField = checkForEmptyField(mealName, mealNameLayout, "Enter a meal name");
        boolean mealIngredientsField = checkForEmptyField(mealIngredients, mealIngredientsLayout, "Enter a meal ingredients");
        boolean mealProceduresField = checkForEmptyField(mealProcedures, mealProceduresLayout, "Enter a meal procedures");
        boolean mealImageField = checkForEmptyField("Meal Image", mealImageLayout, "Upload a meal image");
        boolean haveEmptyField = mealNameField || mealIngredientsField || mealProceduresField || mealImageField;

        hideErrorMsg(mealNameInput, null, mealNameLayout, "Enter the meal name here");
        hideErrorMsg(null, mealIngredientsInput, mealIngredientsLayout, "Enter the meal ingredients here");
        hideErrorMsg(mealProceduresInput, null, mealProceduresLayout, "Enter the meal procedures here");

        if (haveEmptyField) return;

        String path = "images/" + UUID.randomUUID().toString() + "." + getFileExtension(selectedImageUri);
        StorageReference imageRef = storage.getReference(path);
        UploadTask uploadTask = imageRef.putFile(selectedImageUri);

        Task<Uri> downloadUriTask = uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                Log.d(TAG, task.getException().getMessage());
            }
            return imageRef.getDownloadUrl();
        });

        // Store User Post Data with download uri for meal image
        downloadUriTask.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Uri mealImageUri = task.getResult();
                String mealImage = String.valueOf(mealImageUri);
                int heartCount = 0;
                PostModelClass postData = new PostModelClass(UID, userName, userEmail, userProfile, mealName, mealIngrArray, mealProcArray, mealImage, postDate, heartCount);
                Task<DocumentReference> postRef = db.collection("Post").add(postData);

                postRef.addOnCompleteListener(task1 -> {
                    Snackbar.make(getActivity().findViewById(R.id.drawerLayout), "Meal posted in community", Snackbar.LENGTH_SHORT).show();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CommunityFragment()).addToBackStack(null).commit();
                });
            }
        });
        progressIndicator.setVisibility(View.VISIBLE);
        submitBtn.setBackgroundColor(getContext().getResources().getColor(R.color.disableSubmit));
        submitBtn.setEnabled(false);
        browseBtn.setEnabled(false);
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
        if (inputValue.equals("Meal Image") && selectedImageUri == null) {
            textInputLayout.setError(errorMsg);
            textInputLayout.setHelperText("");
            return true;
        }
        return false;
    }

    private void hideErrorMsg(TextInputEditText editText, IngrEditText ingrEditText, TextInputLayout textInputLayout, String helperText) {
        if (ingrEditText != null) {
            ingrEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    textInputLayout.setHelperText(helperText);
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
                    textInputLayout.setHelperText(helperText);
                    textInputLayout.setError("");
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
        }
    }
}