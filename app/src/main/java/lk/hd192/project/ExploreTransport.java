package lk.hd192.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ExploreTransport extends AppCompatActivity {

    RecyclerView recyclerDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_transport);

        recyclerDriver=findViewById(R.id.recycler_driver);


        recyclerDriver.setAdapter(new DriverAdapter());
        recyclerDriver.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));




    }

    class DriverViewHolder extends  RecyclerView.ViewHolder{

        public DriverViewHolder(@NonNull View itemView) {
            super(itemView);
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

        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }
}