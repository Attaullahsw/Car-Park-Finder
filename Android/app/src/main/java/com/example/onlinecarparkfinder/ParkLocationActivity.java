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

import static com.example.onlinecarparkfinder.utility.App.logPre;
import static com.example.onlinecarparkfinder.utility.App.preEditor;

public class ParkLocationActivity extends AppCompatActivity {


    Context context = ParkLocationActivity.this;

    ImageView top_curve;
    ImageView logo;
    TextView login_title;
    TextView txt_sign_in;
    Button btn_add;
    LinearLayout sign_in_layout;
    EditText edt_no_of_slot;
    TextView edt_park_location;
    EditText edt_price_per_hour;
    String lat = "";
    String lon = "";

    static int PLACE_PICKER_REQUEST = 101;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_location);


        viewIntiallization();

        animation();

//
        edt_park_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(ParkLocationActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });


        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String no_of_slot = edt_no_of_slot.getText().toString();
                String price_per_hour = edt_price_per_hour.getText().toString();

                if (no_of_slot.isEmpty()) {
                    edt_no_of_slot.setError("No Of Slot Is Required!");
                    edt_no_of_slot.requestFocus();
                } else if (price_per_hour.isEmpty()) {
                    edt_price_per_hour.setError("Price Per Is Required!");
                    edt_price_per_hour.requestFocus();
                } else if (lat.isEmpty()) {
                    edt_park_location.setError("Location is required");
                } else {

                    addLocation(no_of_slot, price_per_hour);

                }
            }
        });

    }

    private void viewIntiallization() {
        top_curve = findViewById(R.id.top_curve);
        login_title = findViewById(R.id.login_title);
        logo = findViewById(R.id.logo);
        btn_add = findViewById(R.id.btn_add);
        sign_in_layout = findViewById(R.id.sign_in_layout);
        edt_no_of_slot = findViewById(R.id.edt_no_of_slot);
        edt_park_location = findViewById(R.id.edt_park_location);
        edt_price_per_hour = findViewById(R.id.edt_price_per_hour);


    }

    private void animation() {
        Animation top_curve_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.top_down);
        top_curve.startAnimation(top_curve_anim);

        Animation editText_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.edittext_anim);
        edt_no_of_slot.startAnimation(editText_anim);
        edt_park_location.startAnimation(editText_anim);
        edt_price_per_hour.startAnimation(editText_anim);

        Animation field_name_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.field_name_anim);
        logo.startAnimation(field_name_anim);
        login_title.startAnimation(field_name_anim);


        Animation center_reveal_anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.center_reveal_anim);
        btn_add.startAnimation(center_reveal_anim);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);

                lat = String.valueOf(place.getLatLng().latitude);
                lon = String.valueOf(place.getLatLng().longitude);

                edt_park_location.setText("(" + lat + "," + lon + ")");
            }
        }
    }

    public void addLocation(final String no_of_slots, final String price_per_hour) {


        final ProgressDialog dialog = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);
        String url = context.getResources().getString(R.string.url) + "addParkLocation";
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
                                    .setTitleText("Added!")
                                    .setContentText("Your Park Location Successfully!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            try {
                                                preEditor.putString("park_lat", result.getJSONObject("ownerpark").getString("park_lat"));
                                                preEditor.putString("park_lon", result.getJSONObject("ownerpark").getString("park_lon"));
                                                preEditor.putString("no_of_slots", result.getJSONObject("ownerpark").getString("no_of_slots"));
                                                preEditor.putString("price_per_hour", result.getJSONObject("ownerpark").getString("price_per_hour"));
                                                preEditor.apply();
                                                preEditor.commit();
                                                Intent intent = new Intent(context, ParkOwnerDashboardActivity.class);
                                                finish();
                                                startActivity(intent);
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


                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("connection problem!")
                        .setContentText("No internet connection or serve down!")
                        .show();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Accept", "application/json");
                params.put("Authorization", "Bearer " + logPre.getString("owner_login_token", ""));
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("carpark_owner_id", logPre.getString("carpark_owner_id", ""));
                params.put("no_of_slots", no_of_slots);
                params.put("park_lat", lat);
                params.put("park_lon", lon);
                params.put("price_per_hour", price_per_hour);
                return params;
            }
        };


        mQueue.add(request);
//        pDialog.show();


    }
}