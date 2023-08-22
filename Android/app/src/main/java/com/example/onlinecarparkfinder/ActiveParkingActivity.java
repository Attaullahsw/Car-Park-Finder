package com.example.onlinecarparkfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.onlinecarparkfinder.utility.App.logPre;

public class ActiveParkingActivity extends AppCompatActivity {

    Context context = ActiveParkingActivity.this;

    ArrayList<ParkReservationModel> items = new ArrayList<>();

    ActiveParkingAdapter activeParkingAdapter;

    ListView lis_active_park;

    TextView txt_message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_parking);

        lis_active_park = findViewById(R.id.lis_active_park);
        txt_message = findViewById(R.id.txt_message);

        showActiveParking();


    }


    public void showActiveParking() {


        final ProgressDialog dialog = ProgressDialog.show(context, "",
                "Loading. Please wait...", true);
        String url = context.getResources().getString(R.string.url) + "parkReservation/" + logPre.getString("carpark_owner_id", "");
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject result = new JSONObject(response);


                    if (result.has("reservation")) {

                        JSONArray allParks = result.getJSONArray("reservation");
                        for (int i = 0; i < allParks.length(); i++) {
                            JSONObject p = allParks.getJSONObject(i);

                            ParkReservationModel model = new ParkReservationModel(
                                    p.getString("carpark_vehicle_reservation_id"),
                                    p.getString("from_time"),
                                    p.getString("to_time"),
                                    p.getString("amount"),
                                    p.getString("payment_type"),
                                    p.getString("payment_status"),
                                    p.getString("reservation_status")
                            );

                            model.setParkModel(new ParkModel(
                                    p.getString("carpark_owner_id"),
                                    p.getString("name"),
                                    p.getString("no_of_slots"),
                                    p.getString("remain_slot"),
                                    p.getString("price_per_hour"),
                                    p.getString("contact"),
                                    p.getString("email"),
                                    p.getString("park_lat"),
                                    p.getString("park_lon")
                            ));

                            model.setVehicleModel(new VehicleModel(
                                    p.getString("vehicle_id"),
                                    p.getString("vehicle_name"),
                                    p.getString("vehicle_number")
                            ));

                            items.add(model);


                        }

                        if (items.size() <= 0) {
                            txt_message.setVisibility(View.VISIBLE);
                        } else {
                            txt_message.setVisibility(View.GONE);
                        }

                        activeParkingAdapter = new ActiveParkingAdapter(context, items);

                        lis_active_park.setAdapter(activeParkingAdapter);


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
}