package com.dpio.nutrifood.Model;

import java.util.Date;
import java.util.List;

public class PostModelClass {

    String UID, userName, userEmail, mealName, mealImage, userProfile;
    int heartCount;
    Date postDate;
    List<String> mealIngrArray, mealProcArray;

    public PostModelClass() {}

    public PostModelClass(String UID, String userName, String userEmail, String userProfile, String mealName, List<String> mealIngrArray, List<String> mealProcArray, String mealImage, Date postDate, int heartCount) {
        this.UID = UID;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userProfile = userProfile;
        this.mealName = mealName;
        this.mealIngrArray = mealIngrArray;
        this.mealProcArray = mealProcArray;
        this.mealImage = mealImage;
        this.postDate = postDate;
        this.heartCount = heartCount;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealImage() {
        return mealImage;
    }

    public void setMealImage(String mealImage) {
        this.mealImage = mealImage;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }

    public List<String> getMealIngrArray() {
        return mealIngrArray;
    }

    public void setMealIngrArray(List<String> mealIngrArray) {
        this.mealIngrArray = mealIngrArray;
    }

    public List<String> getMealProcArray() {
        return mealProcArray;
    }

    public void setMealProcArray(List<String> mealProcArray) {
        this.mealProcArray = mealProcArray;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public int getHeartCount() {
        return heartCount;
    }

    public void setHeartCount(int heartCount) {
        this.heartCount = heartCount;
    }
}
