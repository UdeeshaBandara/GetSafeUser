package lk.hd192.project.Utils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.MainActivity;
import lk.hd192.project.Welcome;

public class SplashScreen extends Base {
    GetSafeServices getSafeServices;
    TinyDB tinyDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getApplicationContext());


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.e("is logged", String.valueOf(tinyDB.getBoolean("Is_Logged") == true));

        if (tinyDB.getBoolean("Is_Logged") == true) {
            // getCurrentUserData();
            startActivity(new Intent(SplashScreen.this, Welcome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
            finish();
        }
        else {

            startActivity(new Intent(SplashScreen.this, Welcome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
            finish();
        }


    }


//    public void getCurrentUserData() {
//
//        HashMap<String, String> tempParams = new HashMap<>();
//        tempParams.put("token", tinyDB.getString("token"));
//
//        getSafeServices.networkJsonRequest(getApplicationContext(), tempParams, getString(R.string.BASE_URL) + getString(R.string.CURRENT_USER_DATA), 2, new VolleyJsonCallback() {
//            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//            @Override
//            public void onSuccessResponse(JSONObject result) {
//
//
//                try {
//
//                    if (result.getInt("available") == 1) {
//
//                        startActivity(new Intent(getApplicationContext(), Home.class));
//
//                    } else {
//
//
////
////                        Spannable centeredText = new SpannableString(result.getString("message"));
////                        centeredText.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
////                                0, result.getString("message").length() - 1,
////                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
////
////                        Toast.makeText(getApplicationContext(), centeredText, Toast.LENGTH_LONG).show();
////                        startActivity(new Intent(SplashScreen.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));
////
//
//                    }
//
//                    finishAffinity();
//                } catch (Exception e) {
//
//
//                    e.printStackTrace();
//                }
//
//            }
//        });
//    }

}
