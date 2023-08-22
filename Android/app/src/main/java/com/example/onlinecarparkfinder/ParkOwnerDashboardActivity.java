package com.example.onlinecarparkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class ParkOwnerDashboardActivity extends AppCompatActivity {

    Context context = ParkOwnerDashboardActivity.this;

    TextView txt_avail_slot;
    TextView txt_total_slot;
    TextView txt_active;
    TextView txt_park_name;

    Button btn_minus;
    Button btn_plus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_park_owner_dashboard);

        txt_active = findViewById(R.id.txt_active);
        txt_total_slot = findViewById(R.id.txt_total_slot);
        txt_avail_slot = findViewById(R.id.txt_avail_slot);
        txt_park_name = findViewById(R.id.txt_park_name);
        btn_plus = findViewById(R.id.btn_plus);
        btn_minus = findViewById(R.id.btn_minus);

        btn_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAvailSlot(0);
            }
        });

        btn_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateAvailSlot(1);
            }
        });


        dashboardOwner();
    }

    @Override
    protected void onResume() {
        super.onResume();

        dashboardOwner();
    }

    public void dashboardOwner() {


        final ProgressDialog dialog = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);
        String url = context.getResources().getString(R.string.url) + "parkDashboard/" + logPre.getString("carpark_owner_id", "");
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject result = new JSONObject(response);


                    if (result.has("park")) {
                        JSONObject park = result.getJSONObject("park");
                        txt_park_name.setText(park.getString("name"));
                        txt_avail_slot.setText(park.getInt("no_of_slots") - park.getInt("remain_slot") + "");
                        txt_total_slot.setText(park.getString("no_of_slots"));
                    }

                    if (result.has("park_reservation")) {
                        String park_reservation = result.getString("park_reservation");
                        txt_active.setText(park_reservation);
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
//                params.put("Authorization", "Bearer " + "7|5sxbw5ZmeWFYXXbj7TIlZxbM2kg8YxriXStxna0o");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };


        mQueue.add(request);
//        pDialog.show();


    }


    public void updateAvailSlot(int status) {


        final ProgressDialog dialog = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);
        String url = context.getResources().getString(R.string.url) + "remainSlot/" + logPre.getString("carpark_owner_id", "") + "/" + status;
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject result = new JSONObject(response);


                    if (result.has("park")) {


                        JSONObject park = result.getJSONObject("park");
                        txt_park_name.setText(park.getString("name"));
                        txt_avail_slot.setText(park.getInt("no_of_slots") - park.getInt("remain_slot") + "");
                        txt_total_slot.setText(park.getString("no_of_slots"));
                    }

                    if (result.has("park_reservation")) {
                        String park_reservation = result.getString("park_reservation");
                        txt_active.setText(park_reservation);
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
//                params.put("Authorization", "Bearer " + "7|5sxbw5ZmeWFYXXbj7TIlZxbM2kg8YxriXStxna0o");
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }
        };


        mQueue.add(request);
//        pDialog.show();


    }

    public void gotoActivePark(View view) {

        Intent intent = new Intent(context, ActiveParkingActivity.class);
        startActivity(intent);

    }

    public void logOut(View view) {

        preEditor.remove("owner_login_token");
        preEditor.remove("name");
        preEditor.remove("contact");
        preEditor.remove("email");
        preEditor.remove("carpark_owner_id");
        preEditor.commit();
        preEditor.apply();
        Intent intent = new Intent(context, UserOwnerActivity.class);
        startActivity(intent);


    }
}