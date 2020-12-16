package lk.hd192.project.Utils;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kaopiz.kprogresshud.KProgressHUD;

import lk.hd192.project.R;

public class GetSafeBaseFragment extends Fragment {
    static KProgressHUD hud;
    static int device_width, device_height;
    public TinyDB tinyDB;


    public static  boolean isEnable;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        tinyDB = new TinyDB(getActivity());

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        device_width = displayMetrics.widthPixels;
        device_height = displayMetrics.heightPixels;

    }



    public void customToast(String message, int type) {

        View toastView = getLayoutInflater().inflate(R.layout.custom_toast_layout, null);

        toastView.setMinimumWidth(device_width);
        toastView.setMinimumHeight(100);

        if (type == 1)
            toastView.findViewById(R.id.lnr_bg_toast).setBackgroundColor(getResources().getColor(R.color.toast_warning_color));

        TextView textView = toastView.findViewById(R.id.toast_message);

        textView.setText(message);


        Toast toast = new Toast(getActivity());

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
