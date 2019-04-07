package com.jackie.nutshell;

import android.content.Intent;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    /** Represents Firebase Auth object. */
    private FirebaseAuth mAuth;
    private FirebaseDatabase _database;
    private DatabaseReference _myRef;

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
        _database = FirebaseUtils.getFirebaseDatabase();
        _myRef = FirebaseUtils.getUsersDatabaseRef();
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
    public void signUpUsers(final String username, final String email, String password, String confirmPass, final String name) {
        if (username == null || email == null || password == null || confirmPass == null || name == null) {
            Toast.makeText(this, "Please enter your name, email, and password!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPass) || TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please make sure the name, email, and password fields aren't empty!", Toast.LENGTH_LONG).show();
        } else if (password.length() < 6) {
            // Perhaps try implementing it so that as you type it tells you if it's good and/or what it's missing??
            Toast.makeText(this, "Please make your password at least 6 characters long!", Toast.LENGTH_LONG).show();
        } else if (!password.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_LONG).show();
        } else if (checkUsername(username)) {
            // If everything is fine, then create a user with his/her email and password.
            // Could put this in onDataChange maybe??
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
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

    private void addUser(String username, String email, String name) {
        _myRef.child(username).child("email").setValue(email);
        _myRef.child(username).child("name").setValue(name);
    }

    /** Switches to SetupActivity.class if sign-up is successful. */
    public void updateUI() {
        Intent i = new Intent(this, SetupActivity.class);
        startActivity(i);
    }

    // DOES NOT WORK AS INTENDED
    public boolean checkUsername(final String username) {
        // Checks to see if username already exists.
        // Could possibly change this so when they're typing their username, it tells them whether the username exists or not.
        // If it does, it alerts the user and does not let the user proceed.
        final boolean[] exists = {false};
        _myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(username)){
                    // Toast.makeText(SignUpActivity.this, "Username already exists!", Toast.LENGTH_LONG).show();
                    exists[0] = true;
                    return;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return exists[0];
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
