package lk.hd192.project;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Home extends GetSafeBase {
    RecyclerView homeRecycler;

    FloatingActionButton fabAddKid;

    ImageView sideMenuListener;
    DrawerLayout drawerLayout;


    NavigationView navigationView;
    RelativeLayout drawerTransportLyt, drawerLocationLyt, drawerStatsLyt, drawerExpensesLyt, drawerSwapLyt, drawerHelpLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        homeRecycler = findViewById(R.id.home_recycler);


        sideMenuListener = findViewById(R.id.btn_side_menu);
        drawerLayout = findViewById(R.id.main_drawer_layout);
        navigationView = findViewById(R.id.main_navigation);
        fabAddKid = findViewById(R.id.fab_add_kid);

        drawerTransportLyt = navigationView.findViewById(R.id.rlt_transport_services);

        drawerLocationLyt = navigationView.findViewById(R.id.rlt_locations);
        drawerStatsLyt = navigationView.findViewById(R.id.rlt_stats);
        drawerExpensesLyt = navigationView.findViewById(R.id.rlt_expenses);
        drawerSwapLyt = navigationView.findViewById(R.id.rlt_swap);
        drawerHelpLyt = navigationView.findViewById(R.id.rlt_help);

        homeRecycler.setAdapter(new FunctionItemAdapter());
        homeRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        drawerMenu();

        fabAddKid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddNewKid.class));
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


        drawerTransportLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLocationService();
                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    return;
                } else if (!isEnable) {

                    Intent intent = new Intent(getApplicationContext(), ExploreTransport.class);
                    startActivity(intent);

                }

            }
        });
        drawerStatsLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLocationService();
                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    return;
                } else if (!isEnable) {

                    Intent intent = new Intent(getApplicationContext(), Journey.class);
                    startActivity(intent);
                }
            }
        });
        drawerLocationLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        drawerExpensesLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyLocationService();
                if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                    return;
                } else if (!isEnable) {


                    Intent intent = new Intent(getApplicationContext(), JourneyDetails.class);
                    startActivity(intent);
                }


            }
        });

    }

    class FunctionViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout homeSelection;

        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
            homeSelection = itemView.findViewById(R.id.home_selection);
        }
    }

    class FunctionItemAdapter extends RecyclerView.Adapter<FunctionViewHolder> {
        int i = 0;

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
                            verifyLocationService();
                            if (ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Home.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                                askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, 100);
                                return;
                            } else if (!isEnable) {


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
                    customToast("Permission denied.\nCannot open map", 1);
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


    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(Home.this, permission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Home.this, permission)) {

                ActivityCompat.requestPermissions(Home.this, new String[]{permission}, requestCode);


            } else {

//                ActivityCompat.requestPermissions(Home.this, new String[]{permission}, requestCode);
            }
        } else {

            //Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }

    }
}

