package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.FragmentTransaction;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.bodongpractice.Class.GpsTracker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //스팟화면
    Fragment fragmentSpot;
    //그룹화면
    Fragment fragmentGroup;
    //라이브스트리밍
    Fragment fragmentLive;
    //채팅
    Fragment fragmentChatting;
    //마이페이지
    Fragment fragmentMypage;



    //현재위치 받아주기 위한 gps 트래커
    private GpsTracker gpsTracker;

    //현재위치 받아주기 위한 위도;
    double currentLatitude;
    //현재위치 받아주기 위한 경도;
    double currentLongitude;

    //현재 검색한 위치의 주소 명
    public static String currentAddressName;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentSpot = new FragmentSpot();
        fragmentGroup = new FragmentGroup();
        fragmentLive = new FragmentLive();
        fragmentChatting = new FragmentChatting();
        fragmentMypage = new FragmentMypage();



        //gps트래커를 사용해서 현재 내 위치를 찾아온다.
        gpsTracker = new GpsTracker(MainActivity.this);
        currentLatitude = gpsTracker.getLatitude();
        currentLongitude = gpsTracker.getLongitude();

        LatLng initialPosition = new LatLng(currentLatitude, currentLongitude);
        currentAddressName = getCurrentAddress(initialPosition);

        //초기화면
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentSpot).commit();

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch(item.getItemId()) {
                            case  R.id.tab_spot:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentSpot).commit();
                                return true;

                            case R.id.tab_group:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentGroup).commit();
                                return true;

                            case R.id.tab_live:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentLive).commit();
                                return true;

                            case R.id.tab_chatting:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentChatting).commit();
                                return true;

                            case R.id.tab_mypage:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentMypage).commit();
                                return true;
                        }
                        return false;
                    }
                });



    }

    //지도 좌표를 주소로 변환
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제

            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {

            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
//                Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();

            return "주소 미발견";

        } else {
            System.out.println("전체 주소값"+addresses);
//                [Address[addressLines=[0:"대한민국 서울특별시 동작구 사당동 사당로 244"],feature=２４４,admin=서울특별시,sub-admin=null,locality=null,thoroughfare=사당동,postalCode=156-094,countryCode=KR,countryName=대한민국,hasLatitude=true,latitude=37.4829742,hasLongitude=true,longitude=126.97485250000001,phone=null,url=null,extras=null]]

            //이런식으로 정의가 되어 있어서 인덱스 0번째에 있는걸 가지고 온다.
            //가지고 와서 대한민구 서울 특별시는 제거를 해준다.
            //제거를 해준 스트링 값을 리턴 시킨다.
            Address address = addresses.get(0);

//                System.out.println("전체 주소는 어떻게 받아오나 :"+addresses);
            String str = address.getAddressLine(0);
            //띄워쓰기를 제거 해서 반복문으로 구와 동만 주소로 받아온다.
            String[] array = str.split(" ");
            String addressName;
            StringBuilder sb= new StringBuilder();
            System.out.println("주소 랭스는 : "+ array.length);
            for(int i=2; i<3; i++){

                sb.append(array[i]+ " ");
            }
            addressName=sb.toString();
//                System.out.println("현재의 주소"+addressName);
            return addressName;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("온스타트입니다");


    }
}