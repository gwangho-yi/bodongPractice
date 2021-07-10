package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.bodongpractice.Adapter.ReviewAdapter;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Review;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.ReviewInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllReview extends AppCompatActivity {
    RecyclerView reviewRecyclerView;

    String spotId;
    ReviewAdapter reviewAdapter;

    TextView titleName;

    String spotName;

    ArrayList<User> currentUserArrayList;

    String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_review);

        currentUserLoadData();

        userEmail = currentUserArrayList.get(0).getEmail();

        //스팟의 아이디를 받아온다
        Intent intent = getIntent();
        spotId = intent.getStringExtra("spotId");
        spotName = intent.getStringExtra("spotName");

        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.

        titleName=findViewById(R.id.titleName);
        titleName.setText(spotName);
        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);


    }


    @Override
    protected void onStart() {
        super.onStart();
        getReview(spotId);
    }

    //리뷰를 가지고 온다. 리뷰는 스팟의 아이디를 사용해서 가져온다.
    private void getReview(String spotId){
        //스팟의 아이디를 이용해서 아이템을 가지고 온다.
        ReviewInterface reviewInterface =  ApiClient.getApiClient().create(ReviewInterface.class);

        Call<List<Review>> call = reviewInterface.selectReview(spotId,userEmail);

        call.enqueue(new Callback<List<Review>>()
        {
            //연결 성공 시에 싱행되는 부분
            @Override
            public void onResponse(@NonNull Call<List<Review>>  call, @NonNull Response<List<Review>> response)
            {


                //리뷰 객체가 담긴 리스트로 서버에서 받아온다.
                Log.e("onSuccess", String.valueOf(response.body()));
                System.out.println(response.body().size());


                //어댑터를 바로 여기에 끼운다.
                reviewAdapter = new ReviewAdapter(AllReview.this, response.body());

                reviewRecyclerView.setLayoutManager(new LinearLayoutManager(AllReview.this, LinearLayoutManager.VERTICAL, false));
                reviewRecyclerView.setAdapter(reviewAdapter);
            }



            @Override
            public void onFailure(Call<List<Review>> call, Throwable t)
            {
                Log.e("onfail", "에러 = " + t.getMessage());
            }
        });
    }


    //데이터 불러오기
    void currentUserLoadData() {


        //먼저 지슨을 로드해주고
        Gson loadGson = new Gson();
        //리스트의 경우 타입을 지정해줘야한다.
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();


        // 문자열 불러오기
        String loadSharedName = "shared"; // 가져올 SharedPreferences 이름 지정
        String loadKey = "유저정보"; // 가져올 데이터의 Key값 지정
        //여기에 json어레이가 담기도록 해보자
        String loadValue = ""; // 가져올 데이터가 담기는 변수
        String defaultValue = ""; // 가져오는것에 실패 했을 경우 기본 지정 텍스트. 보통 기본 값은 널 값으로 준다.

        SharedPreferences loadShared = getSharedPreferences(loadSharedName, 0);
        loadValue = loadShared.getString(loadKey, defaultValue);

        //loadValue 안에 리스트의 데이터가 스트링 형태로 담아지기 때문에 스트링으로 되어있는 loadValue를 원래의 리스트 형태로 가지고 온다.
        currentUserArrayList = loadGson.fromJson(loadValue, type);

        if (currentUserArrayList == null) {
            currentUserArrayList = new ArrayList<>();
        }
    }
}