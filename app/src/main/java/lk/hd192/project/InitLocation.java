package lk.hd192.project;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class InitLocation extends GetSafeBase {

    View popupView;


    EditText txtAddOne, txtAddTwo, txtCity, txtAddressDropOne, txtAddressDropTwo;
    RelativeLayout txtAddressPick, txtAddressDropPick;
    Button btnRegisterFinish;
    TextView txtLocation, txtDropLocation;
    Dialog dialog;
    boolean isDropPicker = false;
    Button mConfirm;
    GoogleMap googleMap;
    String locationProvider = LocationManager.GPS_PROVIDER;
    CameraPosition cameraPosition;
    LinearLayout lnrDropLocation;
    MapView mPickupLocation;
    GetSafeServices getSafeServices;
    Double latitude, dropLatitude, longitude, dropLongitude;
    TinyDB tinyDB;

    public static LatLng pinnedLocation, dropPinnedLocation;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_location);
        tinyDB = new TinyDB(getApplicationContext());

        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        txtAddOne = findViewById(R.id.txt_address_one);
        txtAddTwo = findViewById(R.id.txt_address_two);
        txtAddressPick = findViewById(R.id.txt_address_pick);
        btnRegisterFinish = findViewById(R.id.btn_register_finish);
        txtCity = findViewById(R.id.txt_city);
        txtLocation = findViewById(R.id.txt_location);
        lnrDropLocation = findViewById(R.id.lnr_drop_location);
        txtAddressDropOne = findViewById(R.id.txt_address_drop_one);
        txtAddressDropTwo = findViewById(R.id.txt_address_drop_two);
        txtAddressDropPick = findViewById(R.id.txt_address_drop_pick);
        txtDropLocation = findViewById(R.id.txt_drop_location);
        getSafeServices = new GetSafeServices();


        if ( !tinyDB.getBoolean("isStaffAccount"))
            lnrDropLocation.setVisibility(View.VISIBLE);
        else
            lnrDropLocation.setVisibility(View.GONE);


        txtAddressPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDropPicker = false;
                onCreateMapPopup(v, savedInstanceState);
            }
        });
        txtAddressDropPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDropPicker = true;
                onCreateMapPopup(v, savedInstanceState);
            }
        });

        btnRegisterFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtAddOne.getText().toString().isEmpty()) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txtAddOne);
                    txtAddOne.setError("Please enter your address");
                    txtAddOne.requestFocus(0);
                } else if (txtAddTwo.getText().toString().isEmpty()) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txtAddTwo);
                    txtAddTwo.setError("Please enter your address");
                    txtAddTwo.requestFocus(0);
                } else if (txtLocation.getText().toString().isEmpty()) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txtAddressPick);
                    txtAddressPick.requestFocus(0);
                } else {

                    //add init location save API call
                    saveInitLocation();

                }
                if ( tinyDB.getBoolean("isStaffAccount")) {
                    if (txtAddressDropOne.getText().toString().isEmpty()) {


                        YoYo.with(Techniques.Bounce)
                                .duration(1000)
                                .playOn(txtAddressDropOne);
                        txtAddressDropOne.setError("Please enter drop-off address");
                        txtAddressDropOne.requestFocus(0);
                    } else if (txtAddressDropTwo.getText().toString().isEmpty()) {


                        YoYo.with(Techniques.Bounce)
                                .duration(1000)
                                .playOn(txtAddressDropTwo);
                        txtAddressDropTwo.setError("Please enter drop-off address");
                        txtAddressDropTwo.requestFocus(0);
                    } else if (txtDropLocation.getText().toString().isEmpty()) {


                        YoYo.with(Techniques.Bounce)
                                .duration(1000)
                                .playOn(txtAddressDropPick);
                        txtAddressDropPick.requestFocus(0);
                    } else {

                        //add init location save API call
                        saveDropLocation();

                    }


                }
            }
        });

    }

    private void saveDropLocation() {
        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("latitude", dropLatitude.toString());
        tempParam.put("longitude", dropLongitude.toString());
        tempParam.put("add1", txtAddressDropOne.getText().toString());
        tempParam.put("add2", txtAddressDropTwo.getText().toString());


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.ADD_PARENT_DROP_LOCATION), 2, tinyDB.getString("token"),
                new VolleyJsonCallback() {

                    @Override
                    public void onSuccessResponse(JSONObject result) {

                        try {
                            Log.e("loc response", result + "");

                            if (result.getBoolean("location_saved_status")) {

                                startActivity(new Intent(getApplicationContext(), Home.class));
                                finishAffinity();

                            } else
                                showToast(dialog, result.getString("validation_errors"), 0);


                        } catch (Exception e) {
                            Log.e("ex loc", e.getMessage());
                            showToast(dialog, "Something went wrong. Please try again", 0);

                        }

                    }
                });

    }

    private void saveInitLocation() {

        //add init location save API call

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("latitude", latitude.toString());
        tempParam.put("longitude", longitude.toString());
        tempParam.put("add1", txtAddOne.getText().toString());
        tempParam.put("add2", txtAddTwo.getText().toString());


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.ADD_PARENT_LOCATION), 2, tinyDB.getString("token"),
                new VolleyJsonCallback() {

                    @Override
                    public void onSuccessResponse(JSONObject result) {

                        try {
                            Log.e("loc response", result + "");

                            if (result.getBoolean("location_saved_status")) {
                                if (! tinyDB.getBoolean("isStaffAccount")) {
                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                    finishAffinity();
                                }

                            } else
                                showToast(dialog, result.getString("validation_errors"), 0);


                        } catch (Exception e) {
                            Log.e("ex loc", e.getMessage());
                            showToast(dialog, "Something went wrong. Please try again", 0);

                        }

                    }
                });


    }

    public static void dimBehind(PopupWindow popupWindow) {

        View container = popupWindow.getContentView().getRootView();
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }

    public void onCreateMapPopup(View view, Bundle savedInstanceState) {


        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.activity_map, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, GetSafeBase.device_width - 150, GetSafeBase.device_height - 250, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);

        mConfirm = popupView.findViewById(R.id.btn_confirmMapLocation);
        mPickupLocation = popupView.findViewById(R.id.map_pickupLocation);


        try {
            MapsInitializer.initialize(getApplicationContext());
            loadMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPickupLocation.onCreate(savedInstanceState);
        mPickupLocation.onResume();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);


        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //  popupWindow.dismiss();
                return true;
            }
        });


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {


            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDropPicker)
                    txtDropLocation.setText(GetSafeBase.LOC_ADDRESS);
                else
                    txtLocation.setText(GetSafeBase.LOC_ADDRESS);
                popupWindow.dismiss();
            }
        });

    }

    public void loadMap() {
        mPickupLocation.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                try {
                    googleMap = mMap;
                    googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    getApplicationContext(), R.raw.dark_map));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);


                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        Intent settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(settings);

                    }
                    final Location location = locationManager.getLastKnownLocation(locationProvider);

                    if (pinnedLocation == null)
                        cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build();
                    else if (dropPinnedLocation == null)
                        cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build();
                    else if (isDropPicker)
                        cameraPosition = new CameraPosition.Builder().target(dropPinnedLocation).zoom(15).build();

                    else
                        cameraPosition = new CameraPosition.Builder().target(pinnedLocation).zoom(15).build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {

                            locationAddress(cameraPosition.target.latitude, cameraPosition.target.longitude);
                            if (isDropPicker) {
                                dropLatitude = cameraPosition.target.latitude;
                                dropLongitude = cameraPosition.target.longitude;
                                dropPinnedLocation = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);

                            } else {

                                latitude = cameraPosition.target.latitude;
                                longitude = cameraPosition.target.longitude;
                                pinnedLocation = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);


                            }
                        }
                    });

                } catch (Exception e) {

                }


            }
        });
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    public void locationAddress(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addressList;

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);


            if (addressList.size() == 0) {


                // customToast("Oops.. \nNo Address Found in this Area ",0);
                mConfirm.setEnabled(false);

            } else {
                mConfirm.setEnabled(true);
                GetSafeBase.LOC_ADDRESS = addressList.get(0).getAddressLine(0);
            }


        } catch (IOException e) {
            //   customToast("Oops.. \nan error occurred",1);
            e.printStackTrace();
        }
    }

}