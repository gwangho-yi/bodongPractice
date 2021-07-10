package com.example.bodongpractice.Class;

import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

import com.example.bodongpractice.R;

public class TitleBar {

    ImageButton backButton;

    public void titleBar(Activity activity){



        //뒤로가기 버튼을 누르면 액티비티가 없어지도록 설정할것
        backButton=activity.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });


    }


}
