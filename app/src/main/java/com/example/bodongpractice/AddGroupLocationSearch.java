package com.example.bodongpractice;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.bodongpractice.Adapter.AddGroupLocationSearchRecyclerViewAdapter;
import com.example.bodongpractice.Adapter.RecyclerViewAdapter;
import com.example.bodongpractice.Class.TitleBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;

public class AddGroupLocationSearch extends AppCompatActivity  implements TextWatcher  {
    EditText locationSearchText;
    RecyclerView recyclerview;


    AddGroupLocationSearchRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_map_search);

        locationSearchText = findViewById(R.id.locationSearchText);
        recyclerview = findViewById(R.id.recyclerview);



        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("모임 지역");


        locationSearchText.addTextChangedListener(this);

        AssetManager assetManager = getAssets();

        ArrayList<String> items = new ArrayList<>();

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
                text = sido + " " + gu+ " "+dong;

                items.add(text);

            }



        } catch (Exception e) {

        }

        adapter = new AddGroupLocationSearchRecyclerViewAdapter(AddGroupLocationSearch.this, items);

        recyclerview.setLayoutManager(new LinearLayoutManager(AddGroupLocationSearch.this, LinearLayoutManager.VERTICAL, false));

        recyclerview.setAdapter(adapter);

        recyclerview.setVisibility(View.INVISIBLE);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {


        //리사이클러뷰를 보여주고
        recyclerview.setVisibility(View.VISIBLE);
        //검색어가 입력되고 있을 때
        adapter.getFilter().filter(s);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }
}