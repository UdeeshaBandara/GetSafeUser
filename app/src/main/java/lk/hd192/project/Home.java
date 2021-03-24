package lk.hd192.project;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.SplashScreen;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class Home extends GetSafeBase {
    RecyclerView homeRecycler, recyclerSelectChild;

    RoundedImageView fabAddKid;
    JSONArray homeOptions;

    JSONObject oneOption, twoOption, threeOption, fourOption, fiveOption, kidList;
    TextView account_type, txt_greeting, txt_account_type, txt_user_name, empty_message;
    Dialog dialog;
    GetSafeServices getSafeServices;
    ImageView sideMenuListener, selectChildDownArrow, selectChildUpArrow, btnNotification;
    DrawerLayout drawerLayout;
    String imageUrl;
    RoundedImageView icon_user;
    int currentPosition = 0;
    Boolean isEmptyKidList = false;
    NavigationView navigationView;
    RelativeLayout drawerSideMenuHeading, drawerSelectChildLyt, drawerAlternativeLyt, drawerTransportLyt, drawerLocationLyt, drawerStatsLyt, drawerSwapLyt, drawerLogoutLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getApplicationContext());
        kidList = new JSONObject();
        homeOptions = new JSONArray();
        oneOption = new JSONObject();
        twoOption = new JSONObject();
        threeOption = new JSONObject();
        fourOption = new JSONObject();
        fiveOption = new JSONObject();
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        tinyDB.putString("token", "7|YD5xDYvDTxaPopXIyHm1UwKpct7jI2kR9nxWY1qS");
        tinyDB.putString("user_name", "Udeesha Induras");
        try {

            oneOption.put("heading", "Current Journey");
            oneOption.put("subHeading", "Live location");
            homeOptions.put(oneOption);

            twoOption.put("heading", "Journey Details");
            twoOption.put("subHeading", "See stats about \nprevious journeys");
            homeOptions.put(twoOption);
            threeOption.put("heading", "Talk to driver");
            threeOption.put("subHeading", "Chat with driver");
            homeOptions.put(threeOption);
            fourOption.put("heading", "Your Payments");
            fourOption.put("subHeading", "Make payments");
            homeOptions.put(fourOption);
            fiveOption.put("heading", "Absent Calendar");
            fiveOption.put("subHeading", "View and add absent days");
            homeOptions.put(fiveOption);

//tinyDB.putString("kid_driver_id","5");
            Log.e("driver id", tinyDB.getString("kid_driver_id"));

        } catch (Exception e) {
            e.printStackTrace();
        }


        icon_user = findViewById(R.id.icon_user);
        sideMenuListener = findViewById(R.id.btn_side_menu);
        drawerLayout = findViewById(R.id.main_drawer_layout);
        txt_account_type = findViewById(R.id.txt_account_type);
        navigationView = findViewById(R.id.main_navigation);
        txt_user_name = findViewById(R.id.txt_user_name);
        empty_message = findViewById(R.id.empty_message);
        fabAddKid = findViewById(R.id.fab_add_kid);
        selectChildDownArrow = findViewById(R.id.select_child_down_arrow);
        selectChildUpArrow = findViewById(R.id.select_child_up_arrow);
        txt_greeting = findViewById(R.id.txt_greeting);
        btnNotification = findViewById(R.id.btn_notification);

        drawerTransportLyt = navigationView.findViewById(R.id.rlt_transport_services);
        drawerSideMenuHeading = navigationView.findViewById(R.id.side_menu_heading);


        drawerStatsLyt = navigationView.findViewById(R.id.rlt_stats);

        drawerSwapLyt = navigationView.findViewById(R.id.rlt_swap);
        drawerLogoutLyt = navigationView.findViewById(R.id.rlt_logout);
        drawerSelectChildLyt = navigationView.findViewById(R.id.rlt_select_child);
        drawerAlternativeLyt = navigationView.findViewById(R.id.rlt_absence);
        account_type = navigationView.findViewById(R.id.account_type);

        homeRecycler = findViewById(R.id.home_recycler);
        recyclerSelectChild = navigationView.findViewById(R.id.recycler_select_child);

        homeRecycler.setAdapter(new FunctionItemAdapter());
        homeRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        recyclerSelectChild.setAdapter(new StudentItemAdapter());
        recyclerSelectChild.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Notification.class));
            }
        });

        drawerMenu();

        getGreeting();

        fabAddKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // customToast("Press and hold to add new kid", 0);

                isDeviceLocationTurnedOn(dialog);

                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    askForPermission();
                    return;
                } else if (isEnable) {

                    Intent intent = new Intent(getApplicationContext(), AddNewKid.class);
                    startActivityForResult(intent, 2);
                }
            }
        });

        fabAddKid.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });


        if (tinyDB.getBoolean("isStaffAccount"))
            setupStaffAccount();
        else
            setupChildAccount();


//        getDeviceToken();

    }

    private void setupStaffAccount() {

        getKidImage(tinyDB.getString("driver_id"), icon_user);

        drawerSelectChildLyt.setVisibility(View.GONE);
        fabAddKid.setVisibility(View.GONE);
        if (recyclerSelectChild.getVisibility() != View.GONE)
            drawerSelectChildLyt.performClick();
        account_type.setText("Switch to your student \ntransport account");
        txt_account_type.setText("Staff Transport Account");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            getAllChildren();
        }
    }

    private void setupChildAccount() {
        getAllChildren();
        getKidImage(tinyDB.getString("kid_driver_id"), icon_user);
        drawerSelectChildLyt.setVisibility(View.VISIBLE);
        fabAddKid.setVisibility(View.VISIBLE
        );
        account_type.setText("Switch to your office \ntransport account");
        txt_account_type.setText("Student Transport Account");

    }

    private void drawerMenu() {

        sideMenuListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);

            }
        });

        drawerSideMenuHeading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), EditProfile.class));
            }
        });

        drawerSelectChildLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmptyKidList) {
                    if (empty_message.getVisibility() == View.GONE) {


                        empty_message.setVisibility(View.VISIBLE);

                        recyclerSelectChild.setVisibility(View.VISIBLE);
                        selectChildUpArrow.setVisibility(View.VISIBLE);
                        selectChildDownArrow.setVisibility(View.GONE);


                    } else {

                        empty_message.setVisibility(View.GONE);


                        selectChildUpArrow.setVisibility(View.GONE);
                        selectChildDownArrow.setVisibility(View.VISIBLE);


                    }

                } else {
                    if (recyclerSelectChild.getVisibility() == View.GONE) {
                        recyclerSelectChild.setVisibility(View.VISIBLE);

                        recyclerSelectChild.setVisibility(View.VISIBLE);
                        selectChildUpArrow.setVisibility(View.VISIBLE);
                        selectChildDownArrow.setVisibility(View.GONE);
                    } else {

                        recyclerSelectChild.setVisibility(View.GONE);
                        selectChildUpArrow.setVisibility(View.GONE);
                        selectChildDownArrow.setVisibility(View.VISIBLE);

                    }


                }


            }
        });
        drawerTransportLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDeviceLocationTurnedOn(dialog);
                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    askForPermission();
                    return;
                } else if (isEnable) {


                    Intent intent = new Intent(getApplicationContext(), ExploreTransport.class);
                    startActivity(intent);

                }

            }
        });
        drawerAlternativeLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDeviceLocationTurnedOn(dialog);
                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    askForPermission();
                    return;
                } else if (isEnable) {

                    if (tinyDB.getBoolean("isStaffAccount")) {
                      validateDriver(3);
                    } else {
                        if (isEmptyKidList)
                            showToast(dialog, "Please add kid to set alternate pickup", 0);
                        else  validateDriver(3);
                    }


                }

            }
        });
        drawerStatsLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDeviceLocationTurnedOn(dialog);
                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    askForPermission();
                    return;
                } else if (isEnable)
                    if (tinyDB.getBoolean("isStaffAccount")) {
                        validateDriver(1);
                    } else {
                        if (isEmptyKidList)
                            showToast(dialog, "Please add kid to set view journey details", 0);
                        else validateDriver(1);
                    }


            }
        });


        drawerSwapLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tinyDB.getBoolean("isStaffAccount")) {
                    tinyDB.putBoolean("isStaffAccount", false);
                    setupChildAccount();
                    showToast(dialog, "Changed to kid(s) account ", 2);
                } else {

                    tinyDB.putBoolean("isStaffAccount", true);
                    setupStaffAccount();
                    showToast(dialog, "Changed to staff account ", 2);
                }
            }
        });
        drawerLogoutLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showToast(dialog, "Are you sure you want to Sign out? ", 3);
            }
        });


    }

    private Bitmap populateImage(String encodedImage) {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

    }

    private void getKidImage(String driverIdImage, final RoundedImageView roundedImageView) {
        HashMap<String, String> param = new HashMap<>();


        getSafeServices.networkJsonRequest(this, param, getString(R.string.BASE_URL) + getString(R.string.KID_PIC) + "?id=" + driverIdImage, 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

//                    Log.e("getKidImage", result + "");

                    if (result.getBoolean("status")) {

                        roundedImageView.setImageBitmap(populateImage(result.getJSONObject("data").getString("image").substring(22)));


                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });


    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rltItemStudent, kidSelector;
        TextView studentName, studentSchool;
        RoundedImageView child_image;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            rltItemStudent = itemView.findViewById(R.id.rlt_item_student);
            kidSelector = itemView.findViewById(R.id.kid_selector);
            studentName = itemView.findViewById(R.id.student_name);
            studentSchool = itemView.findViewById(R.id.student_school);
            child_image = itemView.findViewById(R.id.child_image);
        }
    }

    class StudentItemAdapter extends RecyclerView.Adapter<StudentViewHolder> {

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_student, parent, false);
            return new StudentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final StudentViewHolder holder, final int position) {
            try {


                if (tinyDB.getString("selectedChildId").equals(kidList.getJSONArray("children").getJSONObject(position).getString("id")))

                    holder.kidSelector.setVisibility(View.VISIBLE);
                else
                    holder.kidSelector.setVisibility(View.GONE);

                holder.studentName.setText(kidList.getJSONArray("children").getJSONObject(position).getString("name"));
                holder.studentSchool.setText(kidList.getJSONArray("children").getJSONObject(position).getString("school_name"));


                getKidImage(kidList.getJSONArray("children").getJSONObject(position).getString("id"), holder.child_image);

                holder.rltItemStudent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {


                            if (holder.kidSelector.getVisibility() == View.GONE) {


                                tinyDB.putString("selectedChildId", kidList.getJSONArray("children").getJSONObject(position).getString("id"));
                                tinyDB.putString("selectedChildName", kidList.getJSONArray("children").getJSONObject(position).getString("name"));
                                tinyDB.putString("kid_driver_id", kidList.getJSONArray("children").getJSONObject(position).getString("driver_id"));
                                tinyDB.putBoolean("isKidDriverAssigned", !kidList.getJSONArray("children").getJSONObject(0).getString("driver_id").equals("null"));

                                notifyDataSetChanged();
                                showToast(dialog, "Profile changed to " + tinyDB.getString("selectedChildName"), 2);

                            }

//                            Intent intent = new Intent(getApplicationContext(), EditKidProfile.class);
//
//                            intent.putExtra("kid_id", kidList.getJSONArray("children").getJSONObject(position).getString("id"));
//                            startActivity(intent);
                        } catch (Exception e) {
                            Log.e("bind error 1", e.getMessage());
                        }
                    }
                });
            } catch (Exception e) {
                Log.e("bind error 2", e.getMessage());
            }


        }

        @Override
        public int getItemCount() {
            try {
                return kidList.getJSONArray("children").length();
            } catch (JSONException e) {
                return 0;
            }
        }
    }

    class FunctionViewHolder extends RecyclerView.ViewHolder {
        CardView homeSelection;
        RoundedImageView itemHomeImage;
        TextView txtOptionName, optionHeadingOne;
        ImageView optionIconOne;

        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
            homeSelection = itemView.findViewById(R.id.home_selection);
            itemHomeImage = itemView.findViewById(R.id.item_home_image);
            txtOptionName = itemView.findViewById(R.id.txt_option_name);
            optionHeadingOne = itemView.findViewById(R.id.option_heading_one);
            optionIconOne = itemView.findViewById(R.id.option_icon_one);
        }
    }

    class FunctionItemAdapter extends RecyclerView.Adapter<FunctionViewHolder> {


        @NonNull
        @Override
        public FunctionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_home, parent, false);
            return new FunctionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull FunctionViewHolder holder, final int position) {

            Uri uri = Uri.parse("android.resource://lk.hd192.project/drawable/home" + String.valueOf(position) + "");
            Picasso.get().load(uri).into(holder.itemHomeImage);
            Uri uri1 = Uri.parse("android.resource://lk.hd192.project/drawable/home_icon" + String.valueOf(position) + "");
            Picasso.get().load(uri1).into(holder.optionIconOne);


            try {
                holder.txtOptionName.setText(homeOptions.getJSONObject(position).getString("heading"));
                holder.optionHeadingOne.setText(homeOptions.getJSONObject(position).getString("subHeading"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            holder.homeSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            Log.e("love loc", "ok");

                            isDeviceLocationTurnedOn(dialog);
                            if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//                                askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                                askForPermission();
                                return;
                            } else if (isEnable) {
                                if (tinyDB.getBoolean("isStaffAccount")) {

                                    validateDriver(0);

                                } else {

                                    if (isEmptyKidList) {
                                        showToast(dialog, "Please add kid to view location", 0);


                                    } else {

                                        validateDriver(0);
                                    }
                                }

                            } else {

                            }


                            break;
                        case 1:

                            if (tinyDB.getBoolean("isStaffAccount")) {
                                validateDriver(1);

                            } else {
                                if (isEmptyKidList) {
                                    showToast(dialog, "Please add kid to view journey details", 0);

                                } else {
                                    validateDriver(1);
                                }
                            }


                            break;
                        case 2:
                            if (isEmptyKidList & !tinyDB.getBoolean("isStaffAccount"))
                                showToast(dialog, "No driver assigned", 0);
                            else

                                getDriverDetails();


                            break;
                        case 3:

                            startActivity(new Intent(getApplicationContext(), Payment.class));
                            drawerLayout.closeDrawers();
                            break;
                        case 4:

                            if (tinyDB.getBoolean("isStaffAccount")) {


                                validateDriver(2);

                            } else {
                                if (isEmptyKidList) {
                                    showToast(dialog, "Please add kid to add absents", 0);

                                } else {
                                    validateDriver(2);
                                }

                            }

                            break;

                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return 5;
        }

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
                    showToast(dialog, "Permission denied.\nCannot open map", 1);

                } else {
                    if (ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
                        startActivity(new Intent(getApplicationContext(), LiveLocation.class));
                        drawerLayout.closeDrawers();

                    } else {
                        //set to never ask again
                        Log.e("set to never ask again", permission);
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
            }
        }
    }

    private void getDriverDetails() {
        HashMap<String, String> param = new HashMap<>();

        Log.e("getDriverDetails", "exe");
        getSafeServices.networkJsonRequest(this, param, getString(R.string.BASE_URL) + getString(R.string.GET_DRIVER_DETAILS) + "?id=" + tinyDB.getString("kid_driver_id"), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    Log.e("driver", result + "");
                    if (result.getBoolean("status")) {

                        if (tinyDB.getBoolean("isStaffAccount")) {


                            if (tinyDB.getBoolean("isStaffDriverAssigned")) {
                                Intent intent = new Intent(getApplicationContext(), Messaging.class);
                                intent.putExtra("driver_name", result.getJSONObject("data").getString("name"));
                                startActivity(intent);
                                drawerLayout.closeDrawers();
                            } else
                                showToast(dialog, "No driver assigned", 0);

                        } else {
                            if (isEmptyKidList) {
                                showToast(dialog, "Please add kids", 0);

                            } else if (tinyDB.getBoolean("isKidDriverAssigned")) {
                                Intent intent = new Intent(getApplicationContext(), Messaging.class);


                                intent.putExtra("driver_name", result.getJSONObject("data").getString("name"));
                                startActivity(intent);
                                drawerLayout.closeDrawers();
                            } else
                                showToast(dialog, "No driver assigned", 0);
                        }

                    } else if (result.getString("validation_errors").equals("The id must be an integer.")) {
                        showToast(dialog, "No driver assigned", 0);

                    }


                } catch (
                        Exception e) {
                    e.printStackTrace();
                    Log.e("ex from loc", e.getMessage());

                }

            }
        });

    }

    private void askForPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        10);
            }
        } else {

        }
    }

    private void getGreeting() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 0 && timeOfDay < 12) {

            txt_greeting.setText("Good Morning,\n" + tinyDB.getString("user_name"));
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            txt_greeting.setText("Good Afternoon,\n" + tinyDB.getString("user_name"));

        } else if (timeOfDay >= 16) {

            txt_greeting.setText("Good Evening,\n" + tinyDB.getString("user_name"));
        }


        txt_user_name.setText(tinyDB.getString("user_name"));
//        } else if (timeOfDay >= 21 && timeOfDay < 24) {
//
//            txt_greeting.setText("Good Night,\n" + tinyDB.getString("user_name"));
//        }
    }

    public void getAllChildren() {
        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_ALL_KID), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    kidList = result;

                    if (kidList.getJSONArray("children").length() != 0) {
                        tinyDB.putString("selectedChildId", kidList.getJSONArray("children").getJSONObject(0).getString("id"));
                        tinyDB.putString("selectedChildName", kidList.getJSONArray("children").getJSONObject(0).getString("name"));
                        tinyDB.putString("kid_driver_id", kidList.getJSONArray("children").getJSONObject(0).getString("driver_id"));
//                        tinyDB.putBoolean("isKidDriverAssigned", !kidList.getJSONArray("children").getJSONObject(0).getString("driver_id").equals("null"));
                        isEmptyKidList = false;
                        empty_message.setVisibility(View.GONE);
                        recyclerSelectChild.getAdapter().notifyDataSetChanged();
                    } else {
                        isEmptyKidList = true;

                    }
                } catch (Exception e) {

                }

            }
        });


    }

    private void validateDriver(final int select) {


        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(this, tempParam, getString(R.string.BASE_URL) + getString(R.string.VALIDATE_TOKEN), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try { //startActivity(new Intent(SplashScreen.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT));

                    if (result.getBoolean("logged-in-status")) {


                        if (!result.getJSONObject("user").getString("driver_id").equals("null")) {
                            switch (select) {
                                case 0:
                                    Intent intent = new Intent(getApplicationContext(), LiveLocation.class);
                                    startActivity(intent);
                                    drawerLayout.closeDrawers();
                                    break;
                                case 1:
                                    Intent intent1 = new Intent(getApplicationContext(), Journey.class);
                                    startActivity(intent1);
                                    drawerLayout.closeDrawers();
                                    break;
                                case 2:
                                    Intent intent2 = new Intent(getApplicationContext(), Absence.class);
                                    startActivity(intent2);
                                    drawerLayout.closeDrawers();
                                    break;
                                case 3:
                                    Intent intent3 = new Intent(getApplicationContext(), AlternativeRoutes.class);
                                    startActivity(intent3);
                                    drawerLayout.closeDrawers();
                                    break;

                            }
                        } else {
                            showToast(dialog, "No driver assigned", 0);
                        }

                    } else {
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });

    }

}
