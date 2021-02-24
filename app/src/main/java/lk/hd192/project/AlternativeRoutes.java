package lk.hd192.project;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.Utils.VolleyJsonCallback;
import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;

public class AlternativeRoutes extends GetSafeBase {
    MCalendarView mCalendarView;
    GetSafeServices getSafeServices;
    TinyDB tinyDB;
    Dialog dialog;
    JSONObject absentDetails;
    TextView txt_date,txt_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative_routes);
        mCalendarView = findViewById(R.id.calendar_exp);
        txt_date = findViewById(R.id.txt_date);
        txt_session = findViewById(R.id.txt_session);
        getSafeServices = new GetSafeServices();
        tinyDB = new TinyDB(getApplicationContext());
        dialog = new Dialog(this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
        absentDetails = new JSONObject();


        mCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                showDateDetails(date);
//                Toast.makeText(ViewAbsent.this, String.format("%d-%d", date.getMonth(), date.getDay()), Toast.LENGTH_SHORT).show();
            }
        });

        if (tinyDB.getBoolean("isStaffAccount")) {
            getUserAbsent();
        } else {
            getChildAbsent();
        }

    }

    private void getUserAbsent() {
        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_ABSENT_USER), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

                    Log.e("res",result+"");
                    if (!result.getBoolean("status"))

                        showToast(dialog, "Failed to get dates. Please try again.", 0);
                    else{
                        absentDetails=result;
                        markDates();
                    }



                } catch (Exception e) {

                }

            }
        });
    }

    private void getChildAbsent() {
        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_ABSENT_CHILD), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {
                    Log.e("res",result+"");
                    if (!result.getBoolean("status"))

                        showToast(dialog, "Failed to get dates. Please try again.", 0);
                    else{
                        absentDetails=result;
                        markDates();
                    }

                } catch (Exception e) {

                }

            }
        });
    }

    public void markDates() {

        try {




            for (int i = 0; i < absentDetails.getJSONArray("models").length(); i++) {
                mCalendarView.markDate(new DateData(Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(0,4)),Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(5,7)),Integer.parseInt(absentDetails.getJSONArray("models").getJSONObject(i).getString("date").substring(8,10))).setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, R.color.button_blue)));//mark multiple dates with this code.


              }
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    private  void showDateDetails(DateData date){


        String dtStart =date.getYear()+"-"+date.getMonth()+"-"+date.getDay();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            Date dateYo = format.parse(dtStart);
         Log.e("date",dateYo.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}