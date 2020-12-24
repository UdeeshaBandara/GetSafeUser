package lk.hd192.project;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class OTP extends GetSafeBase {

    EditText confirmOTP_1, confirmOTP_2, confirmOTP_3, confirmOTP_4;
    TextView otpHeading;
    TinyDB tinyDB;
    Dialog dialog;
    GetSafeServices getSafeServices;
    public static int optType = -1;//0 for existing,1 for new

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
        getSafeServices = new GetSafeServices();

        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        otpHeading.setText("A verification code has been sent to your mobile \n (+94 " + tinyDB.getString("phone_no") + ")");

        findViewById(R.id.btn_otp_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Home.class));

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

                    if (optType == 0)
                        validateNewUserOTP();
                    else if (optType == 1)
                        validateExistingUserOTP();

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

    private void validateNewUserOTP() {

        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("otp", confirmOTP_1.getText().toString() + confirmOTP_2.getText().toString() + confirmOTP_3.getText().toString() + confirmOTP_4.getText().toString());


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.REGISTER_SEND_OTP), 2, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    //add API response filed
                    if (result.getBoolean("valid")) {

                        startActivity(new Intent(getApplicationContext(), Home.class));

                    } else
                        showWarningToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {

                }

            }
        });


    }


    private void validateExistingUserOTP() {

        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("otp", confirmOTP_1.getText().toString() + confirmOTP_2.getText().toString() + confirmOTP_3.getText().toString() + confirmOTP_4.getText().toString());


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.REGISTER_SEND_OTP), 2, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    //add API response filed
                    if (result.getBoolean("valid")) {

                        startActivity(new Intent(getApplicationContext(), Home.class));

                    } else
                        showWarningToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {

                }

            }
        });


    }
}