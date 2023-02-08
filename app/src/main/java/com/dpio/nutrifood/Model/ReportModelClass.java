package com.dpio.nutrifood.Model;

import java.util.Date;

public class ReportModelClass {

    String uid, reportMessage, postId, mealName, mealIngr, mealProc, mealImage;
    boolean postIsDeleted;
    Date postDate;

    public ReportModelClass() {
    }

    public ReportModelClass(String uid, String reportMessage, String postId, Date postDate, String mealName, String mealIngr, String mealProc, String mealImage, boolean postIsDeleted) {
        this.uid = uid;
        this.reportMessage = reportMessage;
        this.postId = postId;
        this.postDate = postDate;
        this.mealName = mealName;
        this.mealIngr = mealIngr;
        this.mealProc = mealProc;
        this.mealImage = mealImage;
        this.postIsDeleted = postIsDeleted;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getReportMessage() {
        return reportMessage;
    }

    public void setReportMessage(String reportMessage) {
        this.reportMessage = reportMessage;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getMealIngr() {
        return mealIngr;
    }

    public void setMealIngr(String mealIngr) {
        this.mealIngr = mealIngr;
    }

    public String getMealProc() {
        return mealProc;
    }

    public void setMealProc(String mealProc) {
        this.mealProc = mealProc;
    }

    public String getMealImage() {
        return mealImage;
    }

    public void setMealImage(String mealImage) {
        this.mealImage = mealImage;
    }

    public boolean isPostIsDeleted() {
        return postIsDeleted;
    }

    public void setPostIsDeleted(boolean postIsDeleted) {
        this.postIsDeleted = postIsDeleted;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
    }
}
