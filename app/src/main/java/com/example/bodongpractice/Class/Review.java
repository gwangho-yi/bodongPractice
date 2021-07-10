package com.example.bodongpractice.Class;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Review {


    @Expose
    @SerializedName("spot_id") private String spotId;
    @Expose
    @SerializedName("user_email") private String userEmail;
    @Expose
    @SerializedName("user_nickName") private String userNickName;
    @Expose
    @SerializedName("user_profile") private String userProfile;
    @Expose
    @SerializedName("review_id") private String reviewId;
    @Expose
    @SerializedName("review_content") private String reviewContent;
    @Expose
    @SerializedName("review_img") private ArrayList<String> reviewImg;
    @Expose
    @SerializedName("review_create_date") private String ReviewCreateDate;
    @Expose
    @SerializedName("review_edit_date") private String ReviewEditDate;
    @Expose
    @SerializedName("thumb_count") private String thumbCount;

    //이미 리뷰에 추천했는지 아닌지를 찾아오는 변수
    @Expose
    @SerializedName("already_user_count") private String alreadyUserCount;
    @Expose
    @SerializedName("review_rating") private String ReviewRating;
    @Expose
    @SerializedName("message") private String message;
    @Expose
    @SerializedName("status") private String status;


    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }

    public ArrayList<String> getReviewImg() {
        return reviewImg;
    }

    public void setReviewImg(ArrayList<String> reviewImg) {
        this.reviewImg = reviewImg;
    }

    public String getReviewCreateDate() {
        return ReviewCreateDate;
    }

    public void setReviewCreateDate(String reviewCreateDate) {
        ReviewCreateDate = reviewCreateDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReviewRating() {
        return ReviewRating;
    }

    public void setReviewRating(String reviewRating) {
        ReviewRating = reviewRating;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getThumbCount() {
        return thumbCount;
    }

    public void setThumbCount(String thumbCount) {
        this.thumbCount = thumbCount;
    }

    public String getAlreadyUserCount() {
        return alreadyUserCount;
    }

    public void setAlreadyUserCount(String alreadyUserCount) {
        this.alreadyUserCount = alreadyUserCount;
    }

    public String getReviewEditDate() {
        return ReviewEditDate;
    }

    public void setReviewEditDate(String reviewEditDate) {
        ReviewEditDate = reviewEditDate;
    }
}
