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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriends extends AppCompatActivity {

    private ImageButton serachButton;
    private EditText search_edittext;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Find Friends");

        serachButton = findViewById(R.id.search_button1);
        search_edittext = findViewById(R.id.search_edittext);
        recyclerView = findViewById(R.id.all_users_post_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        serachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search(search_edittext.getText().toString());
            }
        });
    }

    private void search(final String string) {
        Query searchQuery = databaseReference.orderByChild("Fullanme")
                .startAt(string).endAt(string + "\uf8ff");

        Log.i("viewholder", "button");

        FirebaseRecyclerAdapter<SearchedFriends, SearchedFriendsViewHolder> firebaseRecyclerAdapter
                = new FirebaseRecyclerAdapter<SearchedFriends, SearchedFriendsViewHolder>
                (
                        SearchedFriends.class,
                        R.layout.search_result,
                        SearchedFriendsViewHolder.class,
                        searchQuery
                )
        {
            @Override
            protected void populateViewHolder(SearchedFriendsViewHolder viewHolder, SearchedFriends model, int position) {

                final String pos=getRef(position).getKey();

                  viewHolder.setProfileImage(getApplicationContext(), model.getProfileImage());
                  viewHolder.setFullanme(model.getFullanme());
                  viewHolder.setCountry(model.getCountry());

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
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

    public static class SearchedFriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        public SearchedFriendsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setProfileImage(Context context, String image) {
            CircleImageView imageView = mView.findViewById(R.id.search_row_image);
            if (image.equals("" + ".jpg")){
                Picasso.with(context).load(R.mipmap.profile).into(imageView);
            }
            else {
                Picasso.with(context).load(image).into(imageView);
            }
        }

        public void setFullanme(String fullname) {
            TextView textView = mView.findViewById(R.id.search_row_name);
            textView.setText(fullname);
        }

        public void setCountry(String country) {
            TextView textView = mView.findViewById(R.id.search_row_country);
            textView.setText(country);
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
