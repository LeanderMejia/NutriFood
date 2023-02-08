package com.dpio.nutrifood.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dpio.nutrifood.R;
import com.google.firebase.auth.FirebaseAuth;



public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private SharedPreferences sharedPreferences;
    private String firstTimeInstall;
    private boolean accountSignIn;
    private Button facebookBtn, googleBtn, skipBtn;
    private Bundle signInBundle, signOutBundle;
    private String signInValue, signOutValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        facebookBtn = findViewById(R.id.facebookBtn);
        googleBtn = findViewById(R.id.googleBtn);
        skipBtn = findViewById(R.id.skipBtn);

        sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
        firstTimeInstall = sharedPreferences.getString("FirstTimeInstall", "");

        signInBundle = getIntent().getExtras();
        signOutBundle = getIntent().getExtras();

        // To prevent opening activity when user click sign in with google or fb
        if (!accountSignIn || signInBundle != null) {
            openActivity();
        }

        facebookBtn.setOnClickListener(this::signInWithFacebook);
        googleBtn.setOnClickListener(this::signInWithGoogle);
        skipBtn.setOnClickListener(this::skipForNow);

    }

    public void signInWithFacebook(View view) {
        accountSignIn = true;
        startActivity(new Intent(SignInActivity.this, FacebookSignInActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    public void signInWithGoogle(View view) {
        accountSignIn = true;
        startActivity(new Intent(SignInActivity.this, GoogleSignInActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    public void skipForNow(View view) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("FirstTimeInstall", "Yes");
        editor.apply();
        startActivity(new Intent(SignInActivity.this, MainActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    private void openActivity() {
        // To disable redirecting activity when user click sign out and sign in
        if (signOutBundle != null) {
            sharedPreferences = getSharedPreferences("PREFERENCE", MODE_PRIVATE);
            sharedPreferences.edit().remove("FirstTimeInstall").commit();
            signOutValue = signOutBundle.getString("message");
            return;
        }

        if ((mAuth.getCurrentUser() != null) || (firstTimeInstall.equals("Yes") && signInBundle == null)) {
            startActivity(new Intent(SignInActivity.this, MainActivity.class));
            overridePendingTransition(0, 0);
            finish();
        }
    }
}