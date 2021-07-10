package com.example.bodongpractice;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bodongpractice.Adapter.RecyclerViewAdapter;
import com.example.bodongpractice.Adapter.SearchHistoryAdapter;
import com.example.bodongpractice.Class.GpsTracker;
import com.example.bodongpractice.Class.TitleBar;
import com.example.bodongpractice.Class.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class LocationSearch extends AppCompatActivity implements TextWatcher {



    //검색 텍스트
    EditText locationSearchText;

    //검색 자동완성 어댑터
    RecyclerViewAdapter adapter;
    RecyclerView recyclerView;



    //검색기록 리사이클러뷰
    RecyclerView searchHistoryRecyclerView;
    SearchHistoryAdapter searchHistoryAdapter;


    ArrayList<String> items = new ArrayList<>();

    LinearLayout historyLayout;


    LinearLayout myLocationButton;

    //최종적으로 이전 액티비티에 전달되는 주소 텍스트
    String locationText;

    //검색 기록 불러오는 리스트
    ArrayList<String> searchHistoryList = new ArrayList<String>();

    //검색어를 전부 삭제시키는 텍스트뷰
    TextView searchHistoryAllDeleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_search);


        //검색 기록을 가지고 온다.
        searchHistoryLoadData();



        //검색어 전체 삭제
        searchHistoryAllDeleteButton = findViewById(R.id.searchHistoryAllDeleteButton);
        searchHistoryAllDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHistoryList = new ArrayList<String>();
                searchHistorySaveData(searchHistoryList);
                searchHistoryAdapter.mList=new ArrayList<String>();
                searchHistoryAdapter.notifyDataSetChanged();
            }
        });



//타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("위치 검색");


        //검색 기록 리사이클러뷰
        searchHistoryRecyclerView = findViewById(R.id.searchHistoryRecyclerView);
        if(searchHistoryList!=null){
            System.out.println("야 검색기록 값 널 아니다!");
            searchHistoryAdapter = new SearchHistoryAdapter(LocationSearch.this, searchHistoryList);
            searchHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(LocationSearch.this, LinearLayoutManager.VERTICAL, false));

            searchHistoryRecyclerView.setAdapter(searchHistoryAdapter);
        }else{
            System.out.println("야 검색기록 값 널이다!");
        }



        myLocationButton=findViewById(R.id.myLocationButton);
        myLocationButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {

                 GpsTracker gpsTracker = new GpsTracker(LocationSearch.this);
                 double currentLatitude = gpsTracker.getLatitude();
                 double currentLongitude = gpsTracker.getLongitude();
                 locationText = getCurrentAddress(new LatLng(currentLatitude, currentLongitude));

                 OnClickHandler(myLocationButton);
             }
         });



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
                text = sido + " " + gu;

                items.add(text);

            }

            //해시셋을 이용하면 중복을 제거 할 수 있다.
            //중복 되는 값을 없앤다.
            HashSet<String> array2 = new HashSet<String>(items);
            items = new ArrayList<String>(array2);

        } catch (Exception e) {

        }

        System.out.println(items);
        locationSearchText=findViewById(R.id.locationSearchText);



        locationSearchText.addTextChangedListener(this);


        historyLayout= findViewById(R.id.historyLayout);





        recyclerView = findViewById(R.id.recyclerview);


        //처음에는 리사이클러뷰를 지워준다.
        recyclerView.setVisibility(View.INVISIBLE);

        adapter = new RecyclerViewAdapter(LocationSearch.this, items);

        recyclerView.setLayoutManager(new LinearLayoutManager(LocationSearch.this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);
    }

    public void OnClickHandler(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String str = locationText;

        //띄워쓰기를 제거 해서 반복문으로 구와 동만 주소로 받아온다.
        String[] array = str.split(" ");
        String addressName;
        StringBuilder sb= new StringBuilder();
        for(int i=1; i<array.length; i++){

            sb.append(array[i]+ " ");
        }
        addressName=sb.toString();
        //서울특별시 구 동 까지 받아와서 텍스트를 세팅해준다
        builder.setMessage("'"+str+"'"+"(으)로" +
                "\n설정되었습니다.");
        MainActivity.currentAddressName=addressName;

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Intent intent = new Intent();
                intent.putExtra("주소검색결과", locationText);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }




    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
         String searchText = locationSearchText.getText().toString();

        System.out.println("들어가는 텍스트 "+s);



        int s_lenth = s.length();
        if(s_lenth==0){

            //리사이클러뷰는 숨기기고
            recyclerView.setVisibility(View.GONE);
            //검색기록을 보여준다.
            historyLayout.setVisibility(View.VISIBLE);

        }

        else{

            //검색기록은 사겢시켜주고
            historyLayout.setVisibility(View.GONE);

            //리사이클러뷰를 보여주고
            recyclerView.setVisibility(View.VISIBLE);

            //검색어가 입력되고 있을 때
            adapter.getFilter().filter(s);

        }

    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    //지도 좌표를 주소로 변환
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(LocationSearch.this, Locale.getDefault());

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
            for(int i=1; i<3; i++){

                sb.append(array[i]+ " ");
            }
            addressName=sb.toString();
//                System.out.println("현재의 주소"+addressName);
            return addressName;
        }

    }


    //검색기록 불러오기
    void searchHistoryLoadData() {

        //먼저 지슨을 로드해주고
        Gson loadGson = new Gson();
        //리스트의 경우 타입을 지정해줘야한다.
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();


        // 문자열 불러오기
        String loadSharedName = "shared"; // 가져올 SharedPreferences 이름 지정
        String loadKey = "검색기록"; // 가져올 데이터의 Key값 지정
        //여기에 json어레이가 담기도록 해보자
        String loadValue = ""; // 가져올 데이터가 담기는 변수
        String defaultValue = ""; // 가져오는것에 실패 했을 경우 기본 지정 텍스트. 보통 기본 값은 널 값으로 준다.

        SharedPreferences loadShared = getSharedPreferences(loadSharedName, 0);
        loadValue = loadShared.getString(loadKey, defaultValue);

        //loadValue 안에 리스트의 데이터가 스트링 형태로 담아지기 때문에 스트링으로 되어있는 loadValue를 원래의 리스트 형태로 가지고 온다.
        searchHistoryList = loadGson.fromJson(loadValue, type);

        if (searchHistoryList == null) {
            searchHistoryList = new ArrayList<>();
        }
    }

    //자동 저장 체크 시에 쉐어드에 저장한다.
    void searchHistorySaveData(ArrayList<String> arrayList){
//        String userString =  jsonObject.toString();
        // 문자열 저장하기

        Gson saveGson = new Gson();

        String saveSharedName = "shared"; // 저장할 SharedPreferences 이름 지정.
        String saveKey = "검색기록"; // 저장할 데이터의 Key값 지정.
        String saveValue = saveGson.toJson(arrayList); //저장할 데이터의 Content 지정.

        SharedPreferences saveShared = getSharedPreferences(saveSharedName,0);
        SharedPreferences.Editor sharedEditor = saveShared.edit();

        sharedEditor.putString(saveKey,saveValue);
        System.out.println(saveValue);

        sharedEditor.commit();
    }
}