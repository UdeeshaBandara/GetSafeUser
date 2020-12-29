package lk.hd192.project;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.SplashScreen;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

import static java.lang.Math.floor;

public class OTP extends GetSafeBase {

    EditText confirmOTP_1, confirmOTP_2, confirmOTP_3, confirmOTP_4;
    TextView otpHeading, resendCountdown, resendCountdownTitle;
    TinyDB tinyDB;
    Dialog dialog;
    GetSafeServices getSafeServices;
    public static int optType = -1;//0 for existing,1 for new
    public static String otpToken = "";
    LottieAnimationView loading;

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        tinyDB = new TinyDB(getApplicationContext());

        confirmOTP_1 = findViewById(R.id.otp_one);
        confirmOTP_2 = findViewById(R.id.otp_two);
        confirmOTP_3 = findViewById(R.id.otp_three);
        confirmOTP_4 = findViewById(R.id.otp_four);
        otpHeading = findViewById(R.id.otp_heading);
        resendCountdown = findViewById(R.id.resend_countdown);
        resendCountdownTitle = findViewById(R.id.resend_countdown_title);




        loading = findViewById(R.id.loading);
        view = findViewById(R.id.disable_layout);

        getSafeServices = new GetSafeServices();

        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        Log.e("otp token", OTP.otpToken);

        otpHeading.setText("A verification code has been sent to your mobile \n (+94 " + tinyDB.getString("phone_no") + ")");

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                if ((floor(millisUntilFinished / 1000) % 60) < 10)
                    resendCountdown.setText("0 " + floor(millisUntilFinished / 60 * 1000) + ": 0" + ((millisUntilFinished / 1000) % 60));
                else
                    resendCountdown.setText("0 " + floor(millisUntilFinished / 60 * 1000) + ": " + ((millisUntilFinished / 1000) % 60));
//Log.e("millisec",floor(millisUntilFinished / 1000)+"");
            }

            public void onFinish() {

            }

        }.start();


        findViewById(R.id.btn_otp_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceToken();

            }
        });
        findViewById(R.id.btn_otp_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));

            }
        });


        confirmOTP_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (confirmOTP_1.getText().toString().length() == 1) {
                    confirmOTP_2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        confirmOTP_2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (confirmOTP_2.getText().toString().length() == 1) {
                    confirmOTP_3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        confirmOTP_3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (confirmOTP_3.getText().toString().length() == 1) {
                    confirmOTP_4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        confirmOTP_4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!TextUtils.isEmpty(confirmOTP_1.getText().toString()) & !TextUtils.isEmpty(confirmOTP_2.getText().toString()) & !TextUtils.isEmpty(confirmOTP_3.getText().toString()) & !TextUtils.isEmpty(confirmOTP_4.getText().toString())) {


                    View view = OTP.this.getCurrentFocus();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void newUserValidateOTP(String fcm_token) {

        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("otp", confirmOTP_1.getText().toString() + confirmOTP_2.getText().toString() + confirmOTP_3.getText().toString() + confirmOTP_4.getText().toString());

        tempParam.put("otp-token", OTP.otpToken);

        //tempParam.put("fcm-token", fcm_token);

        showLoading();
        getSafeServices.networkJsonRequestWithoutHeader(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.REGISTER_VALIDATE_OTP), 2, new VolleyJsonCallback() {

            @Override
            public void onSuccessResponse(JSONObject result) {
                hideLoading();
                try {
                    Log.e("response", result + "");

                    if (result.getBoolean("otp_token_validity")) {
                        tinyDB.putBoolean("isLogged", true);
                        tinyDB.putString("token", result.getString("access_token"));
                        startActivity(new Intent(getApplicationContext(), InitLocation.class));
                        finishAffinity();

                    } else
                        showWarningToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {
                    hideLoading();
                    Log.e("ex", e.getMessage());
                }

            }
        });


    }


    private void existingUserValidateOTP(String fcm_token) {

        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("otp", confirmOTP_1.getText().toString() + confirmOTP_2.getText().toString() + confirmOTP_3.getText().toString() + confirmOTP_4.getText().toString());
        tempParam.put("otp-token", OTP.otpToken);
        // tempParam.put("fcm-token", fcm_token);
        showLoading();
        getSafeServices.networkJsonRequestWithoutHeader(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.USER_VALIDATE_OTP), 2, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                hideLoading();
                try {
                    Log.e("result", result + "");

                    //add API response filed
                    if (result.getBoolean("otp_token_validity")) {

                        tinyDB.putBoolean("isLogged", true);
                        tinyDB.putString("token", result.getString("access_token"));
                        SplashScreen.token=result.getString("access_token");
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        finishAffinity();

                    } else
                        showWarningToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {

                    Log.e("exec",e.getMessage());

                }

            }
        });


    }



    public void getDeviceToken() {


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            showWarningToast(dialog, "Please try again", 0);


                            return;


                        } else {

                            Log.e("fcm_token",task.getResult().getToken());
                            if (optType == 0)
                                existingUserValidateOTP(task.getResult().getToken());
                            else if (optType == 1)
                                newUserValidateOTP(task.getResult().getToken());


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