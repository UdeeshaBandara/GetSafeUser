package lk.hd192.project;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeBaseFragment;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class AddKidSecondFragment extends GetSafeBaseFragment {

    RelativeLayout pinLocation;

    View popupView;
    TinyDB tinyDB;
//    LinearLayout lnrRemember;
    MapView mPickupLocation;
    GoogleMap googleMap;
    LocationManager locationManager;
    Button mConfirm;
    Dialog dialog;
    AddNewKid addNewKid;
    String locationProvider = LocationManager.GPS_PROVIDER;
    CameraPosition cameraPosition;
//    LottieAnimationView rememberAnimation;
    EditText txtAddressOne, txtAddressTwo, txtCity;
    TextView txtLocation;

    Double latitude, longitude;
    GetSafeServices getSafeServices;
    public static LatLng pinnedLocation;


    public AddKidSecondFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_kid_second, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getActivity());
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        pinLocation = view.findViewById(R.id.pin_location);
        txtAddressOne = view.findViewById(R.id.txt_address_one);
        txtAddressTwo = view.findViewById(R.id.txt_address_two);
        txtCity = view.findViewById(R.id.txt_city);
        txtLocation = view.findViewById(R.id.txt_location);
//        rememberAnimation = view.findViewById(R.id.remember_location_animation);
//        lnrRemember = view.findViewById(R.id.lnr_remember);

//        rememberAnimation.setSpeed(0.35f);
//        rememberAnimation.setMinAndMaxProgress(0.0f, 0.7f);
        addNewKid = new AddNewKid();



        pinLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateMapPopup(v, savedInstanceState);
                View view = getActivity().getCurrentFocus();

                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        });
//        lnrRemember.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (AddNewKid.isLocationRemembered) {
//                    rememberAnimation.setMinAndMaxProgress(0.0f, .7f);
//                    rememberAnimation.setSpeed(1f);
//                    AddNewKid.isLocationRemembered = false;
//                    rememberAnimation.playAnimation();
//
//                } else {
//                    rememberAnimation.setMinAndMaxProgress(0.7f, 1f);
//
//                    rememberAnimation.playAnimation();
//                    AddNewKid.isLocationRemembered = true;
//                    rememberAnimation.setSpeed(0.35f);
//                }
//            }
//        });

    }

    public static void dimBehind(PopupWindow popupWindow) {

        View container = popupWindow.getContentView().getRootView();
        Context context = popupWindow.getContentView().getContext();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.7f;
        wm.updateViewLayout(container, p);
    }

    public void onCreateMapPopup(View view, Bundle savedInstanceState) {


        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.activity_map, null);


        final PopupWindow popupWindow = new PopupWindow(popupView, GetSafeBase.device_width - 150, GetSafeBase.device_height - 250, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);

        mConfirm = popupView.findViewById(R.id.btn_confirmMapLocation);
        mPickupLocation = popupView.findViewById(R.id.map_pickupLocation);


        try {
            MapsInitializer.initialize(getActivity());
            loadMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPickupLocation.onCreate(savedInstanceState);
        mPickupLocation.onResume();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //  popupWindow.dismiss();
                return true;
            }
        });


        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {


            }
        });

        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtLocation.setText(GetSafeBase.LOC_ADDRESS);
                popupWindow.dismiss();
            }
        });

    }

    public void validateFields() {

        if (txtAddressOne.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(txtAddressOne);
            txtAddressOne.setError("Please enter address line one");
            AddNewKid.secondCompleted = false;

        } else if (txtAddressTwo.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(txtAddressTwo);
            txtAddressTwo.setError("Please enter address line two");
            AddNewKid.secondCompleted = false;

        } else if (txtCity.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(txtCity);
            txtCity.setError("Please enter city");
            AddNewKid.secondCompleted = false;

        } else if (txtLocation.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(txtLocation);
            txtLocation.setError("Please select kid pickup location");
            AddNewKid.secondCompleted = false;
        } else {
            AddNewKid.secondCompleted = true;


        }
    }

    public void addKidLocationDetails() {


        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("id", AddNewKid.kidId);
        tempParam.put("latitude", latitude.toString());
        tempParam.put("longitude", longitude.toString());
        tempParam.put("add1", txtAddressOne.getText().toString());
        tempParam.put("add2", txtAddressTwo.getText().toString());
        tinyDB.putString("temp_kid_add",txtAddressOne.getText().toString()+", "+txtAddressTwo.getText().toString()+", "+ txtCity.getText().toString());


        ((AddNewKid) Objects.requireNonNull(getActivity())).showLoading();
        getSafeServices.networkJsonRequest(getActivity(), tempParam, getString(R.string.BASE_URL) + getString(R.string.ADD_KID_LOC), 2, tinyDB.getString("token"),
                new VolleyJsonCallback() {

                    @Override
                    public void onSuccessResponse(JSONObject result) {

                        ((AddNewKid) Objects.requireNonNull(getActivity())).hideLoading();
                        try {
                            Log.e("loc response", result + "");

                            if (result.getBoolean("location_saved_status")) {

                                AddNewKid.kidLocId = result.getJSONObject("location").getString("locationable_id");




                            } else
                                showWarningToast(dialog, result.getString("validation_errors"), 0);


                        } catch (Exception e) {
                            Log.e("ex loc", e.getMessage());
                            showWarningToast(dialog, "Something went wrong. Please try again", 0);

                        }

                    }
                });

    }

    public void loadMap() {
        mPickupLocation.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getActivity(), R.raw.dark_map));
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    return;
                }
                googleMap.setMyLocationEnabled(true);


                try {

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                        Intent settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(settings);

                    }
                    final Location location = locationManager.getLastKnownLocation(locationProvider);

                    if (pinnedLocation == null)
                        cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build();
                    else
                        cameraPosition = new CameraPosition.Builder().target(pinnedLocation).zoom(15).build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {

                            locationAddress(cameraPosition.target.latitude, cameraPosition.target.longitude);
                            latitude = cameraPosition.target.latitude;
                            longitude = cameraPosition.target.longitude;
                            pinnedLocation = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);


                        }
                    });

                } catch (Exception e) {

                }


            }
        });
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }

    public void locationAddress(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addressList;

        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addressList = geocoder.getFromLocation(lat, lon, 1);


            if (addressList.size() == 0) {


                // customToast("Oops.. \nNo Address Found in this Area ",0);
                mConfirm.setEnabled(false);

            } else {
                mConfirm.setEnabled(true);
                GetSafeBase.LOC_ADDRESS = addressList.get(0).getAddressLine(0);
            }


        } catch (IOException e) {
            //   customToast("Oops.. \nan error occurred",1);
            e.printStackTrace();
        }
    }


}