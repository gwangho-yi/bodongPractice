package com.example.bodongpractice;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;


public class FragmentGroup extends Fragment {

    ImageView searchButton;


    ImageView groupAddButton;

    Fragment FragmentGroupAll, fragmentTrick, fragmentDancing, fragmentDownHills, fragmentSlalom, fragmentFreeRiding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_group, container, false);





        //그룹 추가 버튼
       groupAddButton = view.findViewById(R.id.groupAddButton);

        groupAddButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(), AddGroupCategory.class);
               startActivity(intent);
           }
       });
        //여기서부터 탭 레이아웃 작성
        FragmentGroupAll = new FragmentGroupAll();
        fragmentTrick = new FragmentGroupTrick();
        fragmentDancing = new FragmentGroupDancing();
        fragmentDownHills =  new FragmentGroupDownHill();
        fragmentSlalom =  new FragmentGroupSlalom();
        fragmentFreeRiding =  new FragmentGroupFreeRiding();

        //이 부분을 적어줌으로써 초기화면을 설정해준다
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame, FragmentGroupAll).commit();


        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);


        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();

                Fragment selected = null;
                if(position == 0){

                    selected = FragmentGroupAll;

                }else if (position == 1){

                    selected = fragmentTrick;

                }else if (position == 2){

                    selected = fragmentDancing;

                }else if (position == 3){

                    selected = fragmentDownHills;
                }else if (position == 4){

                    selected = fragmentSlalom;
                }else if (position == 5){

                    selected = fragmentFreeRiding;
                }

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame, selected).commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }
}