package lk.hd192.project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.makeramen.roundedimageview.RoundedImageView;

import lk.hd192.project.Utils.GetSafeBase;

public class DriverProfile extends GetSafeBase {

    CardView cardDriverDetails;
    RoundedImageView imgDriver;
    Button btn_driver_route;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        cardDriverDetails=findViewById(R.id.card_driver_details);
        imgDriver=findViewById(R.id.driver_photo);
        btn_driver_route=findViewById(R.id.btn_driver_route);


        findViewById(R.id.btn_driver_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        btn_driver_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), DriverRoute.class));
            }
        });

    }
}