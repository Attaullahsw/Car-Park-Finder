package com.example.onlinecarparkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.onlinecarparkfinder.utility.App.preEditor;

public class ParkOwnerLoginActivity extends AppCompatActivity {


    Context context = ParkOwnerLoginActivity.this;
    ImageView top_curve;
    ImageView logo;
    TextView login_title;
    TextView txt_sign_up;
    TextView txt_forgot;
    TextView txt_error;
    Button btn_login;
    LinearLayout sign_in_layout;
    EditText edt_username;
    EditText edt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_owner_login);


        viewIntiallization();

        animation();


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_username.getText().toString();
                String password = edt_password.getText().toString();

                if (email.isEmpty()) {
                    edt_username.setError("Email Is Required!");
                    edt_username.requestFocus();
                } else if (password.isEmpty()) {
                    edt_password.setError("Password Is Required!");
                    edt_password.requestFocus();
                } else {

                    loginUser(email, password);
                }
            }
        });

        txt_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ParkOwnerRegisteration.class);
                finish();
                startActivity(intent);
            }
        });
    }

    private void viewIntiallization() {
        top_curve = findViewById(R.id.top_curve);
        login_title = findViewById(R.id.login_title);
        logo = findViewById(R.id.logo);
        btn_login = findViewById(R.id.btn_login);
        sign_in_layout = findViewById(R.id.sign_in_layout);
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        txt_sign_up = findViewById(R.id.txt_sign_up);
        txt_forgot = findViewById(R.id.txt_forgot);
        txt_error = findViewById(R.id.txt_error);

        txt_sign_up.setText(Html.fromHtml("No Account Yet?<font color='red'> Sign Up</font>"));
    }

    private void animation() {
        Animation top_curve_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_down);
        top_curve.startAnimation(top_curve_anim);

        Animation editText_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.edittext_anim);
        edt_username.startAnimation(editText_anim);
        edt_password.startAnimation(editText_anim);

        Animation field_name_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.field_name_anim);
        logo.startAnimation(field_name_anim);
        txt_sign_up.startAnimation(field_name_anim);
        login_title.startAnimation(field_name_anim);
        txt_forgot.startAnimation(field_name_anim);

        Animation center_reveal_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.center_reveal_anim);
        btn_login.startAnimation(center_reveal_anim);
    }

    public void loginUser(final String email, final String password) {


        final ProgressDialog dialog = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);
        String url = context.getResources().getString(R.string.url) + "parkLogin";
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject result = new JSONObject(response);

                    if (result.has("errors")) {
                        JSONObject fileds_error = result.getJSONObject("errors");
                    }


                    if (result.has("login")) {
                        boolean registeration = result.getBoolean("login");
                        if (registeration) {

                            preEditor.putString("owner_login_token", result.getString("token"));
                            preEditor.putString("name", result.getJSONObject("ownerpark").getString("name"));
                            preEditor.putString("contact", result.getJSONObject("ownerpark").getString("contact"));
                            preEditor.putString("email", result.getJSONObject("ownerpark").getString("email"));
                            preEditor.putString("carpark_owner_id", result.getJSONObject("ownerpark").getString("carpark_owner_id"));
                            preEditor.apply();
                            preEditor.commit();
                            Intent intent = new Intent(context, ParkOwnerDashboardActivity.class);
                            finish();
                            startActivity(intent);


                        } else {
                            txt_error.setVisibility(View.VISIBLE);
                            Handler handler = new Handler();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
                                    txt_error.setVisibility(View.GONE);
                                }
                            };

                            handler.postDelayed(runnable, 3000);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                pDialog.hide();
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                pDialog.hide();
                dialog.dismiss();
                if (error instanceof AuthFailureError) {
                    txt_error.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            txt_error.setVisibility(View.GONE);
                        }
                    };

                    handler.postDelayed(runnable, 3000);
                } else {
                    new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("connection problem!")
                            .setContentText("No internet connection or serve down!")
                            .show();
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
//                params.put("Authorization", "Bearer " + "7|5sxbw5ZmeWFYXXbj7TIlZxbM2kg8YxriXStxna0o");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };


        mQueue.add(request);
//        pDialog.show();


    }
}