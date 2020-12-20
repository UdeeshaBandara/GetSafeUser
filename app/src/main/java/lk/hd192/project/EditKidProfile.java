package lk.hd192.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import lk.hd192.project.Utils.GetSafeBase;

public class EditKidProfile extends GetSafeBase implements DatePickerDialog.OnDateSetListener {
    View popupView;
    int year;
    int month;
    int day;
    SimpleDateFormat simpleDateFormat;
    TextView txtSchoolName, txtBirthday, txtPickupAddress, txtKidName, txtHeading, txtBottomSheetSearch;
    EditText editTxtKidName;
    LinearLayout lnrLocation, lnrBirthday, lnrSchool;
    Dialog dialog;
    SchoolBottomSheet schoolBottomSheet;
    RecyclerView bottomSheetRecycler;
    Button mConfirm;
    ImageView imgLocEditIndicator, imgBirthdayEditIndicator, imgSchoolEditIndicator, imgUpdateImage, imgKid;
    GoogleMap googleMap;
    String locationProvider = LocationManager.GPS_PROVIDER;
    CameraPosition cameraPosition;
    MapView mPickupLocation;
    public static LatLng pinnedLocation;
    Button btnEditDone;
    private ProgressDialog progressDialog;
    String originalName = "Udeesha Induras Bandara Kalumahanage", originalNumber = "0774787978", originalEmail = "udeeshabandara@gmail.com", originalAddress = "Gajaba road,makola";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_kid_profile);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        txtSchoolName = findViewById(R.id.txt_school_name);
        txtBirthday = findViewById(R.id.txt_birthday);
        txtPickupAddress = findViewById(R.id.txt_pickup_address);
        txtKidName = findViewById(R.id.txt_kid_name);
        imgBirthdayEditIndicator = findViewById(R.id.img_birthday_edit_indicator);
        imgLocEditIndicator = findViewById(R.id.img_loc_edit_indicator);
        imgSchoolEditIndicator = findViewById(R.id.img_school_edit_indicator);
        imgUpdateImage = findViewById(R.id.img_update_image);

        lnrLocation = findViewById(R.id.lnr_location);
        lnrBirthday = findViewById(R.id.lnr_birthday);
        lnrSchool = findViewById(R.id.lnr_school);
        btnEditDone = findViewById(R.id.btn_edit_done);
        imgKid = findViewById(R.id.img_kid);

        editTxtKidName = findViewById(R.id.edit_txt_kid_name);
        txtHeading = findViewById(R.id.txt_heading);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");


        //add init network call
        setOriginalValues();

        imgKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEditDone.getText().toString().equals("Done")) {
                    Intent gallery = new Intent();
                    gallery.setType("image/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), 1);
                    View view = EditKidProfile.this.getCurrentFocus();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }

            }
        });

        lnrSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnEditDone.getText().toString().equals("Done")) {
                    openBottomSheet();
                    View view = EditKidProfile.this.getCurrentFocus();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        editTxtKidName.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {

                        if (cs.equals("")) {
                            return cs;
                        }
                        if (cs.toString().matches("[a-zA-Z ]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });
        editTxtKidName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("\\s+", " ");
                if (!s.toString().equals(result)) {
                    editTxtKidName.setText(result);
                    editTxtKidName.setSelection(result.length());

                }
            }
        });

        txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnEditDone.getText().toString().equals("Done")) {
                    showDate(year, month, day, R.style.DatePickerSpinner);
                    View view = EditKidProfile.this.getCurrentFocus();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        lnrLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEditDone.getText().toString().equals("Done")) {
                    onCreateMapPopup(v, savedInstanceState);
                    View view = EditKidProfile.this.getCurrentFocus();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        btnEditDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEditDone.getText().equals("Edit")) {

                    btnEditDone.setText("Done");
                    txtHeading.setText("Update Kid's details");

                    editTxtKidName.setVisibility(View.VISIBLE);
                    editTxtKidName.setText(txtKidName.getText());
                    imgBirthdayEditIndicator.setVisibility(View.VISIBLE);
                    imgLocEditIndicator.setVisibility(View.VISIBLE);
                    imgSchoolEditIndicator.setVisibility(View.VISIBLE);
                    imgUpdateImage.setVisibility(View.VISIBLE);
//                    editTxtEmail.setVisibility(View.VISIBLE);
//                    editTxtEmail.setText(txtEmail.getText());
//                    editTxtPhoneNumber.setVisibility(View.VISIBLE);
//                    editTxtPhoneNumber.setText(txtPhoneNumber.getText());

                    txtKidName.setVisibility(View.GONE);
//                    txtEmail.setVisibility(View.GONE);
//                    txtPhoneNumber.setVisibility(View.GONE);


                } else {
                    btnEditDone.setText("Edit");
                    txtHeading.setText("Kid details");

                    updateUserWithNewValues();
                    editTxtKidName.setVisibility(View.GONE);

                    imgBirthdayEditIndicator.setVisibility(View.GONE);
                    imgLocEditIndicator.setVisibility(View.GONE);
                    imgSchoolEditIndicator.setVisibility(View.GONE);
                    imgUpdateImage.setVisibility(View.GONE);
//                    editTxtEmail.setVisibility(View.GONE);
//                    editTxtPhoneNumber.setVisibility(View.GONE);
                    txtKidName.setVisibility(View.VISIBLE);
//                    txtEmail.setVisibility(View.VISIBLE);
//                    txtPhoneNumber.setVisibility(View.VISIBLE);


                    hideKeyboard();

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri imgURL = data.getData();
            //set aspect ratio of image
            CropImage.activity(imgURL).setAspectRatio(1, 1).start(this);


        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                progressDialog = new ProgressDialog(EditKidProfile.this);
                progressDialog.setTitle("Uploading Image");
                progressDialog.setMessage("Please wait while we uploading the image");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                try {
                    Uri resultUri = result.getUri();
                    final InputStream imageStream;
                    imageStream = getContentResolver().openInputStream(resultUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imgKid.setImageBitmap(selectedImage);
                    progressDialog.dismiss();
                } catch (Exception e) {

                }


//                StorageReference filePath = imageReference.child("profile_images").child(firebaseUser.getUid() + ".jpg");
//                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//
//                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                downUrl = uri.toString();
//                                Log.e("Down url", downUrl);
//                                if (!TextUtils.isEmpty(downUrl))
//                                    userDatabase.child("Image").setValue(downUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                        @Override
//                                        public void onSuccess(Void aVoid) {
//
//                                            progressDialog.dismiss();
//                                            Toast.makeText(SettingsActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
//                                        }
//
//                                    });
//
//                            }
//                        });
//
//
//                    }
//                });


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    void showDate(int year, int monthOfYear, int dayOfMonth, int spinnerTheme) {
        new SpinnerDatePickerDialogBuilder()
                .context(EditKidProfile.this)
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
        txtBirthday.setText(simpleDateFormat.format(calendar.getTime()));

    }

    private void hideKeyboard() {
        editTxtKidName.onEditorAction(EditorInfo.IME_ACTION_DONE);
//        editTxtBirthday.onEditorAction(EditorInfo.IME_ACTION_DONE);
//        editTxtParentName.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    private void setOriginalValues() {

        //add init network call to get already exist details
        txtKidName.setText(originalName);
//        txtPhoneNumber.setText(originalNumber);
//        txtEmail.setText(originalEmail);
//        txtParentAddress.setText(originalAddress);


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
                txtPickupAddress.setText(GetSafeBase.LOC_ADDRESS);
                popupWindow.dismiss();
            }
        });

    }


    private void updateUserWithNewValues() {
        //add network call inside last if to update user with new values
//        boolean isValidName=true,isValidNumber=true,isValidEmail=true;
        Log.e("updateUserWithNewValues", "executed");

        if (TextUtils.isEmpty(editTxtKidName.getText().toString()) | editTxtKidName.getText().toString().equals(" ") | editTxtKidName.getText().length() < 3) {

//            isValidName=false;
            showWarningToast(dialog, "Your name is not valid", 0);
            txtKidName.setText(originalName);

        } else
            txtKidName.setText(editTxtKidName.getText());

//        if (TextUtils.isEmpty(edi.getText().toString()) | editTxtPhoneNumber.getText().length() < 10) {
////            isValidNumber=false;
//            showWarningToast(dialog, "Your phone number is not valid", 0);
//            txtPhoneNumber.setText(originalNumber);
//
//        } else
//            txtPhoneNumber.setText(editTxtPhoneNumber.getText());
//
//        if (TextUtils.isEmpty(editTxtEmail.getText().toString()) | !Patterns.EMAIL_ADDRESS.matcher(editTxtEmail.getText()).matches()) {
////            isValidEmail=false;
//            showWarningToast(dialog, "Your email is not valid", 0);
//            txtEmail.setText(originalEmail);
//
//        } else txtEmail.setText(editTxtEmail.getText());


    }

    private class SchoolBottomSheet extends BottomSheetDialog {


        public SchoolBottomSheet(Context context) {
            super(context);

        }

        @Override
        protected void onStart() {
            super.onStart();
            getBehavior().setPeekHeight((GetSafeBase.device_height / 3) * 2, false);
            getBehavior().setDraggable(false);


        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            bottomSheetRecycler = findViewById(R.id.bottom_sheet_recycle_view);
            txtBottomSheetSearch = findViewById(R.id.school_search);

            bottomSheetRecycler.setAdapter(new SchoolAdapter());

            bottomSheetRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
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


        schoolBottomSheet = new SchoolBottomSheet(this);
        schoolBottomSheet.setContentView(R.layout.schools_bottom_sheet);
        schoolBottomSheet.show();

    }

    public class SchoolNameViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout rltSchool;

        public SchoolNameViewHolder(@NonNull View itemView) {
            super(itemView);
            rltSchool = itemView.findViewById(R.id.rlt_school);
        }
    }

    public class SchoolAdapter extends RecyclerView.Adapter<SchoolNameViewHolder> implements Filterable {

        @NonNull
        @Override
        public SchoolNameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_school_name, parent, false);
            return new SchoolNameViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull SchoolNameViewHolder holder, int position) {

            holder.rltSchool.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtSchoolName.setText("Mahanama College");
                    txtSchoolName.setHint("Mahanama College");
                    schoolBottomSheet.dismiss();
                }
            });
        }


        @Override
        public int getItemCount() {
            return 20;
        }

        @Override
        public Filter getFilter() {
            return null;
        }

        //Home screen search - filter stories by story name or category name  #Udeesha
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                //   schoolsArr = new JSONArray();
                int put = 0;

                try {
//                    for (int i = 0; i < originalResult.length(); i++) {
//                        if (originalResult.getJSONObject(i).getString("name_en").toLowerCase().contains(constraint.toString().toLowerCase()) ||
//                                originalResult.getJSONObject(i).getString("category_name").toLowerCase().contains(constraint.toString().toLowerCase())) {
//
//                            schoolsArr.put(put++, originalResult.getJSONObject(i));
//                        }
//
//                    }

                } catch (Exception e) {

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