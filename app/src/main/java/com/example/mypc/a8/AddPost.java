package com.example.mypc.a8;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class AddPost extends AppCompatActivity {

    private ImageButton selectPostImage;
    private EditText postText;
    private Button addPostButton;
    final static int def = 1;
    private Uri imageUri;
    private StorageReference postImageReferance;
    private String saveDate, saveTime, postRondomName, downloadUrl, currentUserId, Description;
    private DatabaseReference userRef, postsRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        getSupportActionBar().setTitle("Add post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        selectPostImage = findViewById(R.id.add_post_image);
        postText = findViewById(R.id.add_post_text);
        addPostButton = findViewById(R.id.add_post_button);
        postImageReferance = FirebaseStorage.getInstance().getReference();
        loadingBar=new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();
        userRef= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        postsRef= FirebaseDatabase.getInstance().getReference().child("Posts");

        selectPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, def);
            }
        });

        addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Description = postText.getText().toString();
                if (imageUri==null){
                    Toast.makeText(getApplicationContext(), "Please select picture", Toast.LENGTH_SHORT).show();
                }
                else {
                    loadingBar.setTitle("Adding Post");
                    loadingBar.setMessage("Please wait while we are saving your post");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);

                    Calendar calendar=Calendar.getInstance();
                    SimpleDateFormat dateFormat=new SimpleDateFormat("dd-MMMM-yyyy");
                    saveDate=dateFormat.format(calendar.getTime());

                    SimpleDateFormat timeFormat =new SimpleDateFormat("HH:MM:SS");
                    saveTime = timeFormat.format(calendar.getTime());

                    postRondomName=saveDate+saveTime;


                    StorageReference filepath=postImageReferance.child("Post Images").child(imageUri.getLastPathSegment()+postRondomName+".jpg");
                    filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                downloadUrl=task.getResult().getDownloadUrl().toString();
                                userRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            String userFullName=dataSnapshot.child("Fullanme").getValue().toString();
                                            String userProfileImage=dataSnapshot.child("profileImage").getValue().toString();

                                            HashMap postsMap = new HashMap();
                                            postsMap.put("userId", currentUserId);
                                            postsMap.put("date", saveDate);
                                            postsMap.put("time", saveTime);
                                            postsMap.put("postImage", downloadUrl);
                                            postsMap.put("profileImage", userProfileImage);
                                            postsMap.put("userFullName", userFullName);
                                            postsMap.put("description", postText.getText().toString());
                                            postsRef.child(currentUserId + postRondomName).updateChildren(postsMap)
                                                    .addOnCompleteListener(new OnCompleteListener() {
                                                        @Override
                                                        public void onComplete(@NonNull Task task) {
                                                            if (task.isSuccessful()){
                                                                Toast.makeText(getApplicationContext(),"Post is updated successfuly", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(getApplicationContext(), MyProfile.class));
                                                                loadingBar.dismiss();
                                                            }
                                                            else {
                                                                Toast.makeText(getApplicationContext(),"Error for post update", Toast.LENGTH_SHORT).show();
                                                                loadingBar.dismiss();
                                                            }

                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Error for image upload", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode==def && resultCode==RESULT_OK && data!=null) {
            imageUri=data.getData();
            selectPostImage.setImageURI(imageUri);
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
