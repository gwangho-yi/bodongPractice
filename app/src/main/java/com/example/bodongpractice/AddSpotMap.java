package com.example.bodongpractice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bodongpractice.Adapter.MapSearchAutoCompleteAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.view.View.VISIBLE;

public class AddSpotMap extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener,
        OnMapReadyCallback, TextWatcher, GoogleMap.OnCameraMoveStartedListener,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraMoveCanceledListener,
        GoogleMap.OnCameraIdleListener {

    private GoogleApiClient mGoogleApiClient = null;
    private static final String TAG = "";
    private GoogleMap mMap;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;

    //화면이 중앙에서 멈췄을 때 나오는 주소 텍스트와 등록 버튼
    TextView locationText;
    Button locationAddButton;

    //주소명
    String centerLocationName;
    LatLng center;

    RecyclerView recyclerView;
    EditText editText;
    MapSearchAutoCompleteAdapter adapter;

    ArrayList<String> items = new ArrayList<>();
    ArrayList<String> emptyItems = new ArrayList<>();


    FrameLayout searchFrameLayout;

    FrameLayout frameLayout;
    //화면ㅇ중앙에 위치한 커스텀 마커
    ImageView markerView;

    ImageView backButton;

    //뒤로가기 버튼 : 자동검색 나타나있을때 누르면 보여주고 아닐때 누르면 액티비티가 종료된다.
    public static boolean searchFrameLayoutIsVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spot_map);




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

        locationText = findViewById(R.id.locationText);
        locationAddButton = findViewById(R.id.locationAddButton);


        markerView = findViewById(R.id.markerView);
        int color = Color.parseColor("#E67A00");

        markerView.setColorFilter(color);

        //뒤로가기버튼인데 리사이클러뷰를 보여주는 뷰가 떠 있다면 뷰를 없애주고 떠 있지 않다면 액티비티를 종료 시켜라
        backButton = findViewById(R.id.mapBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchFrameLayoutIsVisible) {
                    searchFrameLayout.setVisibility(View.GONE);
                } else {
                    finish();
                }
            }
        });

        //등록 버튼을 누르면 주소의 위도 경도 위치명을 돌려준다.
        locationAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
//                 System.out.println("최종 위경도는 : "+center);
//                 System.out.println("최종 주소명은 : "+centerLocationName);

                intent.putExtra("위경도", center);
                intent.putExtra("주소명", centerLocationName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        String apiKey = getString(R.string.google_maps_key);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

        //검색 화면을 일단 숨긴다
        searchFrameLayout = findViewById(R.id.searchFrameLayout);

        searchFrameLayout.setVisibility(View.GONE);


        recyclerView = (RecyclerView) findViewById(R.id.recylcerview);
        editText = (EditText) findViewById(R.id.edittext);
        //여기에 텍스트 체인지 리스너를 장착 시키면 입력할때마다 oncreate 밖에 있는 onTextChanged를 작동 시킬 수 있다.
        editText.addTextChangedListener(this);


        AssetManager assetManager = getAssets();

        try {
            //InputStream으로 파일을 읽어오는 통로를 만든다
            InputStream is = assetManager.open("location.json");
            //InputStreamReader로 파일을 한글이 정상 출력되로록한다.
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(isr);

            StringBuilder buffer = new StringBuilder();
            //파일을 한 줄씩 읽어온다.
            String line = reader.readLine();

            while (line != null) {
                //한줄씩 읽어온 데이터를 buffer에 넣어준다.
                buffer.append(line + "\n");
                line = reader.readLine();
            }

            String jsonData = buffer.toString();
            //읽어온 제이슨 데이터 확인
//           textView.setText(jsonData);

            JSONArray jsonArray = new JSONArray(jsonData);
            String text = "";

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String sido = jsonObject.getString("sido");
                String gu = jsonObject.getString("gu");
                String dong = jsonObject.getString("dong");
                text = sido + " " + gu + " " + dong;

                items.add(text);
            }


        } catch (Exception e) {

        }


        emptyItems.add("");


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
//        geocoder = new Geocoder(this);






//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        seoul = new LatLng(37.584009, 126.970626);
////        mMap.addMarker(new MarkerOptions().position(seoul).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(seoul));

        MarkerOptions mOptions = new MarkerOptions();
        CameraPosition position = mMap.getCameraPosition();


//        mMap.setMyLocationEnabled(true);
//        mMap.setOnMyLocationButtonClickListener(this);
//        mMap.setOnMyLocationClickListener(this);
        //빈문자열만 들어가 있으면 오류가 떠서 공백을 하나 넣어준다
        emptyItems.add("");


        adapter = new MapSearchAutoCompleteAdapter(getApplicationContext(), items, emptyItems, searchFrameLayout, mMap);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);






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


    private void showCurrentLocation(double latitude, double longitude) {
        LatLng curPoint = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 18));

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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    //텍스트가 바뀔 때마다 텍스트가 감지를 하여 어탭터에서 필터를 적용하여 텍스트와 일치하는 값을 가지고 와서 리사이클러뷰에 표시해준다.
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        adapter.getFilter().filter(charSequence);

        searchFrameLayout.setVisibility(VISIBLE);
        searchFrameLayoutIsVisible=true;

//        mMap.setVisibility(View.GONE);
    }

    @Override
    public void afterTextChanged(Editable editable) {

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
        //카메라 화면의 중심 좌표를 찾는다.
        center = mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();

        centerLocationName = getCurrentAddress(center);
        System.out.println("화면 중앙의 주소는 : "+centerLocationName);

        if(centerLocationName.contains("주소 미발견")){

        }else{
            locationAddButton.setVisibility(View.VISIBLE);

            locationText.setText(centerLocationName);
            locationText.setVisibility(View.VISIBLE);

        }




    }



    @Override
    public void onCameraMoveStarted(int i) {

        locationAddButton.setVisibility(View.GONE);
        locationText.setVisibility(View.GONE);
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

    @Override
    public void onCameraMoveCanceled() {

    }

    @Override
    public void onCameraMove() {

    }

}





