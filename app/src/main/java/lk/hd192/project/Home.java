package lk.hd192.project;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
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
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class Home extends GetSafeBase {
    RecyclerView homeRecycler, recyclerSelectChild;

    RoundedImageView fabAddKid;
    JSONArray homeOptions;

    JSONObject oneOption, twoOption, threeOption, fourOption, fiveOption, kidList;
    TextView account_type, txt_greeting;
    Dialog dialog;
    GetSafeServices getSafeServices;
    ImageView sideMenuListener, selectChildDownArrow, selectChildUpArrow, btnNotification;
    DrawerLayout drawerLayout;
    String imageUrl;
    int currentPosition = 0;
    NavigationView navigationView;
    RelativeLayout drawerSideMenuHeading, drawerSelectChildLyt, drawerAbsenceLyt, drawerTransportLyt, drawerLocationLyt, drawerStatsLyt, drawerExpensesLyt, drawerSwapLyt, drawerLogoutLyt;

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
        tinyDB.putString("token", "7|gEtW7gkoMyLwmda42WV8maP5kQPr8j3H5UU2Qmjd");
        tinyDB.putString("user_name", "Udeesha Bandara");
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
            fiveOption.put("heading", "Your Route and Calendar");
            fiveOption.put("subHeading", "Add alternative drop-off &\nManage absent days");
            homeOptions.put(fiveOption);

        } catch (Exception e) {

        }


        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        sideMenuListener = findViewById(R.id.btn_side_menu);
        drawerLayout = findViewById(R.id.main_drawer_layout);
        navigationView = findViewById(R.id.main_navigation);
        fabAddKid = findViewById(R.id.fab_add_kid);
        selectChildDownArrow = findViewById(R.id.select_child_down_arrow);
        selectChildUpArrow = findViewById(R.id.select_child_up_arrow);
        txt_greeting = findViewById(R.id.txt_greeting);
        btnNotification = findViewById(R.id.btn_notification);

        drawerTransportLyt = navigationView.findViewById(R.id.rlt_transport_services);
        drawerSideMenuHeading = navigationView.findViewById(R.id.side_menu_heading);


        drawerStatsLyt = navigationView.findViewById(R.id.rlt_stats);
        drawerExpensesLyt = navigationView.findViewById(R.id.rlt_expenses);
        drawerSwapLyt = navigationView.findViewById(R.id.rlt_swap);
        drawerLogoutLyt = navigationView.findViewById(R.id.rlt_logout);
        drawerSelectChildLyt = navigationView.findViewById(R.id.rlt_select_child);
        drawerAbsenceLyt = navigationView.findViewById(R.id.rlt_absence);
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
                } else if (isEnable)

                    startActivity(new Intent(getApplicationContext(), AddNewKid.class));
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

        drawerSelectChildLyt.setVisibility(View.GONE);
        fabAddKid.setVisibility(View.GONE);
        if (recyclerSelectChild.getVisibility() != View.GONE)
            drawerSelectChildLyt.performClick();
        account_type.setText("Switch to your student \ntransport account");
    }

    private void setupChildAccount() {
        getAllChildren();
        drawerSelectChildLyt.setVisibility(View.VISIBLE);
        fabAddKid.setVisibility(View.VISIBLE
        );
        account_type.setText("Switch to your office \ntransport account");

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
                if (recyclerSelectChild.getVisibility() == View.GONE) {
                    recyclerSelectChild.setVisibility(View.VISIBLE);
                    selectChildUpArrow.setVisibility(View.VISIBLE);
                    selectChildDownArrow.setVisibility(View.GONE);


                } else {

                    recyclerSelectChild.setVisibility(View.GONE);
                    selectChildUpArrow.setVisibility(View.GONE);
                    selectChildDownArrow.setVisibility(View.VISIBLE);


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
        drawerAbsenceLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDeviceLocationTurnedOn(dialog);
                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    askForPermission();
                    return;
                } else if (isEnable) {


                    startActivity(new Intent(getApplicationContext(), Absence.class));

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

                    startActivity(new Intent(getApplicationContext(), Journey.class));

            }
        });

        drawerExpensesLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDeviceLocationTurnedOn(dialog);
                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//                    askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    askForPermission();
                    return;
                } else if (isEnable)

                    startActivity(new Intent(getApplicationContext(), JourneyDetails.class));

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
                    Log.e("else ataff","ok");
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

    class StudentViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rltItemStudent, kidSelector;
        TextView studentName, studentSchool;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            rltItemStudent = itemView.findViewById(R.id.rlt_item_student);
            kidSelector = itemView.findViewById(R.id.kid_selector);
            studentName = itemView.findViewById(R.id.student_name);
            studentSchool = itemView.findViewById(R.id.student_school);
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

                holder.rltItemStudent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {


                            if (holder.kidSelector.getVisibility() == View.GONE) {


                                tinyDB.putString("selectedChildId", kidList.getJSONArray("children").getJSONObject(position).getString("id"));
                                tinyDB.putString("selectedChildName", kidList.getJSONArray("children").getJSONObject(position).getString("name"));
                                tinyDB.putString("driver_id", kidList.getJSONArray("children").getJSONObject(position).getString("driver_id"));
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

                            isDeviceLocationTurnedOn(dialog);
                            if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

//                                askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                                askForPermission();
                                return;
                            } else if (isEnable) {


                                startActivity(new Intent(getApplicationContext(), LiveLocation.class));
                                drawerLayout.closeDrawers();
                            }

                            break;
                        case 1:
                            startActivity(new Intent(getApplicationContext(), Journey.class));
                            drawerLayout.closeDrawers();
                            break;
                        case 2:

                            startActivity(new Intent(getApplicationContext(), Messaging.class));
                            drawerLayout.closeDrawers();
                            break;
                        case 3:
                            startActivity(new Intent(getApplicationContext(), Payment.class));
                            drawerLayout.closeDrawers();
                            break;
                        case 4:
                            startActivity(new Intent(getApplicationContext(), AlternativeRoutes.class));
                            drawerLayout.closeDrawers();
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

                    if (kidList!=null) {
                        tinyDB.putString("selectedChildId", kidList.getJSONArray("children").getJSONObject(0).getString("id"));
                        tinyDB.putString("selectedChildName", kidList.getJSONArray("children").getJSONObject(0).getString("name"));
                        tinyDB.putString("driver_id", kidList.getJSONArray("children").getJSONObject(0).getString("driver_id"));

                        recyclerSelectChild.getAdapter().notifyDataSetChanged();
                    }

                } catch (Exception e) {

                }

            }
        });


    }


}
