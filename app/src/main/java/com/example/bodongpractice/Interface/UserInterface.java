package com.example.bodongpractice.Interface;

import com.example.bodongpractice.Class.User;

import java.io.File;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;


//회원가입 인터페이스
public interface UserInterface
{
    @FormUrlEncoded
    @POST("register.php")
    Call<User> getUserRegist(
            @Field("email") String email,
            @Field("nickname") String nickname,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("login.php")
    Call<User> getUserInfo(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("user_info.php")
    Call<User> getUserProfile(
            @Field("email") String email
    );
    //닉네임 바꿀대 사용
    @FormUrlEncoded
    @POST("user_profile_commit.php")
    Call<User> getUserProfileCommit(
            @Field("email") String email,
            @Field("nickname") String nickname

    );


    @FormUrlEncoded
    @POST("email_check.php")
    Call<User> getUserEmailCheck(
            @Field("email") String email
    );

    //프로필 이미지 업로드 메서드
    @Multipart
    @POST("test_image_upload.php")
    Call<User> uploadImage(
            @Part MultipartBody.Part File,
            @Part("email") RequestBody email
    );





}
