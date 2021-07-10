package com.example.bodongpractice.Interface;

import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Class.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

public interface SpotInterface {

    //스팟 등록
    @Multipart
    @POST("add_spot.php")
    Call<Spot> AddSpot(
        @Part List<MultipartBody.Part> File,
        @Part("user_email") RequestBody user_email,
        @Part("spot_name") RequestBody spot_name,
        @Part("spot_content") RequestBody spot_content,
        //이미지의 갯수를 카운트하기 위한 변수다
        @Part("count") RequestBody count,
        @Part("tagCount") RequestBody tagCount,
        @Part("locationName") RequestBody locationName,
        @PartMap Map<String, RequestBody> map,
        @Part("lat") RequestBody lat,
        @Part("lng") RequestBody lng,
        @Part("createTime") RequestBody createTime
    );


    @GET("spot_select.php")
    Call<List<Spot>> selectSpot(
        @Query("my_location") String location,
        @Query("tag") String tag
    );

    @GET("spot_detail.php")
    Call<List<Spot>> SpotDetail(
        @Query("spot_id") String spot_id,
        @Query("user_email") String user_email
    );


    //스팟 수정
    @Multipart
    @POST("edit_spot.php")
    Call<Spot> EditSpot(
            @Part("spot_id") RequestBody spotId,
            @Part List<MultipartBody.Part> File,
            @Part("user_email") RequestBody user_email,
            @Part("spot_name") RequestBody spot_name,
            @Part("spot_content") RequestBody spot_content,
            //이미지의 갯수를 카운트하기 위한 변수다
            @Part("count") RequestBody count,
            @Part("tagCount") RequestBody tagCount,
            @Part("spot_location") RequestBody locationName,
            @PartMap Map<String, RequestBody> map,
            @Part("spot_lat") RequestBody lat,
            @Part("spot_lng") RequestBody lng,
            @Part("edit_date") RequestBody editDate,
            @PartMap Map<String, RequestBody> deleteImgMap,
            @Part("delete_img_count") RequestBody deleteImgCount
    );


    @GET("delete_spot.php")
    Call<Spot> DeleteSpot(
        @Query("spot_id") String spot_id
    );

    @GET("spot_favorite.php")
    Call<Spot> favoriteSpot(
        @Query("spot_id") String spot_id,
        @Query("user_email") String user_email
    );

    //이 위치 검색 버튼을 눌렀을때 서버에서 500미터 안에 있는 스팟의 위치들을 가지고 온다.
    //현재 내가 보고 있는 화면의 중심의 위경도를 서버에 보내서 작업을 한다.
    @GET("map_search.php")
    Call<List<Spot>> mapSelect(
            @Query("current_lat") double lat,
            @Query("current_lng") double lng
    );
    @GET("my_favorite.php")
    Call<List<Spot>> GetFavorite(
        @Query("user_email") String user_email

    );


}
