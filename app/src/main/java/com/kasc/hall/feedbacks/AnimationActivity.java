package com.kasc.hall.feedbacks;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.kasc.hall.R;

public class AnimationActivity extends AppCompatActivity {

    LottieAnimationView feedbackAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_feedback);

        feedbackAnimation = findViewById(R.id.feedbackAnimationView);

        feedbackAnimation.setAnimation(R.raw.ani);

        // Show the Lottie animation
        feedbackAnimation.setVisibility(View.VISIBLE);
        feedbackAnimation.playAnimation();

        feedbackAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                feedbackAnimation.setVisibility(View.GONE);

                finish();
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
