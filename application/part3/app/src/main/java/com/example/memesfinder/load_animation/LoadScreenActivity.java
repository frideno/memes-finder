package com.example.memesfinder.load_animation;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.memesfinder.R;
import com.example.memesfinder.SignInActivity;

public class LoadScreenActivity extends AppCompatActivity {
    private static int SPLASH = 5000;//length in miliseconds of lunch screen
    ImageView logo;
    ImageView logo_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setContentView(R.layout.activity_load_screen);

        logo = findViewById(R.id.lightning);
        logo_text = findViewById(R.id.buzzgamestext);
        AnimationManager.play_left_animation(logo, this);
        AnimationManager.play_right_animation(logo_text, this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadScreenActivity.this, SignInActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH);

        // play music:
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.win_xp);
        mp.start();


    }

    //handle fullscreen no navigation bar and no time bar
    public void onWindowFocusChanged(boolean hasFocus) {//no navigation bar
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
}
