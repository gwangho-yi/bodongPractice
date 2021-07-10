package com.example.bodongpractice.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Class.ApiClient;
import com.example.bodongpractice.Class.Review;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.Class.User;
import com.example.bodongpractice.EditReview;
import com.example.bodongpractice.EditSpot;
import com.example.bodongpractice.Interface.ReviewInterface;
import com.example.bodongpractice.Interface.SpotInterface;
import com.example.bodongpractice.R;
import com.example.bodongpractice.SpotDetail;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    Context context;

    ArrayList<Review> mList;
    ArrayList<String> reviewImg;
    String reviewId;
    String currentUserEmail;


    //좋아요버튼 유저이메일 넘겨주기, 수정 삭제버튼 막기용
    ArrayList<User> currentUserArrayList = new ArrayList<User>();

    public ReviewAdapter(Context context, List<Review> list){
        super();

        this.context = context;
        this.mList = (ArrayList<Review>) list;

        currentUserLoadData();
        currentUserEmail = currentUserArrayList.get(0).getEmail();

    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        ImageView userProfile;
        TextView userNickNameText;
        TextView createDateText;
        RatingBar ratingBar;
        TextView reviewContentText;
        LinearLayout layout;
        Button thumbButton;
        TextView optionText;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userProfile = itemView.findViewById(R.id.userProfile);
            userNickNameText = itemView.findViewById(R.id.userNickNameText);
            createDateText = itemView.findViewById(R.id.createDateText);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            reviewContentText = itemView.findViewById(R.id.reviewContentText);
            layout = itemView.findViewById(R.id.layout);
            thumbButton = itemView.findViewById(R.id.thumbButton);
            optionText=itemView.findViewById(R.id.optionText);
        }
    }


    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {

        //현재로그인한 유저와 리뷰아이템을 작성한 유저가 같으면 수정삭제버튼이 생기도록 한다.
        if(currentUserEmail.contains(mList.get(position).getUserEmail())){
            holder.optionText.setVisibility(View.VISIBLE);
        }
        else{
            holder.optionText.setVisibility(View.GONE);
        }

        //유저가 이미 추천을 눌렀다면 already_user_count 가 1이 된다. 1일때는 이미지를 바꿔주도록 한다.
        if(mList.get(position).getAlreadyUserCount().contains("1")){
            System.out.println("이미 눌렀다네 하하하하하하하하하하하");
            holder.thumbButton.setBackgroundColor(Color.parseColor("#E67A00"));
            holder.thumbButton.setCompoundDrawablesWithIntrinsicBounds( R.drawable.like_white, 0, 0, 0);
            holder.thumbButton.setTextColor(Color.parseColor("#ffffff"));
        }


        //유저의 사진
        String UserProfilePath  = "http://49.247.213.240/UserProfileImg/"+mList.get(position).getUserProfile();
        Glide.with(holder.userProfile)
                .load(UserProfilePath)
                .circleCrop()
                .into(holder.userProfile);
        //유저의 닉네임
        holder.userNickNameText.setText(mList.get(position).getUserNickName());

        if(mList.get(position).getReviewEditDate()!=null){
            System.out.println("비었음");
            //만든날짜
            holder.createDateText.setText(mList.get(position).getReviewEditDate());
        }else{
            //만든날짜
            holder.createDateText.setText(mList.get(position).getReviewCreateDate());
        }

        //리뷰 내용
        holder.reviewContentText.setText(mList.get(position).getReviewContent());

        //리뷰 추천수 숫자 카운트
        holder.thumbButton.setText(mList.get(position).getThumbCount());

        holder.ratingBar.setRating(Float.valueOf(mList.get(position).getReviewRating()));
        //리뷰이미지 세팅
        ArrayList<String> reviewImgArray = new ArrayList<String>();
        reviewImgArray = mList.get(position).getReviewImg();
        System.out.println("이미지 갯수 : "+ reviewImgArray);
        for (int i = 0; i < reviewImgArray.size(); i++){
            String reviewImgPath  = "http://49.247.213.240/ReviewImg/"+reviewImgArray.get(i);
            ImageView imageView = new ImageView(context);
            //글라이드로 이미지 크기 조정
            Glide.with(imageView)
                    .load(reviewImgPath)
                    .override(MATCH_PARENT,MATCH_PARENT)
                    .into(imageView);
            //이미지뷰의 크기 조절
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 300);

            params.leftMargin=20;
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            holder.layout.addView(imageView);
        }




        //추천 버튼
        holder.thumbButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 reviewId = mList.get(position).getReviewId();
                 reviewThumb(currentUserEmail, reviewId,holder.thumbButton);
             }
         });



                //수정 삭제 메뉴

        holder.optionText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //팝업메뉴 생성
                PopupMenu popup = new PopupMenu(context, holder.optionText);
                popup.inflate(R.menu.popup);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //데이터 수정
                            case R.id.dataChange:
                                reviewId = mList.get(position).getReviewId();
                                Intent intent = new Intent(context, EditReview.class);
                                //수정창에 넘어가면 원래 적어놓았던 정보들이 텍스트뷰에 그대로 있어야하기 때문에 그대로 넘겨준다
                                intent.putExtra("reviewId", reviewId);
                                //현재 보고 있는 이 장소가 리스트에서 몇번째에 있는지 식별하기위해 넘겨준다.

                                context.startActivity(intent);

                                return true;


                            //데이터 삭제
                            case R.id.dataDelete:
                                reviewId = mList.get(position).getReviewId();
                                OnClickHandler(reviewId ,position);
                                return true;

                            default:
                                return false;
                        }
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    public void OnClickHandler(String id ,int position)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("리뷰를 삭제하시겠습니까?");
        String reviewid = id;

        //삭제버튼
        builder.setPositiveButton("삭제", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {

//                System.out.println("이 아이템의 번호는!!!"+number);
//                MainSpotArrayList.remove(number);
//                saveData(MainSpotArrayList);

                //스팟의 아이디를 이용해서 아이템을 가지고 온다.
                ReviewInterface reviewInterface =  ApiClient.getApiClient().create(ReviewInterface.class);

                Call<Review> call = reviewInterface.DeleteReview(reviewid);

                call.enqueue(new Callback<Review> ()
                {
                    //연결 성공 시에 싱행되는 부분
                    @Override
                    public void onResponse(@NonNull Call<Review>  call, @NonNull Response<Review>  response)
                    {

//                        String sueccess = response.body().getMessage();
//                        Log.e("onSuccess", response.body().getMessage());

//                        if(sueccess.contains("성공")){
//                            System.out.println("성공");
//                        }

                        mList.remove(position);
                        notifyDataSetChanged();


//                System.out.println(response.body().size());
//                onGetResult(response.body());

                    }



                    @Override
                    public void onFailure(Call<Review> call, Throwable t)
                    {
                        Log.e("onfail", "에러 = " + t.getMessage());
                    }
                });


            }
        });

        builder.setNegativeButton("취소", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {

            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    //리뷰 추천 버튼
    private void reviewThumb(String email, String reviewid, Button button){
        ReviewInterface reviewInterface =  ApiClient.getApiClient().create(ReviewInterface.class);

        Call<Review> call = reviewInterface.thumbInput(reviewid, email);

        call.enqueue(new Callback<Review>()
        {
            //연결 성공 시에 싱행되는 부분
            @Override
            public void onResponse(@NonNull Call<Review>  call, @NonNull Response<Review> response)
            {


                //리뷰 객체가 담긴 리스트로 서버에서 받아온다.
                Log.e("onSuccess", response.body().getThumbCount());
                button.setText(response.body().getThumbCount());

                String message  = response.body().getMessage();

                if(message.contains("추천안함")){
                    button.setBackgroundColor(Color.parseColor("#ffffff"));
                    button.setCompoundDrawablesWithIntrinsicBounds( R.drawable.like_carrot, 0, 0, 0);
                    button.setTextColor(Color.parseColor("#000000"));
                }else{
                    button.setBackgroundColor(Color.parseColor("#E67A00"));
                    button.setCompoundDrawablesWithIntrinsicBounds( R.drawable.like_white, 0, 0, 0);
                    button.setTextColor(Color.parseColor("#ffffff"));
                }




            }



            @Override
            public void onFailure(Call<Review>call, Throwable t)
            {
                Log.e("onfail", "에러 = " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
