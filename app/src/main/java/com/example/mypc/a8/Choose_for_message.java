package com.example.mypc.a8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

public class Choose_for_message extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference UserRef;
    private String currentUserId;
    private Iterable<DataSnapshot> list;
    private Set<String> stringSet = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frineds_list);
        getSupportActionBar().setTitle("Choose for message");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.all_users_post_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Messages");
        firebaseDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                list = new ArrayList<>();
                list = dataSnapshot.getChildren();

                for (DataSnapshot i : list) {
                    if (i.getKey().equals(currentUserId)) {
                    } else {
                        stringSet.add(i.getKey());
                    }
                }
                Log.i("log", "Message Users : " + stringSet);
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


        FirebaseRecyclerAdapter<getUsers, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<getUsers, PostsViewHolder>
                        (
                                getUsers.class,
                                R.layout.search_result,
                                PostsViewHolder.class,
                                UserRef
                        ) {
                    @Override
                    protected void populateViewHolder(PostsViewHolder viewHolder, final getUsers model, int position) {

                        final String pos = getRef(position).getKey();

                        if (stringSet.contains(pos)) {

                            viewHolder.setCountry(model.getCountry());
                            viewHolder.setFullanme(model.getFullanme());
                            viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());

                            viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), Messages.class);
                                    intent.putExtra("Fullname", model.getFullanme());
                                    intent.putExtra("profileImage", model.getProfileImage());
                                    intent.putExtra("Id", pos);
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

        recyclerView.setAdapter(firebaseRecyclerAdapter);


    }
    public static class PostsViewHolder extends RecyclerView.ViewHolder
    {
        View mview;

        public PostsViewHolder(View itemView) {
            super(itemView);
            mview=itemView;

        }

        public void setCountry(String country) {
            TextView textView = mview.findViewById(R.id.search_row_country);
            textView.setText(country);
        }

        public void setFullanme(String fullname) {
            TextView textView = mview.findViewById(R.id.search_row_name);
            textView.setText(fullname);
        }

        public void setProfileImage(Context context, String profileimage) {
            CircleImageView imageView = mview.findViewById(R.id.search_row_image);
            if (profileimage.equals("" + ".jpg")){
                Picasso.with(context).load(R.mipmap.profile).into(imageView);
            }
            else {
                Picasso.with(context).load(profileimage).into(imageView);
            }
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
