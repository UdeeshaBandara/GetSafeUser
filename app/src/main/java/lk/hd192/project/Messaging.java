package lk.hd192.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.hd192.project.Utils.GetTimeAgo;
import lk.hd192.project.Utils.TinyDB;
import lk.hd192.project.pojo.Messages;

public class Messaging extends AppCompatActivity {


    SwipeRefreshLayout mRefreshLayout;
    EditText txtMsgContent;


    private ImageView btnSend;
TextView txt_driver_name;

    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int mCurrentPage = 1;
    private DatabaseReference mRootRef, messageRef;
    private RecyclerView mMessageList, chatMessageView;
    TinyDB tinyDB;
    private final List<Messages> messagesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        tinyDB = new TinyDB(getApplicationContext());
        txt_driver_name=findViewById(R.id.txt_driver_name);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        try{
            txt_driver_name.setText("Driver "+getIntent().getStringExtra("driver_name"));

        }
        catch (Exception e){
            e.printStackTrace();
        }

        mRootRef = FirebaseDatabase.getInstance().getReference();
        if (tinyDB.getBoolean("isStaffAccount"))
            messageRef = mRootRef.child("Staff_Drivers").child(tinyDB.getString("driver_id")).child("Passengers").child(tinyDB.getString("user_id")).child("messages");
        else
            messageRef = mRootRef.child("School_Drivers").child(tinyDB.getString("kid_driver_id")).child("Passengers").child(tinyDB.getString("user_id")).child(tinyDB.getString("selectedChildId")).child("messages");


//        mChatUser = "a0tW1ZdZySMuDb28Za0RyoSDrlz1";
//        mCurrentUserId = mAuth.getCurrentUser().getUid();
//
//        mRootRef = FirebaseDatabase.getInstance().getReference();

        chatMessageView = findViewById(R.id.chat_message_view);
        mAdapter = new MessageAdapter(this, messagesList);
        mMessageList = findViewById(R.id.recycler_messages);
        mRefreshLayout = findViewById(R.id.message_swipe_layout);
        txtMsgContent = findViewById(R.id.txt_msg_content);
        btnSend = findViewById(R.id.btn_send);
        mLinearLayout = new LinearLayoutManager(this);
        mMessageList.setHasFixedSize(true);
        mMessageList.setLayoutManager(mLinearLayout);
        mMessageList.setAdapter(mAdapter);


        loadMessages();


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mCurrentPage++;
                messagesList.clear();
                loadMessages();
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                Log.e("msg send", "ok");
            }
        });

        findViewById(R.id.btn_message_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void sendMessage() {
        String message = txtMsgContent.getText().toString();
        if (!TextUtils.isEmpty(message)) {


            DatabaseReference user_message_push = messageRef.push();
            String push_id = user_message_push.getKey();

            java.util.Map messageMap = new HashMap();
            messageMap.put("message", message);
            messageMap.put("seen", false);
            messageMap.put("from", "user");
            messageMap.put("time", ServerValue.TIMESTAMP);

            txtMsgContent.setText("");
            messageRef.child(push_id).updateChildren(messageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e("Error", databaseError.getMessage());
                    }
                }
            });


        }
    }

    private void loadMessages() {

        //Reference location to user msgs

        Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);
        //Refresh recycle view with the newly sent msgs
        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Messages messages = dataSnapshot.getValue(Messages.class);
                messagesList.add(messages);
                mAdapter.notifyDataSetChanged();
                mMessageList.scrollToPosition(messagesList.size() - 1);
                mRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

        private List<Messages> mMessageList;
        Context mContext;

        public MessageAdapter(Context context, List<Messages> mMessageList) {
            this.mMessageList = mMessageList;
            mContext = context;

        }

        @NonNull
        @Override
        public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_message, parent, false);
            return new MessageViewHolder(v);
        }

        public class MessageViewHolder extends RecyclerView.ViewHolder {
            public TextView txtMsgFrom, txtMsgTo, txtMsgFromTime, txtMsgToTime;
            public LinearLayout lnrTo, lnrFrom;


            public MessageViewHolder(View view) {
                super(view);
                txtMsgFrom = view.findViewById(R.id.txt_msg_from);
                txtMsgTo = view.findViewById(R.id.txt_msg_to);
                txtMsgFromTime = view.findViewById(R.id.txt_msg_from_time);
                txtMsgFromTime = view.findViewById(R.id.txt_msg_from_time);
                txtMsgToTime = view.findViewById(R.id.txt_msg_to_time);
                lnrFrom = view.findViewById(R.id.lnr_from);
                lnrTo = view.findViewById(R.id.lnr_to);
            }


        }

        @Override
        public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {

            Messages c = mMessageList.get(position);


            String from_user = c.getFrom();

            String timeAgo = GetTimeAgo.getTimeAgo(c.getTime(), mContext);


            if (from_user.equals("user")) {
                holder.lnrFrom.setVisibility(View.GONE);
                holder.lnrTo.setVisibility(View.VISIBLE);
                holder.txtMsgTo.setText(c.getMessage());
                holder.txtMsgToTime.setText(timeAgo);

            } else {
                holder.lnrFrom.setVisibility(View.VISIBLE);
                holder.lnrTo.setVisibility(View.GONE);
                holder.txtMsgFrom.setText(c.getMessage());
                holder.txtMsgFromTime.setText(timeAgo);
            }


        }

        @Override
        public int getItemCount() {
            return mMessageList.size();
        }
    }

}