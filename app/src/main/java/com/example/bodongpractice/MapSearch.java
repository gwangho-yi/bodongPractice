package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.bodongpractice.Adapter.SpotViewPagerAdapter;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Interface.SpotInterface;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MapSearch extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener
         {

    private GoogleApiClient mGoogleApiClient = null;
    private static final String TAG = "";
    private GoogleMap mMap;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;


    //카메라 중심좌표 알아내기
    LatLng center;

    //현위치 검색하기 버튼
    Button searchButton;


    //반경 원을 만들어준다
    CircleOptions circle1KM;

    ViewPager2 spotViewpager;


    //마커 객체 생성
    private Marker markerSpot;

    //마커를 보관하는 리스트이다 보관하는 이유는 뷰페이저가 이동했을 때 마커의 좌표를 가지고 와서 카메라를 이동시켜주기 위해서다.
    //뷰 페이저의 position번호에 따라 이 리스트의 postion이 일치 하기 때문에 마커에서 위경도를 가지고 온 다음에 뷰페이저를 이동하면 화면도 같이 따라 움직인다.
    //뷰페이저 스크롤 -> 스크롤 한 아이템의 position 가지고 온다 -> 피스트에서 포지션 번호에 맞는 마커를 가지고 온다. ->마커의 좌표를 사용해서 카메라를 이동시켜준다.
    ArrayList<Marker> markerList = new ArrayList<Marker>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_search);


        spotViewpager= findViewById(R.id.spotViewpager);
        spotViewpager.setVisibility(INVISIBLE);


        searchButton = findViewById(R.id.searchButton);
        searchButton.setVisibility(INVISIBLE);


        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("지도검색");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우
            //최초 권한 요청인지, 혹은 사용자에 의한 재요청인지 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) &&
                    ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_COARSE_LOCATION)) {
                // 사용자가 임의로 권한을 취소시킨 경우
                // 권한 재요청
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            }
        }
        //권한 설정되면 밑의 클래스로 넘어감




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        String apiKey = getString(R.string.google_maps_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }




        spotViewpager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
//                Log.e("페이지 변경", String.valueOf(position));
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                Log.e("Selected_Page", String.valueOf(position));
                LatLng makerPosition = markerList.get(position).getPosition();
                //마커의 인포윈도를 보여줌
                markerList.get(position).showInfoWindow();
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(makerPosition, 14));
                Log.e("마커의 포지션", String.valueOf(makerPosition));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
//                Log.e("페이지 변경 스테이트", String.valueOf(state));
            }
        });


    }











    LatLng seoul;

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setOnCameraMoveCanceledListener(this);
        mMap.setOnMyLocationButtonClickListener(this);
        mMap.setOnMyLocationClickListener(this);

        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        startLocationService();


        MarkerOptions mOptions = new MarkerOptions();
        CameraPosition position = mMap.getCameraPosition();



    }

    //원추가 하는 메서드
    public void onAddCircle(){
//        LatLng position = new LatLng(mLatitude , mLongitude);
//
//        //나의위치 마커
//        MarkerOptions mymarker = new MarkerOptions()
//                .position(position);   //마커위치

        center = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
        this.mMap.clear();
        // 반경 1KM원
        circle1KM = new CircleOptions().center(center) //중심좌표
                .radius(1000)      //반지름 단위 : m
                .strokeWidth(0f)  //선너비 0f : 선없음
                .fillColor(Color.parseColor("#99FE9A2E")); //배경색

//        //마커추가
//        this.googleMap.addMarker(mymarker);

        //원추가
        this.mMap.addCircle(circle1KM);

        //서버에서 아이템 가지고 오는 메서드
        getSpot(center);
    }


    //가져온 객체로 마커를 찍는 메서드
    void addMarker(Spot spot,int spotNumber){
        LatLng point = new LatLng(spot.getSpotLat(), spot.getSpotLng());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point);
        markerOptions.title(spot.getSpotName());

        //마커의 객체에 담을 수 있따
        markerSpot = mMap.addMarker(markerOptions);
        markerSpot.setTitle(spot.getSpotName());
        //마커에 인트로 position 값을 준다.
        markerSpot.setTag(spotNumber);

        markerList.add(markerSpot);


        //클릭 리스너를 달아준다
        mMap.setOnMarkerClickListener(this);

    }
    @Override
    public boolean onMarkerClick(Marker marker) {

        //이 위치 검색 버튼을 없애준다
        searchButton.setVisibility(View.INVISIBLE);
        marker.showInfoWindow();
        spotViewpager.setVisibility(VISIBLE);
        //마커를 클릭하면 인트 포지션 값을 받아온다
        Integer clickCount = (Integer) marker.getTag();
        //뷰페이저가 인트 값에 따라 슬라이드 될 수 있도록 한다.
        spotViewpager.setCurrentItem(clickCount, true);

        return true;
    }

    public void OnPageChangeCallback (){
        
    }

    LocationManager manager;

    public void startLocationService() {
        try {
            Location location = null;


            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String message = "최근 위치1 -> Latitude : " + latitude + "\n Longitude : " + longitude;


                    showCurrentLocation(latitude, longitude);
                    Log.i("MyLocTest", "최근 위치1 호출");
                }


            } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

                location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    String message = "최근 위치2 -> Latitude : " + latitude + "\n Longitude : " + longitude;


                    Log.i("MyLocTest","최근 위치2 호출");
                }


            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    //처음에 들어오면 내 위치를 중심으로 찾는 메서드
    private void showCurrentLocation(double latitude, double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 14));
        onAddCircle();
    }




    @Override
    public void onMyLocationClick(@NonNull Location location) {

    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }



    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");


                        if (mGoogleApiClient.isConnected() == false) {

                            Log.d(TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }

                break;
        }
    }


    @Override
    public void onCameraIdle() {





    }

    @Override
    public void onCameraMoveCanceled() {

    }

    //카메라 움직일때마다 표시 되는 버튼
    @Override
    public void onCameraMove() {


    }


    //이 위치 검색 버튼을 눌렀을때 서버에서 500미터 안에 있는 스팟의 위치들을 가지고 온다.
    //현재 내가 보고 있는 화면의 중심의 위경도를 서버에 보내서 작업을 한다.
    void getSpot(LatLng point){
        double lat= point.latitude;
        double lng= point.longitude;


        SpotInterface spotInterface =  ApiClient.getApiClient().create(SpotInterface.class);
        Call<List<Spot>> call = spotInterface.mapSelect(lat, lng);

        call.enqueue(new Callback<List<Spot>>()
        {
            //연결 성공 시에 싱행되는 부분
            @Override
            public void onResponse(@NonNull Call<List<Spot>> call, @NonNull Response<List<Spot>> response)
            {

                Log.e("onSuccess", String.valueOf(response.body()));

                markerList= new ArrayList<Marker>();

                System.out.println(response.body().size());
                for (int i = 0; i < response.body().size(); i++){

                    spotViewpager.setAdapter(new SpotViewPagerAdapter((ArrayList<Spot>) response.body(),MapSearch.this));
                    spotViewpager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
                    addMarker(response.body().get(i),i);

                }



//                onGetResult(response.body());

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


    @Override
    public void onCameraMoveStarted(int i) {
        String reasonText = "UNKNOWN_REASON";

        switch (i) {
            //카메라가 움직이기 시작한 이유 : 유저가 카메라를 이동 시켰을때
            case GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE:

                spotViewpager.setVisibility(INVISIBLE);

                searchButton.setVisibility(View.VISIBLE);
                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        onAddCircle();
                        System.out.println("현재 보고 있는 중심 좌표는 : "+center);
                        searchButton.setVisibility(INVISIBLE);
                    }
                });
                reasonText = "GESTURE";
                break;
                //카메라가 우밎ㄱ이기 시작한 이유 : 유저가 마커를 클릭해서 애니메이션 효과로 움직이는 경우
            case GoogleMap.OnCameraMoveStartedListener.REASON_API_ANIMATION:

                reasonText = "API_ANIMATION";
                break;
            case GoogleMap.OnCameraMoveStartedListener.REASON_DEVELOPER_ANIMATION:

                reasonText = "DEVELOPER_ANIMATION";
                break;
        }
        Log.d(TAG, "onCameraMoveStarted(" + reasonText + ")");

    }

    //지도 좌표를 주소로 변환
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
//            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
//            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
//            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
//            System.out.println(address);
            String addressName=address.getAddressLine(0);
            addressName = addressName.replace("대한민국 ","");


            return addressName.toString();
        }

    }



}





