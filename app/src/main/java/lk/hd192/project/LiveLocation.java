package lk.hd192.project;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lk.hd192.project.Utils.DirectionsJSONParser;
import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;
import lk.hd192.project.pojo.LocationUpdates;


public class LiveLocation extends GetSafeBase {

    Button btnBack;
    MapView mapView;

    View view;
    LottieAnimationView loading;
    private GoogleMap googleMap;

    LocationManager locationManager;
    GetSafeServices getSafeServices;
    private DatabaseReference mRootRef, locationRef, driverIdRef;
    String locationProvider = LocationManager.GPS_PROVIDER;
    CameraPosition cameraPosition;

    String pushId = "";

    TextView time, distance;
    Polyline polyline;
    LocationListener locationListener;
    LocationManager locationManagerSender;
    TinyDB tinyDB;
    Boolean isFirstTime = true;
    Bitmap originMarker, finalMarker;
    int timeOfDay;
    Double driverLat, dropLat, driverLon, dropLon, currentLat, currentLon;
    Dialog dialog;
    public static String timeS, distanceS;
    ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_location);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        getSafeServices = new GetSafeServices();
        loading = findViewById(R.id.loading);
        view = findViewById(R.id.disable_layout);
        mapView = findViewById(R.id.mapView);
        tinyDB = new TinyDB(getApplicationContext());
        mRootRef = FirebaseDatabase.getInstance().getReference();
        btnBack = findViewById(R.id.btn_location_back);
        time = findViewById(R.id.time);
        distance = findViewById(R.id.distance);

        Calendar c = Calendar.getInstance();
        timeOfDay = c.get(Calendar.HOUR_OF_DAY);


//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference("message");
//        Query lastQuery = myRef.orderByKey().limitToLast(1);
        if (tinyDB.getBoolean("isStaffAccount")) {
            locationRef = mRootRef.child("Staff_Drivers").child(tinyDB.getString("kid_driver_id")).child("Location");

        } else {
            locationRef = mRootRef.child("School_Drivers").child(tinyDB.getString("kid_driver_id")).child("Location");

        }


        if (ActivityCompat.checkSelfPermission(LiveLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LiveLocation.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
            return;
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        originMarker = bitmapSizeByScale(BitmapFactory.decodeResource(getResources(), R.drawable.van_map_marker), 1);
        finalMarker = bitmapSizeByScale(BitmapFactory.decodeResource(getResources(), R.drawable.marker_end), 1);

//        dropLat = 6.965495959761049;
//        dropLon = 79.95475497680536;

        findViewById(R.id.location_heading).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.e("loc", "update");
//                LocationUpdates locationUpdates = new LocationUpdates(6.758254739995794, 79.9313956735707, "new");
//                locationRef.setValue(locationUpdates);
//                onBackPressed();
//                java.util.Map messageMap = new HashMap();
//                messageMap.put("latitude",6.948010842741238);
//                messageMap.put("longitude", 79.87266712016485);
//
////
//                locationRef.updateChildren(messageMap, new DatabaseReference.CompletionListener() {
//                    @Override
//                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
//                        if (databaseError != null) {
//                            Log.e("Error", databaseError.getMessage());
//                        }
//                    }
//                });
            }
        });

        if (tinyDB.getBoolean("isStaffAccount"))
            getLocationDetailsOfUser();
        else
            getLocationDetailsOfChild();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    LocationUpdates locationUpdates = snapshot.getValue(LocationUpdates.class);
//                driverLat = snapshot.child("latitude").getValue(Double.class);
//                driverLon = snapshot.child("longitude").getValue(Double.class);


                    driverLat = locationUpdates.getLatitude();
                    driverLon = locationUpdates.getLongitude();


                    if (googleMap != null)
                        googleMap.clear();
                    if (locationUpdates.getStatus().equals("End Trip") && dropLat != null)
                        drawMapPolyline();
                } else {
                    Log.e("snap", "else");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        locationRef.addValueEventListener(valueEventListener);

//        locationRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });


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

    @Override
    protected void onStop() {
        super.onStop();
        locationRef.removeEventListener(valueEventListener);
    }

    public void loadMap() {

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {

                googleMap = gMap;
                googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getApplicationContext(), R.raw.dark_map));
                if (ActivityCompat.checkSelfPermission(LiveLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(LiveLocation.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    return;
                }


                googleMap.setMyLocationEnabled(true);

                try {


//                    final Location location = locationManager.getLastKnownLocation(locationProvider);
//
//                    cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build();
//
//                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//
//                    googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
//                        @Override
//                        public void onCameraChange(CameraPosition cameraPosition) {
//
//                            locationAddress(cameraPosition.target.latitude, cameraPosition.target.longitude);
//
//
//                        }
//                    });
                    new CountDownTimer(2000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {

//                            drawMapPolyline();
                        }
                    }.start();


                } catch (Exception e) {


                    //Toast.makeText(getApplicationContext(), "Something Went Wrong in Location Service !", Toast.LENGTH_SHORT).show();

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

                showToast(dialog, "Oops.. \nNo Address Found in this Area ", 1);


            } else {
//                mConfirm.setEnabled(true);
//                LOC_ADDRESS = addressList.get(0).getAddressLine(0);
            }


        } catch (IOException e) {


            showToast(dialog, "Oops.. \nan error occurred ", 0);

            e.printStackTrace();
        }
    }

    public void drawMapPolyline() {
        try {
            Log.e("drawMapPolyline", "exe");
//        LatLng origin = new LatLng(pickUpLat, pickUpLon);
            LatLng origin = new LatLng(driverLat, driverLon);
//        LatLng dest = new LatLng(drpickUpLat, pickUpLon);
            LatLng dest = new LatLng(dropLat, dropLon);


            googleMap.addMarker(new MarkerOptions()
                    .position(origin).icon(BitmapDescriptorFactory.fromBitmap(originMarker)).title("Vehicle location"));

            googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(finalMarker)).position(dest).title("Destination"));

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(origin);
            builder.include(dest);

            LatLngBounds bounds = builder.build();
            if (isFirstTime) {
                Log.e("inside", "id");
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 75));

                CameraPosition camPos = new CameraPosition.Builder(googleMap.getCameraPosition()).target(bounds.getCenter()).tilt(40).build();

                googleMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(camPos));
                isFirstTime = false;
            }

//
//        int padding = 1;
//
//        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);


//        googleMap.animateCamera(cu);


            String url = getDirectionsUrl(origin, dest);


            DownloadTask downloadTask = new DownloadTask();


            downloadTask.execute(url);

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        //avoid highways
        String avoidList = "&avoid=highways";

        //drop off locations
//        String wayPoints = "&waypoints=via:7.008079020850186,79.96081405184961";


        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + avoidList + "&key=" + getString(R.string.API_KEY);

        // Output format
        String output = "json";


        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.e("direction url", url);

        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            time.setText("Time Remaining " + timeS);
            distance.setText("Distance " + distanceS);

//            Log.e("POLY - results ", result + "");

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(14);
                lineOptions.color(getResources().getColor(R.color.button_blue));
            }

            // Drawing polyline in the Google Map for the i-th route

            if (polyline != null) {
                polyline.remove();
            }

            try {

                polyline = LiveLocation.this.googleMap.addPolyline(lineOptions);

            } catch (Exception e) {
                e.printStackTrace();
            }


//            googleMap.addPolyline(lineOptions);
        }
    }


    public Bitmap bitmapSizeByScale(Bitmap bitmapIn, float scall_zero_to_one_f) {

        Bitmap bitmapOut = Bitmap.createScaledBitmap(bitmapIn,
                Math.round(bitmapIn.getWidth() * scall_zero_to_one_f),
                Math.round(bitmapIn.getHeight() * scall_zero_to_one_f), false);

        return bitmapOut;
    }

    private void getLocationDetailsOfChild() {
        HashMap<String, String> param = new HashMap<>();
        showLoading();
        getSafeServices.networkJsonRequest(this, param, getString(R.string.BASE_URL) + getString(R.string.GET_KID_LOCATION_BY_ID) + "?id=" + tinyDB.getString("selectedChildId"), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    hideLoading();

                    Log.e("child loc", result + "");
                    if (result.getBoolean("status")) {

                        if (timeOfDay >= 0 && timeOfDay < 12) {
                            dropLon = Double.parseDouble(result.getJSONObject("location").getString("pick_up_longitude"));
                            dropLat = Double.parseDouble(result.getJSONObject("location").getString("pick_up_latitude"));
                        } else if (timeOfDay >= 12) {
                            dropLat = Double.parseDouble(result.getJSONObject("location").getString("drop_off_latitude"));
                            dropLon = Double.parseDouble(result.getJSONObject("location").getString("drop_off_longitude"));
                        }


                        locationRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {


                                try {

                                    if (snapshot.exists()) {
                                        LocationUpdates locationUpdates = snapshot.getValue(LocationUpdates.class);
                                        driverLat = locationUpdates.getLatitude();
                                        driverLon = locationUpdates.getLongitude();
                                        if (googleMap != null)
                                            googleMap.clear();
                                        if (locationUpdates.getStatus().equals("End Trip"))
                                            drawMapPolyline();
                                        else
                                            showToast(dialog, "Driver didn't start the trip", 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });


                    }

                } catch (Exception e) {

                    e.printStackTrace();
                    hideLoading();
                }

            }
        });
    }

    private void getLocationDetailsOfUser() {

        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.USER_LOCATION), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    hideLoading();
                    if (result.getBoolean("status")) {


                        if (timeOfDay >= 0 && timeOfDay < 12) {

                            dropLon = result.getJSONObject("location").getDouble("pick_up_longitude");
                            dropLat = result.getJSONObject("location").getDouble("pick_up_latitude");
                        } else if (timeOfDay >= 12) {
                            dropLat = result.getJSONObject("location").getDouble("drop_off_latitude");
                            dropLon = result.getJSONObject("location").getDouble("drop_off_longitude");
                        }

                        locationRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                try {
                                    if (snapshot.exists()) {
                                        LocationUpdates locationUpdates = snapshot.getValue(LocationUpdates.class);
                                        driverLat = locationUpdates.getLatitude();
                                        driverLon = locationUpdates.getLongitude();
                                        if (googleMap != null)
                                            googleMap.clear();
                                        if (locationUpdates.getStatus().equals("End Trip"))
                                            drawMapPolyline();
                                        else
                                            showToast(dialog, "Driver didn't start the trip", 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    hideLoading();

                }

            }
        });

    }

    void showLoading() {

        view.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        loading.playAnimation();


    }

    void hideLoading() {


        loading.setVisibility(View.GONE);
        view.setVisibility(View.GONE);

    }

}