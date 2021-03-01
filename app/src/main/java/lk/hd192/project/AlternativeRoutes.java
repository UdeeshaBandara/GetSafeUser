package lk.hd192.project;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;


public class AlternativeRoutes extends GetSafeBase {

    JSONObject singleAlternate;
    JSONArray alternateRoute;
    Button btn_add;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alternative_routes);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        btn_add=findViewById(R.id.btn_add);

        alternateRoute=new JSONArray();

        findViewById(R.id.card_date).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.e("date","cli");
                new SingleDateAndTimePickerDialog.Builder(getApplicationContext())
                        //.bottomSheet()
                        //.curved()
                        //.stepSizeMinutes(15)
                        //.displayHours(false)
                        //.displayMinutes(false)
                        //.todayText("aujourd'hui")
                        .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                            @Override
                            public void onDisplayed(SingleDateAndTimePicker picker) {

                            }
                        })
                        .title("Simple")
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {

                            }
                        }).display();
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDate();
            }
        });
    }

    private void addDate(){

        try {
            singleAlternate = new JSONObject();
            singleAlternate.put("date","");
            singleAlternate.put("location","");
            singleAlternate.put("session","");

        }
        catch (Exception e){
            e.printStackTrace();
        }





    }
    class AlternativeAbsentHolder extends RecyclerView.ViewHolder{

        public AlternativeAbsentHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    class AlternativeAbsentAdapter extends RecyclerView.Adapter<AlternativeAbsentHolder>{

        @NonNull
        @Override
        public AlternativeAbsentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_home, parent, false);
            return new AlternativeAbsentHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AlternativeAbsentHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }


}