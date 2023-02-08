package com.dpio.nutrifood.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dpio.nutrifood.Fragment.BabiesMealListFragment;
import com.dpio.nutrifood.Fragment.CommunityFragment;
import com.dpio.nutrifood.Fragment.FavoriteFragment;
import com.dpio.nutrifood.Fragment.HomeFragment;
import com.dpio.nutrifood.Fragment.OurTeamFragment;
import com.dpio.nutrifood.Fragment.ReferenceFragment;
import com.dpio.nutrifood.Fragment.StrategiesFragment;
import com.dpio.nutrifood.Fragment.TermsAndConditionsFragment;
import com.dpio.nutrifood.Fragment.ToddlerMealListFragment;
import com.dpio.nutrifood.R;
import com.facebook.Profile;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView sideNav;
    private BottomNavigationView bottomNav;
    private FrameLayout frameLayout;
    private CircleImageView userProfile;
    private TextView userName, userEmail;
    private Button signOutBtn, signInBtn;
    private FirebaseAuth mAuth;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.sideNav);
        View view = navigationView.getHeaderView(0);

        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);
        sideNav = findViewById(R.id.sideNav);
        bottomNav = findViewById(R.id.bottomNav);
        frameLayout = findViewById(R.id.frameLayout);

        mAuth = FirebaseAuth.getInstance();
        userName = view.findViewById(R.id.userName);
        userEmail = view.findViewById(R.id.userEmail);
        userProfile = view.findViewById(R.id.userProfile);

        signOutBtn = view.findViewById(R.id.signOutBtn);
        signInBtn = view.findViewById(R.id.signInBtn);

        signInBtn.setOnClickListener(view1 -> {
            message("message", "signIn");
        });

        signOutBtn.setOnClickListener(view1 -> {
            message("message", "signOut");

            googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

            googleSignInClient.signOut();
            mAuth.signOut();

            startActivity(new Intent(MainActivity.this, SignInActivity.class));
            overridePendingTransition(0, 0);
        });

        if (mAuth.getCurrentUser() != null) {
            signInBtn.setVisibility(View.GONE);
            signOutBtn.setVisibility(View.VISIBLE);
            displayAccountInfo();
        } else {
            signInBtn.setVisibility(View.VISIBLE);
            signOutBtn.setVisibility(View.GONE);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new HomeFragment()).commit();

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openNavDrawer, R.string.closeNavDrawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        sideNavListener();
        bottomNavListener();
    }

    private void displayAccountInfo() {
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        signInBtn.setVisibility(View.GONE);
        signOutBtn.setVisibility(View.VISIBLE);

        userName.setText(mAuth.getCurrentUser().getDisplayName());
        userEmail.setText(mAuth.getCurrentUser().getEmail());
        List<? extends UserInfo> provider = mAuth.getCurrentUser().getProviderData();
        for (UserInfo signInProvider : provider) {
            if (signInProvider.getProviderId().equals("facebook.com")) {
                Uri profileUrl = Profile.getCurrentProfile().getProfilePictureUri(500, 500);
                Glide.with(this).load(profileUrl).into(userProfile);
            } else {
                Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl()).into(userProfile);
            }
        }
    }

    private void sideNavListener() {
        sideNav.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.babiesMeal:
                    setHomeSelectedNav();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new BabiesMealListFragment()).addToBackStack(null).commit();
                    break;
                case R.id.toddlersMeal:
                    setHomeSelectedNav();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ToddlerMealListFragment()).addToBackStack(null).commit();
                    break;
                case R.id.strategies:
                    setHomeSelectedNav();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new StrategiesFragment()).addToBackStack(null).commit();
                    break;
                case R.id.community:
                    setCommunitySelectedNav();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new CommunityFragment()).addToBackStack(null).commit();
                    break;
                case R.id.ourTeam:
                    setHomeSelectedNav();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new OurTeamFragment()).addToBackStack(null).commit();
                    break;
                case R.id.reference:
                    setHomeSelectedNav();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new ReferenceFragment()).addToBackStack(null).commit();
                    break;
                case R.id.termsAndConditions:
                    setHomeSelectedNav();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new TermsAndConditionsFragment()).addToBackStack(null).commit();
                    break;
                default:
                    break;
            }
            drawerLayout.close();
            return true;
        });

    }

    private void bottomNavListener() {
        bottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            Fragment fragment = null;
            switch (menuItem.getItemId()) {
                case R.id.home:
                    fragment = new HomeFragment();
                    break;
                case R.id.community:
                    fragment = new CommunityFragment();
                    break;
                case R.id.favorite:
                    fragment = new FavoriteFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).addToBackStack(null).commit();
            return true;
        });
    }

    private void message(String key, String value) {
        Intent intent = new Intent(MainActivity.this, SignInActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(key, value);
        intent.putExtras(bundle);
        overridePendingTransition(0, 0);
        startActivity(intent);
        finish();
    }

    private void setHomeSelectedNav() {
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.home);
        bottomNav.setActivated(true);
    }

    private void setCommunitySelectedNav() {
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.community);
        bottomNav.setActivated(true);
    }
}