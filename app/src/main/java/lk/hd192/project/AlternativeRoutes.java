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
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.gson.JsonObject;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;


public class AlternativeRoutes extends GetSafeBase implements DatePickerDialog.OnDateSetListener {

    JSONObject singleAlternate;
    JSONArray alternateRoute;
    GetSafeServices getSafeServices;
    Button btn_add, btn_morning, btn_evening, mConfirm, btn_save_alternate;
    RecyclerView recycler_alternate_dates;
    String selectedSession = "";
    Dialog dialog;
    SimpleDateFormat simpleDateFormat;
    int year;
    int month;
    int day;
    View popupView;
    LatLng pinnedLocation;
    String locationProvider = LocationManager.GPS_PROVIDER;
    CameraPosition cameraPosition;
    MapView mPickupLocation;
    Double latitude, longitude;
    GoogleMap googleMap;
    TextView txt_location, txt_date;
    TinyDB tinyDB;
    ArrayList<LatLng> selectedLatLong = new ArrayList<>();

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative_routes);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btn_add = findViewById(R.id.btn_add);
        recycler_alternate_dates = findViewById(R.id.recycler_alternate_dates);
        btn_morning = findViewById(R.id.btn_morning);
        btn_evening = findViewById(R.id.btn_evening);
        txt_location = findViewById(R.id.txt_location);
        txt_date = findViewById(R.id.txt_date);
        btn_save_alternate = findViewById(R.id.btn_save_alternate);

        tinyDB = new TinyDB(getApplicationContext());

        getSafeServices = new GetSafeServices();
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        alternateRoute = new JSONArray();
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        findViewById(R.id.card_date).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showDate(year, month, day, R.style.DatePickerSpinner);
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDate();
            }
        });

        btn_save_alternate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    for (int g = 0; g < alternateRoute.length(); g++) {
                        String date = alternateRoute.getJSONObject(g).getString("date");
                        String loc = alternateRoute.getJSONObject(g).getString("location");

                        if (tinyDB.getBoolean("isStaffAccount")) {

                            if (alternateRoute.getJSONObject(g).getString("session").equals("Morning Pickup"))
                                saveAlternatePickups(date,loc, selectedLatLong.get(g));
                            else
                                saveAlternateDrop(date,loc, selectedLatLong.get(g));

                        } else {

                            if (alternateRoute.getJSONObject(g).getString("session").equals("Morning Pickup"))
                                saveAlternatePickupsKid(date,loc, selectedLatLong.get(g));
                            else
                                saveAlternateDropKid(date,loc, selectedLatLong.get(g));
                        }


                    }
                    alternateRoute= new JSONArray();
                    recycler_alternate_dates.getAdapter().notifyDataSetChanged();
                    btn_save_alternate.setVisibility(View.INVISIBLE);
                    showToast(dialog,"Alternate routes added",2);
                    txt_date.setText("Select Date");
                    txt_location.setText("Select Location");
                    btn_evening.setBackground(getDrawable(R.drawable.custom_edittext));
                    btn_morning.setBackground(getDrawable(R.drawable.custom_edittext));
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
        findViewById(R.id.card_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateMapPopup(view, savedInstanceState);
            }
        });
        btn_morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSession.equals("Evening Drop-off") || selectedSession.equals("")) {
                    selectedSession = "Morning Pickup";
                    btn_morning.setBackground(getDrawable(R.drawable.bg_absence_date));
                    btn_evening.setBackground(getDrawable(R.drawable.custom_edittext));
                }
            }
        });
        btn_evening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedSession.equals("Morning Pickup") || selectedSession.equals("")) {
                    selectedSession = "Evening Drop-off";
                    btn_evening.setBackground(getDrawable(R.drawable.bg_absence_date));
                    btn_morning.setBackground(getDrawable(R.drawable.custom_edittext));
                }
            }
        });
        recycler_alternate_dates.setAdapter(new AlternativeAbsentAdapter());
        recycler_alternate_dates.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

    }

    @VisibleForTesting
    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(this)
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .showDaySpinner(true)
                .minDate(year, month, day)

                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        txt_date.setText(simpleDateFormat.format(calendar.getTime()));


    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//   //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//add network call here //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//      //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//           ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //
    private void saveAlternatePickups(String date, String location, LatLng loc) {

        Log.e("morning", "exe");
        HashMap<String, String> tempParam = new HashMap<>();

        tempParam.put("latitude", String.valueOf(loc.latitude));
        tempParam.put("longitude", String.valueOf(loc.longitude));
        tempParam.put("date", date);
        tempParam.put("add1", location);
        tempParam.put("repeat_every_week", "0");


        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.SET_ALTERNATE_PICKUP_), 2, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    if (result.getBoolean("status")) {
                        Log.e("resPONSE", result + "");

                    } else {
                        showToast(dialog, result.getString("validation_errors"), 0);

                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

    }

    private void saveAlternatePickupsKid(String date, String location, LatLng loc) {
        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("id", tinyDB.getString("selectedChildId"));


        tempParam.put("latitude", String.valueOf(loc.latitude));
        tempParam.put("longitude", String.valueOf(loc.longitude));
        tempParam.put("date", date);
        tempParam.put("add1", location);
        tempParam.put("repeat_every_week", "0");

        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.SET_ALTERNATE_PICKUP_KID), 2, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    if (result.getBoolean("status")) {
                        Log.e("resPONSE", result + "");

                    } else {
                        showToast(dialog, result.getString("validation_errors"), 0);

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

    }

    private void saveAlternateDrop(String date, String location, LatLng loc) {

        Log.e("evening", "exe");
        HashMap<String, String> tempParam = new HashMap<>();


        tempParam.put("latitude", String.valueOf(loc.latitude));
        tempParam.put("longitude", String.valueOf(loc.longitude));
        tempParam.put("date", date);
        tempParam.put("add1", location);
        tempParam.put("repeat_every_week", "0");

        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.SET_ALTERNATE_DROP), 2, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {


                    if (result.getBoolean("status")) {
                        Log.e("resPONSE", result + "");

                    } else {
                        showToast(dialog, result.getString("validation_errors"), 0);

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

    }

    private void saveAlternateDropKid(String date, String location, LatLng loc) {

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("id", tinyDB.getString("selectedChildId"));
        tempParam.put("date", date);
        tempParam.put("add1", location);
        tempParam.put("latitude", String.valueOf(loc.latitude));
        tempParam.put("longitude", String.valueOf(loc.longitude));

        tempParam.put("repeat_every_week", "0");

        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.SET_ALTERNATE_DROP_KID), 2, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    if (result.getBoolean("status")) {
                        Log.e("resPONSE", result + "");

                    } else {
                        showToast(dialog, result.getString("validation_errors"), 0);

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

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


        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        popupView = inflater.inflate(R.layout.activity_map, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, GetSafeBase.device_width - 150, GetSafeBase.device_height - 250, true);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        dimBehind(popupWindow);

        mConfirm = popupView.findViewById(R.id.btn_confirmMapLocation);
        mPickupLocation = popupView.findViewById(R.id.map_pickupLocation);


        try {
            MapsInitializer.initialize(getApplicationContext());
            loadMap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPickupLocation.onCreate(savedInstanceState);
        mPickupLocation.onResume();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);


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
                showAddressPopup(popupWindow);

//                txtParentAddress.setText(GetSafeBase.LOC_ADDRESS);

            }
        });

    }

    public void locationAddress(double lat, double lon) {
        Geocoder geocoder;
        List<Address> addressList;

        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

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

    public void loadMap() {
        mPickupLocation.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                try {
                    googleMap = mMap;
                    googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    getApplicationContext(), R.raw.dark_map));
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);


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
                            pinnedLocation = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);


                            latitude = cameraPosition.target.latitude;
                            longitude = cameraPosition.target.longitude;


                        }
                    });

                } catch (Exception e) {

                }


            }
        });
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        } else {
            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }


    private void addDate() {
        boolean isAdded = false;
        try {
            if (selectedSession.equals(""))
                showToast(dialog, "Please select session", 0);
            else if (txt_date.getText().toString().equals("Select Date"))
                showToast(dialog, "Please select date", 0);
            else if (txt_location.getText().toString().equals("Select Location"))
                showToast(dialog, "Please select location", 0);

            else {
                singleAlternate = new JSONObject();
                singleAlternate.put("date", txt_date.getText().toString());
                singleAlternate.put("location", txt_location.getText().toString());
                singleAlternate.put("session", selectedSession);
                for (int i = 0; i < alternateRoute.length(); i++) {
                    if (alternateRoute.getJSONObject(i).getString("date").equals(singleAlternate.getString("date")) & alternateRoute.getJSONObject(i).getString("session").equals(singleAlternate.getString("session"))) {

                        showToast(dialog, "Alternate route already added", 0);
                        isAdded = true;
                    }


                }
                if (!isAdded) {
                    btn_save_alternate.setVisibility(View.VISIBLE);
                    alternateRoute.put(singleAlternate);
                    selectedLatLong.add(new LatLng(latitude, longitude));
                    recycler_alternate_dates.getAdapter().notifyDataSetChanged();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void showAddressPopup(final PopupWindow popupWindow) {


        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setTitle(null);

        dialog.setContentView(R.layout.address_popup);


        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(true);

        dialog.getWindow().getAttributes().windowAnimations = R.style.CalendarAnimation;

        final EditText txtAddOne = dialog.findViewById(R.id.txt_add_one);
        final EditText txtAddTwo = dialog.findViewById(R.id.txt_add_two);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (TextUtils.isEmpty(txtAddOne.getText().toString())) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txtAddOne);
                    txtAddOne.setError("Please enter address line one");
                    txtAddOne.requestFocus(0);
                } else if (TextUtils.isEmpty(txtAddTwo.getText().toString())) {


                    YoYo.with(Techniques.Bounce)
                            .duration(1000)
                            .playOn(txtAddTwo);
                    txtAddTwo.setError("Please enter address line two");
                    txtAddTwo.requestFocus(0);
                } else {


                    txt_location.setText(txtAddOne.getText().toString() + " " + txtAddTwo.getText().toString());


                    popupWindow.dismiss();
                    dialog.dismiss();
                }


            }
        });


        dialog.show();
    }


    class AlternativeAbsentHolder extends RecyclerView.ViewHolder {
        TextView alter_session, alter_date, alter_add;
        ImageView btn_delete;

        public AlternativeAbsentHolder(@NonNull View itemView) {
            super(itemView);
            alter_add = itemView.findViewById(R.id.alter_add);
            alter_date = itemView.findViewById(R.id.alter_date);
            alter_session = itemView.findViewById(R.id.alter_session);
            btn_delete = itemView.findViewById(R.id.btn_delete);

        }
    }

    class AlternativeAbsentAdapter extends RecyclerView.Adapter<AlternativeAbsentHolder> {

        @NonNull
        @Override
        public AlternativeAbsentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_alternate, parent, false);
            return new AlternativeAbsentHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AlternativeAbsentHolder holder, final int position) {

            try {
                holder.alter_add.setText(alternateRoute.getJSONObject(position).getString("location"));
                holder.alter_session.setText(alternateRoute.getJSONObject(position).getString("session"));
                holder.alter_date.setText(alternateRoute.getJSONObject(position).getString("date"));

                holder.btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alternateRoute.remove(position);
                        if(alternateRoute.length()==0)
                            btn_save_alternate.setVisibility(View.INVISIBLE);
                       notifyDataSetChanged();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return alternateRoute.length();
        }
    }


}