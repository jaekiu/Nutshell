package com.jackie.nutshell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jackie.nutshell.Utils.FirebaseUtils;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    /** Represents Firebase Auth object. */
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private DatabaseReference usernamesRef;
    private FirebaseStorage storage;

    /** Layout-related variables. */
    private Button signUp;
    private EditText nameText;
    private EditText usernameText;
    private EditText emailText;
    private EditText passwordText;
    private EditText confirmPass;
    private ImageView logo;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Prevents keyboard from popping up when activity launches.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Initiate content.
        mAuth = FirebaseUtils.getFirebaseAuth();
        storage = FirebaseUtils.getFirebaseStorage();
        usersRef = FirebaseUtils.getUsersDatabaseRef();
        usernamesRef = FirebaseUtils.getUsernamesDatabaseRef();
        back = findViewById(R.id.backButtonSUA);
        usernameText = findViewById(R.id.usernameText);
        nameText = findViewById(R.id.nameText);
        emailText = findViewById(R.id.emailText2);
        passwordText = findViewById(R.id.passwordText2);
        confirmPass = findViewById(R.id.confirmPassText);
        signUp = findViewById(R.id.signUpButton);
        logo = findViewById(R.id.logoImage2);

        signUp.setOnClickListener(this);
        back.setOnClickListener(this);

        Glide.with(this).load(R.drawable.nut).fitCenter().into(logo);
    }

    /** Attempts to sign up a new user.
     * @param email: represents user's email that he/she inputs.
     * @param name: represents user's name that he/she inputs.
     * @param password: represents user's password that he/she inputs. */
    public void signUpUsers(final String username, final String email, final String password, String confirmPass, final String name) {
        if (username == null || email == null || password == null || confirmPass == null || name == null) {
            Toast.makeText(this, "Please enter your name, email, and password!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPass) || TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please make sure the name, email, and password fields aren't empty!", Toast.LENGTH_LONG).show();
        } else if (password.length() < 6) {
            // Perhaps try implementing it so that as you type it tells you if it's good and/or what it's missing??
            Toast.makeText(this, "Please make your password at least 6 characters long!", Toast.LENGTH_LONG).show();
        } else if (!password.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show();
        } else {
            // If everything is fine, then create a user with his/her email and password.
            // Could put this in onDataChange maybe??
            usernamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.hasChild(username)) {
                        // run some code
                        Log.d("nani", "username alr exists");
                        Toast.makeText(getApplicationContext(), "Username already exists!", Toast.LENGTH_LONG).show();
                    } else {
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d("FirebaseDebugging", "createUserWithEmail:success");
                                            FirebaseUser user = mAuth.getCurrentUser();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setDisplayName(name)
                                                    .build();

                                            user.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d("FirebaseDebugging", "User profile updated.");
                                                            }
                                                        }
                                                    });
                                            addUser(username, email, name);
                                            updateUI();
                                        } else {
                                            // If sign in fails, display a message to the user.
                                            Log.w("FirebaseDebugging", "createUserWithEmail:failure", task.getException());
                                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void addUser(String username, String email, String name) {
        final String key = mAuth.getCurrentUser().getUid();
        usersRef.child(key).child("username").setValue(username);
        usersRef.child(key).child("email").setValue(email);
        usersRef.child(key).child("name").setValue(name);
        usersRef.child(key).child("karma").setValue(0);
        usernamesRef.child(username).child("name").setValue(name);
        Uri profileImg = Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID + "/drawable/default_profile_pic"); // default profile pic

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        // Create a reference to "GUID.jpg"
        StorageReference imgRef = storageRef.child("users").child(key + ".jpeg");
        imgRef.putFile(profileImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("nani", "i succeeded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("nani", "i failed");
            }
        });
    }

    /** Switches to SetupActivity.class if sign-up is successful. */
    public void updateUI() {
        Intent i = new Intent(this, SetupActivity.class);
        startActivity(i);
    }

    public boolean checkIfUsernameExists(final String username) {
        Log.d("nani", "please fucking work");
        final boolean[] flag = {false};
        usernamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(username)) {
                    // run some code
                    Log.d("nani", "does this even work");
                    flag[0] = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
//        usernamesRef.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for(DataSnapshot data: dataSnapshot.getChildren()){
//                    if (data.exists()) {
//                        flag[0] = true;
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
        return flag[0];
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backButtonSUA:
                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(i);
                break;
            case R.id.signUpButton:
                signUpUsers(usernameText.getText().toString(),
                        emailText.getText().toString(),
                        passwordText.getText().toString(),
                        confirmPass.getText().toString(),
                        nameText.getText().toString());
        }
    }
}
