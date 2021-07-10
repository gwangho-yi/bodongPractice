package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.NicknameCheckInterface;
import com.example.bodongpractice.Interface.UserInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class LoginActivity extends AppCompatActivity {

    //회원가입이동
    TextView goRegisterText;
    //비번찾기 이동
    TextView findPassword;

    Button loginButton;


    //이메일
    TextView email;
    //비밀번호
    TextView password;
    ArrayList<User> currentUserArrayList;
    ArrayList<User> autoLoginUserArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email=findViewById(R.id.email);

        password=findViewById(R.id.password);



        loginButton=findViewById(R.id.loginButton);

        //유저의 정보가 저장 될 리스트



        currentUserLoadData();
//        for (int i = 0; i < currentUserArrayList.size(); i++){
//            currentUserArrayList.remove(i);
//        }
//        currentUserSaveData(currentUserArrayList);

        currentUserArrayList = new ArrayList<User>();

        if(currentUserArrayList.isEmpty()){

        }
        else{


//            System.out.println("유저의 정보는 " + currentUserArrayList.get(0).getEmail());
        }


        //자동 로그인이 되어있으면 다음 화면으로 날라간다
        autoLoginUserLoadData();
//        for (int i = 0; i < autoLoginUserArrayList.size(); i++){
//            autoLoginUserArrayList.remove(i);
//        }
//        autoLoginSaveData(autoLoginUserArrayList);

        if(autoLoginUserArrayList.isEmpty()){

        }
        else{
//            System.out.println("자동로그인 된 유저의 정보는 " + autoLoginUserArrayList.get(0).getEmail());
            //로그인 다 됐으니까 메인 화면으로 이동
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }



        //로그인
        loginButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
//                 Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                 startActivity(intent);
                 final String emailText = email.getText().toString();

                 final String passwordText = password.getText().toString();
                 //이메일과 비번을 서버에 날려준다

                 UserInterface userInterface =  ApiClient.getApiClient().create(UserInterface.class);
                 //유저 메일과 비번을 날려서 가지고 온다.
                 Call<User> call = userInterface.getUserInfo(emailText, passwordText);

                 call.enqueue(new Callback<User>()
                 {
                     //연결 성공 시에 싱행되는 부분
                     @Override
                     public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response)
                     {

                             Log.e("onSuccess", response.body().getMessage());
                             String status = response.body().getStatus();

                             if(status.contains("false")){
                                 Toast.makeText(LoginActivity.this,"존재하지 않는 회원입니다", Toast.LENGTH_SHORT).show();
                             }else{
                                 Toast.makeText(LoginActivity.this,"로그인 되었습니다", Toast.LENGTH_SHORT).show();



//                                 currentUserLoadData();
//                                //유저의 정보가 저장이 되어있으면 지워주기
//                                 //현재 로그인한 유저는 한명이면 되니까
//                                 if(currentUserArrayList!=null){
//                                     currentUserArrayList.remove(0);
//                                 }else{
//
//                                 }
                                 //현재 로그인한 유저정보 저장
                                 //유저의 객체를 생성해서 이메일 비번 닉네임을 저장해주면 되나?
                                 User user = new User();
                                 user.setEmail(response.body().getEmail());
                                 //가지고 와서 현재로그인 한 유저에 이메일을 넣어서 보관한다..

                                 currentUserArrayList.add(user);
                                 currentUserSaveData(currentUserArrayList);


                                 boolean autoLoginCheck = false;
                                 //현재 로그인한 유저 상태 유지
                                 CheckBox checkBox = (CheckBox) findViewById(R.id.autoLoginCheck) ;
                                 if (checkBox.isChecked()) {
                                     ////자동로그인 체크 되어있으면 자동로그인에 유저 정보 저장장
                                     System.out.println("자동로그인 체크 됨");

                                     autoLoginCheck=true;
                                 } else {
                                     // TODO : CheckBox is unchecked.
                                     System.out.println("자동로그인 체크 안됨");
                                     autoLoginCheck=false;
                                 }

                                 //자동로그인에 체크 하였다면 자동로그인 되는걸로
                                 if(autoLoginCheck){
                                     //자동로그인에 유저 정보 저장
                                     autoLoginUserArrayList = new ArrayList<>();
                                     autoLoginUserArrayList.add(user);
                                     autoLoginSaveData(autoLoginUserArrayList);
                                 }
                                 else{

                                 }

                                 //로그인 다 됐으니까 메인 화면으로 이동
                                 Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                                 intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                                 startActivity(intent);


                             }

                     }

                     @Override
                     public void onFailure(@NonNull Call<User> call, @NonNull Throwable t)
                     {
                         Log.e("onfail", "에러 = " + t.getMessage());
                     }
                 });
             }
         });




        //회원가입이동
        goRegisterText=findViewById(R.id.registerText);
        goRegisterText.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(LoginActivity.this, Register.class);
                 startActivity(intent);
             }
         });


        //비밀번호 찾기 이동
        findPassword=findViewById(R.id.findPassword);
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, FindPassword.class);
                startActivity(intent);
            }
        });


    }

    //로그인 성공하면 유저의 정보를 저장한다.
    void currentUserSaveData(ArrayList<User> arrayList){
//        String userString =  jsonObject.toString();
        // 문자열 저장하기

        Gson saveGson = new Gson();

        String saveSharedName = "shared"; // 저장할 SharedPreferences 이름 지정.
        String saveKey = "유저정보"; // 저장할 데이터의 Key값 지정.
        String saveValue = saveGson.toJson(arrayList); //저장할 데이터의 Content 지정.

        SharedPreferences saveShared = getSharedPreferences(saveSharedName,MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = saveShared.edit();

        sharedEditor.putString(saveKey,saveValue);
        System.out.println(saveValue);
        sharedEditor.commit();
    }

    //자동 저장 체크 시에 쉐어드에 저장한다.
    void autoLoginSaveData(ArrayList<User> arrayList){
//        String userString =  jsonObject.toString();
        // 문자열 저장하기

        Gson saveGson = new Gson();

        String saveSharedName = "shared"; // 저장할 SharedPreferences 이름 지정.
        String saveKey = "자동로그인"; // 저장할 데이터의 Key값 지정.
        String saveValue = saveGson.toJson(arrayList); //저장할 데이터의 Content 지정.

        SharedPreferences saveShared = getSharedPreferences(saveSharedName,MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = saveShared.edit();

        sharedEditor.putString(saveKey,saveValue);
        System.out.println(saveValue);

        sharedEditor.commit();
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

        SharedPreferences loadShared = getSharedPreferences(loadSharedName, MODE_PRIVATE);
        loadValue = loadShared.getString(loadKey, defaultValue);

        //loadValue 안에 리스트의 데이터가 스트링 형태로 담아지기 때문에 스트링으로 되어있는 loadValue를 원래의 리스트 형태로 가지고 온다.
        currentUserArrayList = loadGson.fromJson(loadValue, type);

        if (currentUserArrayList == null) {
            currentUserArrayList = new ArrayList<>();
        }
    }

    //데이터 불러오기
    void autoLoginUserLoadData() {

        //먼저 지슨을 로드해주고
        Gson loadGson = new Gson();
        //리스트의 경우 타입을 지정해줘야한다.
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();


        // 문자열 불러오기
        String loadSharedName = "shared"; // 가져올 SharedPreferences 이름 지정
        String loadKey = "자동로그인"; // 가져올 데이터의 Key값 지정
        //여기에 json어레이가 담기도록 해보자
        String loadValue = ""; // 가져올 데이터가 담기는 변수
        String defaultValue = ""; // 가져오는것에 실패 했을 경우 기본 지정 텍스트. 보통 기본 값은 널 값으로 준다.

        SharedPreferences loadShared = getSharedPreferences(loadSharedName, MODE_PRIVATE);
        loadValue = loadShared.getString(loadKey, defaultValue);

        //loadValue 안에 리스트의 데이터가 스트링 형태로 담아지기 때문에 스트링으로 되어있는 loadValue를 원래의 리스트 형태로 가지고 온다.
        autoLoginUserArrayList = loadGson.fromJson(loadValue, type);

        if (autoLoginUserArrayList == null) {
            autoLoginUserArrayList = new ArrayList<>();
        }
    }
}