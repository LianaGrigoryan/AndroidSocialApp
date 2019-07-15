package com.example.mypc.a8;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyService extends Service {
    private String LOG_TAG = "service";
    private String currentUserId;
    private Iterable<DataSnapshot> list;
    private List<String> m = new ArrayList<>();
    private Set<String> stringSet = new HashSet<>();
    private Set<String> stringSet1 = new HashSet<>();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    public boolean stopflag;
    private Thread thread;
    private String str, sender, l , senderfn;
    private DatabaseReference UserRef;

    @Override
    public void onCreate() {
        super.onCreate();
        stopflag = true;
        Log.d(LOG_TAG, "onCreate");
        currentUserId= FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d(LOG_TAG, currentUserId);
        mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        mEditor = mSharedPreferences.edit();
        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");


        firebaseDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                list = new ArrayList<>();
                    list = dataSnapshot.getChildren();
                for (DataSnapshot i : list) {
                    getMessages getMessages = i.getValue(com.example.mypc.a8.getMessages.class);
                    str = getMessages.getFrom();
                    Log.i("s", str);

                    if (str.equals(currentUserId)){
                    }
                    else {
                        stringSet.add(i.getKey());
                    }
                }
                Log.i(LOG_TAG, "messagelist : " + stringSet.toString());
                mEditor.putStringSet("Messageslist", stringSet);
                mEditor.apply();
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
        stringSet.clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (stopflag) {
                    task();
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.i(LOG_TAG, "onTaskRemoved");
        Intent broadcastIntent = new Intent(getApplicationContext(), RestarterBroadcastReceiver.class);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        stopflag = false;
        Log.d(LOG_TAG, "onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void task() {

          //  mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
            final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId);
            firebaseDatabase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                                list = new ArrayList<>();
                                list = dataSnapshot.getChildren();

                                for (DataSnapshot i : list) {
                                    getMessages getMessages = i.getValue(com.example.mypc.a8.getMessages.class);
                                    str = getMessages.getFrom();
                                    Log.i("s", str);

                                    if (str.equals(currentUserId)) {
                                    } else {
                                        stringSet.add(i.getKey());
                                        m.add(i.getKey());
                                    }
                                }
                                Log.i(LOG_TAG, "m : " + m.toString());
                                Log.i(LOG_TAG, "messagelist : " + stringSet.toString());
                                stringSet1 = mSharedPreferences.getStringSet("Messageslist", null);
                                if (stringSet.equals(stringSet1)) {
                                    Log.i(LOG_TAG, "OK");
                                } else {
                                    l = m.get(m.size() - 1);
                                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                                        if (i.getKey().equals(l)) {
                                            sender = i.child("from").getValue().toString();
                                        }
                                    }

                                    UserRef.child(sender).child("Fullanme").addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                           senderfn = dataSnapshot.getValue().toString();
                                            Notification notification = new Notification(getApplicationContext());
                                            notification.onMessageReceives(senderfn);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    Log.i(LOG_TAG, "sender : " + sender);
                                    Log.i(LOG_TAG, "Failed");

                                    mEditor.putStringSet("Messageslist", stringSet);
                                    mEditor.apply();
                                    mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
                                }
                                Log.i(LOG_TAG, "MessageList : " + stringSet1.toString());
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
}
