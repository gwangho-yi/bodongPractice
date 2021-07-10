package com.example.bodongpractice.Interface;

import com.example.bodongpractice.Class.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ChangePasswordInterface {

    @FormUrlEncoded
    @POST("change_password.php")
    Call<User> getUserRegist(
            @Field("email") String email,
            @Field("password") String password
    );
}
