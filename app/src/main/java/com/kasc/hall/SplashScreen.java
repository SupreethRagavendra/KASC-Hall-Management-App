package com.kasc.hall;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kasc.hall.onboarding.OnBoardingScreen;

public class SplashScreen extends AppCompatActivity {

    private static int splashTimer = 3000;

    ImageView vtoplogo;
    TextView devbyshreyxnsh; // Update with your name or app name
    SharedPreferences onBoardingScreen;

    // Animations
    Animation bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);


        //vtoplogo = findViewById(R.id.vtoplogo);
        devbyshreyxnsh = findViewById(R.id.devbyshreyxnsh);  // Update with your developer's name or app name

        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_anim);

        //hallLogo.setAnimation(bottomAnim);
        devbyshreyxnsh.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Initializing shared preference variable
                onBoardingScreen = getSharedPreferences("OnBoardingScreen", MODE_PRIVATE);
                boolean isNewUser = onBoardingScreen.getBoolean("firstTime", true);

                if (isNewUser) {
                    SharedPreferences.Editor editor = onBoardingScreen.edit();
                    editor.putBoolean("firstTime", false);
                    editor.apply();

                    Intent intent = new Intent(SplashScreen.this, OnBoardingScreen.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent i = new Intent(SplashScreen.this, MainActivity.class);  // Update with your HomeActivity
                    startActivity(i);
                    finish();
                }
            }
        }, splashTimer);
    }
}
