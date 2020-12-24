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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import lk.hd192.project.Utils.GetSafeBase;

public class Home extends GetSafeBase {
    RecyclerView homeRecycler, recyclerSelectChild;

    FloatingActionButton fabAddKid;

    Dialog dialog;
    ImageView sideMenuListener, selectChildDownArrow, selectChildUpArrow;
    DrawerLayout drawerLayout;


    NavigationView navigationView;
    RelativeLayout drawerSideMenuHeading,drawerSelectChildLyt, drawerAbsenceLyt,drawerTransportLyt, drawerLocationLyt, drawerStatsLyt, drawerExpensesLyt, drawerSwapLyt, drawerHelpLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDeviceToken();
        homeRecycler = findViewById(R.id.home_recycler);
        recyclerSelectChild = findViewById(R.id.recycler_select_child);

        dialog = new Dialog(this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        sideMenuListener = findViewById(R.id.btn_side_menu);
        drawerLayout = findViewById(R.id.main_drawer_layout);
        navigationView = findViewById(R.id.main_navigation);
        fabAddKid = findViewById(R.id.fab_add_kid);
        selectChildDownArrow = findViewById(R.id.select_child_down_arrow);
        selectChildUpArrow = findViewById(R.id.select_child_up_arrow);

        drawerTransportLyt = navigationView.findViewById(R.id.rlt_transport_services);
        drawerSideMenuHeading = navigationView.findViewById(R.id.side_menu_heading);


        drawerStatsLyt = navigationView.findViewById(R.id.rlt_stats);
        drawerExpensesLyt = navigationView.findViewById(R.id.rlt_expenses);
        drawerSwapLyt = navigationView.findViewById(R.id.rlt_swap);
        drawerHelpLyt = navigationView.findViewById(R.id.rlt_help);
        drawerSelectChildLyt = navigationView.findViewById(R.id.rlt_select_child);
        drawerAbsenceLyt = navigationView.findViewById(R.id.rlt_absence);

        homeRecycler.setAdapter(new FunctionItemAdapter());
        homeRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        recyclerSelectChild.setAdapter(new StudentItemAdapter());
        recyclerSelectChild.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        drawerMenu();

        fabAddKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // customToast("Press and hold to add new kid", 0);

                isDeviceLocationTurnedOn(dialog);

                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                    askForPermission();
                    return;
                }
               else if (isEnable)

                startActivity(new Intent(getApplicationContext(), AddNewKid.class));
            }
        });

        fabAddKid.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return false;
            }
        });

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
                startActivity(new Intent(getApplicationContext(),EditProfile.class));
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

    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rltItemStudent;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            rltItemStudent = itemView.findViewById(R.id.rlt_item_student);
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
        public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {

            holder.rltItemStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }

    class FunctionViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout homeSelection;

        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
            homeSelection = itemView.findViewById(R.id.home_selection);
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

                            break;
                        case 3:

                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return 5;
        }

    }
    public void getDeviceToken() {

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Please Try Again !", Toast.LENGTH_SHORT).show();
                            return;

                        } else {
                            String token = task.getResult().getToken();

                            Log.e("TOKEN >> ", token);

                        }

                    }
                });
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
                    showWarningToast(dialog, "Permission denied.\nCannot open map", 1);

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
}
