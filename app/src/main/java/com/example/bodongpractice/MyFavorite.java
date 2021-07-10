package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.bodongpractice.Adapter.FavoriteAdapter;
import com.example.bodongpractice.Adapter.ReviewAdapter;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Review;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.ReviewInterface;
import com.example.bodongpractice.Interface.SpotInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFavorite extends AppCompatActivity {


    RecyclerView favoriteRecyclerView;

    FavoriteAdapter adapter;


    ArrayList<User> currentUserArrayList;

    String userEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorite);

        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("즐겨찾기 한 스팟");

        favoriteRecyclerView = findViewById(R.id.favoriteRecyclerView);


        currentUserLoadData();
        userEmail = currentUserArrayList.get(0).getEmail();

    }

    @Override
    protected void onStart() {
        super.onStart();

        SpotInterface spotInterface =  ApiClient.getApiClient().create(SpotInterface.class);

        Call<List<Spot>> call = spotInterface.GetFavorite(userEmail);
        call.enqueue(new Callback<List<Spot>>()
        {
            //연결 성공 시에 싱행되는 부분
            @Override
            public void onResponse(@NonNull Call<List<Spot>> call, @NonNull Response<List<Spot>> response)
            {

                Log.e("onSuccess", String.valueOf(response.body()));

                System.out.println(response.body().size());
                onGetResult((ArrayList<Spot>) response.body());

//                String status = response.body().getStatus();
//
//                System.out.println("안녕"+response.body().getMessage());

            }



            @Override
            public void onFailure(@NonNull Call<List<Spot>> call, @NonNull Throwable t)
            {
                Log.e("onfail", "에러 = " + t.getMessage());
            }
        });
    }

    void onGetResult(ArrayList<Spot> spotList){

        //어댑터를 바로 여기에 끼운다.
        adapter = new FavoriteAdapter(MyFavorite.this, spotList);

        favoriteRecyclerView.setLayoutManager(new LinearLayoutManager(MyFavorite.this, LinearLayoutManager.VERTICAL, false));
        favoriteRecyclerView.setAdapter(adapter);

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