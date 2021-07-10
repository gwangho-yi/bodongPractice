package com.example.bodongpractice;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Adapter.GroupMemberAdapter;
import com.example.bodongpractice.Adapter.ReviewAdapter;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Group;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.Interface.GroupInterface;
import com.example.bodongpractice.Interface.SpotInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class FragmentGroupInfo extends Fragment {


    ImageView groupImg;
    TextView groupNameText;
    TextView groupContentText;
    TextView groupMemberCount;
    TextView groupMemberCount2;
    RecyclerView memberRecyclerView;
    GroupMemberAdapter groupMemberAdapter;

    //모임 가입 버튼
    Button groupJoinButton;

    String groupId;

    ArrayList<User> currentUserArrayList = new ArrayList<User>();

    String currentUserEmail;
    //그룹 총원 지정해주는 카운트
    String groupAllMemberCount;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group_info, container, false);

        groupId = getArguments().getString("groupId");

        System.out.println("액티비티로부터 전달 받은 그룹 아이디 : "+ groupId);


        //그룹 대표 이미지
        groupImg = view.findViewById(R.id.groupImg);
        //그룹 이름
        groupNameText = view.findViewById(R.id.groupNameText);
        //그룹 내용
        groupContentText = view.findViewById(R.id.groupContentText);
        //현재멤버 / 멤버 총원
        groupMemberCount = view.findViewById(R.id.groupMemberCount);
        //현재 멤버
        groupMemberCount2 = view.findViewById(R.id.groupMemberCount2);
        //가입된 멤버 리사이클러뷰
        memberRecyclerView = view.findViewById(R.id.memberRecyclerView);

        currentUserLoadData();

        currentUserEmail =  currentUserArrayList.get(0).getEmail();


        //그룹 가입버튼
        groupJoinButton = view.findViewById(R.id.groupJoinButton);
        groupJoinButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 GroupInterface groupInterface =  ApiClient.getApiClient().create(GroupInterface.class);

                 Call<Group> call = groupInterface.GroupJoin(groupId,currentUserEmail);

                 call.enqueue(new Callback<Group>()
                 {
                     //연결 성공 시에 싱행되는 부분
                     @Override
                     public void onResponse(@NonNull Call<Group> call, @NonNull Response<Group> response)
                     {

                         Group group = response.body();
                         System.out.println("가입 상태 : "+response.body().getMessage());

                         //가입 총원
                         groupMemberCount.setText(String.valueOf(group.getGroupCurrentMemberNumber())+"/"+ groupAllMemberCount);

                         //현재 가입된 인원 리스트
                         groupMemberCount2.setText("("+String.valueOf(group.getGroupCurrentMemberNumber())+"명)");

                         if(response.body().getMessage().equals("가입")){
                             System.out.println("가입됐따");
                             groupJoinButton.setBackgroundColor(Color.parseColor("#808080"));
                             groupJoinButton.setText("모임 탈퇴");
                             ArrayList<User> memberList = group.getGroupUserList();



                             //어댑터를 바로 여기에 끼운다.
                             groupMemberAdapter = new GroupMemberAdapter(getActivity(), memberList);

                             memberRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                             memberRecyclerView.setAdapter(groupMemberAdapter);
                         }else{
                             System.out.println("탈퇴했다");
                             groupJoinButton.setBackgroundColor(Color.parseColor("#E67A00"));
                             groupJoinButton.setText("모임 가입");

                             ArrayList<User> memberList = group.getGroupUserList();



                             //어댑터를 바로 여기에 끼운다.
                             groupMemberAdapter = new GroupMemberAdapter(getActivity(), memberList);

                             memberRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                             memberRecyclerView.setAdapter(groupMemberAdapter);
                         }



                         //현재 접속한 유저의 이메일과 아이템을 등록한 유저의 이메일이 같으면 수정 삭제 버튼이 생기도록 한다.
//                if(currentUserEmail.contains(getUserEmail)){
//                    System.out.println("등록한 유저와 같은 이메일입니다.");
//                    optionText.setVisibility(View.VISIBLE);
//                }else{
//                    optionText.setVisibility(View.GONE);
//                }

//                onGetResult((ArrayList<Group>) response.body());
//                System.out.println(response.body().size());


                     }



                     @Override
                     public void onFailure(@NonNull Call<Group> call, @NonNull Throwable t)
                     {
                         Log.e("onfail", "에러 = " + t.getMessage());
                     }
                 });
             }
         });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        GroupInterface groupInterface =  ApiClient.getApiClient().create(GroupInterface.class);

        Call<Group> call = groupInterface.GroupDetail(groupId,currentUserEmail);

        call.enqueue(new Callback<Group>()
        {
            //연결 성공 시에 싱행되는 부분
            @Override
            public void onResponse(@NonNull Call<Group> call, @NonNull Response<Group> response)
            {


                currentUserEmail =  currentUserArrayList.get(0).getEmail();

                Group group = response.body();
                System.out.println("그룹 내용은 : "+group.getGroupContent());



                //현재 접속한 유저의 이메일과 아이템을 등록한 유저의 이메일이 같으면 수정 삭제 버튼이 생기도록 한다.
//                if(currentUserEmail.contains(getUserEmail)){
//                    System.out.println("등록한 유저와 같은 이메일입니다.");
//                    optionText.setVisibility(View.VISIBLE);
//                }else{
//                    optionText.setVisibility(View.GONE);
//                }

//                onGetResult((ArrayList<Group>) response.body());
//                System.out.println(response.body().size());

                //데이터를 받아서 처리
                onGetResult(response.body());

            }



            @Override
            public void onFailure(@NonNull Call<Group> call, @NonNull Throwable t)
            {
                Log.e("onfail", "에러 = " + t.getMessage());
            }
        });


    }

    void onGetResult(Group group){

        //그룹 이미지
        String img =  "http://49.247.213.240/GroupImg/"+group.getGroupTitleImgLocation();
        //글라이드로 이미지 크기 조정
        Glide.with(groupImg)
                .load(img)
                .override(MATCH_PARENT,MATCH_PARENT)
                .into(groupImg);

        groupImg.setScaleType(ImageView.ScaleType.FIT_XY);

        //상세페이지 이름 세팅
        GroupDetail.titleName.setText(group.getGroupName());
        //그룹이름
        groupNameText.setText(group.getGroupName());
        //그룹내용
        groupContentText.setText(group.getGroupContent());
        //그룹 현재인원/총인원
        groupAllMemberCount=String.valueOf(group.getGroupMemberNumber());

        groupMemberCount.setText(String.valueOf(group.getGroupCurrentMemberNumber())+"/"+ groupAllMemberCount);
        //현재 가입된 인원 리스트
        groupMemberCount2.setText("("+String.valueOf(group.getGroupCurrentMemberNumber())+"명)");

        ArrayList<User> memberList = group.getGroupUserList();



                //어댑터를 바로 여기에 끼운다.
        groupMemberAdapter = new GroupMemberAdapter(getActivity(), memberList);

        memberRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        memberRecyclerView.setAdapter(groupMemberAdapter);


        //현재 로그인한 유저가 가입이 되어있는지 상태에 따라 가입 버튼을 바꿔준다.
        if(group.getMessage().equals("가입")){
            groupJoinButton.setBackgroundColor(Color.parseColor("#808080"));
            groupJoinButton.setText("모임 탈퇴");
        }else{
            groupJoinButton.setBackgroundColor(Color.parseColor("#E67A00"));
            groupJoinButton.setText("모임 가입");
        }

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

        SharedPreferences loadShared = getActivity().getSharedPreferences(loadSharedName, 0);
        loadValue = loadShared.getString(loadKey, defaultValue);

        //loadValue 안에 리스트의 데이터가 스트링 형태로 담아지기 때문에 스트링으로 되어있는 loadValue를 원래의 리스트 형태로 가지고 온다.
        currentUserArrayList = loadGson.fromJson(loadValue, type);

        if (currentUserArrayList == null) {
            currentUserArrayList = new ArrayList<>();
        }
    }
}