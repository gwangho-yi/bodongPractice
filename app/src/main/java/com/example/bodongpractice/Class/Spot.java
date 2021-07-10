package com.example.bodongpractice.Class;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Spot {

    @Expose
    @SerializedName("spot_name") private String spotName;
    @Expose
    @SerializedName("spot_content") private String spotContent;
    @Expose
    @SerializedName("user_email") private String userEmail;
    @Expose
    @SerializedName("spot_id") private String SpotId;
    @Expose
    @SerializedName("spot_lat") private double SpotLat;
    @Expose
    @SerializedName("spot_lng") private double SpotLng;
    @Expose
    @SerializedName("spot_location") private String SpotLocation;
    @Expose
    @SerializedName("spot_create_date") private String SpotCreateDate;
    @Expose
    @SerializedName("spot_edit_date") private String spotEditDate;

    @Expose
    @SerializedName("spot_img") private ArrayList<String> spotImg;
    @Expose
    @SerializedName("spot_title_img") private String spotTitleImg;


    @Expose
    @SerializedName("spot_single_img") private String spotSingleImg;

    @Expose
    @SerializedName("status") private String status;
    @Expose
    @SerializedName("message") private String message;

    @Expose
    @SerializedName("spot_tag") private ArrayList<String> spotTag;

    @Expose
    @SerializedName("spot_rating") private String spotRating;
    @Expose
    @SerializedName("spot_review_count") private int spotReviewCount;

    @Expose
    @SerializedName("spot_favorite_count") private int spotFavoriteCount;

    @Expose
    @SerializedName("my_favorite_count") private int myFavoriteCount;

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public String getSpotContent() {
        return spotContent;
    }

    public void setSpotContent(String spotContent) {
        this.spotContent = spotContent;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getSpotId() {
        return SpotId;
    }

    public void setSpotId(String spotId) {
        SpotId = spotId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public double getSpotLat() {
        return SpotLat;
    }

    public void setSpotLat(double spotLat) {
        SpotLat = spotLat;
    }

    public double getSpotLng() {
        return SpotLng;
    }

    public void setSpotLng(double spotLng) {
        SpotLng = spotLng;
    }

    public String getSpotLocation() {
        return SpotLocation;
    }

    public void setSpotLocation(String spotLocation) {
        SpotLocation = spotLocation;
    }

    public String getSpotCreateDate() {
        return SpotCreateDate;
    }

    public void setSpotCreateDate(String spotCreateDate) {
        SpotCreateDate = spotCreateDate;
    }

    public ArrayList<String> getSpotImg() {
        return spotImg;
    }

    public void setSpotImg(ArrayList<String> spotImg) {
        this.spotImg = spotImg;
    }

    public ArrayList<String> getSpotTag() {
        return spotTag;
    }

    public void setSpotTag(ArrayList<String> spotTag) {
        this.spotTag = spotTag;
    }

    public String getSpotEditDate() {
        return spotEditDate;
    }

    public void setSpotEditDate(String spotEditDate) {
        this.spotEditDate = spotEditDate;
    }

    public String getSpotRating() {
        return spotRating;
    }

    public void setSpotRating(String spotRating) {
        this.spotRating = spotRating;
    }

    public int getSpotReviewCount() {
        return spotReviewCount;
    }

    public void setSpotReviewCount(int spotReviewCount) {
        this.spotReviewCount = spotReviewCount;
    }

    public int getSpotFavoriteCount() {
        return spotFavoriteCount;
    }

    public void setSpotFavoriteCount(int spotFavoriteCount) {
        this.spotFavoriteCount = spotFavoriteCount;
    }

    public int getMyFavoriteCount() {
        return myFavoriteCount;
    }

    public void setMyFavoriteCount(int myFavoriteCount) {
        this.myFavoriteCount = myFavoriteCount;
    }


    public String getSpotSingleImg() {
        return spotSingleImg;
    }

    public void setSpotSingleImg(String spotSingleImg) {
        this.spotSingleImg = spotSingleImg;
    }


    public String getSpotTitleImg() {
        return spotTitleImg;
    }

    public void setSpotTitleImg(String spotTitleImg) {
        this.spotTitleImg = spotTitleImg;
    }
}
