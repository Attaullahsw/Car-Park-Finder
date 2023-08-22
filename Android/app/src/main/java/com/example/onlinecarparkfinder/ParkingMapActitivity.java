package com.example.onlinecarparkfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.ClientError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.braintreepayments.api.dropin.DropInRequest;
import com.example.onlinecarparkfinder.utility.Config;
import com.example.onlinecarparkfinder.utility.GPSTracker;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.onlinecarparkfinder.utility.App.logPre;
import static com.example.onlinecarparkfinder.utility.App.preEditor;

public class ParkingMapActitivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, NavigationView.OnNavigationItemSelectedListener {


    public static final int PAYPAL_REQUEST_ID = 7171;
    public static final int REQUEST_CODE = 271;
    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    private GoogleMap mMap;
    DrawerLayout drawer;
    NavigationView sideNavigationView;
    //////////////////////////////////////////////////////////////////
    GPSTracker Gps;
    String lat, lon;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    /////////////////////////////////////////////////////////////////
    LatLng mylocaton;
    SearchView search_map_location;


    LinearLayout layoutBottomSheet;

    BottomSheetBehavior sheetBehavior;

    public BottomNavigationView navigationView;
    ListView list_vehicle;
    ArrayList<VehicleModel> vehicleModels = new ArrayList<>();
    vehicleAdapter vehicle_Adapter;

    boolean doublePressMarker = false;

    ArrayList<ParkModel> parkModels = new ArrayList<>();
    ArrayList<Marker> parkMarker = new ArrayList<>();

    double parking_amount = 0;
    String park_start_date = "";
    String park_end_date = "";


    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map_actitivity);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        bottom_sheet_vehicle();


        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        sideNavigationView = (NavigationView) findViewById(R.id.nav_view);
        sideNavigationView.setNavigationItemSelectedListener(this);


        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
//                        btnBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
//                        btnBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        ////////////////////////////////////////////////////////////////////////////////////////////
        Gps = new GPSTracker(this);
        checkLocationPermission();

        address();
        ////////////////////////////////////////////////////////////////////////////////////////////


//        search_map_location = findViewById(R.id.search_map_location);
//
//        search_map_location.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                String location = search_map_location.getQuery().toString();
//                List<Address> addresses = null;
//
//                if (location != null || !location.equals("")) {
//                    Geocoder geocoder = new Geocoder(ParkingMapActitivity.this);
//                    try {
//
//                        addresses = geocoder.getFromLocationName(location, 1);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    Address address = addresses.get(0);
//                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
//
//                    mMap.addMarker(new MarkerOptions().position(latLng).title(location));
//                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
//                }
//
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                return false;
//            }
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        View headerView = sideNavigationView.getHeaderView(0);
        TextView txt_haeder_name = headerView.findViewById(R.id.txt_haeder_name);
        TextView txt_email = headerView.findViewById(R.id.txt_email);

        txt_haeder_name.setText(logPre.getString("full_name", ""));
        txt_email.setText(logPre.getString("email", ""));
        sideNavigationView.getMenu().findItem(R.id.nav_parking).setChecked(false);
    }

    private void bottom_sheet_vehicle() {
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        list_vehicle = findViewById(R.id.list_vehicle);
        ImageView close_bottom_sheet_park = findViewById(R.id.close_bottom_sheet_park);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        Button btn_add_vehicle = findViewById(R.id.btn_add_vehicle);


        navigationView = findViewById(R.id.bottomNavigationView);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.add_car:
                        if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                        } else {
                            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                        break;

                    case R.id.more:
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else {
                            drawer.openDrawer(GravityCompat.START);
                        }
                        break;

                }
                return false;
            }
        });

        listVehicle();

        btn_add_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVehicleDialog();

            }
        });


        close_bottom_sheet_park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    private void addVehicleDialog() {
        final Dialog dialog = new Dialog(ParkingMapActitivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_car_item);
        Button btn_add_vehicle, btn_cancel;
        final EditText edt_car_name = dialog.findViewById(R.id.edt_car_name);
        final EditText edt_car_number = dialog.findViewById(R.id.edt_car_number);
        btn_add_vehicle = dialog.findViewById(R.id.btn_add_vehicle);
        btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_add_vehicle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edt_car_name.getText().toString().equals("")) {
                    edt_car_name.setError("Vehicle Name is required!");
                    edt_car_name.requestFocus();
                } else if (edt_car_number.getText().toString().equals("")) {
                    edt_car_number.setError("Vehicle Number is required!");
                    edt_car_number.requestFocus();
                } else {

                    addVehicleRequest(edt_car_name.getText().toString(), edt_car_number.getText().toString(), dialog);


                }
            }

        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addVehicleRequest(final String name, final String number, final Dialog d) {
        final ProgressDialog dialog = ProgressDialog.show(ParkingMapActitivity.this, "",
                "Loading. Please wait...", true);
        String url = ParkingMapActitivity.this.getResources().getString(R.string.url) + "addVehicle";
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

                            try {
                                vehicleModels.add(new VehicleModel(
                                        result.getJSONObject("vehicle").getString("vehicle_id"),
                                        result.getJSONObject("vehicle").getString("vehicle_name"),
                                        result.getJSONObject("vehicle").getString("vehicle_number")
                                ));
                                vehicle_Adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            new SweetAlertDialog(ParkingMapActitivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Added!")
                                    .setContentText("Vehicle Added Successfully!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            d.dismiss();
                                        }
                                    })
                                    .show();


                        } else {
                            Toast.makeText(ParkingMapActitivity.this, "Try Again Later!", Toast.LENGTH_LONG).show();
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

                new SweetAlertDialog(ParkingMapActitivity.this, SweetAlertDialog.ERROR_TYPE)
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
                params.put("vehicle_name", name);
                params.put("vehicle_number", number);
                params.put("user_id", logPre.getString("user_id", ""));
                return params;
            }
        };


        mQueue.add(request);
//        pDialog.show();

    }

    private void listVehicle() {


        String url = ParkingMapActitivity.this.getResources().getString(R.string.url) + "userVehicle/" + logPre.getString("user_id", "");
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject result = new JSONObject(response);


                    if (result.has("vehicle")) {


                        JSONArray allVehicle = result.getJSONArray("vehicle");
                        for (int i = 0; i < allVehicle.length(); i++) {
                            JSONObject v = allVehicle.getJSONObject(i);
                            vehicleModels.add(new VehicleModel(
                                    v.getString("vehicle_id"),
                                    v.getString("vehicle_name"),
                                    v.getString("vehicle_number")
                            ));
                        }

                        vehicle_Adapter = new vehicleAdapter(ParkingMapActitivity.this, vehicleModels);
                        list_vehicle.setAdapter(vehicle_Adapter);
                        list_vehicle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                preEditor.putString("selected_vehicle", vehicleModels.get(i).getId());
                                preEditor.commit();
                                preEditor.apply();
                                vehicle_Adapter.notifyDataSetChanged();
                            }
                        });


                    } else {
                        Toast.makeText(ParkingMapActitivity.this, "Try Again Later!", Toast.LENGTH_LONG).show();
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon))));
//        parkMarker.add(marker);


        if (lat.equals("0.0")) {
            new AlertDialog.Builder(this)
                    .setTitle("Location Is Turn Off!")
                    .setMessage("Turn On Your Location First To Access your Current Position!")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Prompt the user once explanation has been shown
                            ActivityCompat.requestPermissions(ParkingMapActitivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_PERMISSIONS_REQUEST_LOCATION);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            })
                    .create()
                    .show();


        } else {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }


            mylocaton = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon))).title("My Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylocaton, 6.0f));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
            mMap.setOnMarkerClickListener(this);

            // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,20));
//            // Zoom in, animating the camera.
//            mMap.animateCamera(CameraUpdateFactory.zoomIn());
//            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


        }


//        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
//            @Override
//            public void onCameraIdle() {
//                //get latlng at the center by calling
//                LatLng midLatLng = mMap.getCameraPosition().target;
//                Toast.makeText(ParkingMapActitivity.this, midLatLng.latitude + " , " + midLatLng.longitude, Toast.LENGTH_LONG).show();
//
//                marker.setPosition(midLatLng);
//            }
//        });


//        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
//
//            @Override
//            public void onMarkerDrag(Marker arg0) {
//                // TODO Auto-generated method stub
//                Log.d("Marker", "Dragging");
//            }
//
//            @Override
//            public void onMarkerDragEnd(Marker arg0) {
//                // TODO Auto-generated method stub
//                LatLng markerLocation = marker.getPosition();
//                Toast.makeText(ParkingMapActitivity.this, markerLocation.toString(), Toast.LENGTH_LONG).show();
//                Log.d("Marker", "finished");
//            }
//
//            @Override
//            public void onMarkerDragStart(Marker arg0) {
//                // TODO Auto-generated method stub
//                Log.d("Marker", "Started");
//
//            }
//        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Do not turn off location!")
                        .setMessage("You can access Your Current Location through this")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ParkingMapActitivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        address();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

        }
    }

    void address() {
        lat = String.valueOf(Gps.getLatitude());
        lon = String.valueOf(Gps.getLongitude());
        Log.d("mes", "address: " + lat + "   " + lon);

    }


    public void showParking(View view) {

        String url = ParkingMapActitivity.this.getResources().getString(R.string.url) + "allPark";
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        final StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                try {
                    final JSONObject result = new JSONObject(response);


                    if (result.has("park")) {


                        parkModels.clear();
                        JSONArray allParks = result.getJSONArray("park");
                        for (int i = 0; i < allParks.length(); i++) {
                            JSONObject p = allParks.getJSONObject(i);
                            if (!p.getString("park_lat").equals("null")) {
                                Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(p.getString("park_lat"))
                                        , Double.parseDouble(p.getString("park_lon")))).title(p.getString("name")));
                                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                                parkMarker.add(marker);

                                int availSlot = p.getInt("no_of_slots") - p.getInt("remain_slot");

                                parkModels.add(new ParkModel(
                                        p.getString("carpark_owner_id"),
                                        p.getString("name"),
                                        p.getString("no_of_slots"),
                                        availSlot + "",
                                        p.getString("price_per_hour"),
                                        p.getString("contact"),
                                        p.getString("email"),
                                        p.getString("park_lat"),
                                        p.getString("park_lon")
                                ));
                                mMap.setOnMarkerClickListener(ParkingMapActitivity.this);
                            }
                        }


                    } else {
                        Toast.makeText(ParkingMapActitivity.this, "Try Again Later!", Toast.LENGTH_LONG).show();
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


    @Override
    public boolean onMarkerClick(Marker marker) {

        if (doublePressMarker) {
            for (int i = 0; i < parkMarker.size(); i++) {
                if (marker.getId().equals(parkMarker.get(i).getId())) {

                    final ParkModel parkModel = parkModels.get(i);

                    final Dialog dialog = new Dialog(ParkingMapActitivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.park_bottom_sheet);
                    Button btn_start_parking;
                    final TextView txt_park_name = dialog.findViewById(R.id.txt_park_name);
                    final TextView txt_email = dialog.findViewById(R.id.txt_email);
                    final TextView txt_contact = dialog.findViewById(R.id.txt_contact);
                    final TextView txt_no_slot = dialog.findViewById(R.id.txt_no_slot);
                    final TextView txt_current_slot = dialog.findViewById(R.id.txt_current_slot);
                    final TextView txt_price = dialog.findViewById(R.id.txt_price);
                    final ImageView close_bottom_sheet_park = dialog.findViewById(R.id.close_bottom_sheet_park);

                    txt_park_name.setText(parkModel.name);
                    txt_no_slot.setText(parkModel.no_of_slot);
                    txt_current_slot.setText(parkModel.available_slot);
                    txt_contact.setText(parkModel.contact);
                    txt_price.setText(parkModel.price_per_hour);
                    txt_email.setText(parkModel.email);

                    btn_start_parking = dialog.findViewById(R.id.btn_start_parking);
                    btn_start_parking.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            startParkingDialog(parkModel);
                        }

                    });

                    close_bottom_sheet_park.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                }
            }

        } else {
            this.doublePressMarker = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doublePressMarker = false;
                }
            }, 3000);
        }


        return false;
    }

    private void startParkingDialog(final ParkModel parkModel) {

        final Dialog dialog = new Dialog(ParkingMapActitivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.park_date_time);

        Button btn_start_parking;
        ImageView button_close;
        final TextView txt_park_name = dialog.findViewById(R.id.txt_park_name);
        final LinearLayout lin_time_date = dialog.findViewById(R.id.lin_time_date);
        final TextView txt_date = dialog.findViewById(R.id.txt_date);
        final TextView txt_time = dialog.findViewById(R.id.txt_time);
        final TextView park_amount = dialog.findViewById(R.id.park_amount);
        final TextView park_total_time = dialog.findViewById(R.id.park_total_time);
        final Calendar myCalendar = Calendar.getInstance();
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        final SimpleDateFormat sdf = new SimpleDateFormat(myFormat);

        {
            Date c = Calendar.getInstance().getTime();

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = df.format(c);

            Date c1 = Calendar.getInstance().getTime();

            SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
            String formattedTime = df1.format(c1);

            txt_time.setText(formattedTime);
            txt_date.setText("Valid " + formattedDate);

        }


        final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                txt_date.setText("valid " + sdf.format(myCalendar.getTime()));

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(ParkingMapActitivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        txt_time.setText(selectedHour + ":" + selectedMinute);

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {

                            Date c = Calendar.getInstance().getTime();

                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                            String formattedDate = df.format(c);

                            park_start_date = formattedDate;
                            park_end_date = sdf.format(myCalendar.getTime()) + " " + selectedHour + ":" + selectedMinute;

                            Date date1 = simpleDateFormat.parse(park_end_date);
                            Date date2 = simpleDateFormat.parse(formattedDate);

                            long different = date1.getTime() - date2.getTime();

                            long secondsInMilli = 1000;
                            long minutesInMilli = secondsInMilli * 60;
                            long hoursInMilli = minutesInMilli * 60;
                            long daysInMilli = hoursInMilli * 24;

                            long elapsedDays = different / daysInMilli;
                            different = different % daysInMilli;

                            long elapsedHours = different / hoursInMilli;
                            different = different % hoursInMilli;

                            long elapsedMinutes = different / minutesInMilli;
                            different = different % minutesInMilli;

                            long elapsedSeconds = different / secondsInMilli;

                            parking_amount = (elapsedDays * 24 + elapsedHours) * Double.parseDouble(parkModel.price_per_hour);
                            park_total_time.setText("Parking Time: " + ((elapsedDays * 24) + elapsedHours) + "h:" + elapsedMinutes + "m");
                            park_amount.setText("Parking Price: " + parking_amount + " $");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }

        };


        lin_time_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ParkingMapActitivity.this, date2, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        txt_park_name.setText(parkModel.name);

        btn_start_parking = dialog.findViewById(R.id.btn_start_parking);
        button_close = dialog.findViewById(R.id.button_close);
        btn_start_parking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startParkingRequest(parkModel);

//                onBraintreeSubmit();

//                prcessPayment("100");
            }

        });

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void startParkingRequest(final ParkModel model) {

        if (logPre.getString("selected_vehicle", "").isEmpty()) {
            Toast.makeText(ParkingMapActitivity.this, "Select Vehicle", Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog dialog = ProgressDialog.show(ParkingMapActitivity.this, "",
                "Loading. Please wait...", true);
        String url = ParkingMapActitivity.this.getResources().getString(R.string.url) + "addVehicleReservation";
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
                            new SweetAlertDialog(ParkingMapActitivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success!")
                                    .setContentText("Your Reservation Created Successfully!")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.dismissWithAnimation();
                                            new SweetAlertDialog(ParkingMapActitivity.this, SweetAlertDialog.WARNING_TYPE)
                                                    .setTitleText("Navigation!")
                                                    .setContentText("Start Navigation!")
                                                    .setConfirmText("Yes")
                                                    .setCancelButton("Later", new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                                                            sweetAlertDialog.dismissWithAnimation();
                                                        }
                                                    })
                                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                        @Override
                                                        public void onClick(SweetAlertDialog sDialog) {
                                                            sDialog.dismissWithAnimation();
                                                            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", Float.parseFloat(model.lat),
                                                                    Float.parseFloat(model.lon));
                                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                                                            startActivity(intent);
                                                        }
                                                    })
                                                    .show();

                                        }
                                    })
                                    .show();


                        } else {
                            Toast.makeText(ParkingMapActitivity.this, "Try Again Later!", Toast.LENGTH_LONG).show();
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

                new SweetAlertDialog(ParkingMapActitivity.this, SweetAlertDialog.ERROR_TYPE)
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
                params.put("user_id", logPre.getString("user_id", ""));
                params.put("vehicle_id", logPre.getString("selected_vehicle", ""));
                params.put("carpark_owner_id", model.id);
                params.put("from_time", park_start_date);
                params.put("to_time", park_end_date + "");
                params.put("amount", parking_amount + "");
                params.put("reservation_status", "0");
                params.put("payment_status", "0");
                return params;
            }
        };


        mQueue.add(request);
//        pDialog.show();


    }

    private void prcessPayment(String amount) {

        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(amount), "USD", "For Parking", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_ID);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PAYPAL_REQUEST_ID) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmActivity confirmActivity = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmActivity != null) {
                    String paymentDetails = confirmActivity.toString();
                    Toast.makeText(ParkingMapActitivity.this, paymentDetails, Toast.LENGTH_LONG).show();
//                    startActivity(new Intent(this, PayPalPaymentDetails.class).putExtra("PaymentDetails", paymentDetails
//                    ).putExtra("PaymentAmount", "100"));
                } else {

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(ParkingMapActitivity.this, "Cancel", Toast.LENGTH_LONG).show();
            }
        } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Toast.makeText(ParkingMapActitivity.this, "Invalid", Toast.LENGTH_LONG).show();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.nav_logout) {
            preEditor.remove("user_login_token");
            preEditor.remove("full_name");
            preEditor.remove("cnic");
            preEditor.remove("email");
            preEditor.remove("address");
            preEditor.remove("user_id");
            preEditor.apply();
            preEditor.commit();
            Intent intent = new Intent(ParkingMapActitivity.this, UserOwnerActivity.class);
            finish();
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(ParkingMapActitivity.this, USerProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_parking) {
            Intent intent = new Intent(ParkingMapActitivity.this, UserParking.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onBraintreeSubmit() {
        DropInRequest dropInRequest = new DropInRequest()
                .tokenizationKey("sandbox_f252zhq7_hh4cpc39zq4rgjcg");
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
    }
}