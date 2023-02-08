package com.dpio.nutrifood.Class;

import com.google.firebase.auth.FirebaseAuth;

public class FavoriteClass {
    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public String UID;

    public void validateUser() {
        if (mAuth.getCurrentUser() != null) {
            UID = mAuth.getCurrentUser().getUid();
        } else {
            UID = "No Account";
        }
    }

    public boolean mealIsExist(String mealNameData, String mealName) {
        return mealNameData.equals(mealName);
    }
}
