package lk.hd192.project;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FoldingCube;
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


public class Register extends GetSafeBase {


    EditText txtFullName, txt_email, txt_mobile;
    GetSafeServices getSafeServices;
    TinyDB tinyDB;
    Dialog dialog;
    LottieAnimationView loading;
    private FirebaseAuth mAuth;
    View view;
    private DatabaseReference firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtFullName = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);
        txt_mobile = findViewById(R.id.txt_mobile);
        loading = findViewById(R.id.loading);
        mAuth = FirebaseAuth.getInstance();
        view = findViewById(R.id.disable_layout);

        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getApplicationContext());


        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        txtFullName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("\\s+", " ");
                if (!s.toString().equals(result)) {
                    txtFullName.setText(result);
                    txtFullName.setSelection(result.length());

                }
            }
        });
        txt_mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (s.toString().length() == 1 && s.toString().startsWith("0")) {
                    s.clear();
                }
            }
        });


        findViewById(R.id.btn_register_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(txtFullName.getText().toString()) | txtFullName.getText().toString().equals(" ") | txtFullName.getText().length() < 3) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txtFullName);
                    txtFullName.setError("Please check your name");
                    txtFullName.requestFocus(1);
                } else if (txt_email.getText().toString().isEmpty() | !Patterns.EMAIL_ADDRESS.matcher(txt_email.getText()).matches()) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txt_email);
                    txt_email.setError("Your email is not valid");
                    txt_email.requestFocus(2);
                } else if (txt_mobile.getText().toString().isEmpty()) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txt_mobile);
                    txt_mobile.setError("Please enter your name");
                    txt_mobile.requestFocus(0);
                } else {
//                    registerUser();
registerFirebaseUser();
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


        getSafeServices.networkJsonRequestWithoutHeader(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.SAVE_USER), 2, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    if (result.getBoolean("user_saved_status")) {

                        tinyDB.putString("user_id", result.getJSONObject("user").getString("id"));
                        tinyDB.putString("phone_no", result.getJSONObject("user").getString("phone_no"));
                        tinyDB.putString("email", txt_email.getText().toString());
                        OTP.optType = 1;
                        newUserSendOtp();

                    } else
                        showWarningToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {

                }

            }
        });

    }

    private void newUserSendOtp() {


        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("phone", tinyDB.getString("phone_no"));
        showLoading();

        getSafeServices.networkJsonRequestWithoutHeader(this, tempParam, getString(R.string.BASE_URL)+getString(R.string.REGISTER_SEND_OTP), 2, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                hideLoading();
                try {

                    if (result.getBoolean("otp_sent_status")) {


                        Log.e("otp token from origin", result.getString("otp_token"));

                        OTP.otpToken = result.getString("otp_token");
                        registerFirebaseUser();



                    } else
                        showWarningToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {

                }

            }
        });

    }
    private void registerFirebaseUser() {
        mAuth.createUserWithEmailAndPassword(txt_email.getText().toString(), txt_mobile.getText().toString())
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

                                        startActivity(new Intent(getApplicationContext(), Messaging.class));
                                        finishAffinity();

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