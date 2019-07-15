package com.example.mypc.a8;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messages extends AppCompatActivity {

    private String Fullname, profileImage, receiverId, senderId, saveDate, saveTime, token, data;
    private ImageButton message_send_button;
    private EditText message_text;
    private RecyclerView recyclerView;
    private final List<getMessages> getMessagesList = new ArrayList<>();
    private MessagesAdapter messagesAdapter;
    private LinearLayoutManager linearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        Fullname = getIntent().getStringExtra("Fullname");
        profileImage = getIntent().getStringExtra("profileImage");
        receiverId = getIntent().getStringExtra("Id");
//          token = getIntent().getStringExtra("token");
//        Log.d("token", token);
        senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        getSupportActionBar().setTitle(Fullname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        message_send_button = findViewById(R.id.message_send_button);
        message_text = findViewById(R.id.message_text);

        message_send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        messagesAdapter = new MessagesAdapter(getMessagesList);
        recyclerView = findViewById(R.id.all_users_post_list);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(messagesAdapter);

        FetchMessages();
    }

    private void FetchMessages() {
        FirebaseDatabase.getInstance().getReference().child("Messages").child(senderId).child(receiverId)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()) {
                            getMessages messages = dataSnapshot.getValue(getMessages.class);
                            getMessagesList.add(messages);
                            messagesAdapter.notifyDataSetChanged();


                            if (!messages.getFrom().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                            }
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void sendMessage() {

        final String messageText = message_text.getText().toString();

        if (TextUtils.isEmpty(messageText)){
            Toast.makeText(getApplicationContext(), "Please write text", Toast.LENGTH_SHORT).show();
        }
        else {
            String message_sender_ref = "Messages/" + senderId + "/" + receiverId;
            String message_receiver_ref = "Messages/" + receiverId + "/" + senderId;

            DatabaseReference user_message_key = FirebaseDatabase.getInstance().getReference().child("Messages").child(senderId).child(receiverId).push();
            String message_push_id = user_message_key.getKey();

            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
            saveDate=dateFormat.format(calendar.getTime());

            SimpleDateFormat timeFormat =new SimpleDateFormat("HH:MM:SS aa");
            saveTime = timeFormat.format(calendar.getTime());

            Map messageTextBody = new HashMap();
            messageTextBody.put("message", messageText);
            messageTextBody.put("date", saveDate);
            messageTextBody.put("time", saveTime);
            messageTextBody.put("type", "text");
            messageTextBody.put("from", senderId);
            messageTextBody.put("read", "unread");


            Map messageBodyDetalis = new HashMap();
            messageBodyDetalis.put(message_sender_ref + "/" + message_push_id + "/", messageTextBody);
            messageBodyDetalis.put(message_receiver_ref + "/" + message_push_id + "/", messageTextBody);

            FirebaseDatabase.getInstance().getReference().updateChildren(messageBodyDetalis).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                        message_text.setText("");
                    }
                    else {
                        String error = task.getException().getMessage();
                        Toast.makeText(getApplicationContext(), "Failed " + error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
