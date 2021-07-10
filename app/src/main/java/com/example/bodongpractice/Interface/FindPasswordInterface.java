package com.example.bodongpractice.Interface;

import com.example.bodongpractice.Class.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface FindPasswordInterface {

    //유저가 입력한 이메일이 등록된 이메일인지 확인하는 php
    //회원가입시에 등록된이메일 중복확인
    //비번찾을 때 등록된 이메일인지 중복확인
    @FormUrlEncoded
    @POST("email_check.php")
    Call<User> getUserRegist(
        @Field("email") String email


    );
}
