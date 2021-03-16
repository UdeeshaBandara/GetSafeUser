package lk.hd192.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.auth.FirebaseAuth;
//import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class EditProfile extends GetSafeBase {
    View popupView;
    RecyclerView recyclerKidList;
    TextView txtPhoneNumber, txtEmail, txtParentAddress, txtParentDropAddress, txtParentName, txtHeading;
    EditText editTxtPhoneNumber, editTxtEmail, editTxtParentName;
    LinearLayout lnrLocation, locationDropMain;
    Dialog dialog;
    Button mConfirm;
    GoogleMap googleMap;
    TinyDB tinyDB;
    View view;
    LottieAnimationView loading;
    String locationProvider = LocationManager.GPS_PROVIDER;
    CameraPosition cameraPosition;
    MapView mPickupLocation;
    JSONArray kidList;
    GetSafeServices getSafeServices;
    private ProgressDialog progressDialog;
    ImageView imgUpdateImage, imgLocEditIndicator, imgDropLocEditIndicator, imgParent;
    public static LatLng pinnedLocation, pinnedDropLocation;
    Button btnEditDone;
    Boolean isDropLocation;
    Double latitude, dropLatitude, longitude, dropLongitude;
    public static boolean needToEnableEditMode;
    String imgDecodableString = "", originalName, originalNumber, originalEmail, originalAddressOne, originalAddressTwo, originalDropAddressOne, originalDropAddressTwo;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tinyDB = new TinyDB(getApplicationContext());
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
//        tinyDB.putString("token","1|q2pWwtm1SMNxGFSk9tzUkvB2cAGWPVK1zZ4e014y");
        kidList = new JSONArray();
        getSafeServices = new GetSafeServices();
        recyclerKidList = findViewById(R.id.recycler_kid_list);
        txtPhoneNumber = findViewById(R.id.txt_phone_number);
        editTxtPhoneNumber = findViewById(R.id.edit_txt_phone_number);
        editTxtEmail = findViewById(R.id.edit_txt_email);
        txtEmail = findViewById(R.id.txt_email);
        txtParentAddress = findViewById(R.id.txt_parent_address);
        lnrLocation = findViewById(R.id.lnr_location);
        locationDropMain = findViewById(R.id.location_drop_main);
        btnEditDone = findViewById(R.id.btn_edit_done);
        editTxtParentName = findViewById(R.id.edit_txt_parent_name);
        txtParentName = findViewById(R.id.txt_parent_name);
        txtHeading = findViewById(R.id.txt_heading);
        imgUpdateImage = findViewById(R.id.img_update_image);
        imgLocEditIndicator = findViewById(R.id.img_loc_edit_indicator);
        imgDropLocEditIndicator = findViewById(R.id.img_drop_loc_edit_indicator);
        imgParent = findViewById(R.id.img_parent);
        txtParentDropAddress = findViewById(R.id.txt_parent_drop_address);

        loading = findViewById(R.id.loading);
        view = findViewById(R.id.disable_layout);


        if (tinyDB.getBoolean("isStaffAccount"))
            locationDropMain.setVisibility(View.VISIBLE);
        else
            locationDropMain.setVisibility(View.GONE);


        //init network call to get already exist details
        loadUserDetails();

        recyclerKidList.setAdapter(new StudentItemAdapter());
        recyclerKidList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


        btnEditDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("performClick", "performClick");
                if (btnEditDone.getText().equals("Edit")) {

                    btnEditDone.setText("Done");
                    txtHeading.setText("Update your details");

                    editTxtParentName.setVisibility(View.VISIBLE);
                    editTxtParentName.setText(txtParentName.getText());
                    editTxtEmail.setVisibility(View.VISIBLE);
                    editTxtEmail.setText(txtEmail.getText());
                    editTxtPhoneNumber.setVisibility(View.VISIBLE);
                    editTxtPhoneNumber.setText(txtPhoneNumber.getText());
                    imgUpdateImage.setVisibility(View.VISIBLE);
                    imgLocEditIndicator.setVisibility(View.VISIBLE);
                    imgDropLocEditIndicator.setVisibility(View.VISIBLE);
                    txtParentName.setVisibility(View.GONE);
                    txtEmail.setVisibility(View.GONE);
                    txtPhoneNumber.setVisibility(View.GONE);


                } else {
                    btnEditDone.setText("Edit");
                    txtHeading.setText("Your details");

                    validateUserNewValues();
                    imgUpdateImage.setVisibility(View.GONE);
                    editTxtParentName.setVisibility(View.GONE);
                    editTxtEmail.setVisibility(View.GONE);
                    imgLocEditIndicator.setVisibility(View.INVISIBLE);
                    imgDropLocEditIndicator.setVisibility(View.INVISIBLE);
                    editTxtPhoneNumber.setVisibility(View.GONE);
                    txtParentName.setVisibility(View.VISIBLE);
                    txtEmail.setVisibility(View.VISIBLE);
                    txtPhoneNumber.setVisibility(View.VISIBLE);


                    hideKeyboard();

                }
            }
        });
        if (needToEnableEditMode) {
//            btnEditDone.setText("Done");
            Log.e("needToEnableEditMode", needToEnableEditMode + "");
            btnEditDone.performClick();
            needToEnableEditMode = false;
        }
        editTxtParentName.setFilters(new InputFilter[]{
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

        editTxtParentName.addTextChangedListener(new TextWatcher() {
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
                    editTxtParentName.setText(result);
                    editTxtParentName.setSelection(result.length());

                }
            }
        });
        editTxtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String result = s.toString().replaceAll("\\s+", "");
                if (!s.toString().equals(result)) {
                    editTxtEmail.setText(result);
                    editTxtEmail.setSelection(result.length());

                }
                String result2 = s.toString().replaceAll(" ", "");
                if (!s.toString().equals(result2)) {
                    editTxtEmail.setText(result2);
                    editTxtEmail.setSelection(result2.length());

                }
            }
        });
        lnrLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEditDone.getText().toString().equals("Done")) {
                    isDropLocation = false;
                    onCreateMapPopup(v, savedInstanceState);
                    View view = EditProfile.this.getCurrentFocus();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });
        locationDropMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEditDone.getText().toString().equals("Done")) {
                    isDropLocation = true;
                    onCreateMapPopup(v, savedInstanceState);
                    View view = EditProfile.this.getCurrentFocus();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });

        findViewById(R.id.img_loc_edit_indicator).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEditDone.getText().toString().equals("Done")) {
                    isDropLocation = false;
                    onCreateMapPopup(v, savedInstanceState);

                    View view = EditProfile.this.getCurrentFocus();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        });

        imgParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnEditDone.getText().toString().equals("Done")) {
//                    selectImage(EditProfile.this);
                    Intent gallery = new Intent();
                    gallery.setType("image/*");
                    gallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(gallery, "SELECT IMAGE"), 1);
                    View view = EditProfile.this.getCurrentFocus();

                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }

            }
        });

        getAllChildren();

    }


    @Override
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
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                    showToast(dialog, "Permission denied.\nCannot open camera", 0);

                } else {
                    if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
//                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(takePicture, 0);


                    } else {
                        //set to never ask again

                        somePermissionsForeverDenied = true;
                    }
                }
            }
            if (somePermissionsForeverDenied) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Permissions Required")
                        .setMessage("You have forcefully denied some of the required permissions " +
                                "for this action. Please open settings, go to permissions and allow them.")
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                        Uri.fromParts("package", getPackageName(), null));
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
                //act according to the request code used while requesting the permission(s).
                case 23:
                    Intent m_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
                    Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
                    m_intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(m_intent, 0);
                    break;
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("onactivityResult", data + "");
//
//        if ((requestCode == 1 | requestCode == 0) & resultCode == RESULT_OK & null != data) {
//            Uri imgURL;
//            Log.e("inside big if", "ok");
//            //set aspect ratio of image
//            if (resultCode != RESULT_CANCELED) {
//                Log.e("inside small if", "ok");
//                switch (requestCode) {
//                    case 0:
//                        if (resultCode == RESULT_OK && data != null) {
//                            //File object of camera image
//                            File file = new File(Environment.getExternalStorageDirectory(), "MyPhoto.jpg");
//
//                            //Uri of camera image
//                            Uri uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
//
//                            Log.e("CropImage", "before");
//
//                            CropImage.activity(uri).setAspectRatio(1, 1).start(this);
//
//
//                            //  imgParent.setImageBitmap(selectedImage);
//                        }
//
//                        break;
//                    case 1:
//                        if (data != null) {
//                            imgURL = data.getData();
//                            Log.e("URL gallery", imgURL + "");
//                            CropImage.activity(imgURL).setAspectRatio(1, 1).start(this);
//
////                            String[] filePathColumn = {MediaStore.Images.Media.DATA};
////                            if (selectedImage != null) {
////                                Cursor cursor = getContentResolver().query(selectedImage,
////                                        filePathColumn, null, null, null);
////                                if (cursor != null) {
////                                    cursor.moveToFirst();
////
////                                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
////                                    String picturePath = cursor.getString(columnIndex);
////                                    imgParent.setImageBitmap(BitmapFactory.decodeFile(picturePath));
////                                    cursor.close();
////                                }
////                            }
//
//                        }
//                        break;
//                }
//            }
//
//
//        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                progressDialog = new ProgressDialog(EditProfile.this);
//                progressDialog.setTitle("Uploading Image");
//                progressDialog.setMessage("Please wait while we uploading the image");
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.show();
//
//            }
//
//            try {
//                Uri resultUri = result.getUri();
//                final InputStream imageStream;
//                imageStream = getContentResolver().openInputStream(resultUri);
//                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                imgParent.setImageBitmap(selectedImage);
//
//                progressDialog.dismiss();
//                updateImage(selectedImage);
//            } catch (Exception e) {
//
//            }
//        }
    }

    private void updateImage(Bitmap selectedImage) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();

        //Use your Base64 String as you wish
        String encodedString = Base64.encodeToString(byteArray, Base64.DEFAULT);


    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1 & resultCode == RESULT_OK & null != data) {
//            Uri imgURL = data.getData();
//            //set aspect ratio of image
//            CropImage.activity(imgURL).setAspectRatio(1, 1).start(this);
//
//
//        }
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                progressDialog = new ProgressDialog(EditProfile.this);
//                progressDialog.setTitle("Uploading Image");
//                progressDialog.setMessage("Please wait while we uploading the image");
//                progressDialog.setCanceledOnTouchOutside(false);
//                progressDialog.show();
//
//
//                try {
//                    Uri resultUri = result.getUri();
//                    final InputStream imageStream;
//                    imageStream = getContentResolver().openInputStream(resultUri);
//                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                    imgParent.setImageBitmap(selectedImage);
//                    progressDialog.dismiss();
//                } catch (Exception e) {
//
//                }
//
//
////                StorageReference filePath = imageReference.child("profile_images").child(firebaseUser.getUid() + ".jpg");
////                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
////                    @Override
////                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
////
////
////                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
////                            @Override
////                            public void onSuccess(Uri uri) {
////                                downUrl = uri.toString();
////                                Log.e("Down url", downUrl);
////                                if (!TextUtils.isEmpty(downUrl))
////                                    userDatabase.child("Image").setValue(downUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
////                                        @Override
////                                        public void onSuccess(Void aVoid) {
////
////                                            progressDialog.dismiss();
////                                            Toast.makeText(SettingsActivity.this, "Profile picture updated", Toast.LENGTH_SHORT).show();
////                                        }
////
////                                    });
////
////                            }
////                        });
////
////
////                    }
////                });
//
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//    }

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
                    if (pinnedDropLocation == null)
                        cameraPosition = new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build();
                    else if (isDropLocation)
                        cameraPosition = new CameraPosition.Builder().target(pinnedDropLocation).zoom(15).build();
                    else
                        cameraPosition = new CameraPosition.Builder().target(pinnedLocation).zoom(15).build();

                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


                    googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {

                            locationAddress(cameraPosition.target.latitude, cameraPosition.target.longitude);
                            pinnedLocation = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);

                            if (isDropLocation) {
                                dropLatitude = cameraPosition.target.latitude;
                                dropLongitude = cameraPosition.target.longitude;
                                pinnedDropLocation = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);

                            } else {

                                latitude = cameraPosition.target.latitude;
                                longitude = cameraPosition.target.longitude;
                                pinnedLocation = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);


                            }
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


    private void validateUserNewValues() {
        //add network call inside last if to update user with new values
        boolean isValidated = true;
        Log.e("updateUserWithNewValues", "executed");

        if (TextUtils.isEmpty(editTxtParentName.getText().toString()) | editTxtParentName.getText().toString().equals(" ") | editTxtParentName.getText().length() < 3) {

            isValidated = false;
            showToast(dialog, "Your name is not valid", 0);
            txtParentName.setText(originalName);

        } else
            txtParentName.setText(editTxtParentName.getText());

        if (TextUtils.isEmpty(editTxtPhoneNumber.getText().toString()) | editTxtPhoneNumber.getText().length() < 9) {
            isValidated = false;
            showToast(dialog, "Your phone number is not valid", 0);
            txtPhoneNumber.setText(originalNumber);

        } else
            txtPhoneNumber.setText(editTxtPhoneNumber.getText());

        if (TextUtils.isEmpty(editTxtEmail.getText().toString()) | !Patterns.EMAIL_ADDRESS.matcher(editTxtEmail.getText()).matches()) {
            isValidated = false;
            showToast(dialog, "Your email is not valid", 0);
            txtEmail.setText(originalEmail);

        } else txtEmail.setText(editTxtEmail.getText());

//        if (TextUtils.isEmpty(txtParentAddress.getText().toString()) | TextUtils.isEmpty(txtParentDropAddress.getText().toString())) {
//            isValidated = false;
//            txtParentDropAddress.setText(originalDropAddress);
//            showWarningToast(dialog, "Your address is not valid", 0);
//
//
//        } else txtParentDropAddress.setText(editTxtEmail.getText());
//

        if (isValidated) {
            updateUserPickupLocation();
            if (tinyDB.getBoolean("isStaffAccount"))
                updateUserDropLocation();
        }

    }


    private void hideKeyboard() {
        editTxtEmail.onEditorAction(EditorInfo.IME_ACTION_DONE);
        editTxtPhoneNumber.onEditorAction(EditorInfo.IME_ACTION_DONE);
        editTxtParentName.onEditorAction(EditorInfo.IME_ACTION_DONE);
    }


    class StudentViewHolder extends RecyclerView.ViewHolder {
        CardView rltKidEdit;
        TextView txtKidName, txtKidSchool;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            rltKidEdit = itemView.findViewById(R.id.rlt_kid_edit);
            txtKidName = itemView.findViewById(R.id.txt_kid_name);

            txtKidSchool = itemView.findViewById(R.id.txt_kid_school);
        }
    }

    class StudentItemAdapter extends RecyclerView.Adapter<StudentViewHolder> {

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_student_edit, parent, false);
            return new StudentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentViewHolder holder, final int position) {

            try {
                holder.txtKidName.setText(kidList.getJSONObject(position).getString("name"));

                holder.txtKidSchool.setText(kidList.getJSONObject(position).getString("school_name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.rltKidEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(getApplicationContext(), EditKidProfile.class);

                        intent.putExtra("kid_id", kidList.getJSONObject(position).getString("id"));
                        startActivity(intent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return kidList.length();
        }
    }

    public void getAllChildren() {
        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_ALL_KID), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {


                    kidList = result.getJSONArray("children");
                    Log.e("kids from edit", kidList + "");

                    recyclerKidList.getAdapter().notifyDataSetChanged();


                } catch (Exception e) {

                }

            }
        });


    }

    private void updateUserPickupLocation() {

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("latitude", latitude.toString());
        tempParam.put("longitude", longitude.toString());


        tempParam.put("add1", originalAddressOne);
        tempParam.put("add2", originalAddressTwo);

        showLoading();
        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.UPDATE_USER_PICKUP), 3, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    if (result.getBoolean("location_saved_status"))
                        if (!tinyDB.getBoolean("isStaffAccount")) {
                            showToast(dialog, "Updated Successfully", 2);
                            hideLoading();
                        }


                } catch (Exception e) {
                    hideLoading();
                }

            }
        });

    }

    private void updateUserDropLocation() {

        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("latitude", dropLatitude.toString());
        tempParam.put("longitude", dropLongitude.toString());


        tempParam.put("add1", originalDropAddressOne);
        tempParam.put("add2", originalDropAddressTwo);
        showLoading();

        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.UPDATE_USER_DROP), 3, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    hideLoading();
                    if (result.getBoolean("location_saved_status"))
                        showToast(dialog, "Updated Successfully", 2);


                } catch (Exception e) {
                    hideLoading();
                }

            }
        });

    }

    private void loadUserDetails() {

        HashMap<String, String> tempParam = new HashMap<>();
        showLoading();

        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.VALIDATE_TOKEN), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    originalName = result.getJSONObject("user").getString("name");
                    originalEmail = result.getJSONObject("user").getString("email");
                    originalNumber = result.getJSONObject("user").getString("phone_no");

                    txtParentName.setText(originalName);
                    txtPhoneNumber.setText(originalNumber);
                    txtEmail.setText(originalEmail);
                    loadUserLocationDetails();

                } catch (Exception e) {


                }

            }
        });

    }

    private void loadUserLocationDetails() {

        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.USER_LOCATION), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    hideLoading();
                    if (result.getBoolean("status")) {

                        originalAddressOne = result.getJSONObject("location").getString("pick_up_add1");
                        originalAddressTwo = " " + result.getJSONObject("location").getString("pick_up_add2");
                        originalDropAddressOne = result.getJSONObject("location").getString("drop_off_add1");
                        originalDropAddressTwo = " " + result.getJSONObject("location").getString("drop_off_add2");

                        txtParentAddress.setText(originalAddressOne + originalAddressTwo);
                        txtParentDropAddress.setText(originalDropAddressOne + originalDropAddressTwo);
                        longitude = result.getJSONObject("location").getDouble("pick_up_longitude");
                        latitude = result.getJSONObject("location").getDouble("pick_up_latitude");
                        pinnedLocation = new LatLng(latitude, longitude);
                        dropLatitude = result.getJSONObject("location").getDouble("drop_off_latitude");
                        dropLongitude = result.getJSONObject("location").getDouble("drop_off_longitude");
                        pinnedDropLocation = new LatLng(dropLatitude, dropLatitude);

                    }

                } catch (Exception e) {
                    hideLoading();

                }

            }
        });

    }

    public void showAddressPopup(final PopupWindow popupWindow) {


        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);


        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setTitle(null);

        dialog.setContentView(R.layout.address_popup);


        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

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
                    if (isDropLocation) {

                        originalDropAddressOne = txtAddOne.getText().toString();
                        originalDropAddressTwo = " " + txtAddTwo.getText().toString();
                        txtParentDropAddress.setText(originalDropAddressOne + originalDropAddressTwo);

                    } else {
                        originalAddressOne = txtAddOne.getText().toString();
                        originalAddressTwo = " " + txtAddTwo.getText().toString();
                        txtParentAddress.setText(originalAddressOne + originalAddressTwo);


                    }
                    popupWindow.dismiss();
                    dialog.dismiss();
                }


            }
        });


        dialog.show();
    }

    void showLoading() {

        view.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        loading.playAnimation();


    }

    void hideLoading() {


        loading.setVisibility(View.GONE);
        view.setVisibility(View.GONE);

    }
}