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

import lk.hd192.project.Utils.GetSafeBase;

public class Notification extends GetSafeBase {

    RecyclerView recyclerNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        recyclerNotification=findViewById(R.id.recycler_notification);
        recyclerNotification.setAdapter(new NotificationAdapter());
        recyclerNotification.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));


    }


    class NotificationItem extends RecyclerView.ViewHolder{

        public NotificationItem(@NonNull View itemView) {
            super(itemView);
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

        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

}