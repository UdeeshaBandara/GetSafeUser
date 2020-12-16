package lk.hd192.project;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lk.hd192.project.Utils.GetSafeBase;

public class Absence extends GetSafeBase {

    Button btnMorning, btnEvening, btnBoth, btnBothSelect, btnMorningSelect, btnEveningSelect;

    RecyclerView recyclerMonth, recyclerMonthDate;
    int currentYear,currentMonth,currentDate;

    SingleDateAndTimePicker monthPicker, dayPicker;

    ArrayList<String> monthArray;
    ArrayList<String> dayArray;

    SimpleDateFormat dateInFormat;
    Date date;

    int selectedItem = -1;

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        monthArray = new ArrayList<>();
        dayArray = new ArrayList<>();


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

        dayPicker.setEnabled(false);

//customToast("you have nothing",1);


        monthPicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {
                Log.e("date", date.getMonth()+"");
                dayPicker.setEnabled(true);

               if(currentDate>date.getMonth()){}


            }
        });

//        recyclerMonth = findViewById(R.id.recycler_month);
//        recyclerMonthDate = findViewById(R.id.recycler_month_date);


//        recyclerMonth.setAdapter(new MonthItemAdapter());
//        recyclerMonth.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
//        recyclerMonthDate.setAdapter(new DateItemAdapter());
//        recyclerMonthDate.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        btnBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBothSelect.setVisibility(View.VISIBLE);
                btnBoth.setVisibility(View.GONE);
                btnMorningSelect.setVisibility(View.GONE);
                btnMorning.setVisibility(View.VISIBLE);
                btnEveningSelect.setVisibility(View.GONE);
                btnEvening.setVisibility(View.VISIBLE);
                showAlertDialogButtonClicked(getApplicationContext(),"You have nothing!");


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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getDaysForMonth(int month, int year) {

        try {
            dayArray = new ArrayList<>();
            String day = "";
            int monthLength = YearMonth.of(year, month).lengthOfMonth();

            for (int i = 0; i < monthLength; i++) {

                date = dateInFormat.parse((i + 1) + "-" + month + "-" + year);

                day = android.text.format.DateFormat.format("EEEE", date).toString();
                dayArray.add(i, day);

            }


        } catch (Exception e) {

        }


    }


//    class MonthViewHolder extends RecyclerView.ViewHolder {
//        RelativeLayout rltMonth, rltMonthFocus;
//        TextView txtMonth;
//
//
//        public MonthViewHolder(@NonNull View itemView) {
//            super(itemView);
//            rltMonth = itemView.findViewById(R.id.rlt_month);
//            rltMonthFocus = itemView.findViewById(R.id.rlt_month_focus);
//            txtMonth = itemView.findViewById(R.id.txt_month);
//        }
//
//
//    }
//
//    class MonthItemAdapter extends RecyclerView.Adapter<MonthViewHolder> {
//
//
//
//        @NonNull
//        @Override
//        public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(getApplicationContext())
//                    .inflate(R.layout.item_month, parent, false);
//            return new MonthViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull final MonthViewHolder holder, final int position) {
//
//
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
//        }
//
//        @Override
//        public int getItemCount() {
//            return monthArray.size();
//        }
//    }

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
//    private String getDateSuffix(int i) {
//        switch (i) {
//            case 1:
//            case 21:
//            case 31:
//                return ("st");
//
//            case 2:
//            case 22:
//                return ("nd");
//
//            case 3:
//            case 23:
//                return ("rd");
//
//            case 4:
//            case 5:
//            case 6:
//            case 7:
//            case 8:
//            case 9:
//            case 10:
//            case 11:
//            case 12:
//            case 13:
//            case 14:
//            case 15:
//            case 16:
//            case 17:
//            case 18:
//            case 19:
//            case 20:
//            case 24:
//            case 25:
//            case 26:
//            case 27:
//            case 28:
//            case 29:
//            case 30:
//                return ("th");
//            default:
//                return ("");
//        }
//    }
}