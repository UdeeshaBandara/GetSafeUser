package lk.hd192.project;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lk.hd192.project.Utils.GetSafeBase;

public class Absence extends GetSafeBase {

    Button btnMorning, btnEvening, btnBoth, btnBothSelect, btnMorningSelect, btnEveningSelect, btnSaveAbsence, btnAddToList;
    LottieAnimationView mainSaveAnimation;
    RecyclerView recyclerMonth, recyclerMonthDate, recyclerAbsence;
    Context context;
    int currentYear, currentMonth, currentDate;

    SingleDateAndTimePicker monthPicker, dayPicker;
    TextView subHeadingAbsence;
    ArrayList<String> monthArray;
    ArrayList<String> dayArray;
    JSONObject absenceDate;
    JSONArray absenceDateList;

    SimpleDateFormat dateInFormat;
    Date date;
    int selectedMonthNumber = 0, selectedDateNumber = 0;
    String selectedDay = "";
    String[] dayNames = new String[7];

    Dialog dialog;
    int selectedItem = -1;
    int absenceDatesIndex = 0;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context = getApplicationContext();

        monthArray = new ArrayList<>();
        dayArray = new ArrayList<>();
        absenceDateList = new JSONArray();
        absenceDate = new JSONObject();


        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

        dayNames[0] = "Sunday";
        dayNames[1] = "Monday";
        dayNames[2] = "Tuesday";
        dayNames[3] = "Wednesday";
        dayNames[4] = "Thursday";
        dayNames[5] = "Friday";
        dayNames[6] = "Saturday";

        monthArray.add("January");
        monthArray.add("February");
        monthArray.add("March");
        monthArray.add("April");
        monthArray.add("May");
        monthArray.add("June");
        monthArray.add("July");
        monthArray.add("August");
        monthArray.add("September");
        monthArray.add("October");
        monthArray.add("November");
        monthArray.add("December");

        currentYear = Calendar.getInstance().get(Calendar.YEAR);
        currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        currentDate = Calendar.getInstance().get(Calendar.DATE);
        dateInFormat = new SimpleDateFormat("dd-MM-yyyy");


        btnBoth = findViewById(R.id.btn_both);
        btnBothSelect = findViewById(R.id.btn_both_select);
        btnEvening = findViewById(R.id.btn_evening);
        btnMorning = findViewById(R.id.btn_morning);
        btnEveningSelect = findViewById(R.id.btn_evening_select);
        btnMorningSelect = findViewById(R.id.btn_morning_select);
        monthPicker = findViewById(R.id.month_picker);
        dayPicker = findViewById(R.id.day_picker);
        btnSaveAbsence = findViewById(R.id.btn_save_absence);
        mainSaveAnimation = findViewById(R.id.main_save_animation);
        subHeadingAbsence = findViewById(R.id.sub_heading_absence);
        btnAddToList = findViewById(R.id.btn_add_to_list);


        dayPicker.setEnabled(false);
        btnSaveAbsence.setVisibility(View.INVISIBLE);
        subHeadingAbsence.setVisibility(View.INVISIBLE);
//        btnSaveAbsence.setBackgroundColor(getResources().getColor(R.color.sec_color));


//customToast("you have nothing",1);


        btnSaveAbsence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainSaveAnimation.setVisibility(View.VISIBLE);
                mainSaveAnimation.playAnimation();
            }
        });

        monthPicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                Log.e("date", date.getMonth() + "");
                dayPicker.setEnabled(true);
                selectedMonthNumber = date.getMonth();
                if (currentDate > date.getMonth()) {
                }


            }
        });
        btnAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ontap", "btnAddToList");
                absenceDate = new JSONObject();

                try {
                    absenceDate.put("month", monthArray.get(selectedMonthNumber));
                    absenceDate.put("year", currentYear);
                    absenceDate.put("date", getDateSuffix(selectedDateNumber) + selectedDay);


                    if (btnBothSelect.getVisibility() == View.GONE & btnEveningSelect.getVisibility() == View.GONE & btnMorningSelect.getVisibility() == View.GONE)
                        showWarningToast(dialog, "Please select session", 0);

                    else {
                        if (btnBothSelect.getVisibility() == View.VISIBLE)
                            absenceDate.put("session", "Both");
                        else if (btnEveningSelect.getVisibility() == View.VISIBLE)
                            absenceDate.put("session", "Evening");
                        else if (btnMorningSelect.getVisibility() == View.VISIBLE)
                            absenceDate.put("session", "Morning");


                         absenceDateList.put(absenceDate);

                        if(absenceDateList.length()>1)
                            Log.e("hash", String.valueOf(String.valueOf( absenceDateList.get(1)).hashCode()));
                        else

                            Log.e("hash", String.valueOf(String.valueOf( absenceDateList.get(0)).hashCode()));

                        btnSaveAbsence.setVisibility(View.VISIBLE);
                        subHeadingAbsence.setVisibility(View.VISIBLE);

                        btnSaveAbsence.setBackground(getDrawable(R.drawable.bg_btn_resend));
                        recyclerAbsence.getAdapter().notifyDataSetChanged();
                    }


                    if (currentDate > date.getMonth()) {
                    }

                } catch (Exception e) {

                    Log.e("exception", e.getMessage());
                }

            }
        });
        dayPicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                selectedDateNumber = date.getDate();
                selectedDay = dayNames[date.getDay()];
            }
        });
        mainSaveAnimation.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mainSaveAnimation.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        finish();
                    }
                }, 800);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

//        recyclerMonth = findViewById(R.id.recycler_month);
//        recyclerMonthDate = findViewById(R.id.recycler_month_date);
        recyclerAbsence = findViewById(R.id.recycler_absence);


//        recyclerMonth.setAdapter(new MonthItemAdapter());
//        recyclerMonth.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
//        recyclerMonthDate.setAdapter(new DateItemAdapter());
        recyclerAbsence.setAdapter(new MonthItemAdapter());
        recyclerAbsence.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));


        btnBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBothSelect.setVisibility(View.VISIBLE);
                btnBoth.setVisibility(View.GONE);
                btnMorningSelect.setVisibility(View.GONE);
                btnMorning.setVisibility(View.VISIBLE);
                btnEveningSelect.setVisibility(View.GONE);
                btnEvening.setVisibility(View.VISIBLE);
//                showToast(dialog,"Please enable location servicePlease enable location service",1);
//                showWarningToast(dialog, "Please enable location service", 0);


            }
        });
//        btnBothSelect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                btnEveningSelect.setVisibility(View.GONE);
//                btnMorningSelect.setVisibility(View.GONE);
//
//            }
//        });
//        btnMorningSelect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btnEveningSelect.setVisibility(View.GONE);
//                btnBothSelect.setVisibility(View.GONE);
//
//
//            }
//        });
        btnMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMorningSelect.setVisibility(View.VISIBLE);
                btnMorning.setVisibility(View.GONE);
                btnEveningSelect.setVisibility(View.GONE);
                btnEvening.setVisibility(View.VISIBLE);
                btnBothSelect.setVisibility(View.GONE);
                btnBoth.setVisibility(View.VISIBLE);
            }
        });
//        btnEveningSelect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btnBothSelect.setVisibility(View.GONE);
//
//                btnMorningSelect.setVisibility(View.GONE);
//
//            }
//        });
        btnEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEveningSelect.setVisibility(View.VISIBLE);
                btnEvening.setVisibility(View.GONE);
                btnMorningSelect.setVisibility(View.GONE);
                btnMorning.setVisibility(View.VISIBLE);
                btnBothSelect.setVisibility(View.GONE);
                btnBoth.setVisibility(View.VISIBLE);
            }
        });


    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void getDaysForMonth(int month, int year) {
//
//        try {
//            dayArray = new ArrayList<>();
//            String day = "";
//            int monthLength = YearMonth.of(year, month).lengthOfMonth();
//
//            for (int i = 0; i < monthLength; i++) {
//
//                date = dateInFormat.parse((i + 1) + "-" + month + "-" + year);
//
//                day = android.text.format.DateFormat.format("EEEE", date).toString();
//                dayArray.add(i, day);
//
//            }
//
//
//        } catch (Exception e) {
//
//        }
//
//
//    }


    class MonthViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rltMonth, rltMonthFocus;
        TextView txtMonth, txtYear, txtDate, txtSession;
        ImageView btnDeleteDate;


        public MonthViewHolder(View itemView) {
            super(itemView);
            rltMonth = itemView.findViewById(R.id.rlt_month);
            rltMonthFocus = itemView.findViewById(R.id.rlt_month_focus);
            txtMonth = itemView.findViewById(R.id.txt_month);
            txtYear = itemView.findViewById(R.id.txt_year);
            txtDate = itemView.findViewById(R.id.txt_date);
            btnDeleteDate = itemView.findViewById(R.id.btn_delete_date);
            txtSession = itemView.findViewById(R.id.txt_session);
        }


    }

    class MonthItemAdapter extends RecyclerView.Adapter<MonthViewHolder> {

        private void deleteItem(View view, final int position) {

            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_bottom);
            anim.setDuration(300);
            view.startAnimation(anim);

            new Handler().postDelayed(new Runnable() {
                public void run() {

                    absenceDateList.remove(position);
                    notifyDataSetChanged();
                    if (recyclerAbsence.getAdapter() != null) {
                        if( recyclerAbsence.getAdapter().getItemCount()==0)
                        {
                            btnSaveAbsence.setVisibility(View.INVISIBLE);
                            subHeadingAbsence.setVisibility(View.INVISIBLE);
                        }
                    }
                }

            }, anim.getDuration());
        }

        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_absence_date, parent, false);
            return new MonthViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MonthViewHolder holder, final int position) {


            try {


                holder.txtYear.setText(absenceDateList.getJSONObject(position).getString("year"));
                holder.txtMonth.setText(absenceDateList.getJSONObject(position).getString("month"));
                holder.txtDate.setText(absenceDateList.getJSONObject(position).getString("date"));
                holder.txtSession.setText(absenceDateList.getJSONObject(position).getString("session"));


                holder.btnDeleteDate.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (monthArray.size() == 1)
                            subHeadingAbsence.setVisibility(View.INVISIBLE);
                        deleteItem(holder.rltMonth, position);

                    }
                });
            } catch (Exception e) {

            }
        }

//            if (selectedItem == position)
//                holder.rltMonthFocus.setSelected(true);
//            else
//                holder.rltMonthFocus.setSelected(false);
//
//
//            holder.txtMonth.setText(monthArray.get(position));
//            holder.rltMonth.setOnClickListener(new View.OnClickListener() {
//                @RequiresApi(api = Build.VERSION_CODES.O)
//                @Override
//                public void onClick(View v) {
//
//                    getDaysForMonth(position + 1, year);
//                    selectedItem = position;
//                    notifyDataSetChanged();
//                    recyclerMonthDate.getAdapter().notifyDataSetChanged();
//                }
//            });
//    }

        @Override
        public int getItemCount() {
            return absenceDateList.length();
        }
    }

    //    class DateViewHolder extends RecyclerView.ViewHolder {
//        RelativeLayout rltDate;
//        TextView txtDate;
//
//        public DateViewHolder(@NonNull View itemView) {
//            super(itemView);
//            rltDate = itemView.findViewById(R.id.rlt_date);
//            txtDate = itemView.findViewById(R.id.txt_date);
//        }
//    }
//
//    class DateItemAdapter extends RecyclerView.Adapter<DateViewHolder> {
//
//        @NonNull
//        @Override
//        public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(getApplicationContext())
//                    .inflate(R.layout.item_date, parent, false);
//            return new DateViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
//
//            holder.txtDate.setText(String.valueOf(position + 1) + getDateSuffix(position + 1) + " " + dayArray.get(position));
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return dayArray.size();
//        }
//    }
//
    private String getDateSuffix(int i) {
        switch (i) {
            case 1:
            case 21:
            case 31:
                return (String.valueOf(i) + "st ");

            case 2:
            case 22:
                return (String.valueOf(i) + "nd ");

            case 3:
            case 23:
                return (String.valueOf(i) + "rd ");

            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
                return (String.valueOf(i) + "th ");
            default:
                return ("");
        }
    }
}