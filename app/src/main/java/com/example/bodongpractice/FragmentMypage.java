package com.example.bodongpractice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.NicknameCheckInterface;
import com.example.bodongpractice.Interface.UserInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;


public class FragmentMypage extends Fragment {

    TextView titleName;
    TextView userNicknameText;
    TextView userEmailText;

    LinearLayout linearLayout;

    //즐겨찾기 페이지 이동
    LinearLayout goFavorite;

    ArrayList<User> currentUserArrayList = new ArrayList();
    ArrayList<User> autoLoginUserArrayList = new ArrayList();

    Button logoutButton;

    ImageView profileImage;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mypage,container,false);
        titleName=view.findViewById(R.id.titleName);

        titleName.setText("마이페이지");

        //유저의 정보를 찾아오는 텍스트 2개
        userNicknameText = view.findViewById(R.id.userNicknameText);
        userEmailText = view.findViewById(R.id.userEmailText);


        profileImage= view.findViewById(R.id.profileImage);

        currentUserLoadData(getActivity());

        //정보 수정창으로 이동
        linearLayout = view.findViewById(R.id.linearLayout);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileChange.class);
                startActivity(intent);
            }
        });




        logoutButton = view.findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("클릭");

                //자동로그인 유저정보 제거
                //다시 로그인 창으로 돌아갔을때 아무 정보도 없어서 다시 로그인 해야한다.
//                if(autoLoginUserArrayList.isEmpty()){
//
//                }else{
//                    autoLoginUserArrayList.remove(0);
//                    autoLoginSaveData(autoLoginUserArrayList);
//                }


//                //현재로그인한 유저정보 제거
//                currentUserArrayList.remove(0);
//                currentUserSaveData(currentUserArrayList);
                currentUserArrayList = new ArrayList();
                currentUserSaveData(currentUserArrayList, getActivity());

                autoLoginUserArrayList= new ArrayList();
                autoLoginSaveData(autoLoginUserArrayList, getActivity());
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });


        //즐겨찾기 이동
        goFavorite = view.findViewById(R.id.goFavorite);
        goFavorite.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getActivity(), MyFavorite.class);
                 startActivity(intent);
             }
         });



        // Inflate the layout for this fragment
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();

        //정보창에 이메일 닉네임 표시
        //유저 이메일
        if(currentUserArrayList!=null){
            User user = currentUserArrayList.get(0);

            String userEmail = user.getEmail();
            System.out.println("마이페이지 유저 이메일"+userEmail);
            userEmailText.setText(userEmail);



            //유저 닉네임 찾아오기
            UserInterface userInterface = ApiClient.getApiClient().create(UserInterface.class);
            //유저가 입력한 닉네임을 맴버변수로 넣어준다
            Call<User> call = userInterface.getUserProfile(userEmail);
            call.enqueue(new Callback<User>()
            {
                @Override
                public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        //통신이 잘 되었을 때 결과 확인 로그
                        Log.e("onSuccess", response.body().getStatus());


                        String status = response.body().getStatus();
                        System.out.println(status);
                        if(status.contains("true")){
                            //닉네임 세팅
                            userNicknameText.setText(response.body().getNickname());

                            Glide.with(FragmentMypage.this)
                                    .load("http://49.247.213.240/UserProfileImg/"+response.body().getProfile())
                                    .circleCrop()
                                    .into(profileImage);
                        }
                        //닉네임이 중복됐을 때
                        else{

                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<User> call, @NonNull Throwable t)
                {
                    Log.e(TAG, "에러 = " + t.getMessage());
                }
            });

        }else{

        }

    }


    //데이터 불러오기
    void currentUserLoadData(Context context) {


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

        SharedPreferences loadShared = context.getSharedPreferences(loadSharedName, 0);
        loadValue = loadShared.getString(loadKey, defaultValue);

        //loadValue 안에 리스트의 데이터가 스트링 형태로 담아지기 때문에 스트링으로 되어있는 loadValue를 원래의 리스트 형태로 가지고 온다.
        currentUserArrayList = loadGson.fromJson(loadValue, type);

        if (currentUserArrayList == null) {
            currentUserArrayList = new ArrayList<>();
        }
    }

    //자동 저장 체크 시에 쉐어드에 저장한다.
    void autoLoginSaveData(ArrayList<User> arrayList,Context context){
//        String userString =  jsonObject.toString();
        // 문자열 저장하기

        Gson saveGson = new Gson();

        String saveSharedName = "shared"; // 저장할 SharedPreferences 이름 지정.
        String saveKey = "자동로그인"; // 저장할 데이터의 Key값 지정.
        String saveValue = saveGson.toJson(arrayList); //저장할 데이터의 Content 지정.

        SharedPreferences saveShared = context.getSharedPreferences(saveSharedName,MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = saveShared.edit();

        sharedEditor.putString(saveKey,saveValue);
        System.out.println(saveValue);

        sharedEditor.commit();
    }

    //로그인 성공하면 유저의 정보를 저장한다.
    void currentUserSaveData(ArrayList<User> arrayList, Context context){
//        String userString =  jsonObject.toString();
        // 문자열 저장하기

        Gson saveGson = new Gson();

        String saveSharedName = "shared"; // 저장할 SharedPreferences 이름 지정.
        String saveKey = "유저정보"; // 저장할 데이터의 Key값 지정.
        String saveValue = saveGson.toJson(arrayList); //저장할 데이터의 Content 지정.

        SharedPreferences saveShared = context.getSharedPreferences(saveSharedName,MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = saveShared.edit();

        sharedEditor.putString(saveKey,saveValue);
        System.out.println(saveValue);
        sharedEditor.commit();
    }



}