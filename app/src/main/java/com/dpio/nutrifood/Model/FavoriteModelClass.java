package com.dpio.nutrifood.Model;

public class FavoriteModelClass {
    String mealName, mealImage, mealCategory, mealIndex;

    public FavoriteModelClass() {
    }

    public FavoriteModelClass(String mealName, String mealImage, String mealCategory, String mealIndex) {
        this.mealName = mealName;
        this.mealImage = mealImage;
        this.mealCategory = mealCategory;
        this.mealIndex = mealIndex;
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

    public String getMealCategory() {
        return mealCategory;
    }

    public void setMealCategory(String mealCategory) {
        this.mealCategory = mealCategory;
    }

    public String getMealIndex() {
        return mealIndex;
    }

    public void setMealIndex(String mealIndex) {
        this.mealIndex = mealIndex;
    }

}
