package lk.hd192.project.Utils;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.Home;
import lk.hd192.project.Login;
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




        validateToken();


//        if (tinyDB.getBoolean("Is_Logged") == true) {
//            // getCurrentUserData();
//            startActivity(new Intent(SplashScreen.this, Welcome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
//            finish();
//        }
//        else {
//
//            startActivity(new Intent(SplashScreen.this, Welcome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
//            finish();
//        }


    }

    private void validateToken() {


        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("otp", tinyDB.getString("token"));


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.REGISTER_SEND_OTP), 2, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    //add API response filed
                    if (result.getBoolean("valid"))

                        startActivity(new Intent(SplashScreen.this, Welcome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
                    else

                        startActivity(new Intent(SplashScreen.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));


                    finish();


                } catch (Exception e) {

                }

            }
        });

    }


}
