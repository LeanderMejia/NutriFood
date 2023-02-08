package com.dpio.nutrifood.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.dpio.nutrifood.R;
import com.google.android.gms.ads.rewarded.RewardedAd;


public class SplashScreenActivity extends AppCompatActivity {

    private RewardedAd mRewardedAd;
    private final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreenActivity.this, SignInActivity.class));
            finish();
        }, 1000);
    }
}