package com.example.bodongpractice.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.R;

import java.util.ArrayList;

public class SpotViewPagerAdapter extends RecyclerView.Adapter<SpotViewHolderPage> {

    private ArrayList<Spot> mList;

    Context mContext;

    public SpotViewPagerAdapter(ArrayList<Spot> data, Context context) {
        this.mList = data;
        this.mContext = context;
    }

    @Override
    public SpotViewHolderPage onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_spot_viewpager, parent, false);
        return new SpotViewHolderPage(view,mContext);

    }


    @Override
    public void onBindViewHolder(SpotViewHolderPage holder, int position) {
        if(holder instanceof SpotViewHolderPage){
            SpotViewHolderPage viewHolder = (SpotViewHolderPage) holder;
            //여기서 뷰 홀더에 데이터를 꽂아 넣어준다.
            viewHolder.onBind(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}