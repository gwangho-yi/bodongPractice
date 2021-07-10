package com.example.bodongpractice;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bodongpractice.Adapter.SpotMainAdapter;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Interface.SpotInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.bodongpractice.MainActivity.currentAddressName;


public class FragmentSpotTrick extends Fragment {
    ArrayList<Spot> spotList = new ArrayList<Spot>();

    RecyclerView spotRecyclerView;

    SpotMainAdapter spotAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_spot_trick, container, false);

        //리사이클러뷰 꽂아준다.
        spotRecyclerView = view.findViewById(R.id.spotRecyclerView);



        return view;
    }

    private void onGetResult(List<Spot> lists)
    {
        spotList = (ArrayList<Spot>) lists;


        spotAdapter = new SpotMainAdapter(getContext(), spotList);
        System.out.println("리스트의 사이즈는 " +spotList.size());
        spotRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        spotRecyclerView.setAdapter(spotAdapter);

    }


    @Override
    public void onStart() {
        super.onStart();

        //스팟의 정보를 가지고온다
        SpotInterface spotInterface =  ApiClient.getApiClient().create(SpotInterface.class);
        //현재 내 위치와 어떤 탭 레이아웃에 있는지에 따라 다른것을 불러온다.
        System.out.println("스태틱 주소 : "+currentAddressName);
        Call<List<Spot>> call = spotInterface.selectSpot(currentAddressName, "트릭");

        call.enqueue(new Callback<List<Spot>>()
        {
            //연결 성공 시에 싱행되는 부분
            @Override
            public void onResponse(@NonNull Call<List<Spot>> call, @NonNull Response<List<Spot>> response)
            {

                Log.e("onSuccess", String.valueOf(response.body()));

                onGetResult(response.body());

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
}