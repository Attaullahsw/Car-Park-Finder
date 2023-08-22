package com.example.onlinecarparkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.onlinecarparkfinder.utility.App.logPre;
import static com.example.onlinecarparkfinder.utility.App.preEditor;

public class USerProfileActivity extends AppCompatActivity {

    TextView txt_name;
    TextView txt_cnic;
    TextView txt_contact;
    TextView txt_email;
    TextView txt_address;

    Button btn_change_password;
    Button btn_change_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_ser_profile);

        txt_name = findViewById(R.id.txt_name);
        txt_cnic = findViewById(R.id.txt_cnic);
        txt_contact = findViewById(R.id.txt_contact);
        txt_email = findViewById(R.id.txt_email);
        txt_address = findViewById(R.id.txt_address);
        btn_change_password = findViewById(R.id.btn_change_password);
        btn_change_profile = findViewById(R.id.btn_change_profile);

        btn_change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(USerProfileActivity.this,UserEditProfile.class);
                startActivity(intent);
            }
        });

        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(USerProfileActivity.this,UserEditProfile.class);
                startActivity(intent);
            }
        });


    }


    @Override
    protected void onResume() {
        super.onResume();


        txt_name.setText(logPre.getString("full_name", ""));
        txt_cnic.setText(logPre.getString("cnic", ""));
        txt_email.setText(logPre.getString("email", ""));
        txt_address.setText(logPre.getString("address", ""));
        txt_contact.setText(logPre.getString("contact", ""));

    }
}