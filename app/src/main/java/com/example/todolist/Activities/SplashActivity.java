package com.example.todolist.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.todolist.R;

public class SplashActivity extends AppCompatActivity {

    Animation bottom_anim, bottom_anim2;
    TextView textView;
    ImageView imageView;
    private static final int SPLASH=2300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        bottom_anim= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        bottom_anim2= AnimationUtils.loadAnimation(this,R.anim.bottom_animation2);
        textView=findViewById(R.id.text);
        imageView=findViewById(R.id.logo);

        textView.setAnimation(bottom_anim);
        imageView.setAnimation(bottom_anim2);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this,SignInActivity.class));
                finish();
            }
        },SPLASH);
    }
}