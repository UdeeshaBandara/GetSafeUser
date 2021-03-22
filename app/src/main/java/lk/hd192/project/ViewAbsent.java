package lk.hd192.project;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBaseFragment;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.listeners.OnMonthChangeListener;
import sun.bob.mcalendarview.vo.DateData;


public class ViewAbsent extends GetSafeBaseFragment {

    MCalendarView mCalendarView;
    GetSafeServices getSafeServices;
    TinyDB tinyDB;
    Dialog dialog;
    Button btn_delete_absent;
    JSONObject absentDetails;
    TextView txt_date, txt_session, txt_month,txt_no_date;
    static String months[];
    String selectedDateID;
    LinearLayout lnr_absent_details;

    public ViewAbsent() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_absent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mCalendarView = view.findViewById(R.id.calendar_exp);
        txt_date = view.findViewById(R.id.txt_date);
        txt_session = view.findViewById(R.id.txt_session);
        lnr_absent_details = view.findViewById(R.id.lnr_absent_details);

        txt_month = view.findViewById(R.id.txt_month);
        txt_no_date = view.findViewById(R.id.txt_no_date);
        btn_delete_absent = view.findViewById(R.id.btn_delete_absent);
        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getActivity());
        dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);


        months = new String[13];
        months[0] = null;
        months[1] = "January";
        months[2] = "February";
        months[3] = "March";
        months[4] = "April";
        months[5] = "May";
        months[6] = "June";
        months[7] = "July";
        months[8] = "August";
        months[9] = "September";
        months[10] = "October";
        months[11] = "November";
        months[12] = "December";

        btn_delete_absent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                deleteAbsent();
            }
        });
        mCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
//                txt_no_date.
                showDateDetails(date);
//                Toast.makeText(getActivity(), String.format("%d-%d", date.getMonth(), date.getDay()), Toast.LENGTH_SHORT).show();
            }
        });


        mCalendarView.setOnMonthChangeListener(new OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {


                txt_month.setText(months[month] + " " + String.valueOf(year));

            }
        });

        if (tinyDB.getBoolean("isStaffAccount")) {
            getUserAbsent();
        } else {
            getChildAbsent();
        }

    }

    public void getUserAbsent() {
        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getActivity(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_ABSENT_USER), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("absent dates", result + "");
                    if (!result.getBoolean("status"))

                        showToast(dialog, "Failed to get dates. Please try again.", 0);
                    else {
                        absentDetails = new JSONObject();
                        absentDetails = result;
                        markDates();
                    }


                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });
    }

    public void getChildAbsent() {
        HashMap<String, String> tempParam = new HashMap<>();

        getSafeServices.networkJsonRequest(getActivity(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_ABSENT_CHILD) + "?id=" + tinyDB.getString("selectedChildId"), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("absent dates", result + "");

                    if (!result.getBoolean("status"))

                        showToast(dialog, "Failed to get dates. Please try again.", 0);
                    else {
                        absentDetails = new JSONObject();
                        absentDetails = result;
                        markDates();
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });
    }

    public void removeDates() {
        try {

            Log.e("UNmark", "exe");
            for (int i = 0; i < absentDetails.getJSONArray("models").length(); i++) {

                mCalendarView.unMarkDate(new DateData(Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(0, 4)), Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(5, 7)), Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(8, 10))));//mark multiple dates with this code.
                Log.e("unmark", "dates");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void markDates() {

        try {

            Log.e("mark", absentDetails.getJSONArray("models").getJSONObject(0).getString("date"));
            for (int i = 0; i < absentDetails.getJSONArray("models").length(); i++) {
                mCalendarView.markDate(new DateData(Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(0, 4)), Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(5, 7)), (Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(8, 10)))).setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, R.color.button_blue)));//mark multiple dates with this code.


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showDateDetails(DateData date) {
        try {
           boolean isAvailable=false;
            String dtStart;
            if (String.valueOf(date.getMonth()).length() == 1 & String.valueOf(date.getDay()).length() == 1)

                dtStart = date.getYear() + "-0" + date.getMonth() + "-0" + date.getDay();
            else if (String.valueOf(date.getMonth()).length() == 1 & String.valueOf(date.getDay()).length() == 2)
                dtStart = date.getYear() + "-0" + date.getMonth() + "-" + date.getDay();
            else if (String.valueOf(date.getMonth()).length() == 2 & String.valueOf(date.getDay()).length() == 1)
                dtStart = date.getYear() + "-" + date.getMonth() + "-0" + date.getDay();
            else
                dtStart = date.getYear() + "-" + date.getMonth() + "-" + date.getDay();


            for (int i = 0; i < absentDetails.getJSONArray("models").length(); i++) {
//                mCalendarView.markDate(new DateData(Integer.parseInt(), Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(5, 7)), Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(8, 10))).setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, R.color.button_blue)));//mark multiple dates with this code.
                if (absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(0, 10).equals(dtStart)) {
                    txt_no_date.setVisibility(View.GONE);
                    lnr_absent_details.setVisibility(View.VISIBLE);
                    btn_delete_absent.setVisibility(View.VISIBLE);
                    isAvailable=true;
                    selectedDateID = absentDetails.getJSONArray("models").getJSONObject(i).getString("id");
                    if ((Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(8, 10)) + 1) == 4) {
                        Log.e("session", absentDetails.getJSONArray("models").getJSONObject(i).getString("type"));
                    }
                    if (absentDetails.getJSONArray("models").getJSONObject(i).getInt("type") == 3)
                        txt_session.setText("Morning & Evening");
                    else if (absentDetails.getJSONArray("models").getJSONObject(i).getInt("type") == 2)
                        txt_session.setText("Evening");
                    else if (absentDetails.getJSONArray("models").getJSONObject(i).getInt("type") == 1)
                        txt_session.setText("Morning");
                    txt_date.setText(dtStart);

                }

            }

            if(!isAvailable){
                txt_no_date.setText("Absent not available on selected date");
                txt_no_date.setVisibility(View.VISIBLE);
                lnr_absent_details.setVisibility(View.GONE);
                btn_delete_absent.setVisibility(View.GONE);

            }

            Log.e("date", dtStart.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteAbsent() {


        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getActivity(), tempParam, getString(R.string.BASE_URL) + getString(R.string.DELETE_ABSENT_CHILD) + "?id=" + selectedDateID, 4, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    if (!result.getBoolean("status"))

                        showToast(dialog, "Failed to delete. Please try again.", 0);
                    else {

                        showToast(dialog, "Deleted successfully.", 2);
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });


    }
}