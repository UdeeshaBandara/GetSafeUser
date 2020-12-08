package lk.hd192.project;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kaopiz.kprogresshud.KProgressHUD;

import lk.hd192.project.Utils.TinyDB;


public class GetSafeBase extends AppCompatActivity {

    static KProgressHUD hud;
    static int device_width, device_height;
    public TinyDB tinyDB;
    public static String PICKUP_LAT, PICKUP_LOG, DROP_LAT, DROP_LOG, PICKUP_LOCATION, DROP_LOCATION,LOC_ADDRESS;
    int dropNPickAddressDistinguish;
    public static Double pickLat;

    public static Double pickLng;

    public static boolean MAP_SELECTED=false;

    public static String pickAddress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hud = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        tinyDB = new TinyDB(getApplicationContext());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_width = displayMetrics.widthPixels;
        device_height = displayMetrics.heightPixels;

    }

    public void customToast(String message) {

        View toastView = getLayoutInflater().inflate(R.layout.custom_toast_layout, null);

        toastView.setMinimumWidth(device_width);
        toastView.setMinimumHeight(100);

        TextView textView = toastView.findViewById(R.id.toast_message);

        textView.setText(message);


        Toast toast = new Toast(this);

        toast.setView(toastView);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.show();
    }

    static public void showHUD(String msg) {

        hud.setLabel(msg);
        if (hud.isShowing()) {
            hud.dismiss();
        }
        hud.show();
    }

    static public void hideHUD() {
        if (hud.isShowing()) {
            hud.dismiss();
        }
    }
}
