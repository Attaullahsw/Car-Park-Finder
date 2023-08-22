package com.example.onlinecarparkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
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

import static com.example.onlinecarparkfinder.utility.App.logPre;
import static com.example.onlinecarparkfinder.utility.App.preEditor;

public class UserEditProfile extends AppCompatActivity {


    Context context = UserEditProfile.this;

    ImageView top_curve;
    ImageView logo;
    TextView login_title;
    TextView txt_error;
    Button btn_sign_up;
    LinearLayout sign_in_layout;
    EditText edt_address;
    EditText edt_username;
    EditText edt_name;
    EditText edt_contact;
    EditText edt_cnic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_edit_profile);


        viewIntiallization();

        animation();


        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_username.getText().toString();
                String name = edt_name.getText().toString();
                String cnic = edt_cnic.getText().toString();
                String contact = edt_contact.getText().toString();
                String address = edt_address.getText().toString();

                if (name.isEmpty()) {
                    edt_name.setError("Name Is Required!");
                    edt_name.requestFocus();
                } else if (contact.isEmpty()) {
                    edt_contact.setError("Contact Is Required!");
                    edt_contact.requestFocus();
                } else if (cnic.isEmpty()) {
                    edt_cnic.setError("CNIC Is Required!");
                    edt_cnic.requestFocus();
                } else if (address.isEmpty()) {
                    edt_address.setError("Address Is Required!");
                    edt_address.requestFocus();
                } else if (email.isEmpty()) {
                    edt_username.setError("Email Is Required!");
                    edt_username.requestFocus();
                } else {

                    registerUser(name, contact, cnic, address, email);

                }
            }
        });

    }

    private void viewIntiallization() {
        top_curve = findViewById(R.id.top_curve);
        login_title = findViewById(R.id.login_title);
        logo = findViewById(R.id.logo);
        btn_sign_up = findViewById(R.id.btn_sign_up);
        sign_in_layout = findViewById(R.id.sign_in_layout);
        edt_name = findViewById(R.id.edt_name);
        edt_contact = findViewById(R.id.edt_contact);
        edt_cnic = findViewById(R.id.edt_cnic);
        edt_username = findViewById(R.id.edt_username);
        edt_address = findViewById(R.id.edt_address);
        txt_error = findViewById(R.id.txt_error);


        edt_name.setText(logPre.getString("full_name", ""));
        edt_cnic.setText(logPre.getString("cnic", ""));
        edt_username.setText(logPre.getString("email", ""));
        edt_address.setText(logPre.getString("address", ""));
        edt_contact.setText(logPre.getString("contact", ""));
    }

    private void animation() {
        Animation top_curve_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_down);
        top_curve.startAnimation(top_curve_anim);

        Animation editText_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.edittext_anim);
        edt_name.startAnimation(editText_anim);
        edt_contact.startAnimation(editText_anim);
        edt_cnic.startAnimation(editText_anim);
        edt_username.startAnimation(editText_anim);
        edt_address.startAnimation(editText_anim);

        Animation field_name_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.field_name_anim);
        logo.startAnimation(field_name_anim);
        login_title.startAnimation(field_name_anim);


        Animation center_reveal_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.center_reveal_anim);
        btn_sign_up.startAnimation(center_reveal_anim);
    }


    public void registerUser(final String name, final String contact, final String cnic, final String address, final String email) {


        final ProgressDialog dialog = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);
        String url = context.getResources().getString(R.string.url) + "updateUserAccount";
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject result = new JSONObject(response);

                    if (result.has("errors")) {
                        JSONObject fileds_error = result.getJSONObject("errors");
                    }


                    if (result.has("insert")) {
                        boolean registeration = result.getBoolean("insert");
                        if (registeration) {
                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Updated!")
                                    .setContentText("Your Account Updated Successfully!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            try {
                                                preEditor.putString("full_name", result.getJSONObject("user").getString("full_name"));
                                                preEditor.putString("cnic", result.getJSONObject("user").getString("cnic"));
                                                preEditor.putString("email", result.getJSONObject("user").getString("email"));
                                                preEditor.putString("address", result.getJSONObject("user").getString("address"));
                                                preEditor.putString("contact", result.getJSONObject("user").getString("contact"));
                                                preEditor.putString("user_id", result.getJSONObject("user").getString("user_id"));
                                                preEditor.apply();
                                                preEditor.commit();
                                                finish();
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    })
                                    .show();


                        } else {
                            Toast.makeText(context, "Try Again Later!", Toast.LENGTH_LONG).show();
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
                if (error instanceof ClientError) {
                    edt_username.setError("Email is already Taken");
                    edt_username.requestFocus();
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
                params.put("user_id", logPre.getString("user_id", ""));
                params.put("full_name", name);
                params.put("cnic", cnic);
                params.put("email", email);
                params.put("contact", contact);
                params.put("address", address);
                return params;
            }
        };


        mQueue.add(request);
//        pDialog.show();


    }
}