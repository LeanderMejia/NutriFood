package com.dpio.nutrifood.Model;

import java.util.Date;

public class CommentModelClass {
    String UID, userName, userProfile, comment, postId;
    Date postDate;

    public CommentModelClass() {
    }

    public CommentModelClass(String UID, String userName, String userProfile, String comment, String postId, Date postDate) {
        this.UID = UID;
        this.userName = userName;
        this.userProfile = userProfile;
        this.comment = comment;
        this.postId = postId;
        this.postDate = postDate;
    }

    public Date getPostDate() {
        return postDate;
    }

    public void setPostDate(Date postDate) {
        this.postDate = postDate;
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

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
