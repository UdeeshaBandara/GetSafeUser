package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LiveLocation extends AppCompatActivity {

    Button btnBack;
    MapView mapView;


    private GoogleMap googleMap;

    LocationManager locationManager;

    String locationProvider = LocationManager.GPS_PROVIDER;
    CameraPosition cameraPosition;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_location);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mapView = findViewById(R.id.mapView);

        btnBack = findViewById(R.id.btn_location_back);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        try {

            MapsInitializer.initialize(getApplicationContext());
            loadMap();

        } catch (Exception e) {

            Log.e("Map Load Err >> ", e + "");
            e.printStackTrace();

        }
        findViewById(R.id.btn_location_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
    }



    public void loadMap() {

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {

                googleMap = gMap;


                if (ActivityCompat.checkSelfPermission(LiveLocation.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LiveLocation.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    askForPermission(Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    return;
                }


                googleMap.setMyLocationEnabled(true);

                try {

                    final Location location = locationManager.getLastKnownLocation(locationProvider);

                    cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(10).build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {

                            //     Base.pickLat = cameraPosition.target.latitude;
                            //   Base.pickLng = cameraPosition.target.longitude;


                            locationAddress(cameraPosition.target.latitude, cameraPosition.target.longitude);


                        }
                    });

                } catch (Exception e) {

                    Log.e("Map EXC >> ", e + "");
                    Toast.makeText(getApplicationContext(), "Something Went Wrong in Location Service !", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(LiveLocation.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(LiveLocation.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(LiveLocation.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(LiveLocation.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    public void locationAddress(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addressList;

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);

            Log.e("address", addressList.size() + " ");

            if (addressList.size() == 0) {


//                mapConfirmLocation.setEnabled(false);
                Toast.makeText(this, "No Address Found", Toast.LENGTH_LONG);


            } else {

//                mapConfirmLocation.setEnabled(true);
                //  Base.pickAddress = addressList.get(0).getAddressLine(0);


                Log.e("address", addressList + " ");

            }


        } catch (IOException e) {
            Toast.makeText(this, "An error occurred", Toast.LENGTH_LONG);
            e.printStackTrace();
        }
    }
}