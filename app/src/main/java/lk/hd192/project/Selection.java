package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import static lk.hd192.project.Utils.GetSafeBase.userType;

public class Selection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);


        findViewById(R.id.school).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType="kid";
                startActivity(new Intent(getApplicationContext(),Register.class));

            }
        });
        findViewById(R.id.staff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType="staff";
                startActivity(new Intent(getApplicationContext(),Register.class));

            }
        });
    }
}