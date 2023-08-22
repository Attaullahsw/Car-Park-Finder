package com.example.onlinecarparkfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.onlinecarparkfinder.utility.App.logPre;
import static com.example.onlinecarparkfinder.utility.App.preEditor;

public class vehicleAdapter extends ArrayAdapter<VehicleModel> {

    Context context;
    ArrayList<VehicleModel> items;


    public vehicleAdapter(@NonNull Context context, ArrayList<VehicleModel> items) {
        super(context, R.layout.car_item, items);

        this.context = context;
        this.items = items;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.car_item, parent, false);

        final VehicleModel item = items.get(position);

        TextView txt_vehicle_name = view.findViewById(R.id.txt_vehicle_name);
        TextView txt_vehicle_number = view.findViewById(R.id.txt_vehicle_number);
        TextView txt_select_vehicle = view.findViewById(R.id.txt_select_vehicle);
        ImageView img_delete_vehicle = view.findViewById(R.id.img_delete_vehicle);

        txt_vehicle_name.setText(item.getName());
        txt_vehicle_number.setText(item.getNumber());

        if(logPre.getString("selected_vehicle","").equals(item.getId())){
            txt_select_vehicle.setVisibility(View.VISIBLE);
        }else {
            txt_select_vehicle.setVisibility(View.GONE);
        }


        img_delete_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Are you sure?")
                        .setContentText("Won't be able to recover It!")
                        .setConfirmText("Yes,delete it!")
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismissWithAnimation();
                                deleteVehicle(item.getId(), position);
                            }
                        })
                        .show();

            }
        });


        return view;
    }

    private void deleteVehicle(final String id, final int position) {



        String url = context.getResources().getString(R.string.url) + "deleteVehicle/" + id;
        RequestQueue mQueue = Volley.newRequestQueue(context.getApplicationContext());

        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject result = new JSONObject(response);


                    if (result.has("delete")) {

                        if (result.getBoolean("delete")) {

                            if(logPre.getString("selected_vehicle","").equals(id)){
                                preEditor.remove("selected_vehicle");
                                preEditor.commit();
                                preEditor.apply();
                            }

                            Toast.makeText(context, "Deleted!", Toast.LENGTH_LONG).show();
                            items.remove(position);
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "Try Again Later!", Toast.LENGTH_LONG).show();
                        }


                    } else {
                        Toast.makeText(context, "Try Again Later!", Toast.LENGTH_LONG).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                pDialog.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                pDialog.hide();

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
                return params;
            }
        };


        mQueue.add(request);
//        pDialog.show();

    }
}

