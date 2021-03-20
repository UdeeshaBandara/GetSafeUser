package lk.hd192.project;

import androidx.annotation.NonNull;
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

import com.airbnb.lottie.LottieAnimationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class Journey extends GetSafeBase {
    RecyclerView recycleJourney;
    TextView btn_from, btn_to;
    Dialog dialog;
    CalendarView filterCalendar;
    String selectedDate;
    SimpleDateFormat formatter;
    GetSafeServices getSafeServices;
    TinyDB tinyDB;
    JSONArray tripDetails;
    Button btn_reset;
    View view;
    LottieAnimationView loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journey);

        getSafeServices = new GetSafeServices();
        tripDetails = new JSONArray();
        tinyDB = new TinyDB(getApplicationContext());
        formatter = new SimpleDateFormat("yyyy-MM-dd");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        recycleJourney = findViewById(R.id.recycler_journey);
        btn_from = findViewById(R.id.btn_from);
        btn_to = findViewById(R.id.btn_to);
        btn_reset = findViewById(R.id.btn_reset);
        loading = findViewById(R.id.loading);
        view = findViewById(R.id.disable_layout);

        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        recycleJourney.setAdapter(new JourneyAdapter());
        recycleJourney.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        findViewById(R.id.btn_journey_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btn_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar("from");
            }
        });
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_from.setText("From date");
                btn_to.setText("To date");
                if (tinyDB.getBoolean("isStaffAccount"))
                    getAllJourneyDetails();
                else
                    getAllJourneyDetailsForChild();

            }
        });
        btn_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_from.getText().toString().equals("From date"))
                    showToast(dialog, "please select from date first", 0);
                else

                    showCalendar("to");
            }
        });
        if (tinyDB.getBoolean("isStaffAccount"))
            getAllJourneyDetails();
        else
            getAllJourneyDetailsForChild();

    }

    private void showCalendar(final String selectedFrom) {

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
//        selectedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date(System.currentTimeMillis()));

        try {
            if (selectedFrom.equals("to"))
                filterCalendar.setMinDate(formatter.parse(btn_from.getText().toString()).getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }


        filterCalendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                int sMonth = (month + 1);

                if (sMonth < 10 && dayOfMonth < 10) {
                    selectedDate = year + "-0" + sMonth + "-0" + dayOfMonth;

                } else if (sMonth < 10 && dayOfMonth >= 10) {
                    selectedDate = year + "-0" + sMonth + "-" + dayOfMonth;

                } else if (sMonth >= 10 && dayOfMonth < 10) {
                    selectedDate = year + "-" + sMonth + "-0" + dayOfMonth;

                } else {
                    selectedDate = year + "-" + sMonth + "-" + dayOfMonth;
                }

//                selectedDate = String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);

                if (selectedFrom.equals("to"))
                    btn_to.setText(selectedDate);
                else {
                    btn_from.setText(selectedDate);

                }


                if (!btn_from.getText().toString().equals("From date")) {
                    Log.e("inside filter", "yes");

                    if (tinyDB.getBoolean("isStaffAccount"))
                        filterJourneyDetails();
                    else
                        filterJourneyDetailsForUser();
                }


                dialog.dismiss();


            }
        });

        dialog.show();
    }

    class JourneyItemHolder extends RecyclerView.ViewHolder {
        RelativeLayout rltCardSchoolToHome, rltCardHomeToSchool, rltItemJourney;
        TextView txt_pick_up_time, txt_drop_off_time, txt_date;

        public JourneyItemHolder(@NonNull View itemView) {
            super(itemView);
            rltCardHomeToSchool = itemView.findViewById(R.id.rlt_card_home_to_school);
            rltCardSchoolToHome = itemView.findViewById(R.id.rlt_card_school_to_home);
            rltItemJourney = itemView.findViewById(R.id.rlt_item_journey);
            txt_pick_up_time = itemView.findViewById(R.id.txt_pick_up_time);
            txt_drop_off_time = itemView.findViewById(R.id.txt_drop_off_time);
            txt_date = itemView.findViewById(R.id.txt_date);
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
        public void onBindViewHolder(@NonNull JourneyItemHolder holder, final int position) {
            try {
                if (position % 2 == 0) {
                    holder.rltCardHomeToSchool.setVisibility(View.GONE);
                    holder.rltCardSchoolToHome.setVisibility(View.VISIBLE);


                } else {
                    holder.rltCardHomeToSchool.setVisibility(View.VISIBLE);
                    holder.rltCardSchoolToHome.setVisibility(View.GONE);
                }

                holder.txt_date.setText(tripDetails.getJSONObject(position).getString("pickup_time").substring(0, 10));
                holder.txt_pick_up_time.setText("Pickup time - " + tripDetails.getJSONObject(position).getString("pickup_time").substring(11, 16));
                holder.txt_drop_off_time.setText("Drop-off time - " + tripDetails.getJSONObject(position).getString("dropoff_time").substring(11, 16));

                holder.rltItemJourney.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent = new Intent(getApplicationContext(), JourneyDetails.class);
                            intent.putExtra("trip_id", tripDetails.getJSONObject(position).getString("trip_id"));
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        @Override
        public int getItemCount() {
            return tripDetails.length();
        }
    }

    private void getAllJourneyDetails() {

        HashMap<String, String> tempParam = new HashMap<>();

        showLoading();
        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_ALL_JOURNEY_DETAILS), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("jor nw",result+"");

                    if (result.getBoolean("status")) {
                        tripDetails = result.getJSONArray("models");
                        recycleJourney.getAdapter().notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
                hideLoading();
            }
        });


    }

    private void getAllJourneyDetailsForChild() {

        HashMap<String, String> tempParam = new HashMap<>();

        showLoading();
        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_ALL_JOURNEY_DETAILS_CHILD) + "?childid=" + tinyDB.getString("selectedChildId"), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("jor nw",result+"");

                    if (result.getBoolean("status")) {
                        tripDetails = result.getJSONArray("models");
                        recycleJourney.getAdapter().notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
                hideLoading();
            }
        });



    }

    private void filterJourneyDetails() {


        HashMap<String, String> tempParam = new HashMap<>();

        showLoading();
        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.JOURNEY_DETAILS_RANGE) + "?fromdate=" + btn_from.getText().toString() + "&todate=" + btn_to.getText().toString(), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("jor nw",result+"");
                    if (result.getBoolean("status")) {
                        tripDetails = result.getJSONArray("models");
                        recycleJourney.getAdapter().notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
                hideLoading();
            }
        });



    }


    private void filterJourneyDetailsForUser() {

        HashMap<String, String> tempParam = new HashMap<>();

        showLoading();
        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.JOURNEY_DETAILS_RANGE_CHILD) + "?id=" + tinyDB.getString("selectedChildId") + "&fromdate=" + btn_from.getText().toString() + "&todate=" + btn_to.getText().toString(), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
Log.e("jor nw",result+"");

                    if (result.getBoolean("status")) {
                        tripDetails = result.getJSONArray("models");
                        recycleJourney.getAdapter().notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }
                hideLoading();
            }
        });


    }

    void showLoading() {

        view.setVisibility(View.VISIBLE);
        loading.setVisibility(View.VISIBLE);
        loading.playAnimation();


    }

    void hideLoading() {


        loading.setVisibility(View.GONE);
        view.setVisibility(View.GONE);

    }

}