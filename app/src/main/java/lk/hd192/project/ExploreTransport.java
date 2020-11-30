package lk.hd192.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class ExploreTransport extends AppCompatActivity {

    RecyclerView recyclerDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_transport);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerDriver=findViewById(R.id.recycler_driver);


        recyclerDriver.setAdapter(new DriverAdapter());
        recyclerDriver.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        findViewById(R.id.btn_explore_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



    }

    class DriverViewHolder extends  RecyclerView.ViewHolder{

        RelativeLayout oneDriver;
        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
            oneDriver=itemView.findViewById(R.id.one_driver);
        }
    }
    class DriverAdapter extends RecyclerView.Adapter<DriverViewHolder>{

        @NonNull
        @Override
        public DriverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_drivers, parent, false);
            return new DriverViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DriverViewHolder holder, int position) {

            holder.oneDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),DriverProfile.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }
}