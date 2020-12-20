package lk.hd192.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Journey extends AppCompatActivity {
    RecyclerView recycleJourney;
    Button btnFilter;
    Dialog dialog;
    CalendarView filterCalendar;
    String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        recycleJourney = findViewById(R.id.recycler_journey);
        btnFilter = findViewById(R.id.btn_filter);


        recycleJourney.setAdapter(new JourneyAdapter());
        recycleJourney.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        findViewById(R.id.btn_journey_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar();
            }
        });

    }

    private void showCalendar() {

        Window window = dialog.getWindow();
        window.setGravity(Gravity.TOP);


        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setTitle(null);
        dialog.setContentView(R.layout.calendar_popup);


        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        dialog.getWindow().getAttributes().windowAnimations = R.style.CalendarAnimation;


        filterCalendar = dialog.findViewById(R.id.filter_calendar);
        filterCalendar.setMaxDate(System.currentTimeMillis());
        selectedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis()));


        filterCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = String.valueOf(dayOfMonth) + "/" + String.valueOf(month+1) + "/" +String.valueOf(year);
                Log.e("selected date",selectedDate);
            }
        });

        dialog.show();
    }

    class JourneyItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout rltCardSchoolToHome, rltCardHomeToSchool, rltItemJourney;

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
                    startActivity(new Intent(getApplicationContext(), JourneyDetails.class));
                }
            });
        }

        @Override
        public int getItemCount() {
            return 10;
        }
    }
}