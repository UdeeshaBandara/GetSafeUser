package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.nfc.TagLostException;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class JourneyDetails extends GetSafeBase {

    GetSafeServices getSafeServices;
    TinyDB tinyDB;
    String tripId,driverId;
    TextView driver_name, txt_date, txt_pick_up_time, txt_prickup_location, txt_drop_off_time, txt_dropoff_location, trip_type, trp_id;
    JSONObject response;
    View view;
    LottieAnimationView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey_details);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getApplicationContext());
        driver_name = findViewById(R.id.driver_name);
        txt_date = findViewById(R.id.txt_date);
        txt_pick_up_time = findViewById(R.id.txt_pick_up_time);
        trp_id = findViewById(R.id.trp_id);
        txt_drop_off_time = findViewById(R.id.txt_drop_off_time);
        txt_prickup_location = findViewById(R.id.txt_prickup_location);
        txt_dropoff_location = findViewById(R.id.txt_dropoff_location);
        trip_type = findViewById(R.id.trip_type);
        loading = findViewById(R.id.loading);
        view = findViewById(R.id.disable_layout);
        response = new JSONObject();

        findViewById(R.id.btn_view_driver_profile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             Intent intent=new Intent(getApplicationContext(), DriverProfile.class);
             intent.putExtra("driver_id",driverId);
             startActivity(intent);
            }
        });

        findViewById(R.id.btn_journey_details_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        try {
            Log.e("trip id", "before");
            tripId = getIntent().getStringExtra("trip_id");
            Log.e("trip id", tripId);
        } catch (Exception e) {
            e.printStackTrace();

        }

        if (tinyDB.getBoolean("isStaffAccount"))
            getSingleTripDetails();
        else
            getSingleTripDetailsForChild();


    }

    private void getSingleTripDetailsForChild() {
        HashMap<String, String> tempParam = new HashMap<>();

showLoading();
        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_SINGLE_JOURNEY_DETAILS_CHILD) + "?tripid=" + tripId + "&childid=" + tinyDB.getString("selectedChildId"), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {


                    if (result.getBoolean("status")) {
                        response = result;
                        setValues();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
hideLoading();
            }
        });


    }

    private void getSingleTripDetails() {

        HashMap<String, String> tempParam = new HashMap<>();

showLoading();
        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_SINGLE_JOURNEY_DETAILS) + "?id=" + tripId, 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("res", result + "");

                    if (result.getBoolean("status")) {
                        response = result;
                        setValues();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
                hideLoading();

            }
        });


    }

    private void setValues() {
        try {
            txt_date.setText(response.getJSONObject("models").getString("date").substring(0, 10));
//            txt_date.setText("response.getJSONObject(.getString");
            driverId=response.getJSONObject("models").getJSONObject("info").getString("driver_id");
            trp_id.setText(response.getJSONObject("models").getJSONObject("info").getString("id"));
            txt_pick_up_time.setText(response.getJSONObject("models").getString("date"));
            txt_prickup_location.setText(response.getJSONObject("models").getJSONObject("pick_up_location_data").getString("pick_up_add1") + " " + response.getJSONObject("models").getJSONObject("pick_up_location_data").getString("pick_up_add2"));
            txt_dropoff_location.setText(response.getJSONObject("models").getJSONObject("pick_up_location_data").getString("drop_off_add1") + " " + response.getJSONObject("models").getJSONObject("pick_up_location_data").getString("drop_off_add2"));

            trip_type.setText(response.getJSONObject("models").getString("type"));

        } catch (Exception e) {
            e.printStackTrace();
        }


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