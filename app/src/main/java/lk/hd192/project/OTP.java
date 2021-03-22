package lk.hd192.project;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

import static java.lang.Math.floor;

public class OTP extends GetSafeBase {

    EditText confirmOTP_1, confirmOTP_2, confirmOTP_3, confirmOTP_4;
    TextView otpHeading, resendCountdown, resendCountdownTitle;
    TinyDB tinyDB;
    Dialog dialog;
    private FirebaseAuth mAuth;
    GetSafeServices getSafeServices;
    public static int optType = -1;//0 for existing,1 for new
    public static String otpToken = "";
    LottieAnimationView loading;
    private DatabaseReference firebaseDatabase;

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
//        resendCountdown = findViewById(R.id.resend_countdown);
//        resendCountdownTitle = findViewById(R.id.resend_countdown_title);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mAuth = FirebaseAuth.getInstance();
        loading = findViewById(R.id.loading);
        view = findViewById(R.id.disable_layout);

        getSafeServices = new GetSafeServices();

        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        Log.e("otp token", OTP.otpToken);

        otpHeading.setText("A verification code has been sent to your mobile \n (+94 " + tinyDB.getString("phone_no") + ")");

//        new CountDownTimer(60000, 1000) {
//
//            public void onTick(long millisUntilFinished) {
//                if ((floor(millisUntilFinished / 1000) % 60) < 10)
//                    resendCountdown.setText("0 " + floor(millisUntilFinished / 60 * 1000) + ": 0" + ((millisUntilFinished / 1000) % 60));
//                else
//                    resendCountdown.setText("0 " + floor(millisUntilFinished / 60 * 1000) + ": " + ((millisUntilFinished / 1000) % 60));
////Log.e("millisec",floor(millisUntilFinished / 1000)+"");
//            }
//
//            public void onFinish() {
//
//            }
//
//        }.start();


        findViewById(R.id.btn_otp_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (optType == 0)
                    existingUserValidateOTP();
                else if (optType == 1)
                    newUserValidateOTP();


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
//        getDeviceToken();

    }

    private void newUserValidateOTP() {

        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("otp", confirmOTP_1.getText().toString() + confirmOTP_2.getText().toString() + confirmOTP_3.getText().toString() + confirmOTP_4.getText().toString());

        tempParam.put("otp-token", OTP.otpToken);


        showLoading();
        getSafeServices.networkJsonRequestWithoutHeader(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.REGISTER_VALIDATE_OTP), 2, new VolleyJsonCallback() {

            @Override
            public void onSuccessResponse(JSONObject result) {
                hideLoading();
                try {
                    Log.e("response", result + "");

                    if (result.getBoolean("otp_token_validity")) {

                        tinyDB.putString("token", result.getString("access_token"));
//                        registerFirebaseUser();
                        getDeviceFcmToken();
//                        firebaseLogin();
                        startActivity(new Intent(getApplicationContext(), InitLocation.class));
                        finishAffinity();

                    } else
                        showToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {
                    hideLoading();
                    Log.e("ex", e.getMessage());
                }

            }
        });


    }

    private void registerFirebaseUser() {
        mAuth.createUserWithEmailAndPassword(tinyDB.getString("email"), tinyDB.getString("phone_no"))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String Uid = currentUser.getUid();
                            firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Uid);
                            HashMap<String, String> userMap = new HashMap<>();
                            userMap.put("Image", "Default");
                            firebaseDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {

//                                        startActivity(new Intent(getApplicationContext(), Messaging.class));
//                                        finishAffinity();

                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.


                        }

                        // ...
                    }
                });
    }

    private void firebaseLogin() {

        mAuth.signInWithEmailAndPassword(tinyDB.getString("email"), tinyDB.getString("phone_no")).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    Log.e("firebase login", "success");
//                    startActivity(new Intent(getApplicationContext(),Messaging.class));
//                    finishAffinity();

                } else {

                }
            }
        });


    }


    private void existingUserValidateOTP() {

        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("otp", confirmOTP_1.getText().toString() + confirmOTP_2.getText().toString() + confirmOTP_3.getText().toString() + confirmOTP_4.getText().toString());
        tempParam.put("otp-token", OTP.otpToken);

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
                        tinyDB.putString("user_id", result.getJSONObject("user").getString("id"));
                        tinyDB.putString("user_name", result.getJSONObject("user").getString("name"));
//                        SplashScreen.token = result.getString("access_token");
                        getDeviceFcmToken();
//                        firebaseLogin();
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        finishAffinity();

                    } else
                        showToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {

                    Log.e("exec", e.getMessage());

                }

            }
        });


    }


    public void getDeviceFcmToken() {


        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            showToast(dialog, "Please try again", 0);


                            return;


                        } else {

                            updateUserFcmToken(task.getResult().getToken());


                        }

                    }
                });
    }

    private void updateUserFcmToken(final String fcmToken) {

        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("fcm_token", fcmToken);

        showLoading();
        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.UPDATE_FCM), 2, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {
                hideLoading();
                try {

                    if (result.getBoolean("saved_status")) {

                        tinyDB.putString("fcmToken", fcmToken);


                    } else
                        showToast(dialog, result.getString("validation_errors"), 0);


                } catch (Exception e) {


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