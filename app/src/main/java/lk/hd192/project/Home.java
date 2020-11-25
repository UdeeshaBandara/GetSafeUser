package lk.hd192.project;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

public class Home extends AppCompatActivity {
    RecyclerView homeRecycler;
    ImageView sideMenuListener;
    DrawerLayout drawerLayout;
    NavigationView navigationView;


    RelativeLayout drawerTransportLyt,drawerHomeLyt, drawerLocationLyt, drawerStatsLyt, drawerExpensesLyt, drawerSwapLyt,drawerHelpLyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        homeRecycler=findViewById(R.id.home_recycler);
        sideMenuListener=findViewById(R.id.btn_side_menu);
        drawerLayout =findViewById(R.id.main_drawer_layout);
        navigationView = findViewById(R.id.main_navigation);

        drawerTransportLyt = navigationView.findViewById(R.id.rlt_transport_services);
        drawerHomeLyt = navigationView.findViewById(R.id.rlt_home);
        drawerLocationLyt = navigationView.findViewById(R.id.rlt_locations);
        drawerStatsLyt = navigationView.findViewById(R.id.rlt_stats);
        drawerExpensesLyt = navigationView.findViewById(R.id.rlt_expenses);
        drawerSwapLyt = navigationView.findViewById(R.id.rlt_swap);
        drawerHelpLyt = navigationView.findViewById(R.id.rlt_help);


        homeRecycler.setAdapter(new FunctionItemAdapter());
        homeRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        sideMenuListener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT);

            }
        });


        drawerTransportLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ExploreTransport.class);
                startActivity(intent);
            }
        });
        drawerStatsLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Statistics.class);
                startActivity(intent);
            }
        });
        drawerLocationLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LiveLocation.class);
                startActivity(intent);
            }
        });
        drawerExpensesLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), JourneyDetails.class);
                startActivity(intent);
            }
        });
    }

    class FunctionViewHolder extends RecyclerView.ViewHolder {

        public FunctionViewHolder(@NonNull View itemView) {
            super(itemView);
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
        public void onBindViewHolder(@NonNull FunctionViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }


}