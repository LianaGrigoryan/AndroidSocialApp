package com.example.mypc.a8;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    private EditText userProfileStatus, userUsername, userFullname, userCountry, userBirthday, userGender, userRelationshipstatus;
    private Button updateProfile;
    private CircleImageView userProfileimage;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private String currentUserId;
    private static int def=1;
    private ProgressDialog loadingBar;
    private StorageReference UserProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userProfileStatus= findViewById(R.id.settings_profile_status);
        userUsername = findViewById(R.id.settings_username);
        userFullname = findViewById(R.id.settings_fullname);
        userCountry = findViewById(R.id.settings_country);
        userBirthday = findViewById(R.id.settings_birthday);
        userGender=findViewById(R.id.settings_Gender);
        userRelationshipstatus = findViewById(R.id.settings_relationship_status);
        updateProfile = findViewById(R.id.settings_update_button);
        userProfileimage = findViewById(R.id.settings_profile_image);
        loadingBar = new ProgressDialog(this);
        mAuth=FirebaseAuth.getInstance();
        currentUserId=mAuth.getCurrentUser().getUid();

        databaseReference= FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    userProfileStatus.setText(dataSnapshot.child("Status").getValue().toString());
                    userUsername.setText(dataSnapshot.child("Username").getValue().toString());
                    userFullname.setText(dataSnapshot.child("Fullanme").getValue().toString());
                    userCountry.setText(dataSnapshot.child("Country").getValue().toString());
                    userBirthday.setText(dataSnapshot.child("Birthday").getValue().toString());
                    userGender.setText(dataSnapshot.child("Gender").getValue().toString());
                    userRelationshipstatus.setText(dataSnapshot.child("Relationshipstatus").getValue().toString());
                    String iamge = dataSnapshot.child("profileImage").getValue().toString();
                    if (iamge.equals("" + ".jpg")){
                        Picasso.with(getApplicationContext()).load(R.mipmap.profile).into(userProfileimage);
                    }
                    else {
                        Picasso.with(getApplicationContext()).load(iamge).into(userProfileimage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status = userProfileStatus.getText().toString();
                String username = userUsername.getText().toString();
                String fullname = userFullname.getText().toString();
                String country = userCountry.getText().toString();
                String birthday = userBirthday.getText().toString();
                final String gender = userGender.getText().toString();
                String relationship = userRelationshipstatus.getText().toString();

                if (TextUtils.isEmpty(username)){
                    userUsername.setError("Fill this field");
                }
                else if (TextUtils.isEmpty(fullname)){
                    userFullname.setError("Fill this field");
                }
                else if (TextUtils.isEmpty(country)){
                    userCountry.setError("Fill this field");
                }
                else if (TextUtils.isEmpty(gender)){
                    userGender.setError("Fill this field");
                }
                else {
                    HashMap hashMap =new HashMap();
                    hashMap.put("Status",status);
                    hashMap.put("Username", username);
                    hashMap.put("Fullanme", fullname);
                    hashMap.put("Country", country);
                    hashMap.put("Birthday", birthday);
                    hashMap.put("Gender", gender);
                    hashMap.put("Relationshipstatus", relationship);
                    databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(), MyProfile.class));
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        userProfileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, def);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==def && resultCode==RESULT_OK && data!=null){
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                mAuth = FirebaseAuth.getInstance();
                currentUserId=mAuth.getCurrentUser().getUid();
                UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");

                loadingBar.setTitle("Updating Image");
                loadingBar.setMessage("Please wait, while we updating data ... ");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                Uri resultUri = result.getUri();
                StorageReference filePath=UserProfileImageRef.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Profile image stored",Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            databaseReference.child("profileImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                Intent intent=new Intent(getApplicationContext(), Settings.class);
                                                startActivity(intent);
                                                loadingBar.dismiss();
                                            }
                                            else {
                                                String error = task.getException().getMessage();
                                                Toast.makeText(getApplicationContext(), "Error" + error, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }
                });
            }
            else {
                Toast.makeText(getApplicationContext(),"Error image can not be cropped", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
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
