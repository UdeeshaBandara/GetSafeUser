package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;


import lk.hd192.project.Utils.TinyDB;



public class Selection extends AppCompatActivity {
TinyDB tinyDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection);

        tinyDB=new TinyDB(getApplicationContext());
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        findViewById(R.id.school).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinyDB.putBoolean("isStaffAccount",false);


                startActivity(new Intent(getApplicationContext(),Register.class));

            }
        });
        findViewById(R.id.staff).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tinyDB.putBoolean("isStaffAccount",true);
                startActivity(new Intent(getApplicationContext(),Register.class));

            }
        });
    }
}