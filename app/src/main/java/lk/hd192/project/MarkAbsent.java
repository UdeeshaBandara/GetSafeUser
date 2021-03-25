package lk.hd192.project;

import android.animation.Animator;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBaseFragment;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;

import static lk.hd192.project.Absence.isNewAbsentAdded;

public class MarkAbsent extends GetSafeBaseFragment {
    Button btnMorning, btnEvening, btnBoth, btnBothSelect, btnMorningSelect, btnEveningSelect, btnSaveAbsence, btnAddToList;

    RecyclerView recyclerAbsence;
    CheckBox chk_repeat;
    TinyDB tinyDB;

    int currentYear, currentMonth, currentDate;

    SingleDateAndTimePicker monthPicker;
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
    GetSafeServices getSafeServices;
    TextView sub_heading_absence;
    Dialog dialog;

    public MarkAbsent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mark_absent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        monthArray = new ArrayList<>();
        dayArray = new ArrayList<>();
        absenceDateList = new JSONArray();
        absenceDate = new JSONObject();
        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getActivity());


        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);

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
        selectedMonthNumber = currentMonth;
        selectedDateNumber = currentDate;
        selectedDay = dayNames[Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1];
        dateInFormat = new SimpleDateFormat("dd-MM-yyyy");


        btnBoth = view.findViewById(R.id.btn_both);
        btnBothSelect = view.findViewById(R.id.btn_both_select);
        btnEvening = view.findViewById(R.id.btn_evening);
        btnMorning = view.findViewById(R.id.btn_morning);
        btnEveningSelect = view.findViewById(R.id.btn_evening_select);
        btnMorningSelect = view.findViewById(R.id.btn_morning_select);
        monthPicker = view.findViewById(R.id.month_picker);
        sub_heading_absence = view.findViewById(R.id.sub_heading_absence);

        btnSaveAbsence = view.findViewById(R.id.btn_save_absence);

        subHeadingAbsence = view.findViewById(R.id.sub_heading_absence);
        btnAddToList = view.findViewById(R.id.btn_add_to_list);
        chk_repeat = view.findViewById(R.id.chk_repeat);

        chk_repeat.setText("Repeat on every " + selectedDay);


        btnSaveAbsence.setVisibility(View.INVISIBLE);
        subHeadingAbsence.setVisibility(View.INVISIBLE);
        if (tinyDB.getBoolean("isStaffAccount")) {

            sub_heading_absence.setVisibility(View.INVISIBLE);
        } else {

            sub_heading_absence.setText(tinyDB.getString("selectedChildName") + " Absence on");
        }
//        btnSaveAbsence.setBackgroundColor(getResources().getColor(R.color.sec_color));


//customToast("you have nothing",1);


        btnSaveAbsence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for (int j = 0; j < absenceDateList.length(); j++) {
                    try {
                        String type = "";
                        if (absenceDateList.getJSONObject(j).getString("session").equals("Both"))
                            type = "3";
                        else if (absenceDateList.getJSONObject(j).getString("session").equals("Evening"))
                            type = "2";
                        else if (absenceDateList.getJSONObject(j).getString("session").equals("Morning"))
                            type = "1";
                        String date = absenceDateList.getJSONObject(j).getString("date").substring(0, 2) + "-" + (String.valueOf(monthArray.indexOf(absenceDateList.getJSONObject(j).getString("month")) + 1)) + "-" + absenceDateList.getJSONObject(j).getString("year");
                        Log.e("selected date ", date);
                        if (tinyDB.getBoolean("isStaffAccount")) {
                            addUserAbsent(date, type);
                        } else {
                            addChildAbsent(date, type);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


            }
        });

        monthPicker.addOnDateChangedListener(new SingleDateAndTimePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(String displayed, Date date) {


                if (date.getMonth() == currentMonth & date.getDate() < currentDate)
                    currentYear = Integer.parseInt(String.valueOf(date.getYear()).substring(1)) + 2001;
                else if (date.getMonth() < currentMonth)
                    currentYear = Integer.parseInt(String.valueOf(date.getYear()).substring(1)) + 2001;
                else
                    currentYear = Integer.parseInt(String.valueOf(date.getYear()).substring(1)) + 2000;
                selectedMonthNumber = date.getMonth();
                selectedDateNumber = date.getDate();
                Log.e("selectedDateNumber", String.valueOf(selectedDateNumber));
                selectedDay = dayNames[date.getDay()];
                chk_repeat.setText("Repeat on every " + selectedDay);


            }
        });

        btnAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("ontap", "btnAddToList");
                boolean isAdded = false;
                absenceDate = new JSONObject();

                if (tinyDB.getBoolean("isStaffAccount")) {

                    sub_heading_absence.setVisibility(View.INVISIBLE);
                }
                try {
                    absenceDate.put("month", monthArray.get(selectedMonthNumber));
                    absenceDate.put("year", currentYear);
                    absenceDate.put("date", getDateSuffix(selectedDateNumber) + selectedDay);


                    if (btnBothSelect.getVisibility() == View.GONE & btnEveningSelect.getVisibility() == View.GONE & btnMorningSelect.getVisibility() == View.GONE)
                        showToast(dialog, "Please select session", 0);

                    else {
                        if (btnBothSelect.getVisibility() == View.VISIBLE)
                            absenceDate.put("session", "Both");
                        else if (btnEveningSelect.getVisibility() == View.VISIBLE)
                            absenceDate.put("session", "Evening");
                        else if (btnMorningSelect.getVisibility() == View.VISIBLE)
                            absenceDate.put("session", "Morning");
//
                        if (absenceDateList.length() == 0)
                            absenceDateList.put(absenceDate);
                        else {
                            for (int i = 0; i < absenceDateList.length(); i++) {
                                if (absenceDateList.getJSONObject(i).getString("month").equals(absenceDate.getString("month")) & absenceDateList.getJSONObject(i).getString("year").equals(absenceDate.getString("year")) & absenceDateList.getJSONObject(i).getString("date").equals(absenceDate.getString("date"))) {
                                    if(absenceDate.getString("session").equals("Both")&!absenceDateList.getJSONObject(i).getString("session").equals("Both")){
                                        showToast(dialog,  absenceDateList.getJSONObject(i).getString("session") +" session is ", 0);
                                        absenceDateList.remove(i);
                                        absenceDateList.put(absenceDate);

                                    }else if(absenceDate.getString("session").equals("Evening")||absenceDate.getString("session").equals("Morning")){

                                        absenceDateList.getJSONObject(i).put("session","Both");
                                    }

//if ( absenceDateList.getJSONObject(i).getString("session").equals(absenceDate.getString("session"))

//                                    showToast(dialog, "Date already added", 0);
                                    isAdded = true;
                                }


                            }
                            if (!isAdded)
                                absenceDateList.put(absenceDate);
                        }


//
//                            Log.e("hash", String.valueOf(String.valueOf( absenceDateList.get(1)).hashCode()));
//                        else
//
//                            Log.e("hash", String.valueOf(String.valueOf( absenceDateList.get(0)).hashCode()));

                        btnSaveAbsence.setVisibility(View.VISIBLE);
                        subHeadingAbsence.setVisibility(View.VISIBLE);

                        btnSaveAbsence.setBackground(getActivity().getDrawable(R.drawable.bg_btn_resend));
                        recyclerAbsence.getAdapter().notifyDataSetChanged();
                    }


                } catch (Exception e) {

                    Log.e("exception", e.getMessage());
                }

            }
        });

//
//        mainSaveAnimation.addAnimatorListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                mainSaveAnimation.setVisibility(View.GONE);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//
//                    }
//                }, 800);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animation) {
//
//            }
//        });


        recyclerAbsence = view.findViewById(R.id.recycler_absence);


        recyclerAbsence.setAdapter(new MonthItemAdapter());
        recyclerAbsence.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        btnBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBothSelect.setVisibility(View.VISIBLE);
                btnBoth.setVisibility(View.GONE);
                btnMorningSelect.setVisibility(View.GONE);
                btnMorning.setVisibility(View.VISIBLE);
                btnEveningSelect.setVisibility(View.GONE);
                btnEvening.setVisibility(View.VISIBLE);

            }
        });
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

            Animation anim = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.slide_bottom);
            anim.setDuration(300);
            view.startAnimation(anim);

            new Handler().postDelayed(new Runnable() {
                public void run() {

                    absenceDateList.remove(position);
                    notifyDataSetChanged();
                    if (recyclerAbsence.getAdapter() != null) {
                        if (recyclerAbsence.getAdapter().getItemCount() == 0) {
                            btnSaveAbsence.setVisibility(View.INVISIBLE);
                            subHeadingAbsence.setVisibility(View.INVISIBLE);
                        }
                    }
                }

            }, anim.getDuration());
        }

        @Override
        public MonthViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity())
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


        @Override
        public int getItemCount() {
            return absenceDateList.length();
        }
    }

    private String getDateSuffix(int i) {
        switch (i) {
            case 1:
                return ("0" + String.valueOf(i) + "st ");
            case 21:
            case 31:
                return (String.valueOf(i) + "st ");

            case 2:
                return (String.valueOf(i) + "nd ");
            case 22:
                return ("0" + String.valueOf(i) + "nd ");

            case 3:
                return ("0" + String.valueOf(i) + "rd ");
            case 23:
                return (String.valueOf(i) + "rd ");

            case 4:
            case 9:
            case 5:
            case 6:
            case 7:
            case 8:
                return ("0" + String.valueOf(i) + "th ");


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

    public void addUserAbsent(String date, String type) {
        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("date", date);
        tempParam.put("type", type);
        tempParam.put("repeat", "0");


        getSafeServices.networkJsonRequest(getActivity(), tempParam, getString(R.string.BASE_URL) + getString(R.string.USER_ABSENCE), 2, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("res", result + "");

                    if (result.getBoolean("status")) {
                        showToast(dialog, "Absent date(s) added", 2);
                        absenceDateList = new JSONArray();
                        recyclerAbsence.getAdapter().notifyDataSetChanged();
                        btnEveningSelect.setVisibility(View.GONE);
                        btnEvening.setVisibility(View.VISIBLE);
                        btnMorningSelect.setVisibility(View.GONE);
                        btnMorning.setVisibility(View.VISIBLE);
                        btnBothSelect.setVisibility(View.GONE);
                        btnBoth.setVisibility(View.VISIBLE);
                        btnSaveAbsence.setVisibility(View.INVISIBLE);
                        subHeadingAbsence.setVisibility(View.INVISIBLE);
                        isNewAbsentAdded = true;
                    } else
                        showToast(dialog, result.getString("validation_errors"), 0);


                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });


    }

    public void addChildAbsent(String date, String type) {
        HashMap<String, String> tempParam = new HashMap<>();
        tempParam.put("id", tinyDB.getString("selectedChildId"));
        tempParam.put("date", date);
        tempParam.put("type", type);
        tempParam.put("repeat", "0");


        getSafeServices.networkJsonRequest(getActivity(), tempParam, getString(R.string.BASE_URL) + getString(R.string.CHILD_ABSENCE), 2, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    Log.e("res", result + "");
                    if (result.getBoolean("status")) {
                        showToast(dialog, "Absent date(s) added", 2);
                        absenceDateList = new JSONArray();
                        recyclerAbsence.getAdapter().notifyDataSetChanged();
                        btnEveningSelect.setVisibility(View.GONE);
                        btnEvening.setVisibility(View.VISIBLE);
                        btnMorningSelect.setVisibility(View.GONE);
                        btnMorning.setVisibility(View.VISIBLE);
                        btnBothSelect.setVisibility(View.GONE);
                        btnBoth.setVisibility(View.VISIBLE);
                        btnSaveAbsence.setVisibility(View.INVISIBLE);
                        subHeadingAbsence.setVisibility(View.INVISIBLE);
                        isNewAbsentAdded = true;
                    } else
                        showToast(dialog, result.getString("validation_errors"), 0);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }
}