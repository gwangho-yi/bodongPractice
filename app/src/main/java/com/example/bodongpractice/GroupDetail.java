package com.example.bodongpractice;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.bodongpractice.Class.TitleBar;
import com.google.android.material.tabs.TabLayout;

public class GroupDetail extends AppCompatActivity {

    String groupId;
    public static TextView titleName;

    Fragment FragmentGroupInfo, fragmentGroupArticle, fragmentGroupAlbum, fragmentGroupChatting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        //스팟의 아이디를 받아온다
        Intent intent = getIntent();
        groupId=intent.getStringExtra("groupId");


        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);


        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.

        titleName=findViewById(R.id.titleName);



        FragmentGroupInfo = new FragmentGroupInfo();
        //번들로 데이터를 프래그먼트에 전달 가능하다
        Bundle bundle = new Bundle();
        bundle.putString("groupId", groupId);
        FragmentGroupInfo.setArguments(bundle);

        getSupportFragmentManager().beginTransaction().add(R.id.frame, FragmentGroupInfo).commit();

        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();

                Fragment selected = null;
                if(position == 0){

                    selected = FragmentGroupInfo;

                }

                getSupportFragmentManager().beginTransaction().replace(R.id.frame, selected).commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
}