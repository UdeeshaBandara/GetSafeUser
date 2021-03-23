package lk.hd192.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import lk.hd192.project.Utils.GetSafeBase;
import lk.hd192.project.Utils.GetSafeServices;
import lk.hd192.project.Utils.VolleyJsonCallback;

public class Notification extends GetSafeBase {

    RecyclerView recyclerNotification;
    Button btnNotificationBack;
    JSONArray notifications;
    GetSafeServices getSafeServices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        btnNotificationBack=findViewById(R.id.btn_notification_back);

        notifications= new JSONArray();
        getSafeServices= new GetSafeServices();

        recyclerNotification=findViewById(R.id.recycler_notification);
        recyclerNotification.setAdapter(new NotificationAdapter());
        recyclerNotification.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


        btnNotificationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getAllNotifications();
    }


    class NotificationItem extends RecyclerView.ViewHolder{


        ImageView mark_as_read;
        TextView notification_date,notification_title,notification_content;


        public NotificationItem(@NonNull View itemView) {
            super(itemView);
            mark_as_read=itemView.findViewById(R.id.mark_as_read);
            notification_date=itemView.findViewById(R.id.notification_date);
            notification_title=itemView.findViewById(R.id.notification_title);
            notification_content=itemView.findViewById(R.id.notification_content);
        }
    }
    class NotificationAdapter extends RecyclerView.Adapter<NotificationItem>{

        @NonNull
        @Override
        public NotificationItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(R.layout.item_notification, parent, false);
            return new NotificationItem(view);
        }

        @Override
        public void onBindViewHolder(@NonNull NotificationItem holder, int position) {

            try{

                holder.notification_content.setText(notifications.getJSONObject(position).getString("message"));
                holder.notification_title.setText(notifications.getJSONObject(position).getString("title"));
                holder.notification_date.setText(notifications.getJSONObject(position).getString("created_at").substring(11,16));


                holder.mark_as_read.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return notifications.length();
        }
    }

    public void getAllNotifications() {
        HashMap<String, String> tempParam = new HashMap<>();


        getSafeServices.networkJsonRequest(getApplicationContext(), tempParam, getString(R.string.BASE_URL) + getString(R.string.GET_ALL_NOTIFICATIONS), 1, tinyDB.getString("token"), new VolleyJsonCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) {

                try {

Log.e("noti",result+"");

                    if (result.getBoolean("status")) {

                        notifications= result.getJSONArray("model");
                        recyclerNotification.getAdapter().notifyDataSetChanged();
                    } else {


                    }
                } catch (Exception e) {
                    e.printStackTrace();

                }

            }
        });


    }


}