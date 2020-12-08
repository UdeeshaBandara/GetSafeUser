package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Map extends GetSafeBase {

    MapView mPickupLocation;
    private GoogleMap googleMap;
    LocationManager locationManager;
    Button mConfirm;
    String locationProvider = LocationManager.GPS_PROVIDER;
    CameraPosition cameraPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mPickupLocation=findViewById(R.id.map_pickupLocation);
        mConfirm=findViewById(R.id.btn_confirmMapLocation);



        try {
            MapsInitializer.initialize(getApplicationContext());
            loadMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPickupLocation.onCreate(savedInstanceState);
        mPickupLocation.onResume();


        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PICKUP_LAT=String.valueOf(googleMap.getCameraPosition().target.latitude);
                PICKUP_LOG=String.valueOf(googleMap.getCameraPosition().target.longitude);
                DROP_LAT=String.valueOf(googleMap.getCameraPosition().target.latitude);
                DROP_LOG=String.valueOf(googleMap.getCameraPosition().target.longitude);
                MAP_SELECTED=true;
                onBackPressed();
            }
        });


    }

    public void loadMap() {
        mPickupLocation.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ActivityCompat.checkSelfPermission(Map.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Map.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    return;
                }
                googleMap.setMyLocationEnabled(true);



                try{

//                    boolean success = googleMap.setMapStyle(
//                            MapStyleOptions.loadRawResourceStyle(
//                                    ActivitySIngleView.this, R.raw.style_json));

                }catch (Exception e){

                }
                locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                // For zooming automatically to the location of the marker
                final Location location = locationManager.getLastKnownLocation(locationProvider);

                cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build();

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {

                        locationAddress(cameraPosition.target.latitude, cameraPosition.target.longitude);


                    }
                });


            }
        });
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(Map.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Map.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(Map.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(Map.this, new String[]{permission}, requestCode);
            }
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void locationAddress(double lat, double lon){
        Geocoder geocoder;
        List<Address> addressList;

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addressList = geocoder.getFromLocation(lat,lon,1);

            Log.e("address", addressList.size()+" ");

            if(addressList.size()==0){


                customToast("Oops.. \nNo Address Found in this Area ");
                mConfirm.setEnabled(false);

            }else{
                mConfirm.setEnabled(true);
                LOC_ADDRESS = addressList.get(0).getAddressLine(0);
            }




        } catch (IOException e) {
            customToast("Oops.. \nan error occurred");
            e.printStackTrace();
        }
    }

}