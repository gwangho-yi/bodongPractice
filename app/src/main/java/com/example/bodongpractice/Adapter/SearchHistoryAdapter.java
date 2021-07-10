package com.example.bodongpractice.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bodongpractice.Class.Review;
import com.example.bodongpractice.MainActivity;
import com.example.bodongpractice.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.MyViewHolder>{

    public ArrayList<String> mList;
    Context context;



    ArrayList<String> searchHistoryList = new ArrayList<String>();


    public SearchHistoryAdapter(Context context, List<String> list){
        super();
        this.context = context;
        this.mList = (ArrayList<String>) list;

        searchHistoryLoadData();



    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView searchHistoryText;
        TextView searchHistoryDeleteButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            searchHistoryText = (TextView)itemView.findViewById(R.id.searchHistoryText);
            searchHistoryDeleteButton = (TextView)itemView.findViewById(R.id.searchHistoryDeleteButton);
        }
    }

    @NonNull
    @Override
    public SearchHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_history, parent, false);
        return new SearchHistoryAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SearchHistoryAdapter.MyViewHolder holder, int position) {
        holder.searchHistoryText.setText(mList.get(position));

        //검색어를 누르면 주소가 바뀌도록한다.

        holder.searchHistoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //선택한 주소의 주소를 좌표로 구한다
                System.out.println(holder.searchHistoryText.getText().toString());
                LatLng point = getLatLng(holder.searchHistoryText.getText().toString());
                System.out.println("순턴학 주소의 조촤 : "+point);
                //좌표로 주소를 구한다
                String locationame=getCurrentAddress(point);
                System.out.println("나온 주소는 : " + locationame);
                MainActivity.currentAddressName = locationame;


                System.out.println("바뀐 메인의 주소는 : "+ MainActivity.currentAddressName);
                ((Activity) context).finish();
            }
        });



        //검색어 삭제 시키기
        holder.searchHistoryDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHistoryList.remove(mList.get(position));
                System.out.println("삭제된 검색어는 ? :" + searchHistoryList);
                searchHistorySaveData(searchHistoryList);
                mList.remove(mList.get(position));
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //자동 저장 체크 시에 쉐어드에 저장한다.
    void searchHistorySaveData(ArrayList<String> arrayList){
//        String userString =  jsonObject.toString();
        // 문자열 저장하기

        Gson saveGson = new Gson();

        String saveSharedName = "shared"; // 저장할 SharedPreferences 이름 지정.
        String saveKey = "검색기록"; // 저장할 데이터의 Key값 지정.
        String saveValue = saveGson.toJson(arrayList); //저장할 데이터의 Content 지정.

        SharedPreferences saveShared = context.getSharedPreferences(saveSharedName,0);
        SharedPreferences.Editor sharedEditor = saveShared.edit();

        sharedEditor.putString(saveKey,saveValue);
        System.out.println(saveValue);

        sharedEditor.commit();
    }


    //데이터 불러오기
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

        SharedPreferences loadShared = context.getSharedPreferences(loadSharedName, 0);
        loadValue = loadShared.getString(loadKey, defaultValue);

        //loadValue 안에 리스트의 데이터가 스트링 형태로 담아지기 때문에 스트링으로 되어있는 loadValue를 원래의 리스트 형태로 가지고 온다.
        searchHistoryList = loadGson.fromJson(loadValue, type);

        System.out.println("검색 기록에 담긴 내용은 : "+searchHistoryList);
        if (searchHistoryList == null) {
            searchHistoryList = new ArrayList<>();
        }
    }

    private LatLng getLatLng(String location) {
//지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressList = null;
        try {
            // editText에 입력한 텍스트(주소, 지역, 장소 등)을 지오 코딩을 이용해 변환
            addressList = geocoder.getFromLocationName(
                    location, // 주소
                    10); // 최대 검색 결과 개수
            System.out.println("주소" + addressList);
        } catch (
                IOException e) {
            e.printStackTrace();
        }

//        System.out.println(addressList.get(0).toString());
        // 콤마를 기준으로 split
        String[] splitStr = addressList.get(0).toString().split(",");
        String address = splitStr[0].substring(splitStr[0].indexOf("\"") + 1, splitStr[0].length() - 2); // 주소
//        System.out.println(address);

        String latitude = splitStr[10].substring(splitStr[10].indexOf("=") + 1); // 위도
        String longitude = splitStr[12].substring(splitStr[12].indexOf("=") + 1); // 경도
        System.out.println(latitude);
        System.out.println(longitude);

        // 좌표(위도, 경도) 생성
        LatLng point = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));


        return point;

    }

    //지도 좌표를 주소로 변환
    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

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

}
