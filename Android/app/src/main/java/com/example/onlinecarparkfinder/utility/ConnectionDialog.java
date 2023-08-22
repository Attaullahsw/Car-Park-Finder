package com.example.onlinecarparkfinder.utility;

import android.app.Activity;
import android.content.Context;

import com.example.onlinecarparkfinder.R;
import com.tapadoo.alerter.Alerter;

public class ConnectionDialog {

    /**********************Internet Warning Dialog *****************************************/
    public static void showCustomDialog2(final Context context) {


        Alerter.create((Activity) context)
                .setTitle("Connection Back")
                .setText("")
                .setBackgroundColorRes(R.color.success) // or setBackgroundColorInt(Color.CYAN)
                .show();
    }

    public static void showCustomDialog(final Context context) {


        Alerter.create((Activity) context)
                .setTitle("Connection Problem")
                .setText("No Internet Connection / Server Problem. Try Again Later")
                .setBackgroundColorRes(R.color.danger) // or setBackgroundColorInt(Color.CYAN)
                .show();


    }

    /**********************Internet Warning Dialog *****************************************/
}
