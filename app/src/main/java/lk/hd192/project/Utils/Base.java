package lk.hd192.project.Utils;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

class Base extends AppCompatActivity {
    public static TinyDB tinyDB;


    //Lucky: Device Dimension variables
    public static int DeviceWidth, DeviceHeight;


    public static boolean initialLogin = false;

    public static int NOTIFICATION_COUNT = 0;


    public static  Double pickLat;

    public static  Double pickLng;

    public static  String pickAddress;




//    public static KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tinyDB = new TinyDB(getApplicationContext());



//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        DeviceWidth = displayMetrics.widthPixels;
//        DeviceHeight = displayMetrics.heightPixels;
    }

}
