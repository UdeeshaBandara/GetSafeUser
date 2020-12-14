package lk.hd192.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class AddKidThirdFragment extends Fragment {

    LottieAnimationView editDetails;
    RelativeLayout rltDropDown, mapScrollContainer;
    ScrollView mapScrollView;
    AddNewKid addNewKid;
    TextView reviewKidName, reviewSchoolAddress, reviewSchoolName, reviewKidDob, reviewAddress, reviewMapLocationAddress;

    Bitmap finalMarker;

    MapView mPickupLocation;
    private GoogleMap googleMap;
    LocationManager locationManager;
    ImageView reviewUnderButton, reviewUpperButton;
    CameraPosition cameraPosition;

    public AddKidThirdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_kid_third, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        editDetails = view.findViewById(R.id.edit_animation);
        reviewKidName = view.findViewById(R.id.review_kid_name);
        reviewSchoolAddress = view.findViewById(R.id.review_school_name);
        reviewSchoolName = view.findViewById(R.id.review_school_name);
        reviewKidDob = view.findViewById(R.id.review_kid_dob);
        reviewAddress = view.findViewById(R.id.review_address);
        reviewMapLocationAddress = view.findViewById(R.id.review_map_location_address);
        mapScrollView = view.findViewById(R.id.map_scroll_view);
        rltDropDown = view.findViewById(R.id.rlt_drop_down);
        mapScrollContainer = view.findViewById(R.id.map_scroll_container);
        reviewUnderButton = view.findViewById(R.id.review_under_button);
        reviewUpperButton = view.findViewById(R.id.review_upper_button);

        mPickupLocation = view.findViewById(R.id.review_map);
        addNewKid = new AddNewKid();

        finalMarker = bitmapSizeByScale(BitmapFactory.decodeResource(getResources(), R.drawable.map_marker), 1);
        mPickupLocation.onCreate(savedInstanceState);
        mPickupLocation.onResume();
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mPickupLocation.setClickable(false);
        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewKid.goToFirstStep();

                AddNewKid.isEditing = true;
            }
        });

        rltDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mapScrollContainer.getVisibility() == View.GONE) {
                    mapScrollContainer.setVisibility(View.VISIBLE);
                    reviewUpperButton.setVisibility(View.VISIBLE);
                    reviewUnderButton.setVisibility(View.GONE);
                } else {
                    mapScrollContainer.setVisibility(View.GONE);
                    reviewUpperButton.setVisibility(View.GONE);
                    reviewUnderButton.setVisibility(View.VISIBLE);
                }

            }
        });


        mPickupLocation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mapScrollView.requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mapScrollView.requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return mPickupLocation.onTouchEvent(event);
            }
        });


    }

    public void updateFields() {


        reviewKidName.setText(AddNewKid.FirstName + " " + AddNewKid.LastName);
        reviewSchoolName.setText(AddNewKid.SchoolName);
        reviewKidDob.setText(AddNewKid.Birthday);

        reviewAddress.setText(AddNewKid.AddOne + ", " + AddNewKid.AddTwo + ", " + AddNewKid.City);

        reviewMapLocationAddress.setText(GetSafeBase.LOC_ADDRESS);

        try {
            MapsInitializer.initialize(getActivity());
            loadMap();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public Bitmap bitmapSizeByScale(Bitmap bitmapIn, float scale_zero_to_one_f) {

        Bitmap bitmapOut = Bitmap.createScaledBitmap(bitmapIn,
                Math.round(bitmapIn.getWidth() * scale_zero_to_one_f),
                Math.round(bitmapIn.getHeight() * scale_zero_to_one_f), false);

        return bitmapOut;
    }

    public void loadMap() {
        mPickupLocation.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;

                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    return;
                }
                googleMap.setMyLocationEnabled(true);


                try {

                    if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        //  customToast("Please enable location services",1);
                        Intent settings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(settings);

                    }

                    cameraPosition = new CameraPosition.Builder().target(AddKidSecondFragment.pinnedLocation).zoom(18).build();
                    googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(finalMarker)).position(AddKidSecondFragment.pinnedLocation).title(AddNewKid.FirstName + "'s pick up location"));
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permissions.length == 0) {
            return;
        }
        boolean allPermissionsGranted = true;
        if (grantResults.length > 0) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }
        }
        if (!allPermissionsGranted) {
            boolean somePermissionsForeverDenied = false;
            for (String permission : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {

                } else {
                    if (ActivityCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(new Intent(getActivity(), LiveLocation.class));


                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", permission);
                        somePermissionsForeverDenied = true;
                    }
                }
            }
            if (somePermissionsForeverDenied) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage("You have forcefully denied some of the required permissions " +
                                "for this action. Please open settings, go to permissions and allow them.")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getActivity().getPackageName(), null));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setCancelable(false)
                        .create()
                        .show();
            }
        } else {
            switch (requestCode) {
    //
    //                case 100:
    //                    try {
    //                        MapsInitializer.initialize(getActivity());
    //                        loadMap();
    //                    } catch (Exception e) {
    //                        e.printStackTrace();
    //                    }
    //                    break;
            }
        }
    }
}