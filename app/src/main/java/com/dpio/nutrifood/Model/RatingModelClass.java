package com.dpio.nutrifood.Model;

public class RatingModelClass {

    String uid, postId;
    boolean heartRating;

    public RatingModelClass(){}

    public RatingModelClass(String uid, String postId, boolean heartRating) {
        this.uid = uid;
        this.postId = postId;
        this.heartRating = heartRating;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public boolean isHeartRating() {
        return heartRating;
    }

    public void setHeartRating(boolean heartRating) {
        this.heartRating = heartRating;
    }
}
