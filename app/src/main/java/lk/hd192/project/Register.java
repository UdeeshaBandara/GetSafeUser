package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;
import lk.hd192.project.pojo.NearestTowns;


public class Register extends GetSafeBase {


    TextView txtFullName, txt_email, txt_mobile;
    GetSafeServices getSafeServices;
    TinyDB tinyDB;
    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtFullName = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);
        txt_mobile = findViewById(R.id.txt_mobile);

        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getApplicationContext());

        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);


        findViewById(R.id.btn_register_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (txtFullName.getText().toString().isEmpty()) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txtFullName);
                    txtFullName.setError("Please enter your name");
                    txtFullName.requestFocus(1);
                } else if (txt_email.getText().toString().isEmpty()) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txt_email);
                    txt_email.setError("Please enter your email");
                    txt_email.requestFocus(2);
                } else if (txt_mobile.getText().toString().isEmpty()) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txt_mobile);
                    txt_mobile.setError("Please enter your name");
                    txt_mobile.requestFocus(0);
                } else {
                    registerUser();
                }
            }
        });
        findViewById(R.id.btn_register_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Selection.class));
                finish();
            }
        });
    }

    private void registerUser() {

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("email", txt_email.getText().toString());
        tempParam.put("phone", txt_mobile.getText().toString());
        tempParam.put("name", txtFullName.getText().toString());
        tempParam.put("type", "Parent");
        tempParam.put("fcm_token", "token");


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.SAVE_USER), 2, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    if (result.getBoolean("user_saved")) {
                        tinyDB.putString("token", result.getString("registration-token"));
                        tinyDB.putString("user_id", result.getJSONObject("user").getString("id"));
                        tinyDB.putString("phone_no", result.getJSONObject("user").getString("phone_no"));
                        sendOTP();

                    } else
                        showWarningToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {

                }

            }
        });

    }
    private void sendOTP(){


        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("phone", tinyDB.getString("phone_no"));



        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.REGISTER_SEND_OTP), 2, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    if (result.getBoolean("OTPsent")) {

                        OTP.optType=1;
                        startActivity(new Intent(getApplicationContext(), OTP.class));


                    } else
                        showWarningToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {

                }

            }
        });

    }
}