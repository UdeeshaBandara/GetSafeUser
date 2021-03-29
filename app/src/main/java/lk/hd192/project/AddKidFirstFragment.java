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
import androidx.annotation.VisibleForTesting;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
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


public class AddKidFirstFragment extends GetSafeBaseFragment implements DatePickerDialog.OnDateSetListener {
    int year;
    int month;
    int day;
    Dialog dialog;
    TextView calenderBirthday, txtSchoolName, txtBottomSheetSearch;
    EditText txtFirstName, txtLastName;
    SimpleDateFormat simpleDateFormat;
    RecyclerView bottomSheetRecycler;
    SchoolBottomSheet schoolBottomSheet;
    GetSafeServices getSafeServices;
    RadioGroup rbnGrpGender;
    TinyDB tinyDB;
    LocationManager locationManager;
    AddNewKid addNewKid;
    View popupView;
    Button mConfirm;
    Double latitude, longitude;
    JSONArray schoolList, originalSchoolList;
    MapView mPickupLocation;
    GoogleMap googleMap;
    String locationProvider = LocationManager.GPS_PROVIDER;
    CameraPosition cameraPosition;

    public AddKidFirstFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_kid_first, container, false);
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        calenderBirthday = view.findViewById(R.id.calender_birthday);
        txtSchoolName = view.findViewById(R.id.txt_school_name);
        txtFirstName = view.findViewById(R.id.txt_first_name);

        txtLastName = view.findViewById(R.id.txt_last_name);
        rbnGrpGender = view.findViewById(R.id.rbn_grp_gender);
        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getActivity());
        schoolList = new JSONArray();
        originalSchoolList = new JSONArray();


        try {
//            schools = new JSONObject(readFile());

        } catch (Exception e) {

        }

        if (AddNewKid.isEditing) {


            txtFirstName.setText(AddNewKid.FirstName);
            txtLastName.setText(AddNewKid.LastName);
            txtSchoolName.setText(AddNewKid.SchoolName);
            calenderBirthday.setText(AddNewKid.Birthday);

        }
        addNewKid = new AddNewKid();
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");


        calenderBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDate(year, month, day, R.style.DatePickerSpinner);
            }
        });

        txtSchoolName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
                getSchoolList();
            }
        });


        rbnGrpGender.clearCheck();
        rbnGrpGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbn_gender_male:
                        AddNewKid.Gender = "male";
                        break;
                    case R.id.rbn_gender_female:
                        AddNewKid.Gender = "female";
                        break;
                }
            }
        });
        return view;

    }

    public void validateFields() {

        if (txtFirstName.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(txtFirstName);
            txtFirstName.setError("Please enter kid first name");
            AddNewKid.firstCompleted = false;

        } else if (txtLastName.getText().toString().isEmpty()) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(txtLastName);
            txtLastName.setError("Please enter kid last name");
            AddNewKid.firstCompleted = false;

        } else if (txtSchoolName.getText().toString().equals("School Name")) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(txtSchoolName);
            txtSchoolName.setError("Please select school name");
            AddNewKid.firstCompleted = false;

        } else if (AddNewKid.Gender.equals("null")) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(rbnGrpGender);
            showWarningToast(dialog, "Please select gender", 0);

            AddNewKid.firstCompleted = false;

        } else if (calenderBirthday.getText().toString().equals("Birthday")) {
            YoYo.with(Techniques.Bounce)
                    .duration(1000)
                    .playOn(calenderBirthday);
            calenderBirthday.setError("Please select kid's birthday");
            AddNewKid.firstCompleted = false;
        } else {
            AddNewKid.firstCompleted = true;


        }
    }


    public void addKidBasicDetails() {
        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("name", txtFirstName.getText().toString() + " " + txtLastName.getText().toString());
        tempParam.put("school-name", txtSchoolName.getText().toString());
        tempParam.put("birthday", calenderBirthday.getText().toString());
        tempParam.put("gender", AddNewKid.Gender);
        tempParam.put("guardian", "");

        ((AddNewKid) Objects.requireNonNull(getActivity())).showLoading();
        getSafeServices.networkJsonRequest(getActivity(), tempParam, getString(R.string.BASE_URL) + getString(R.string.ADD_NEW_KID), 2, tinyDB.getString("token"),
                new VolleyJsonCallback() {

                    @Override
                    public void onSuccessResponse(JSONObject result) {
                        ((AddNewKid) Objects.requireNonNull(getActivity())).hideLoading();
                        try {
                            Log.e("loc response", result + "");

                            if (result.getBoolean("saved_status")) {

                                AddNewKid.kidId = result.getJSONObject("child").getString("id");
                                AddNewKid.FirstName = txtFirstName.getText().toString();
                                AddNewKid.LastName = txtLastName.getText().toString();
                                AddNewKid.SchoolName = txtSchoolName.getText().toString();
                                AddNewKid.Birthday = calenderBirthday.getText().toString();
//                                addKidDropDetails();


                            } else
                                showWarningToast(dialog, result.getString("validation_errors"), 0);


                        } catch (Exception e) {
                            addNewKid.hideLoading();
                            Log.e("ex loc", e.getMessage());

                            showWarningToast(dialog, "Something went wrong. Please try again", 0);

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

                popupWindow.dismiss();
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


                    cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {

                            locationAddress(cameraPosition.target.latitude, cameraPosition.target.longitude);
                            latitude = cameraPosition.target.latitude;
                            longitude = cameraPosition.target.longitude;
//                            pinnedLocation = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);


                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();

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



    public void getSchoolList() {
        HashMap<String, String> tempParam = new HashMap<>();

//        ((AddNewKid) Objects.requireNonNull(getActivity())).showLoading();
        getSafeServices.networkJsonRequest(getActivity(), tempParam, getString(R.string.BASE_URL) + getString(R.string.SCHOOL_LIST), 1, tinyDB.getString("token"),
                new VolleyJsonCallback() {

                    @Override
                    public void onSuccessResponse(JSONObject result) {
                        ((AddNewKid) Objects.requireNonNull(getActivity())).hideLoading();
                        try {


                            if (!result.getBoolean("status")) {

                                schoolList = result.getJSONArray("model");
                                originalSchoolList = result.getJSONArray("model");
                                bottomSheetRecycler.getAdapter().notifyDataSetChanged();

                            } else
                                showWarningToast(dialog, "Schoolist" + result.getString("validation_errors"), 0);


                        } catch (Exception e) {

                            Log.e("ex loc", e.getMessage());

                            showWarningToast(dialog, "Something went wrong. Please try again", 0);

                        }

                    }
                });

    }

    @VisibleForTesting
    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(getActivity())
                .callback(this)
                .spinnerTheme(spinnerTheme)
                .showDaySpinner(true)
                .maxDate(year - 4, month, day)
                .minDate(year - 20, month, day)
                .defaultDate(year, monthOfYear, dayOfMonth)
                .build()
                .show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        calenderBirthday.setText(simpleDateFormat.format(calendar.getTime()));

    }

    private class SchoolBottomSheet extends BottomSheetDialog {


        public SchoolBottomSheet(Context context) {
            super(context);

        }

        @Override
        protected void onStart() {
            super.onStart();


        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            bottomSheetRecycler = findViewById(R.id.bottom_sheet_recycle_view);
            txtBottomSheetSearch = findViewById(R.id.school_search);

            bottomSheetRecycler.setAdapter(new SchoolAdapter());

            bottomSheetRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            txtBottomSheetSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                    if (txtBottomSheetSearch.getText().toString().equals("")) {
                        //  schoolsArr = originalResult;
                        bottomSheetRecycler.getAdapter().notifyDataSetChanged();
                    } else {

                        SchoolAdapter homeAdapter = new SchoolAdapter();
                        homeAdapter.getFilter().filter(s);
                    }

                }
            });


        }
    }

    public void openBottomSheet() {


        schoolBottomSheet = new SchoolBottomSheet(getActivity());
        schoolBottomSheet.setContentView(R.layout.schools_bottom_sheet);
        schoolBottomSheet.show();

    }

    public class SchoolNameViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rltSchool;
        TextView textSchoolName;

        public SchoolNameViewHolder(@NonNull View itemView) {
            super(itemView);
            rltSchool = itemView.findViewById(R.id.rlt_school);
            textSchoolName = itemView.findViewById(R.id.text_school_name);
        }
    }

    public class SchoolAdapter extends RecyclerView.Adapter<SchoolNameViewHolder> implements Filterable {

        @NonNull
        @Override
        public SchoolNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
                    .inflate(R.layout.item_school_name, parent, false);
            return new SchoolNameViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull SchoolNameViewHolder holder, final int position) {

            try {
                holder.textSchoolName.setText(schoolList.getJSONObject(position).getString("school_name"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            holder.rltSchool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        txtSchoolName.setText(schoolList.getJSONObject(position).getString("school_name"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    schoolBottomSheet.dismiss();
                }
            });
        }

        @Override
        public int getItemCount() {
            return schoolList.length();
        }

        @Override
        public Filter getFilter() {
            return filter;
        }

        //Home screen search - filter stories by story name or category name  #Udeesha
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                schoolList = new JSONArray();
                int put = 0;

                try {
                    for (int i = 0; i < originalSchoolList.length(); i++) {
                        if (originalSchoolList.getJSONObject(i).getString("school_name").toLowerCase().contains(constraint.toString().toLowerCase())) {

                            schoolList.put(put++, originalSchoolList.getJSONObject(i));
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }


                return null;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                bottomSheetRecycler.getAdapter().notifyDataSetChanged();

            }
        };

    }


}
