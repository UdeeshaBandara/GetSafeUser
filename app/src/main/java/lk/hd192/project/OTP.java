package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class OTP extends AppCompatActivity {

    EditText confirmOTP_1, confirmOTP_2, confirmOTP_3, confirmOTP_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);


        confirmOTP_1 = findViewById(R.id.otp_one);
        confirmOTP_2 = findViewById(R.id.otp_two);
        confirmOTP_3 = findViewById(R.id.otp_three);
        confirmOTP_4 = findViewById(R.id.otp_four);

        findViewById(R.id.btn_otp_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Home.class));

            }
        });
        findViewById(R.id.btn_otp_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));

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

                if (!TextUtils.isEmpty(confirmOTP_1.getText().toString()) & !TextUtils.isEmpty(confirmOTP_2.getText().toString()) & !TextUtils.isEmpty(confirmOTP_3.getText().toString()) & !TextUtils.isEmpty(confirmOTP_4.getText().toString()) ) {



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
}