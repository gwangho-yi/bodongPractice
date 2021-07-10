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

import com.example.bodongpractice.Adapter.GroupMainAdapter;
import com.example.bodongpractice.Adapter.SpotMainAdapter;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Group;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Interface.GroupInterface;
import com.example.bodongpractice.Interface.SpotInterface;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.bodongpractice.MainActivity.currentAddressName;


public class FragmentGroupTrick extends Fragment {



    RecyclerView groupAllRecyclerView;
    GroupMainAdapter groupAdapter;


    ArrayList<Group> groupArrayList = new ArrayList();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_group_all, container, false);


        groupAllRecyclerView= view.findViewById(R.id.groupAllRecyclerView);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();

//스팟의 정보를 가지고온다
        GroupInterface spotInterface =  ApiClient.getApiClient().create(GroupInterface.class);
        Call<List<Group>> call = spotInterface.SelectGroup("동작구", "트릭");

        call.enqueue(new Callback<List<Group>>()
        {
            //연결 성공 시에 싱행되는 부분
            @Override
            public void onResponse(@NonNull Call<List<Group>> call, @NonNull Response<List<Group>> response)
            {

                Log.e("onSuccess", String.valueOf(response.body()));

                System.out.println(response.body().size());
                onGetResult(response.body());



            }



            @Override
            public void onFailure(@NonNull Call<List<Group>> call, @NonNull Throwable t)
            {
                Log.e("onfail", "에러 = " + t.getMessage());
            }
        });


    }

    //결과를 받아와서 리사이클러뷰 세팅해주는 메서드
    private void onGetResult(List<Group> lists)
    {
        groupArrayList = (ArrayList<Group>) lists;

        groupAdapter= new GroupMainAdapter(getActivity(), groupArrayList);


        System.out.println("리스트의 사이즈는 " +groupArrayList.size());
        groupAllRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        groupAllRecyclerView.setAdapter(groupAdapter);

    }


}