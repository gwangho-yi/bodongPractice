package com.example.bodongpractice.Class;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    //이메일 닉네임 비밀번호
    @Expose
    @SerializedName("email") private String email;
    @Expose
    @SerializedName("nickname") private String nickname;
    @Expose
    @SerializedName("password") private String password;

    @Expose
    @SerializedName("status") private String status;
    @Expose
    @SerializedName("message") private String message;

    @Expose
    @SerializedName("profile") private String profile;

    @Expose
    @SerializedName("member_grade") private String grade;



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }
}
