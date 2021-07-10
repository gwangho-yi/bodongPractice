package com.example.bodongpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bodongpractice.Class.TitleBar;

public class AddGroupCategory extends AppCompatActivity {

    LinearLayout nextButton;

    ImageView trickImg;
    ImageView downhillImg;
    ImageView dancingImg;
    ImageView freeridingImg;
    ImageView slalomImg;


    ImageView trickCheckImg;
    ImageView downhillCheckImg;
    ImageView dancingCheckImg;
    ImageView freeridingCheckImg;
    ImageView slalomCheckImg;


    String category;

    //선택하는 순간 어둡게 만들어줄 카테고리 이미지 변수를 만든다
    //선택한 이미지가 여기에 담긴다
    ImageView categoryImg;

    //체크모양 이미지다.
    //카테고리를 선택하면 체크 이미지가 뜨도록한다.
    ImageView checkImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group_category);

        //타이틀바 뒤로가기 누르면 사라지도록 만든 부분
        TitleBar titleBar=new TitleBar();
        titleBar.titleBar(this);

        //타이틀은 액티비티의 이름에 따라 달라지도록 아마 각 액티티비 이름에 맞추어서 변경시킬 예정이다.
        TextView titleName;
        titleName=findViewById(R.id.titleName);
        titleName.setText("모임 개설");



        //다음 버튼
        nextButton = findViewById(R.id.nextButton);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddGroupCategory.this, AddGroup.class);
                intent.putExtra("category", category);
                startActivity(intent);
                finish();
            }
        });



        //체크 이미지
        trickCheckImg=findViewById(R.id.trickCheckImg);
        downhillCheckImg=findViewById(R.id.downhillCheckImg);
        dancingCheckImg=findViewById(R.id.dancingCheckImg);
        freeridingCheckImg=findViewById(R.id.freeridingCheckImg);
        slalomCheckImg=findViewById(R.id.slalomCheckImg);

        trickCheckImg.setVisibility(View.INVISIBLE);
        downhillCheckImg.setVisibility(View.INVISIBLE);
        dancingCheckImg.setVisibility(View.INVISIBLE);
        freeridingCheckImg.setVisibility(View.INVISIBLE);
        slalomCheckImg.setVisibility(View.INVISIBLE);



        //카테고리 이미지들
        trickImg = findViewById(R.id.trickImg);

        trickImg.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 category = "트릭";
                 System.out.println(category);


                 //이미지가 이미 선택이 되어있다면 이미지의 컬러를 지워준다.
                 if(categoryImg!=null){
                     //먼저 컬리필터를 없애준다
                     categoryImg.setColorFilter(null);
                     //체크이미지를 숨긴다
                     checkImg.setVisibility(View.INVISIBLE);

                     //선택한 이미지를 다시 넣어주고 흐리게 만든다
                     categoryImg = trickImg;
                     categoryImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);
                     //체크이미지를 나타낸다
                     checkImg = trickCheckImg;
                     checkImg.setVisibility(View.VISIBLE);


                 }
                 //카테고리 이미지에 아무것도 없다는 것은 처음에 들어와서 널값이라는거다.
                 //그래서 처음에 선택할때는 에러가 뜰거다.
                 else{



                     //이미지를 넣어주고 색을 흐리게한다.
                     categoryImg = trickImg;
                     categoryImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);

                     checkImg = trickCheckImg;
                     checkImg.setVisibility(View.VISIBLE);
                 }


             }
         });

        downhillImg = findViewById(R.id.downhillImg);
        downhillImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "다운힐";
                //이미지가 이미 선택이 되어있다면 이미지의 컬러를 지워준다.
                if(categoryImg!=null){
                    //먼저 컬리필터를 없애준다
                    categoryImg.setColorFilter(null);
                    //체크이미지를 숨긴다
                    checkImg.setVisibility(View.INVISIBLE);

                    //선택한 이미지를 다시 넣어주고 흐리게 만든다
                    categoryImg = downhillImg;
                    categoryImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);
                    //체크이미지를 나타낸다
                    checkImg = downhillCheckImg;
                    checkImg.setVisibility(View.VISIBLE);


                }
                //카테고리 이미지에 아무것도 없다는 것은 처음에 들어와서 널값이라는거다.
                //그래서 처음에 선택할때는 에러가 뜰거다.
                else{



                    //이미지를 넣어주고 색을 흐리게한다.
                    categoryImg = downhillImg;
                    categoryImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);

                    checkImg = downhillCheckImg;
                    checkImg.setVisibility(View.VISIBLE);
                }
            }
        });

        dancingImg = findViewById(R.id.dancingImg);
        dancingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "댄싱";
                System.out.println(category);

                //이미지가 이미 선택이 되어있다면 이미지의 컬러를 지워준다.
                if(categoryImg!=null){
                    //먼저 컬리필터를 없애준다
                    categoryImg.setColorFilter(null);
                    //체크이미지를 숨긴다
                    checkImg.setVisibility(View.INVISIBLE);

                    //선택한 이미지를 다시 넣어주고 흐리게 만든다
                    categoryImg = dancingImg;
                    categoryImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);
                    //체크이미지를 나타낸다
                    checkImg = dancingCheckImg;
                    checkImg.setVisibility(View.VISIBLE);


                }
                //카테고리 이미지에 아무것도 없다는 것은 처음에 들어와서 널값이라는거다.
                //그래서 처음에 선택할때는 에러가 뜰거다.
                else{



                    //이미지를 넣어주고 색을 흐리게한다.
                    categoryImg = dancingImg;
                    categoryImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);

                    checkImg = dancingCheckImg;
                    checkImg.setVisibility(View.VISIBLE);
                }
            }
        });

        freeridingImg = findViewById(R.id.freeridingImg);
        freeridingImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "프리라이딩";
                System.out.println(category);

                //이미지가 이미 선택이 되어있다면 이미지의 컬러를 지워준다.
                if(categoryImg!=null){
                    //먼저 컬리필터를 없애준다
                    categoryImg.setColorFilter(null);
                    //체크이미지를 숨긴다
                    checkImg.setVisibility(View.INVISIBLE);

                    //선택한 이미지를 다시 넣어주고 흐리게 만든다
                    categoryImg = freeridingImg;
                    categoryImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);
                    //체크이미지를 나타낸다
                    checkImg = freeridingCheckImg;
                    checkImg.setVisibility(View.VISIBLE);


                }
                //카테고리 이미지에 아무것도 없다는 것은 처음에 들어와서 널값이라는거다.
                //그래서 처음에 선택할때는 에러가 뜰거다.
                else{



                    //이미지를 넣어주고 색을 흐리게한다.
                    categoryImg = freeridingImg;
                    categoryImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);

                    checkImg = freeridingCheckImg;
                    checkImg.setVisibility(View.VISIBLE);
                }
            }
        });

        slalomImg = findViewById(R.id.slalomImg);
        slalomImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = "슬라럼";
                System.out.println(category);

                //이미지가 이미 선택이 되어있다면 이미지의 컬러를 지워준다.
                if(categoryImg!=null){
                    //먼저 컬리필터를 없애준다
                    categoryImg.setColorFilter(null);
                    //체크이미지를 숨긴다
                    checkImg.setVisibility(View.INVISIBLE);

                    //선택한 이미지를 다시 넣어주고 흐리게 만든다
                    categoryImg = slalomImg;
                    categoryImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);
                    //체크이미지를 나타낸다
                    checkImg = slalomCheckImg;
                    checkImg.setVisibility(View.VISIBLE);


                }
                //카테고리 이미지에 아무것도 없다는 것은 처음에 들어와서 널값이라는거다.
                //그래서 처음에 선택할때는 에러가 뜰거다.
                else{



                    //이미지를 넣어주고 색을 흐리게한다.
                    categoryImg = slalomImg;
                    categoryImg.setColorFilter(Color.parseColor("#b3b3b3"), PorterDuff.Mode.MULTIPLY);

                    checkImg = slalomCheckImg;
                    checkImg.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}