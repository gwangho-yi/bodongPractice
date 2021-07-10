package com.example.bodongpractice.Adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bodongpractice.AddSpotMap;
import com.example.bodongpractice.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class MapSearchAutoCompleteAdapter extends RecyclerView.Adapter<MapSearchAutoCompleteAdapter.MyViewHolder> implements Filterable {

    private Geocoder geocoder;

    Context context;
    //리스트의 전체 데이터가 들어갈 리스트
    ArrayList<String> unFilteredlist;
    //필터가 적용되어 화면에 나타날 리스트
    ArrayList<String> filteredList;

    FrameLayout searchFrameLayout;
    GoogleMap mMap;

    public MapSearchAutoCompleteAdapter(Context context, ArrayList<String> list, ArrayList<String> emptyList , FrameLayout Layout , GoogleMap map) {
        super();
        this.context = context;
        this.unFilteredlist = list;
        this.filteredList = emptyList;
        this.searchFrameLayout = Layout;
        this.mMap = map;
        geocoder = new Geocoder(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recyclerview_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.textView.setText(filteredList.get(position));



    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {


        TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);

            textView = (TextView)itemView.findViewById(R.id.textview);

            //아이템의 뷰를 클릭하면 텍스트가 로그에 출력 되도록한다.
            itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     System.out.println(textView.getText().toString());
                     searchFrameLayout.setVisibility(GONE);
                     getLatLng(textView.getText().toString());
                     AddSpotMap.searchFrameLayoutIsVisible = false;

                 }
             });

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
//            CharSequence constraint은 입력 된 스트링 값이다.
            //메인에 onTextChanged 에서 입력값이 바귈 때마다  adapter.getFilter().filter(charSequence); 가 실행 되는데
            //
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();

                //전체 리스트에서 내가 입력한 값이 들어있는지 비교해주는 코드
                ArrayList<String> filteringList = new ArrayList<>();
                for(String name : unFilteredlist) {
                    if(name.toLowerCase().contains(charString.toLowerCase())) {
                        //내가 입력한 값이 들어가 있다면 필터링 리스트에 값을 추가한다.
                        filteringList.add(name);
                    }
                }
                //필터링 된 리스트의 값을 필터가 완료된 리스트에 넣어준다
                filteredList = filteringList;

                //완료된 리스트의 값을 filterResults.values에 넣어준다
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                //위에서 입력된 filterResults.values의 값이 여기서 최종적으로 필터된 리스트에 받아지고
                //리사이클러뷰를 갱신해서 filteredList에 담긴 값을 세팅한다.
                filteredList = (ArrayList<String>)results.values;
                notifyDataSetChanged();
            }
        };
    }


    private void getLatLng(String location) {

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point,18));

//                getJsonData(seoul, point);
    }

}
