package com.example.bodongpractice.Adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

import static android.app.Activity.RESULT_OK;

public class AddGroupLocationSearchRecyclerViewAdapter extends RecyclerView.Adapter<AddGroupLocationSearchRecyclerViewAdapter.MyViewHolder> implements Filterable {


    private Geocoder geocoder;
    Context context;
    ArrayList<String> unFilteredlist;
    ArrayList<String> filteredList;


    String sido;
    String dong;

    public AddGroupLocationSearchRecyclerViewAdapter(Context context, ArrayList<String> list) {
        super();
        this.context = context;
        this.unFilteredlist = list;
        this.filteredList = list;
        geocoder = new Geocoder(context);


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_add_group_location_search, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        String[] locationWholeText = filteredList.get(position).split(" ");



        //자동완성 할때 json 파일에 서울특별시 동작구 만 있는데 칼럼이 있다 그래서 배열의 크키가 2개가 되는 경우가 있어서 예외처리를 해줬다.
        if(locationWholeText.length>2){

            dong =locationWholeText[2];

            sido = locationWholeText[0]+" "+locationWholeText[1];
            holder.dongText.setText(dong);
            holder.sidoText.setText(sido);

        }


        holder.dongText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //선택한 주소의 주소를 좌표로 구한다
                System.out.println(holder.dongText.getText().toString());
//                LatLng point = getLatLng(holder.dongText.getText().toString());
//                System.out.println("순턴학 주소의 조촤 : "+point);
                //좌표로 주소를 구한다
//                String locationame=getCurrentAddress(point);
//                System.out.println("나온 주소는 : " + locationame);

                Intent intent = new Intent();

                intent.putExtra("주소명", sido);
                ((Activity) context).setResult(RESULT_OK, intent);
                ((Activity) context).finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dongText;
        TextView sidoText;

        public MyViewHolder(View itemView) {
            super(itemView);
            dongText = (TextView)itemView.findViewById(R.id.dongText);
            sidoText = (TextView)itemView.findViewById(R.id.sidoText);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    filteredList = unFilteredlist;
                } else {
                    ArrayList<String> filteringList = new ArrayList<>();
                    for(String name : unFilteredlist) {
                        if(name.toLowerCase().contains(charString.toLowerCase())) {
                            filteringList.add(name);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<String>)results.values;
                notifyDataSetChanged();
            }
        };
    }


    private LatLng getLatLng(String location) {

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
