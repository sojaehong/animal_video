package com.jaehong.soo.animalvideo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;


public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        // 2초 후 인트로 액티비티 제거
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
                startApp();
            }
        }, 1000);
        
    }

    private void startApp(){
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
    }

}
