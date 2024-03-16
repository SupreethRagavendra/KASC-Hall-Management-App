package com.kasc.hall.Booking;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.kasc.hall.R;

public class Animation extends AppCompatActivity {

    private LottieAnimationView successAnimationView;
    private static final long ANIMATION_DURATION = 2500; // Duration of animation in milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

        successAnimationView = findViewById(R.id.successAnimationView);

        successAnimationView.setAnimation(R.raw.booking);

        // Show the Lottie animation
        successAnimationView.setVisibility(View.VISIBLE);
        successAnimationView.playAnimation();

        successAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {

                new Handler().postDelayed(() -> {
                    successAnimationView.setVisibility(View.GONE);
                    finish();
                }, ANIMATION_DURATION);
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {
            }
        });
    }
}
