package com.example.onlinecarparkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import static com.example.onlinecarparkfinder.utility.App.logPre;

public class SplashScreenActivity extends AppCompatActivity {


    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                if (!logPre.getString("user_login_token", "").isEmpty()) {
                    Intent intent = new Intent(SplashScreenActivity.this, ParkingMapActitivity.class);
                    finish();
                    startActivity(intent);
                } else if (!logPre.getString("owner_login_token", "").isEmpty()) {
                    Intent intent = new Intent(SplashScreenActivity.this, ParkOwnerDashboardActivity.class);
                    finish();
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SplashScreenActivity.this, UserOwnerActivity.class);
                    finish();
                    startActivity(intent);
                }

            }
        };

        handler.postDelayed(runnable, 3000);
    }
}