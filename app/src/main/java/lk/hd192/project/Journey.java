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

public class Journey extends AppCompatActivity {
    RecyclerView recycleJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recycleJourney = findViewById(R.id.recycler_journey);

        recycleJourney.setAdapter(new JourneyAdapter());
        recycleJourney.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        findViewById(R.id.btn_journey_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    class JourneyItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout rltCardSchoolToHome, rltCardHomeToSchool,rltItemJourney;

        public JourneyItemHolder(@NonNull View itemView) {
            super(itemView);
            rltCardHomeToSchool = itemView.findViewById(R.id.rlt_card_home_to_school);
            rltCardSchoolToHome = itemView.findViewById(R.id.rlt_card_school_to_home);
            rltItemJourney = itemView.findViewById(R.id.rlt_item_journey);
        }
    }

    class JourneyAdapter extends RecyclerView.Adapter<JourneyItemHolder> {

        @NonNull
        @Override
        public JourneyItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_journey, parent, false);
            return new JourneyItemHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull JourneyItemHolder holder, int position) {
            if (position % 2 == 0) {
                holder.rltCardHomeToSchool.setVisibility(View.GONE);
                holder.rltCardSchoolToHome.setVisibility(View.VISIBLE);

            } else {
                holder.rltCardHomeToSchool.setVisibility(View.VISIBLE);
                holder.rltCardSchoolToHome.setVisibility(View.GONE);
            }

            holder.rltItemJourney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(),JourneyDetails.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }
}