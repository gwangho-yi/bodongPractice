package com.example.bodongpractice.Interface;

import com.example.bodongpractice.Class.User;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface NicknameCheckInterface {

    @FormUrlEncoded
    @POST("nickname_check.php")
    Call<User> getUserNickname(

        //@Field("nickname")은 키 값으로 넘겨주는 방식이다 이렇게 써서 php에 날려주면 php에서 $_POST['nickname']으로 받을 수 있다
        @Field("nickname") String nickname

    );
}
