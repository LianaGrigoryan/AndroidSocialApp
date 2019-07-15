package com.example.mypc.a8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {

    private CircleImageView profileImage;
    private TextView profileFullname, profileUsername, profileStatus, profileCountry, profileRelationship;
    private DatabaseReference databaseReference, PostsRef, LikesRef, FriendRef;
    private FirebaseAuth mAuth;
    private String currentuserId, userIdpos, fullname, country, profileimage, token;
    private RecyclerView myposts;
    private ImageButton add_friend, delete_friend, send_message;
    private  Query searchQuery;
    private Boolean LikeChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userIdpos = getIntent().getStringExtra("userId");

        profileImage = findViewById(R.id.myprofile_image);
        profileFullname = findViewById(R.id.myprofile_fullname);
        profileUsername = findViewById(R.id.myprofile_username);
        profileStatus = findViewById(R.id.myprofile_status);
        profileCountry = findViewById(R.id.myprofile_country);
        profileRelationship = findViewById(R.id.myprofile_relationship);
        add_friend = findViewById(R.id.profile_add_friend);
        delete_friend = findViewById(R.id.profile_delete_friend);
        send_message = findViewById(R.id.profile_send_message);
        add_friend.setVisibility(View.INVISIBLE);
        delete_friend.setVisibility(View.INVISIBLE);
        send_message.setVisibility(View.INVISIBLE);
        mAuth = FirebaseAuth.getInstance();
        currentuserId = mAuth.getCurrentUser().getUid();
        myposts = findViewById(R.id.all_users_post_list);
        myposts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        myposts.setLayoutManager(linearLayoutManager);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        FriendRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserId).child("Friends");

        if (userIdpos==null) {

            searchQuery = PostsRef.orderByChild("userId")
                    .startAt(currentuserId).endAt(currentuserId + "\uf8ff");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentuserId);
        }
        else {

            add_friend.setVisibility(View.VISIBLE);
            delete_friend.setVisibility(View.VISIBLE);
            send_message.setVisibility(View.VISIBLE);
            searchQuery = PostsRef.orderByChild("userId")
                    .startAt(userIdpos).endAt(userIdpos + "\uf8ff");
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userIdpos);
        }

        FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                        (
                                Posts.class,
                                R.layout.all_posts,
                                PostsViewHolder.class,
                                searchQuery
                        ) {
                    @Override
                    protected void populateViewHolder(PostsViewHolder viewHolder, Posts model, int position) {
                        Log.i("profile", "adapter");

                        final String PostKey = getRef(position).getKey();
                        viewHolder.setLikeButtonStatus(PostKey);

                        String arr[]=model.getTime().split(":");

                        viewHolder.setUserFullName(model.getUserFullName());
                        viewHolder.setDate(model.getDate());
                        viewHolder.setTime(arr[0]+":"+arr[1]);
                        viewHolder.setDescription(model.getDescription());
                        viewHolder.setPostImage(getApplicationContext(), model.getPostImage());
                        viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());

                        viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), Click_Post.class);
                                intent.putExtra("PostKey", PostKey);
                                startActivity(intent);
                            }
                        });

                        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LikeChecked = true;

                                LikesRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (LikeChecked.equals(true)) {
                                            if (dataSnapshot.child(PostKey).hasChild(currentuserId)) {
                                                LikesRef.child(PostKey).child(currentuserId).removeValue();
                                                LikeChecked = false;
                                            } else {
                                                LikesRef.child(PostKey).child(currentuserId).setValue(true);
                                                LikeChecked = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        viewHolder.commentButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), Comment.class);
                                intent.putExtra("PostKey", PostKey);
                                startActivity(intent);
                            }
                        });
                    }
                };

        myposts.setAdapter(firebaseRecyclerAdapter);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    profileimage = dataSnapshot.child("profileImage").getValue().toString();
                    fullname = dataSnapshot.child("Fullanme").getValue().toString();
                    country = dataSnapshot.child("Country").getValue().toString();
                   // token = dataSnapshot.child("token").getValue().toString();
                    if (profileimage.equals(""+".jpg")){
                        Picasso.with(getApplicationContext()).load(R.mipmap.profile).into(profileImage);
                    }
                    else {
                        Picasso.with(getApplicationContext()).load(profileimage).into(profileImage);
                    }
                    profileFullname.setText(fullname);
                    profileUsername.setText(dataSnapshot.child("Username").getValue().toString());
                    profileStatus.setText(dataSnapshot.child("Status").getValue().toString());
                    profileCountry.setText(country);
                    profileRelationship.setText(dataSnapshot.child("Relationshipstatus").getValue().toString());
                    if (profileStatus.equals("")) {
                        profileStatus.setVisibility(View.INVISIBLE);
                    }
                    if (profileRelationship.equals("")) {
                        profileRelationship.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap hashMap = new HashMap();
                hashMap.put("Fullname", fullname);
                hashMap.put("Country", country);
                hashMap.put("Profileimage", profileimage);
                hashMap.put("token", token);
                FriendRef.child(userIdpos).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "You add new friend", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), FrinedsList.class));
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Friend adding failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        delete_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendRef.child(userIdpos).removeValue();
                Toast.makeText(getApplicationContext(), "You remove your friend", Toast.LENGTH_SHORT).show();
            }
        });

        send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        Intent intent = new Intent(getApplicationContext(), Messages.class);
//                        intent.putExtra("Fullname", dataSnapshot.child("Fullanme").getValue().toString());
//                        intent.putExtra("profileImage", dataSnapshot.child("profileImage").getValue().toString());
//                        intent.putExtra("Id", userIdpos);

                        String fullname = dataSnapshot.child("Fullanme").getValue().toString();
                        String profileimage = dataSnapshot.child("profileImage").getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Intent intent = new Intent(getApplicationContext(), Messages.class);
                intent.putExtra("Fullname", fullname);
                intent.putExtra("profileImage", profileimage);
                intent.putExtra("Id", userIdpos);
                startActivity(intent);
            }
        });
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

    private static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mview;
        ImageButton likeButton, commentButton;
        TextView likesCount;
        int countLikes;
        DatabaseReference LikesRef;
        String currentUserId;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mview = itemView;

            likeButton = mview.findViewById(R.id.like);
            commentButton = mview.findViewById(R.id.comment);
            likesCount = mview.findViewById(R.id.like_count);

            LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
            currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        public void setLikeButtonStatus(final String postKey) {

            LikesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postKey).hasChild(currentUserId)){
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.like);
                        likesCount.setText(Integer.toString(countLikes) + " likes");
                    }
                    else {
                        countLikes = (int) dataSnapshot.child(postKey).getChildrenCount();
                        likeButton.setImageResource(R.drawable.dislike);
                        likesCount.setText(Integer.toString(countLikes) + " likes");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setUserFullName(String userFullName) {
            TextView UserFullName = mview.findViewById(R.id.all_posts_fullname);
            UserFullName.setText(userFullName);
        }

        public void setDate(String date) {
            TextView setdate = mview.findViewById(R.id.all_posts_date);
            setdate.setText("  " + date);
        }

        public void setTime(String time) {
            TextView settime = mview.findViewById(R.id.all_posts_time);
            settime.setText("  " + time);
        }

        public void setDescription(String description) {
            TextView setdescription = mview.findViewById(R.id.all_posts_description);
            setdescription.setText(description);
        }

        public void setProfileImage(Context context, String profileImage) {
            CircleImageView setimage = mview.findViewById(R.id.all_posts_profile_img);
            Picasso.with(context).load(profileImage).into(setimage);
        }

        public void setPostImage(Context context, String postImage) {
            ImageView imageView = mview.findViewById(R.id.all_posts_image);
            Picasso.with(context).load(postImage).into(imageView);
        }
    }
}
