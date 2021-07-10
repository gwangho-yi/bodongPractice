package com.example.bodongpractice.Class;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Group {

    @Expose
    @SerializedName("group_id") private String groupId;
    @Expose
    @SerializedName("group_name") private String groupName;
    @Expose
    @SerializedName("group_content") private String groupContent;
    @Expose
    @SerializedName("group_category") private String groupCategory;
    @Expose
    @SerializedName("group_create_date") private String groupCreateDate;
    @Expose
    @SerializedName("group_update_date") private String groupUpdateDate;
    @Expose
    @SerializedName("group_member_count") private int groupMemberNumber;
    @Expose
    @SerializedName("group_current_member_count") private int groupCurrentMemberNumber;

    @Expose
    @SerializedName("group_title_img_location") private String groupTitleImgLocation;
    @Expose
    @SerializedName("group_location") private String groupLocation;

    @Expose
    @SerializedName("group_title_img") private String groupTitleImg;

    @Expose
    @SerializedName("message") private String message;
    @Expose
    @SerializedName("status") private String status;

    @Expose
    @SerializedName("user_list") private ArrayList<User> groupUserList;


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupContent() {
        return groupContent;
    }

    public void setGroupContent(String groupContent) {
        this.groupContent = groupContent;
    }

    public String getGroupCategory() {
        return groupCategory;
    }

    public void setGroupCategory(String groupCategory) {
        this.groupCategory = groupCategory;
    }

    public String getGroupCreateDate() {
        return groupCreateDate;
    }

    public void setGroupCreateDate(String groupCreateDate) {
        this.groupCreateDate = groupCreateDate;
    }

    public String getGroupUpdateDate() {
        return groupUpdateDate;
    }

    public void setGroupUpdateDate(String groupUpdateDate) {
        this.groupUpdateDate = groupUpdateDate;
    }

    public int getGroupMemberNumber() {
        return groupMemberNumber;
    }

    public void setGroupMemberNumber(int groupMemberNumber) {
        this.groupMemberNumber = groupMemberNumber;
    }

    public String getGroupTitleImgLocation() {
        return groupTitleImgLocation;
    }

    public void setGroupTitleImgLocation(String groupTitleImgLocation) {
        this.groupTitleImgLocation = groupTitleImgLocation;
    }

    public String getGroupLocation() {
        return groupLocation;
    }

    public void setGroupLocation(String groupLocation) {
        this.groupLocation = groupLocation;
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

    public String getGroupTitleImg() {
        return groupTitleImg;
    }

    public void setGroupTitleImg(String groupTitleImg) {
        this.groupTitleImg = groupTitleImg;
    }

    public ArrayList<User> getGroupUserList() {
        return groupUserList;
    }

    public void setGroupUserList(ArrayList<User> groupUserList) {
        this.groupUserList = groupUserList;
    }

    public int getGroupCurrentMemberNumber() {
        return groupCurrentMemberNumber;
    }

    public void setGroupCurrentMemberNumber(int groupCurrentMemberNumber) {
        this.groupCurrentMemberNumber = groupCurrentMemberNumber;
    }
}
