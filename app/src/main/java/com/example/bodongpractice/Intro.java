package com.example.bodongpractice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class Intro extends AppCompatActivity {

    TextView skateText;
    ImageView skateImage;

    Animation anim_FadeIn;
    Animation anim_ball;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        skateImage = findViewById(R.id.skateImage);
        skateText = findViewById(R.id.skateText);

        anim_FadeIn=AnimationUtils.loadAnimation(this,R.anim.anim_splash_fadein);
        anim_ball= AnimationUtils.loadAnimation(this,R.anim.anim_splash_ball);

        anim_FadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(Intro.this, LoginActivity.class));

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        skateText.startAnimation(anim_FadeIn);

        skateImage.startAnimation(anim_ball);

    }
}