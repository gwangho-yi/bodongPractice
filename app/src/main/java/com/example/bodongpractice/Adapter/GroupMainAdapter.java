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
import com.example.bodongpractice.Class.Group;
import com.example.bodongpractice.Class.Spot;
import com.example.bodongpractice.GroupDetail;
import com.example.bodongpractice.R;
import com.example.bodongpractice.SpotDetail;

import java.util.ArrayList;

public class GroupMainAdapter extends RecyclerView.Adapter<GroupMainAdapter.SpotMainViewHolder> {

    Context context;
    ArrayList<Group> mList;






    public GroupMainAdapter(Context context, ArrayList<Group> list){
        super();

        this.context = context;
        this.mList = list;

    }


    public class SpotMainViewHolder extends RecyclerView.ViewHolder{

        ImageView groupImg;
        TextView groupLocationText;
        TextView GroupNameText;
        TextView GroupMemberCountText;



        public SpotMainViewHolder(@NonNull View itemView) {
            super(itemView);



            groupImg = itemView.findViewById(R.id.groupImg);
            groupLocationText = itemView.findViewById(R.id.groupLocationText);
            GroupNameText = itemView.findViewById(R.id.GroupNameText);
            GroupMemberCountText = itemView.findViewById(R.id.GroupMemberCountText);


            //아이템을 클ㄹ릭하면 상세페이지로 이동하도록
            itemView.setOnClickListener(new View.OnClickListener() {


                 @Override
                 public void onClick(View v) {
                     int position = getAdapterPosition();
                     Intent intent = new Intent(context, GroupDetail.class);
                     //스팟의 아이디를 넘겨줘서 상세페이지에 들어가면 아이디를 이용해서 아이템을 가져온다.
                     String groupId = mList.get(position).getGroupId();
                     intent.putExtra("groupId", groupId);

                     context.startActivity(intent);
                 }
            });

        }
    }

    @NonNull
    @Override
    public SpotMainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_group, parent, false);
        return new GroupMainAdapter.SpotMainViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpotMainViewHolder holder, int position) {
        //스팟의 이름
        holder.GroupNameText.setText(mList.get(position).getGroupName());
        //이미지는 스팟리스트 안에 있는 이미지 리스트의 0번째에 있는 걸로 가져다 쓴다.



        String imgPath  = "http://49.247.213.240/GroupImg/"+mList.get(position).getGroupTitleImgLocation();

        System.out.println("그룹의의 이미지는 : "+mList.get(position).getGroupTitleImgLocation());

        Glide.with(holder.groupImg)
                .load(imgPath)
                .into(holder.groupImg);

        //스팟의 위치명
        holder.groupLocationText.setText(mList.get(position).getGroupLocation());

        holder.GroupMemberCountText.setText(String.valueOf(mList.get(position).getGroupMemberNumber()));


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}

