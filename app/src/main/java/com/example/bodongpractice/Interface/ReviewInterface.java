package com.example.bodongpractice.Interface;

import com.example.bodongpractice.Class.Review;
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

public interface ReviewInterface {

    //리뷰 등록
    @Multipart
    @POST("add_review.php")
    Call<Review> AddReview(
        @Part("spot_id") RequestBody spotId,
        @Part("review_rating") RequestBody reviewRating,
        @Part List<MultipartBody.Part> File,
        @Part("user_email") RequestBody user_email,
        @Part("review_content") RequestBody review_content,
        //이미지의 갯수를 카운트하기 위한 변수다
        @Part("count") RequestBody count,
        @Part("review_create_date") RequestBody createDate
    );

    //리뷰 가져오기
    @GET("review_select.php")
    Call<List<Review>> selectReview(
        @Query("spot_id") String spotId,
        @Query("user_email") String userEmail
    );

    //리뷰 추천버튼
    @GET("review_thumb.php")
    Call<Review> thumbInput(
        @Query("review_id") String reviewId,
        @Query("user_email") String userEmail
    );


    @GET("review_thumb_view.php")
    Call<Review> thumbView(
        @Query("review_id") String reviewId,
        @Query("user_email") String userEmail
    );


    //리뷰 수정 할때 정보를 가져오는 메서드
    @GET("edit_get_review.php")
    Call<Review> editGetReview(
        @Query("review_id") String reviewId
    );


    //리뷰 수정 메서드
    @Multipart
    @POST("edit_review.php")
    Call<Review> editReview(
        @Part List<MultipartBody.Part> File,
        @Part("review_id") RequestBody reviewId,
        @Part("review_rating") RequestBody reviewRating,
        @Part("review_content") RequestBody reviewContent,
        @Part("count") RequestBody count,
        @Part("edit_date") RequestBody editDate,
        @PartMap Map<String, RequestBody> deleteImgMap,
        @Part("delete_img_count") RequestBody deleteImgCount
    );
    //리뷰 삭제
    @GET("delete_review.php")
    Call<Review> DeleteReview(
            @Query("review_id") String reviewId
    );
}
