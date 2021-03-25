package lk.hd192.project;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class Login extends GetSafeBase {

    EditText one, two, three, four, five, six, seven, eight, nine;
    Dialog dialog;
    GetSafeServices getSafeServices;
    TinyDB tinyDB;

    View view;
    LottieAnimationView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        one = findViewById(R.id.txt_number_one);
        two = findViewById(R.id.txt_number_two);
        three = findViewById(R.id.txt_number_three);
        four = findViewById(R.id.txt_number_four);
        five = findViewById(R.id.txt_number_five);
        six = findViewById(R.id.txt_number_six);
        loading = findViewById(R.id.loading);
        view = findViewById(R.id.disable_layout);
        seven = findViewById(R.id.txt_number_seven);
        eight = findViewById(R.id.txt_number_eight);
        nine = findViewById(R.id.txt_number_nine);

        tinyDB = new TinyDB(getApplicationContext());
        getSafeServices = new GetSafeServices();
        tinyDB.putString("email", "mccbcpl@gmail.com");
        requestFocus();
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        findViewById(R.id.btn_login_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!TextUtils.isEmpty(one.getText().toString()) &
                        !TextUtils.isEmpty(two.getText().toString()) &
                        !TextUtils.isEmpty(three.getText().toString()) &
                        !TextUtils.isEmpty(four.getText().toString()) &
                        !TextUtils.isEmpty(five.getText().toString()) &
                        !TextUtils.isEmpty(six.getText().toString()) &
                        !TextUtils.isEmpty(seven.getText().toString()) &
                        !TextUtils.isEmpty(eight.getText().toString()) &
                        !TextUtils.isEmpty(nine.getText().toString())) {
                    Log.e("existingUserSendOtp", "ok");
                    existingUserSendOtp();

//                    firebaseLogin();

                } else {
                    YoYo.with(Techniques.Bounce)
                            .duration(2500)
                            .playOn(findViewById(R.id.lnr_number));
                    showToast(dialog, "Please enter correct phone number", 0);

                }
            }
        });


    }


    private void existingUserSendOtp() {

        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("phone", one.getText().toString() +
                two.getText().toString() +
                three.getText().toString() +
                four.getText().toString() +
                five.getText().toString() +
                six.getText().toString() +
                seven.getText().toString() +
                eight.getText().toString() +
                nine.getText().toString());
        //    tempParam.put("fcm_token", token);
        tinyDB.putString("phone_no", one.getText().toString() +
                two.getText().toString() +
                three.getText().toString() +
                four.getText().toString() +
                five.getText().toString() +
                six.getText().toString() +
                seven.getText().toString() +
                eight.getText().toString() +
                nine.getText().toString());
        showLoading();
        getSafeServices.networkJsonRequestWithoutHeader(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.USER_SEND_OTP), 2, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("success", "ok");
                    Log.e("result", result + "");

                    if (result.getBoolean("otp_sent_status")) {

                        OTP.otpToken = result.getString("otp_token");
                        OTP.optType = 0;
                        startActivity(new Intent(getApplicationContext(), OTP.class));

                    } else if (!result.getString("validation_errors").equals("null"))
                        showToast(dialog, result.getString("validation_errors"), 0);
                    else
                        showToast(dialog, "Failed to send OTP", 0);


                    hideLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }


    private void requestFocus() {


        one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (one.getText().toString().length() == 1) {
                    two.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (two.getText().toString().length() == 1) {
                    three.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (three.getText().toString().length() == 1) {
                    four.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (four.getText().toString().length() == 1) {
                    five.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        five.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (five.getText().toString().length() == 1) {
                    six.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        six.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (six.getText().toString().length() == 1) {
                    seven.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        seven.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (seven.getText().toString().length() == 1) {
                    eight.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        eight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (eight.getText().toString().length() == 1) {
                    nine.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        nine.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!TextUtils.isEmpty(one.getText().toString()) &
                        !TextUtils.isEmpty(two.getText().toString()) &
                        !TextUtils.isEmpty(three.getText().toString()) &
                        !TextUtils.isEmpty(four.getText().toString()) &
                        !TextUtils.isEmpty(five.getText().toString()) &
                        !TextUtils.isEmpty(six.getText().toString()) &
                        !TextUtils.isEmpty(seven.getText().toString()) &
                        !TextUtils.isEmpty(eight.getText().toString()) &
                        !TextUtils.isEmpty(nine.getText().toString())) {


                    View view = Login.this.getCurrentFocus();

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