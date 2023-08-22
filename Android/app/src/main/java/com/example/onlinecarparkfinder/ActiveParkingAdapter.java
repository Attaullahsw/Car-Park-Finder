package com.example.onlinecarparkfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.onlinecarparkfinder.utility.App.logPre;
import static com.example.onlinecarparkfinder.utility.App.preEditor;

public class ActiveParkingAdapter extends ArrayAdapter<ParkReservationModel> {

    Context context;
    ArrayList<ParkReservationModel> items;


    public ActiveParkingAdapter(@NonNull Context context, ArrayList<ParkReservationModel> items) {
        super(context, R.layout.active_slot, items);

        this.context = context;
        this.items = items;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.active_slot, parent, false);

        final ParkReservationModel item = items.get(position);

        TextView txt_vehicle_name = view.findViewById(R.id.txt_vehicle_name);
        TextView txt_vehicle_number = view.findViewById(R.id.txt_vehicle_number);
        TextView txt_date = view.findViewById(R.id.txt_date);
        Button btn_car_in = view.findViewById(R.id.btn_car_in);

        txt_vehicle_name.setText(item.getVehicleModel().getName());
        txt_vehicle_number.setText(item.getVehicleModel().getNumber());

        txt_date.setText("From: " + item.getFrom_time() + ",  To: " + item.getTo_time());


//        btn_car_in.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
//                        .setTitleText("Are you sure?")
//                        .setContentText("Won't be able to recover It!")
//                        .setConfirmText("Car In!")
//                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sweetAlertDialog) {
//                                sweetAlertDialog.dismissWithAnimation();
//                            }
//                        })
//                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
//                            @Override
//                            public void onClick(SweetAlertDialog sDialog) {
//                                sDialog.dismissWithAnimation();
//                                deleteVehicle(item.getId(), position);
//                            }
//                        })
//                        .show();
//
//            }
//        });
        return view;
    }

//    public void updateAvailSlot(int status) {
//
//
//        final ProgressDialog dialog = ProgressDialog.show(context, "",
//                "Loading. Please wait...", true);
//        String url = context.getResources().getString(R.string.url) + "remainSlot/" + logPre.getString("carpark_owner_id", "") + "/" + status;
//        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());
//
//        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//
//                try {
//                    final JSONObject result = new JSONObject(response);
//
//
//                    if (result.has("park")) {
//                        JSONObject park = result.getJSONObject("park");
//                        txt_park_name.setText(park.getString("name"));
//                        txt_avail_slot.setText(park.getInt("no_of_slots") - park.getInt("remain_slot") + "");
//                        txt_total_slot.setText(park.getString("no_of_slots"));
//                    }
//
//                    if (result.has("park_reservation")) {
//                        String park_reservation = result.getString("park_reservation");
//                        txt_active.setText(park_reservation);
//                    }
//
//                } catch (JSONException e) {
//                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
//                    e.printStackTrace();
//                }
////                pDialog.hide();
//                dialog.dismiss();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                pDialog.hide();
//                dialog.dismiss();
//
//                new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
//                        .setTitleText("connection problem!")
//                        .setContentText("No internet connection or serve down!")
//                        .show();
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Accept", "application/json");
////                params.put("Authorization", "Bearer " + "7|5sxbw5ZmeWFYXXbj7TIlZxbM2kg8YxriXStxna0o");
//                return params;
//            }
//
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                return params;
//            }
//        };
//
//
//        mQueue.add(request);
////        pDialog.show();
//
//
//    }
}

