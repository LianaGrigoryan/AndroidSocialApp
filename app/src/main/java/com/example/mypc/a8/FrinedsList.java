package com.example.mypc.a8;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FrinedsList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference FriendRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frineds_list);
        getSupportActionBar().setTitle("Friends");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.all_users_post_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        FriendRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Friends");


        FirebaseRecyclerAdapter<getFriends, PostsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<getFriends, PostsViewHolder>
                        (
                                getFriends.class,
                                R.layout.search_result,
                                PostsViewHolder.class,
                                FriendRef
                        ) {
                    @Override
                    protected void populateViewHolder(PostsViewHolder viewHolder, getFriends model, int position) {

                       final String pos = getRef(position).getKey();

                        viewHolder.setCountry(model.getCountry());
                        viewHolder.setFullname(model.getFullname());
                        viewHolder.setProfileimage(getApplicationContext(), model.getProfileimage());

                        viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getApplicationContext(), MyProfile.class);
                                intent.putExtra("userId", pos);
                                startActivity(intent);
                            }
                        });
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

        public void setFullname(String fullname) {
            TextView textView = mview.findViewById(R.id.search_row_name);
            textView.setText(fullname);
        }

        public void setProfileimage(Context context, String profileimage) {
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
