package com.example.onlinecarparkfinder;

import androidx.annotation.Nullable;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.onlinecarparkfinder.utility.App.preEditor;

public class ParkOwnerRegisteration extends AppCompatActivity {


    Context context = ParkOwnerRegisteration.this;

    ImageView top_curve;
    ImageView logo;
    TextView login_title;
    TextView txt_sign_in;
    TextView txt_error;
    Button btn_sign_up;
    LinearLayout sign_in_layout;
    EditText edt_username;
    EditText edt_password;
    EditText edt_name;
    EditText edt_contact;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_owner_registeration);

        viewIntiallization();

        animation();

//
//        edt_address.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                try {
//                    startActivityForResult(builder.build(ParkOwnerRegisteration.this), PLACE_PICKER_REQUEST);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
//            }
//        });


        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edt_username.getText().toString();
                String password = edt_password.getText().toString();
                String name = edt_name.getText().toString();
                String contact = edt_contact.getText().toString();

                if (name.isEmpty()) {
                    edt_name.setError("Name Is Required!");
                    edt_name.requestFocus();
                } else if (contact.isEmpty()) {
                    edt_contact.setError("Contact Is Required!");
                    edt_contact.requestFocus();
                } else if (email.isEmpty()) {
                    edt_username.setError("Email Is Required!");
                    edt_username.requestFocus();
                } else if (password.isEmpty()) {
                    edt_password.setError("Password Is Required!");
                    edt_password.requestFocus();
                } else {

                    registerOwner(name, contact, email, password);

                }
            }
        });

        txt_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ParkOwnerLoginActivity.class);
                finish();
                startActivity(intent);
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
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        txt_sign_in = findViewById(R.id.txt_sign_in);
        txt_error = findViewById(R.id.txt_error);

        txt_sign_in.setText(Html.fromHtml("Already Account?<font color='green'> Sign In</font>"));
    }

    private void animation() {
        Animation top_curve_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_down);
        top_curve.startAnimation(top_curve_anim);

        Animation editText_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.edittext_anim);
        edt_name.startAnimation(editText_anim);
        edt_contact.startAnimation(editText_anim);
        edt_username.startAnimation(editText_anim);
        edt_password.startAnimation(editText_anim);

        Animation field_name_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.field_name_anim);
        logo.startAnimation(field_name_anim);
        txt_sign_in.startAnimation(field_name_anim);
        login_title.startAnimation(field_name_anim);


        Animation center_reveal_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.center_reveal_anim);
        btn_sign_up.startAnimation(center_reveal_anim);
    }


    public void registerOwner(final String name, final String contact, final String email, final String password) {


        final ProgressDialog dialog = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);
        String url = context.getResources().getString(R.string.url) + "createOwnerAccount";
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
                            preEditor.putString("owner_login_token", result.getString("token"));
                            preEditor.putString("name", result.getJSONObject("ownerpark").getString("name"));
                            preEditor.putString("contact", result.getJSONObject("ownerpark").getString("contact"));
                            preEditor.putString("email", result.getJSONObject("ownerpark").getString("email"));
                            preEditor.putString("carpark_owner_id", result.getJSONObject("ownerpark").getString("carpark_owner_id"));
                            preEditor.apply();
                            preEditor.commit();
                            new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Registered!")
                                    .setContentText("Your Account Created Successfully!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();

                                            Intent intent = new Intent(context, ParkLocationActivity.class);
                                            finish();
                                            startActivity(intent);

                                        }
                                    })
                                    .show();


                        } else {
                            Toast.makeText(context, "Try Again Later!", Toast.LENGTH_LONG).show();
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
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
                params.put("name", name);
                params.put("email", email);
                params.put("contact", contact);
                params.put("password", password);
                return params;
            }
        };


        mQueue.add(request);
//        pDialog.show();


    }
}