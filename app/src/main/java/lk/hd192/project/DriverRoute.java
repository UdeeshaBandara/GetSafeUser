package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lk.hd192.project.Utils.DirectionsJSONParser;
import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class DriverRoute extends GetSafeBase {

    MapView mapView;

    private GoogleMap googleMap;
    Bitmap originMarker;
    LocationManager locationManager;
    Button mConfirm;
    Polyline polyline;
    String locationProvider = LocationManager.GPS_PROVIDER, wayPoints;
    CameraPosition cameraPosition;
    Dialog dialog;
    String driver_id_route;
    ArrayList<DriverLatLong> driverLatLongs = new ArrayList<>();

    GetSafeServices getSafeServices;
    JSONObject driverRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_route);

        mapView = findViewById(R.id.map_pickupLocation);
        mConfirm = findViewById(R.id.btn_confirmMapLocation);
        getSafeServices = new GetSafeServices();
        driverRoute = new JSONObject();
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        originMarker = bitmapSizeByScale(BitmapFactory.decodeResource(getResources(), R.drawable.marker_end), 1);
        try {

            driver_id_route = getIntent().getStringExtra("driver_id");
            Log.e("driver id", driver_id_route);

            MapsInitializer.initialize(getApplicationContext());
            loadMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        findViewById(R.id.btn_driver_route_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getDriverRoute();

    }

    public void loadMap() {

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {

                googleMap = gMap;
                googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getApplicationContext(), R.raw.dark_map));
                if (ActivityCompat.checkSelfPermission(DriverRoute.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DriverRoute.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    return;
                }


                googleMap.setMyLocationEnabled(false);

                try {

                    new CountDownTimer(2000, 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {

                            drawMapPolyline();
                        }
                    }.start();


                } catch (Exception e) {


                    //Toast.makeText(getApplicationContext(), "Something Went Wrong in Location Service !", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(DriverRoute.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            ActivityCompat.requestPermissions(DriverRoute.this, new String[]{permission}, requestCode);
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDriverRoute() {

        HashMap<String, String> param = new HashMap<>();


        getSafeServices.networkJsonRequest(this, param, getString(R.string.BASE_URL) + getString(R.string.GET_DRIVER_ROUTE) + "?id=" + driver_id_route, 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("route", result + "");

                    if (result.getBoolean("status")) {

                        driverRoute = result.getJSONObject("data");

                        getLatLong();


                    }


                } catch (Exception e) {
                    Log.e("ex from loc", e.getMessage());

                }

            }
        });
    }

    private void getLatLong() {
        try {
            int counter = 0;
            for (int jk = 0; jk < driverRoute.getJSONArray("pickups").length(); jk++) {

                DriverLatLong driverLat = new DriverLatLong(driverRoute.getJSONArray("pickups").getJSONObject(jk).getString("lat"), driverRoute.getJSONArray("pickups").getJSONObject(jk).getString("lng"));

                driverLatLongs.add(counter++, driverLat);
            }
            for (int jk = 0; jk < driverRoute.getJSONArray("dropoffs").length(); jk++) {

                DriverLatLong driverLat = new DriverLatLong(driverRoute.getJSONArray("dropoffs").getJSONObject(jk).getString("lat"), driverRoute.getJSONArray("pickups").getJSONObject(jk).getString("lng"));

                driverLatLongs.add(counter++, driverLat);
            }


        } catch (Exception e) {

        }
//        driverLatLongs.get(0).


    }

    public void drawMapPolyline() {

        if (driverLatLongs.size() != 0) {
            LatLng origin = new LatLng(Double.parseDouble(driverLatLongs.get(0).getLatitude()), Double.parseDouble(driverLatLongs.get(0).getLongitude()));

            LatLng dest = new LatLng(Double.parseDouble(driverLatLongs.get(driverLatLongs.size() - 1).getLatitude()), Double.parseDouble(driverLatLongs.get(driverLatLongs.size() - 1).getLongitude()));

            wayPoints = "&waypoints=";
            for (int l = 0; l < driverLatLongs.size(); l++) {
                googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(driverLatLongs.get(l).getLatitude()), Double.parseDouble(driverLatLongs.get(l).getLongitude()))).icon(BitmapDescriptorFactory.fromBitmap(originMarker)).title(""));

                if (l == 0 || l == driverLatLongs.size() - 1)
                    continue;
                else if (l == 1)
                    wayPoints += "via:" + driverLatLongs.get(l).getLatitude() + "," + driverLatLongs.get(l).getLongitude() + "";
                else
                    wayPoints += "|" + driverLatLongs.get(l).getLatitude() + "," + driverLatLongs.get(l).getLongitude() + "";
            }


            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(origin);
            builder.include(dest);

            LatLngBounds bounds = builder.build();

            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 75));

            CameraPosition camPos = new CameraPosition.Builder(googleMap.getCameraPosition()).target(bounds.getCenter()).tilt(40).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(camPos));

//            String url = "https://maps.googleapis.com/maps/api/directions/json?origin=6.9441934936406895,79.9466785199289&destination=6.980947231978186,79.96678822559392&waypoints=via:6.940655356509514,79.96678822559392|6.9874269748480735,79.9466785199289&avoid=highways&key=AIzaSyAPrTPADT_tYmMJYjg6nZZ4jUHLJILoWpM";
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();


            downloadTask.execute(url);

        }


    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        //avoid highways
        String avoidList = "&avoid=highways";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + wayPoints + avoidList + "&key=" + getString(R.string.API_KEY);

        // Output format
        String output = "json";


        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.e("direction url", url);

        return url;
    }

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

                polyline = DriverRoute.this.googleMap.addPolyline(lineOptions);

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

    private class DriverLatLong {
        private String latitude, longitude;

        public DriverLatLong(String latitude, String longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public String getLongitude() {
            return longitude;
        }

    }
}