package com.kasc.hall.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kasc.hall.Facultymanagement.LoginActivity;
import com.kasc.hall.R;
import com.kasc.hall.onboarding.OnBoardingScreen;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private static final int splashTimer = 5500;
    private static final int TEXT_APPEAR_DELAY = 150;

    private SharedPreferences onBoardingScreen;
    private TextView devbysr;
    private final String developerText = "Developed by Supreeth Ragavendra";
    private int charIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        devbysr = findViewById(R.id.devbysr);
        animateText();

        new Handler().postDelayed(() -> {
            // Check if it's the first time user is launching the app
            onBoardingScreen = getSharedPreferences("OnBoardingScreen", MODE_PRIVATE);
            boolean isNewUser = onBoardingScreen.getBoolean("firstTime", true);

            if (isNewUser) {
                // If it's the first time, launch OnBoardingScreen activity
                SharedPreferences.Editor editor = onBoardingScreen.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();

                Intent intent = new Intent(SplashScreen.this, OnBoardingScreen.class);
                startActivity(intent);
            } else {
                // If not the first time, launch LoginActivity
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);
            }

            finish();
        }, splashTimer);
    }

    private void animateText() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (charIndex <= developerText.length()) {
                    devbysr.setText(developerText.substring(0, charIndex));
                    charIndex++;
                    handler.postDelayed(this, TEXT_APPEAR_DELAY);
                }
            }
        }, TEXT_APPEAR_DELAY);
    }
}
