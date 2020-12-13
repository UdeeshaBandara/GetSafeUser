package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;


public class Register extends AppCompatActivity {


    TextView txtFullName, txt_email, txt_mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtFullName = findViewById(R.id.txt_name);
        txt_email = findViewById(R.id.txt_email);
        txt_mobile = findViewById(R.id.txt_mobile);

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
                    startActivity(new Intent(getApplicationContext(), OTP.class));
                    finish();
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
}