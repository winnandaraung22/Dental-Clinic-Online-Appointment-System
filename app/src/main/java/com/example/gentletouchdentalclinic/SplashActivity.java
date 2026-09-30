package com.example.gentletouchdentalclinic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class SplashActivity extends AppCompatActivity {
    ImageView logo;
    TextView clinic, dental, tagline;
    MaterialButton btnLogin, btnRegister, button;
    LinearLayout buttonLayout;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        logo = findViewById(R.id.imgLogo);
        clinic = findViewById(R.id.txtClinic);
        dental = findViewById(R.id.txtDental);
        tagline = findViewById(R.id.txtTagline);
        button = findViewById(R.id.btnStart);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        buttonLayout = findViewById(R.id.buttonLayout);

        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_animation);
        logo.startAnimation(logoAnim);
        logoAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                clinic.setVisibility(View.VISIBLE);
                Animation clinicAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.text_reveal);
                clinic.startAnimation(clinicAnim);

                clinic.postDelayed(() -> {
                    dental.setVisibility(View.VISIBLE);
                    tagline.setVisibility(View.VISIBLE);

                    Animation detailAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.text_reveal);
                    dental.startAnimation(detailAnim);
                    tagline.startAnimation(detailAnim);

                }, 700);

                clinic.postDelayed(() -> {
                    button.setVisibility(View.VISIBLE);

                    Animation buttonAnim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.button_animation);

                    button.startAnimation(buttonAnim);

                }, 1200);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }
        });

        button.setOnClickListener(v -> {
            button.setVisibility(View.GONE);
            buttonLayout.setVisibility(View.VISIBLE);

            Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.button_animation);
            buttonLayout.startAnimation(animation);
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
