package com.example.mypc.a8;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Comment extends AppCompatActivity {

    private ImageButton commentSendButton;
    private EditText commentText;
    private RecyclerView recyclerView;
    private String PostKey, currentUserId, saveDate, saveTime, commentRondomName;
    private DatabaseReference CommentRef, UsersRef ;
    private String comPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actitity_comment);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        PostKey = getIntent().getStringExtra("PostKey");
        CommentRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey).child("Comments");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        commentSendButton = findViewById(R.id.comment_send_button);
        commentText = findViewById(R.id.comment_text);
        recyclerView = findViewById(R.id.all_users_post_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        commentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UsersRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String userFullName = dataSnapshot.child("Fullanme").getValue().toString();
                        String profileImage = dataSnapshot.child("profileImage").getValue().toString();

                        ValidateComment(userFullName, profileImage);

                        commentText.setText("");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();
        DisplayAllComments();
    }

    private void DisplayAllComments() {


        FirebaseRecyclerAdapter<getComments, PostsViewHolder > firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<getComments, PostsViewHolder>
                        (
                                getComments.class,
                                R.layout.comment_row,
                                PostsViewHolder.class,
                                CommentRef
                        ) {

                    @Override
                    protected void populateViewHolder(final PostsViewHolder viewHolder, final getComments model, final int position) {

                        final String PostKey = getRef(position).getKey();

                            String arr[] = model.getTime().split(":");
                            viewHolder.setFullname(model.getFullname());
                            viewHolder.setDate(model.getDate());
                            viewHolder.setTime(arr[0] + ":" + arr[1]);
                            viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                            viewHolder.setCommentText(model.getCommentText());

                        viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (model.getUserId().equals(currentUserId)) {
                                    comPosition = getRef(position).getKey();
                                    registerForContextMenu(viewHolder.mview);
                                }
                            }
                        });

                    }
                };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

                menu.add(0, 1, 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                CommentRef.child(comPosition).removeValue();
                break;

        }
        return super.onContextItemSelected(item);
    }



    private static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mview;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mview=itemView;

        }

        public void setDate(String date) {
            TextView textView = mview.findViewById(R.id.comment_date);
            textView.setText(date);
        }

        public void setCommentText(String commentText) {
            TextView textView = mview.findViewById(R.id.comment_comment);
            textView.setText(commentText);
        }

        public void setFullname(String fullname) {
            TextView textView = mview.findViewById(R.id.comment_fullname);
            textView.setText(fullname);
        }

        public void setProfileImage(Context context, String profileImage) {
            CircleImageView image = mview.findViewById(R.id.comment_image);
            Picasso.with(context).load(profileImage).into(image);
        }

        public void setTime(String time) {
            TextView textView = mview.findViewById(R.id.comment_time);
            textView.setText(time);
        }
    }


    private void ValidateComment(String userFullName, String profileImage) {

        String commText = commentText.getText().toString();

        if (TextUtils.isEmpty(commText)){
            commentText.setError(getString(R.string.error_empty));
        }
        else {
            Calendar calendar=Calendar.getInstance();
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
            saveDate=dateFormat.format(calendar.getTime());
            SimpleDateFormat timeFormat =new SimpleDateFormat("HH:MM:SS");
            saveTime = timeFormat.format(calendar.getTime());

            commentRondomName=currentUserId + saveDate + saveTime;

            HashMap hashMap = new HashMap();
            hashMap.put("userId", currentUserId);
            hashMap.put("Date", saveDate);
            hashMap.put("Time", saveTime);
            hashMap.put("CommentText", commText);
            hashMap.put("Fullname", userFullName);
            hashMap.put("ProfileImage", profileImage);
            CommentRef.child(commentRondomName).updateChildren(hashMap)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){

                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Comment save failed", Toast.LENGTH_SHORT).show();
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
