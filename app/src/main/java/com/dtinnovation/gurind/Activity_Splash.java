package com.dtinnovation.gurind;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.os.Vibrator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.eftimoff.androidplayer.Player;
import com.eftimoff.androidplayer.actions.property.PropertyAction;

public class Activity_Splash extends AppCompatActivity {

    private ImageView imgLogo;
    private ImageView manufacturerLogo;
    private ImageView imgBrownsLogo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        if (android.os.Build.VERSION.SDK_INT > 21)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        imgLogo = findViewById(R.id.imgLogo);
        manufacturerLogo = findViewById(R.id.manufacturerLogo);
        imgBrownsLogo = findViewById(R.id.imgBrownsLogo);

        Player.init().animate(PropertyAction.newPropertyAction(imgLogo).
                scaleX(0).
                scaleY(0).
                duration(750).
                interpolator(new AccelerateDecelerateInterpolator()).
                build()).play();

        Player.init().animate(PropertyAction.newPropertyAction(manufacturerLogo).
                translationY(500).
                duration(550).
                alpha(0f).
                build()).play();

        Player.init().animate(PropertyAction.newPropertyAction(imgBrownsLogo).
                translationY(-500).
                duration(550).
                alpha(0f).
                build()).play();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Activity_Splash.this, Activity_SignIn.class));
                Animatoo.animateInAndOut(Activity_Splash.this);
                finish();
            }
        },2000);


    }
}
