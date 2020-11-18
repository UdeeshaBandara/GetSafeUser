package lk.hd192.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class Statistics extends AppCompatActivity {
    RecyclerView recycleJourney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recycleJourney = findViewById(R.id.recycler_journey);

        recycleJourney.setAdapter(new JourneyAdapter());
        recycleJourney.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


    }

    class JourneyItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout rltCardSchoolToHome, rltCardHomeToSchool;

        public JourneyItemHolder(@NonNull View itemView) {
            super(itemView);
            rltCardHomeToSchool = itemView.findViewById(R.id.rlt_card_home_to_school);
            rltCardSchoolToHome = itemView.findViewById(R.id.rlt_card_school_to_home);
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

        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }
}