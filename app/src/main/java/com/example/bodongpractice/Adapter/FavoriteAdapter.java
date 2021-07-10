package com.example.bodongpractice.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Group;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.GroupDetail;
import com.example.bodongpractice.Interface.SpotInterface;
import com.example.bodongpractice.R;
import com.example.bodongpractice.SpotDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.SpotMainViewHolder> {

    Context context;
    ArrayList<Spot> mList;



    ArrayList<User> currentUserArrayList = new ArrayList<User>();
    String currentUserEmail;


    public FavoriteAdapter(Context context, ArrayList<Spot> list){
        super();

        this.context = context;
        this.mList = list;

    }


    public class SpotMainViewHolder extends RecyclerView.ViewHolder{

        ImageView favoriteImg;
        TextView spotName;
        TextView spotLocation;
        TextView reviewCount;
        TextView favoriteCount;
        ImageView favoriteButton;



        public SpotMainViewHolder(@NonNull View itemView) {
            super(itemView);



            favoriteImg = itemView.findViewById(R.id.favoriteImg);
            spotName = itemView.findViewById(R.id.spotName);
            spotLocation = itemView.findViewById(R.id.spotLocation);
            reviewCount = itemView.findViewById(R.id.reviewCount);
            favoriteCount = itemView.findViewById(R.id.favoriteCount);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);





//            //아이템을 클릭하면 상세페이지로 이동하도록
            itemView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     int position = getAdapterPosition();
                     Intent intent = new Intent(context, SpotDetail.class);
                     //스팟의 아이디를 넘겨줘서 상세페이지에 들어가면 아이디를 이용해서 아이템을 가져온다.
                     String spotId = mList.get(position).getSpotId();
                     intent.putExtra("spotId", spotId);

                     context.startActivity(intent);
                 }
            });

        }
    }

    @NonNull
    @Override
    public SpotMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_favorite, parent, false);
        return new FavoriteAdapter.SpotMainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpotMainViewHolder holder, int position) {
        //스팟의 이름
        holder.spotName.setText(mList.get(position).getSpotName());
        //이미지는 스팟리스트 안에 있는 이미지 리스트의 0번째에 있는 걸로 가져다 쓴다.

        holder.favoriteButton.setColorFilter(Color.parseColor("#E67A00"));

        String imgPath  = "http://49.247.213.240/SpotImg/"+mList.get(position).getSpotTitleImg();

        Glide.with(holder.favoriteImg)
                .load(imgPath)
                .into(holder.favoriteImg);

        //스팟의 위치명
        String[] locationArray = mList.get(position).getSpotLocation().split(" ");

        String s="";
        ArrayList<String> location = new ArrayList<String>();
        for (int i = 1; i < locationArray.length; i++){
            location.add(locationArray[i]);
            s=s+locationArray[i]+" ";
        }


        holder.spotLocation.setText(String.valueOf(s));

        //리뷰 카운트
        holder.reviewCount.setText(String.valueOf(mList.get(position).getSpotReviewCount()));
        //즐찾 카운트
        holder.favoriteCount.setText(String.valueOf(mList.get(position).getSpotFavoriteCount()));

        String spotId = mList.get(position).getSpotId();

        //즐찾 버튼
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUserLoadData();
                currentUserEmail =  currentUserArrayList.get(0).getEmail();

                SpotInterface spotInterface =  ApiClient.getApiClient().create(SpotInterface.class);
                Call<Spot> call = spotInterface.favoriteSpot(spotId, currentUserEmail);

                call.enqueue(new Callback<Spot>()
                {
                    //연결 성공 시에 싱행되는 부분
                    @Override
                    public void onResponse(@NonNull Call<Spot> call, @NonNull Response<Spot> response)
                    {

                        Log.e("onSuccess", response.body().getMessage());
//                         Log.e("onSuccess", response.body().getMessage());

                        int favoriteCount = response.body().getSpotFavoriteCount();

                        holder.favoriteCount.setText(Integer.toString(favoriteCount));

                        if(response.body().getMessage().contains("추천함")){
                            holder.favoriteButton.setImageResource(R.drawable.bookmark);
                            holder.favoriteButton.setColorFilter(Color.parseColor("#E67A00"));
                        }else{
                            holder.favoriteButton.setImageResource(R.drawable.bookmark_white);
//                             favoriteButton.setColorFilter(null);
                        }







                    }



                    @Override
                    public void onFailure(@NonNull Call<Spot> call, @NonNull Throwable t)
                    {
                        Log.e("onfail", "에러 = " + t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //데이터 불러오기
    void currentUserLoadData() {


        //먼저 지슨을 로드해주고
        Gson loadGson = new Gson();
        //리스트의 경우 타입을 지정해줘야한다.
        Type type = new TypeToken<ArrayList<User>>() {
        }.getType();


        // 문자열 불러오기
        String loadSharedName = "shared"; // 가져올 SharedPreferences 이름 지정
        String loadKey = "유저정보"; // 가져올 데이터의 Key값 지정
        //여기에 json어레이가 담기도록 해보자
        String loadValue = ""; // 가져올 데이터가 담기는 변수
        String defaultValue = ""; // 가져오는것에 실패 했을 경우 기본 지정 텍스트. 보통 기본 값은 널 값으로 준다.

        SharedPreferences loadShared = context.getSharedPreferences(loadSharedName, 0);
        loadValue = loadShared.getString(loadKey, defaultValue);

        //loadValue 안에 리스트의 데이터가 스트링 형태로 담아지기 때문에 스트링으로 되어있는 loadValue를 원래의 리스트 형태로 가지고 온다.
        currentUserArrayList = loadGson.fromJson(loadValue, type);

        if (currentUserArrayList == null) {
            currentUserArrayList = new ArrayList<>();
        }
    }
}

