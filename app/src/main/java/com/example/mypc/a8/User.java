package com.example.mypc.a8;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class User extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private  FirebaseAuth mauth;
    private DatabaseReference UsersRef, PostsRef, LikesRef;
    private  DrawerLayout drawerLayout;
    private  NavigationView navigationView;
    private  View hView;
    private CircleImageView NavProfileImage;
    private TextView NavName, NavUserName;
    private String currentUserId;
    private RecyclerView allPostList;
    private Boolean LikeChecked;
    private NotificationCompat.Builder notification_builder;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = new Intent(getApplicationContext(), User.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);


        context = getApplicationContext();
        mauth =FirebaseAuth.getInstance();
        currentUserId=mauth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Posts");
        LikesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        allPostList = findViewById(R.id.all_users_post_list);
        allPostList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        allPostList.setLayoutManager(linearLayoutManager);


        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        drawerLayout =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        hView =  navigationView.getHeaderView(0);
        NavProfileImage = hView.findViewById(R.id.prifile_image);
        NavName = hView.findViewById(R.id.profile_username);
        NavUserName = hView.findViewById(R.id.show_email);
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    DataSnapshot contactSnapshot = dataSnapshot.child("Friends");
                    Iterable<DataSnapshot> contactChildren = contactSnapshot.getChildren();

                    for (DataSnapshot contact : contactChildren) {
                       getFriends c = contact.getValue(getFriends.class);
                        Log.i("contact:: ", c.getCountry());
                    }

                    String  name=dataSnapshot.child("Fullanme").getValue().toString();
                    String image=dataSnapshot.child("profileImage").getValue().toString();
                    String username=dataSnapshot.child("Username").getValue().toString();
                    NavName.setText(name);
                    NavUserName.setText(username);
                    if (image.equals("" + ".jpg")){
                        Picasso.with(getApplicationContext()).load(R.mipmap.profile).placeholder(R.mipmap.profile).into(NavProfileImage);
                    }
                    else {
                        Picasso.with(getApplicationContext()).load(image).placeholder(R.mipmap.profile).into(NavProfileImage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

       DisplayAllUsersPost();

    }


    private void DisplayAllUsersPost() {
        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                FirebaseRecyclerAdapter<Posts, PostsViewHolder > firebaseRecyclerAdapter =
                        new FirebaseRecyclerAdapter<Posts, PostsViewHolder>
                                (
                                        Posts.class,
                                        R.layout.all_posts,
                                        PostsViewHolder.class,
                                        PostsRef
                                )
                        {
                            @Override
                            protected void populateViewHolder(PostsViewHolder viewHolder, final Posts model, int position) {

                                final String PostKey = getRef(position).getKey();
                                String string =model.getUserId();

                                if (dataSnapshot.child("Friends").hasChild(string)) {
                                    String arr[] = model.getTime().split(":");
                                    viewHolder.setUserFullName(model.getUserFullName());
                                    viewHolder.setDate(model.getDate());
                                    viewHolder.setTime(arr[0] + ":" + arr[1]);
                                    viewHolder.setDescription(model.getDescription());
                                    viewHolder.setPostImage(getApplicationContext(), model.getPostImage());
                                    viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());

                                    viewHolder.setLikeButtonStatus(PostKey);

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
                                                        if (dataSnapshot.child(PostKey).hasChild(currentUserId)) {
                                                            LikesRef.child(PostKey).child(currentUserId).removeValue();
                                                            LikeChecked = false;
                                                        } else {
                                                            LikesRef.child(PostKey).child(currentUserId).setValue(true);
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
                                else {
                                    viewHolder.mview.setVisibility(View.INVISIBLE);
                                    viewHolder.mview.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                                }
                            }
                        };

                allPostList.setAdapter(firebaseRecyclerAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mview;
        ImageButton likeButton, commentButton;
        TextView likesCount;
        int countLikes;
        DatabaseReference LikesRef;
        String currentUserId;


        public PostsViewHolder(View itemView) {
            super(itemView);
            mview=itemView;

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
            TextView setdate=mview.findViewById(R.id.all_posts_date);
            setdate.setText("  " + date);
        }

        public void setTime(String time) {
            TextView settime=mview.findViewById(R.id.all_posts_time);
            settime.setText("  " + time);
        }

        public void setDescription(String description) {
            TextView setdescription=mview.findViewById(R.id.all_posts_description);
            setdescription.setText(description);
        }

        public void setProfileImage(Context context, String profileImage) {
            CircleImageView setimage = mview.findViewById(R.id.all_posts_profile_img);
            Picasso.with(context).load(profileImage).into(setimage);
        }

        public void setPostImage(Context context, String postImage) {
            ImageView imageView=mview.findViewById(R.id.all_posts_image);
            Picasso.with(context).load(postImage).into(imageView);
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {


        int id = item.getItemId();

        if (id==R.id.nav_add_post){
            startActivity(new Intent(getApplicationContext(), AddPost.class));

        }
        else if (id==R.id.nav_profile){
            startActivity(new Intent(getApplicationContext(), MyProfile.class));



        }
        else if (id==R.id.nav_home){
            startActivity(new Intent(getApplicationContext(), User.class));

        }
        else  if (id==R.id.nav_friends){
            startActivity(new Intent(getApplicationContext(), FrinedsList.class));
        }
        else  if (id==R.id.nav_find_friends){
            startActivity(new Intent(getApplicationContext(), FindFriends.class));

        }
        else  if (id==R.id.nav_messages){
            startActivity(new Intent(getApplicationContext(), Choose_for_message.class));

        }
        else  if (id==R.id.nav_settings){
            startActivity(new Intent(getApplicationContext(), Settings.class));

        }
        else if (id == R.id.signout) {
            mauth.signOut();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent1 = new Intent(context, MyService.class);
            context.stopService(intent1);
            if (user == null) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
