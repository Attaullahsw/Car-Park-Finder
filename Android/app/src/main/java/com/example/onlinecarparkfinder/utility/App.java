package com.example.onlinecarparkfinder.utility;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import com.stripe.android.PaymentConfiguration;


public class App extends Application {

    public static SharedPreferences logPre;
    public static SharedPreferences.Editor preEditor;
    public static String token;

    public static final String message_style_notification = "message_style_notification";


    @Override
    public void onCreate() {
        super.onCreate();

        logPre = getSharedPreferences("Login", MODE_PRIVATE);
        preEditor = logPre.edit();

        PaymentConfiguration.init(
                getApplicationContext(),
                "pk_test_TYooMQauvdEDq54NiTphI7jx"
        );

//        createNotificationChannels();

    }

//    private void createNotificationChannels() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel1 = new NotificationChannel(
//                    message_style_notification,
//                    "Message Notification",
//                    NotificationManager.IMPORTANCE_HIGH
//            );
//            channel1.setDescription("This notification is for chating");
//            NotificationManager manager = getSystemService(NotificationManager.class);
//            manager.createNotificationChannel(channel1);
//        }
//    }

}
