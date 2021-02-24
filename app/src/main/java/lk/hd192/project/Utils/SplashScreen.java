package lk.hd192.project.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.Home;
import lk.hd192.project.Login;
import lk.hd192.project.OTP;
import lk.hd192.project.R;
import lk.hd192.project.Welcome;

public class SplashScreen extends GetSafeBase {
    GetSafeServices getSafeServices;
    TinyDB tinyDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getApplicationContext());


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Log.e("islogged", tinyDB.getBoolean("isLogged") + "");


        if (tinyDB.getBoolean("isLogged")) {

            validateToken();


        } else {
            OTP.optType = 0;
            Log.e("otpType", OTP.optType + "");
            startActivity(new Intent(SplashScreen.this, Welcome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
            finish();
        }


    }

    private void validateToken() {


        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.VALIDATE_TOKEN), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try { //startActivity(new Intent(SplashScreen.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));

                    if (result.getBoolean("logged-in-status")) {

                        tinyDB.putString("user_id", result.getJSONObject("user").getString("id"));
                        tinyDB.putString("driver_id", result.getJSONObject("user").getString("driver_id"));
                        startActivity(new Intent(SplashScreen.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
                    } else {
                        OTP.optType = 0;
                        startActivity(new Intent(SplashScreen.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
                    }
                    Log.e("token", tinyDB.getString("token"));

                    finish();


                } catch (Exception e) {

                }

            }
        });

    }


}
