package com.example.mypc.a8;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class Click_Post extends AppCompatActivity {

    private ImageButton imageButton;
    private EditText editText;
    private Button editButton, deleteButton;
    private String PostKey, currentUserId, databaseUserId, description, image;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click__post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Post");

        imageButton=findViewById(R.id.edit_edit_img);
        editText=findViewById(R.id.edit_edit_text);
        editButton=findViewById(R.id.edit_edit_post);
        deleteButton=findViewById(R.id.edit_delete_post);
        auth=FirebaseAuth.getInstance();
        currentUserId=auth.getCurrentUser().getUid();
        editButton.setVisibility(View.INVISIBLE);
        deleteButton.setVisibility(View.INVISIBLE);
        editText.setEnabled(false);
        PostKey = getIntent().getExtras().get("PostKey").toString();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Posts").child(PostKey);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    description = dataSnapshot.child("description").getValue().toString();
                    image = dataSnapshot.child("postImage").getValue().toString();
                    databaseUserId=dataSnapshot.child("userId").getValue().toString();
                    editText.setText(description);
                    Picasso.with(getApplicationContext()).load(image).into(imageButton);

                    if (databaseUserId.equals(currentUserId)){
                        editButton.setVisibility(View.VISIBLE);
                        deleteButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.removeValue();
                Toast.makeText(getApplicationContext(),"Deleted", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MyProfile.class));
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(Click_Post.this);
                builder.setTitle("Edit Post");
                final EditText editText =new EditText(Click_Post.this);
                editText.setText(description);
                builder.setView(editText);
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child("description").setValue(editText.getText().toString());
                        Toast.makeText(getApplicationContext(),"Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MyProfile.class));
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                Dialog dialog=builder.create();
                dialog.show();
                dialog.getWindow().setBackgroundDrawableResource(R.color.colorPrimary);
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
}
