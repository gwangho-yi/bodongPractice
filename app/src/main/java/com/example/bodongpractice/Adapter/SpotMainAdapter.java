package com.example.bodongpractice.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.R;
import com.example.bodongpractice.SpotDetail;

import java.util.ArrayList;

public class SpotMainAdapter extends RecyclerView.Adapter<SpotMainAdapter.SpotMainViewHolder> {

    Context context;
    ArrayList<Spot> spotList;
    ArrayList<String> spotImg;

    ImageView markerImage;

    ImageView reviewImage;
    ImageView favoriteImage;



    public SpotMainAdapter(Context context, ArrayList<Spot> list){
        super();

        this.context = context;
        this.spotList = list;

    }


    public class SpotMainViewHolder extends RecyclerView.ViewHolder{

        ImageView spotImg;
        TextView spotName;
        TextView spotLocation;
        TextView spotRatingText;
        TextView reviewCountText;
        TextView favoriteCountText;

        //별모양 이미지뷰
        //이미지뷰는 리뷰가 1개 이상일때 나타난다.
        ImageView RatingImage;

        public SpotMainViewHolder(@NonNull View itemView) {
            super(itemView);
            spotImg= itemView.findViewById(R.id.spotImg);
            spotName = itemView.findViewById(R.id.spotName);
            spotLocation = itemView.findViewById(R.id.spotLocation);
            spotRatingText = itemView.findViewById(R.id.spotRatingText);
            reviewCountText = itemView.findViewById(R.id.reviewCountText);
            favoriteCountText = itemView.findViewById(R.id.favoriteCountText);
            //마커 이미지 색 주확생으로 바꾸기
            int color = Color.parseColor("#E67A00");
            markerImage = itemView.findViewById(R.id.markerImage);

            RatingImage = itemView.findViewById(R.id.RatingImage);

//
            RatingImage.setColorFilter(color);
            RatingImage.setVisibility(View.GONE);
            //리뷰수와 즐겨찾기 이미지 색
            color = Color.parseColor("#ffffff");
            reviewImage = itemView.findViewById(R.id.reviewImage);
            favoriteImage = itemView.findViewById(R.id.favoriteImage);

            markerImage.setColorFilter(color);
            reviewImage.setColorFilter(color);
            favoriteImage.setColorFilter(color);

            //아이템을 클ㄹ릭하면 상세페이지로 이동하도록
            itemView.setOnClickListener(new View.OnClickListener() {


                 @Override
                 public void onClick(View v) {
                     int position = getAdapterPosition();
                     Intent intent = new Intent(context, SpotDetail.class);
                     //스팟의 아이디를 넘겨줘서 상세페이지에 들어가면 아이디를 이용해서 아이템을 가져온다.
                     String spotId = spotList.get(position).getSpotId();
                     intent.putExtra("spotId", spotId);

                     context.startActivity(intent);
                 }
            });

        }
    }

    @NonNull
    @Override
    public SpotMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_spot, parent, false);
        return new SpotMainAdapter.SpotMainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpotMainViewHolder holder, int position) {
        //스팟의 이름
        holder.spotName.setText(spotList.get(position).getSpotName());
        //이미지는 스팟리스트 안에 있는 이미지 리스트의 0번째에 있는 걸로 가져다 쓴다.


        String imgPath  = "http://49.247.213.240/SpotImg/"+spotList.get(position).getSpotImg().get(0);

        System.out.println("스팟의 이미지는 : "+spotList.get(position).getSpotImg());

        Glide.with(holder.spotImg)
                .load(imgPath)
                .into(holder.spotImg);

        holder.spotImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);
        //스팟의 위치명
        holder.spotLocation.setText(spotList.get(position).getSpotLocation());

        //리뷰 쓴 갯수가 1개 이상일 경우에만 별점을 표시해주고 리뷰 개수를 표시해준다.
        if(spotList.get(position).getSpotReviewCount()>0){
            System.out.println("별점있다!");
            String rating = spotList.get(position).getSpotRating();

            holder.spotRatingText.setText(spotList.get(position).getSpotRating());


            holder.reviewCountText.setText(String.valueOf(spotList.get(position).getSpotReviewCount()));
            holder.RatingImage.setVisibility(View.VISIBLE);
        }else{

        }
        holder.favoriteCountText.setText(String.valueOf(spotList.get(position).getSpotFavoriteCount()));
    }

    @Override
    public int getItemCount() {
        return spotList.size();
    }
}

