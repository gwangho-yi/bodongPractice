package com.example.bodongpractice.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.R;
import com.example.bodongpractice.Register;
import com.example.bodongpractice.SpotDetail;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SpotViewHolderPage extends RecyclerView.ViewHolder {

    private TextView spotTitle;
    private ImageView spotImg;
    private LinearLayout layout;
    Spot spot;
    Context mContext;

    SpotViewHolderPage(View itemView, Context context) {
        super(itemView);
        spotTitle = itemView.findViewById(R.id.spotName);
        spotImg = itemView.findViewById(R.id.spotImg);
        layout = itemView.findViewById(R.id.layout);
        this.mContext=context;
    }

    //뷰페이저 어댑터에 있는 온바인드에서 데이터를 받아온다.
    public void onBind(Spot data){
        this.spot = data;

        spotTitle.setText(spot.getSpotName());

        String uri = "http://49.247.213.240/SpotImg/"+spot.getSpotSingleImg();
        //글라이드로 이미지 크기 조정
        Glide.with(spotImg)
                .load(uri)
                .override(MATCH_PARENT,MATCH_PARENT)
                .into(spotImg);
        spotImg.setScaleType(ImageView.ScaleType.FIT_XY);

        layout.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent intent = new Intent(mContext, SpotDetail.class);
                 intent.putExtra("spotId", spot.getSpotId());
                 mContext.startActivity(intent);

             }
         });

//        rl_layout.setBackgroundResource(data.getColor());
    }
}
