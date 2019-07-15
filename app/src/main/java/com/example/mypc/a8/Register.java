package com.example.mypc.a8;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Register extends AppCompatActivity {

    Intent intent;
    private EditText name;
    private EditText lastname;
    private AutoCompleteTextView email;
    private EditText password;
    private EditText confirm_pass;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDB;
    private FirebaseUser user;
    private  String userkey;
    private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Registration");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadingBar = new ProgressDialog(this);
        firebaseAuth =FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance();
        mDB =mDatabase.getReference("Database");
        user = firebaseAuth.getCurrentUser();
//        userkey =user.getUid();



//        name = findViewById(R.id.register_name);
//        lastname=findViewById(R.id.register_lastname);
        email=findViewById(R.id.register_email);
        password= findViewById(R.id.register_pass);
        confirm_pass=findViewById(R.id.confirm_pass);

        Button button_register=findViewById(R.id.button_register);
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


//
                    if (TextUtils.isEmpty(email.getText().toString())) {
                    email.setError(getString(R.string.error_empty));
                }
                else if (!isEmailValid(email.getText().toString())) {
                    email.setError(getString(R.string.error_valid));
                }
                else if (TextUtils.isEmpty(password.getText().toString())) {
                    password.setError(getString(R.string.error_empty));
                }
                else if (!isPasswordValid(password.getText().toString())) {
                    password.setError(getString(R.string.error_valid_pass));
                }
                else if (TextUtils.isEmpty(confirm_pass.getText().toString())) {
                    confirm_pass.setError(getString(R.string.error_empty));
                }
                else if (!confirm_pass.getText().toString().equals(password.getText().toString())) {
                    Log.i("tag1",confirm_pass.getText().toString() + "\t" + password.getText().toString());
                    confirm_pass.setError(getString(R.string.error_confirm));
                }
                else {
                    loadingBar.setTitle("Creating New Account");
                    loadingBar.setMessage("Please wait, while we are creating your account...");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);
                    firebase(email.getText().toString(), password.getText().toString());

                    intent = new Intent(getApplicationContext(), SetUpProfile.class);
                    startActivity(intent);
                }
            }
        });

    }
    private  void  firebase(String email_str, String password_str){
        firebaseAuth.createUserWithEmailAndPassword(email_str,password_str)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("tg", "createUserWithEmail:success");
                            Toast.makeText(getApplicationContext(), "Success",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("tg", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
//

                        }
                    }
                });
    }
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 8;
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
