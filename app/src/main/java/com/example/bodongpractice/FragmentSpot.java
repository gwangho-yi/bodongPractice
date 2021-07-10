package com.example.bodongpractice;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bodongpractice.Adapter.ImageSliderAdapter;
import com.example.bodongpractice.Class.GpsTracker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class FragmentSpot extends Fragment {


    ImageView searchButton;
    ImageView searchMapButton;

    ImageView spotAddButton;

    Fragment fragmentAll, fragmentTrick, fragmentDancing, fragmentDownHills, fragmentSlalom, fragmentFreeRiding;

    private ViewPager2 sliderViewPager;
    private LinearLayout layoutIndicator;

    private String[] images = new String[] {
            "http://49.247.213.240/UserProfileImg/bodong035026_5729429747065834492.jpg",
            "http://49.247.213.240/UserProfileImg/bodong035026_5729429747065834492.jpg",
            "http://49.247.213.240/UserProfileImg/bodong035026_5729429747065834492.jpg"
    };




    TextView myLocationTextView;


    @Override
    public void  onStart() {
        super.onStart();
        myLocationTextView.setText(MainActivity.currentAddressName);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot,container,false);




        //현재 내 주소 텍스트가 바뀌게 한다.
        myLocationTextView = view.findViewById(R.id.myLocationTextView);
        myLocationTextView.setText(MainActivity.currentAddressName);

        //주소 검색버튼
        searchButton = view.findViewById(R.id.searchButton);

        //지도 검색버튼
        searchMapButton = view.findViewById(R.id.searchMapButton);

        //슬라이드광고
        sliderViewPager = view.findViewById(R.id.sliderViewPager);
        layoutIndicator = view.findViewById(R.id.layoutIndicators);

        sliderViewPager.setOffscreenPageLimit(1);
        sliderViewPager.setAdapter(new ImageSliderAdapter(getContext(), images));

        sliderViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });

        setupIndicators(images.length);

        int color = Color.parseColor("#FF000000");
        searchButton.setColorFilter(color);
        searchMapButton.setColorFilter(color);
        searchMapButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(getActivity(), MapSearch.class);

                 startActivity(intent);
             }
         });



        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LocationSearch.class);
                startActivity(intent);
            }
        });

        //스팟 등록 버튼
        spotAddButton = view.findViewById(R.id.spotAddButton);
        color = Color.parseColor("#E67A00");
        spotAddButton.setColorFilter(color);

        spotAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddSpot.class);
                startActivity(intent);
            }
        });



        //여기서부터 탭 레이아웃 작성
        fragmentAll = new FragmentSpotAll();
        fragmentTrick = new FragmentSpotTrick();
        fragmentDancing = new FragmentSpotDancing();
        fragmentDownHills = new FragmentSpotDownHill();
        fragmentSlalom = new FragmentSpotSlaom();
        fragmentFreeRiding = new FragmentSpotFreeRiding();

        //이 부분을 적어줌으로써 초기화면을 설정해준다
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame, fragmentAll).commit();

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);



        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();

                Fragment selected = null;
                if(position == 0){

                    selected = fragmentAll;

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


    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getContext(),
                    R.drawable.bg_indicator_inactive));
            indicators[i].setLayoutParams(params);
            layoutIndicator.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }

    private void setCurrentIndicator(int position) {
        int childCount = layoutIndicator.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutIndicator.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getContext(),
                        R.drawable.bg_indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getContext(),
                        R.drawable.bg_indicator_inactive
                ));
            }
        }
    }



}