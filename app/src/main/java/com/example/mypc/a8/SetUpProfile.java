package com.example.mypc.a8;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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

public class SetUpProfile extends AppCompatActivity {

    private CircleImageView profileimage;
    private EditText username, fullname, country;
    private Button confirm;
    private FirebaseAuth mAuth;
    private DatabaseReference UsersRef;
    private String currentUserId, pic;
    private StorageReference UserProfileImageRef;
    private ProgressDialog loadingBar;
    final static int def=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_up_profile);

        getSupportActionBar().setTitle("Set Up Profle");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        profileimage = findViewById(R.id.setup_image);
        username = findViewById(R.id.setup_username);
        fullname = findViewById(R.id.setup_fullname);
        country = findViewById(R.id.setup_country);
        confirm = findViewById(R.id.setup_confirm);
        loadingBar=new ProgressDialog(this);
        pic = getIntent().getStringExtra("pic");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                currentUserId = mAuth.getCurrentUser().getUid();
                UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
                String Username = username.getText().toString();
                String Fullname = fullname.getText().toString();
                String Country = country.getText().toString();

                if (TextUtils.isEmpty(Username)) {
                    username.setError(getString(R.string.error_empty));
                } else if (TextUtils.isEmpty(Fullname)) {
                    fullname.setError(getString(R.string.error_empty));
                } else if (TextUtils.isEmpty(Country)) {
                    country.setError(getString(R.string.error_empty));
                } else {
                    HashMap userMap = new HashMap();
                    userMap.put("Username", Username);
                    userMap.put("Fullanme", Fullname);
                    userMap.put("Country", Country);
                    userMap.put("Status", "Hey");
                    userMap.put("Gender", "");
                    userMap.put("Relationshipstatus", "");
                    userMap.put("Birthday", "");
                    if (pic==null) {
                        userMap.put("profileImage", "" + ".jpg");
                    }

                    UsersRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Your account is created.",
                                        Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), User.class);
                                //intent.putExtra("username", username.getText().toString());
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Your account isn't created ",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                }
                Intent intent1 = new Intent(getApplicationContext(), MyService.class);
                getApplicationContext().startService(intent1);
            }
        });

        profileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, def);
            }
        });

        if (getIntent().getStringExtra("k")!=null) {

            mAuth = FirebaseAuth.getInstance();
            currentUserId = mAuth.getCurrentUser().getUid();
            UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
            UsersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String image = dataSnapshot.child("profileImage").getValue().toString();

                        Picasso.with(SetUpProfile.this).load(image).placeholder(R.mipmap.profile).into(profileimage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("tag", "onActivityResult: ");
        if (requestCode==def && resultCode==RESULT_OK && data!=null){
            Log.i("tag", "if1 ");
            Uri imageUri = data.getData();
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this);
        }

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            Log.i("tag", "if2 ");
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK){
                Log.i("tag", "if3 ");
                mAuth = FirebaseAuth.getInstance();
                currentUserId=mAuth.getCurrentUser().getUid();
                UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);
                UserProfileImageRef= FirebaseStorage.getInstance().getReference().child("Profile Images");

                loadingBar.setTitle("Saving Image");
                loadingBar.setMessage("Please wait, while we saveing data ... ");
                loadingBar.show();
                loadingBar.setCanceledOnTouchOutside(true);
                Uri resultUri = result.getUri();
                StorageReference filePath=UserProfileImageRef.child(currentUserId + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        Log.i("tag","kkkkkkkk");
                        if (task.isSuccessful()){
                            Toast.makeText(getApplicationContext(), "Profile image stored",Toast.LENGTH_SHORT).show();
                            final String downloadUrl = task.getResult().getDownloadUrl().toString();
                            UsersRef.child("profileImage").setValue(downloadUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){

                                        Intent intent=new Intent(getApplicationContext(), SetUpProfile.class);
                                        intent.putExtra("k", "1");
                                        intent.putExtra("pic", "1");
                                        startActivity(intent);


                                        Toast.makeText(getApplicationContext(), "Profile image stored in database", Toast.LENGTH_SHORT).show();
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


