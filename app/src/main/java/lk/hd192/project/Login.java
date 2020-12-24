package lk.hd192.project;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

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
        seven = findViewById(R.id.txt_number_seven);
        eight = findViewById(R.id.txt_number_eight);
        nine = findViewById(R.id.txt_number_nine);

        tinyDB = new TinyDB(getApplicationContext());
        getSafeServices = new GetSafeServices();

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

                    userSendOtp();


                } else {
                    YoYo.with(Techniques.Bounce)
                            .duration(2500)
                            .playOn(findViewById(R.id.lnr_number));
                    showWarningToast(dialog, "Please enter correct phone number", 0);

                }
            }
        });
    }


    private void userSendOtp() {

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


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.USER_SEND_OTP), 2, new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    if (result.getBoolean("OTPsent")) {

                        OTP.optType = 0;

                        startActivity(new Intent(getApplicationContext(), OTP.class));

                    } else
                        showWarningToast(dialog, "Something went wrong. Please try again", 0);


                } catch (Exception e) {

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
}