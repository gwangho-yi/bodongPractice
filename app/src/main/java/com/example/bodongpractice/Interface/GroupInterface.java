package com.example.bodongpractice.Interface;

import com.example.bodongpractice.Class.Group;
import com.example.bodongpractice.Class.Spot;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface GroupInterface {

    //스팟 등록
    @Multipart
    @POST("add_group.php")
    Call<Group> AddGroup(
            @Part MultipartBody.Part File,
            @Part("group_name") RequestBody groupName,
            @Part("group_content") RequestBody groupContent,
            @Part("group_category") RequestBody groupCategory,
            @Part("group_location") RequestBody groupLocation,
            @Part("group_create_date") RequestBody groupCreateDate,
            @Part("group_people_number") RequestBody groupPeopleNumber,
            @Part("user_email") RequestBody userEmail
    );


    @GET("group_select.php")
    Call<List<Group>> SelectGroup(
            //내 위치와 카테고리를 사용해서 가지고 온다.
            @Query("my_location") String location,
            @Query("category") String category
    );

    @GET("group_detail.php")
    Call<Group> GroupDetail(
            //내 위치와 카테고리를 사용해서 가지고 온다.
            @Query("group_id") String groupid,
            @Query("user_email") String userEmail
    );

    @GET("group_join.php")
    Call<Group> GroupJoin(
            //내 위치와 카테고리를 사용해서 가지고 온다.
            @Query("group_id") String groupid,
            @Query("user_email") String userEmail
    );
}
