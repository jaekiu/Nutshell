package com.jackie.nutshell;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static com.jackie.nutshell.Utils.FirebaseUtils.getFirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /** Firebase-related variables. */
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    /** Layout-related variables. */
    private Button signUp;
    private EditText emailText;
    private EditText passwordText;
    private ImageView logo;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Prevents keyboard from popping up when activity launches.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mAuth = getFirebaseAuth();
        signIn = findViewById(R.id.signInButton);
        emailText = findViewById(R.id.emailText);
        passwordText = findViewById(R.id.passwordText);
        signUp = findViewById(R.id.signUpButton);
        logo = findViewById(R.id.logoImage);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    Log.d("FirebaseDebugging", "onAuthStateChanged:signed_in");
                    updateUI();
                }else{
                    Log.d("FirebaseDebugging", "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
        signUp.setOnClickListener(this);
        signIn.setOnClickListener(this);

        Glide.with(this).load(R.drawable.nut).fitCenter().into(logo);

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    /** Attempts to login the user.
     * @param email: Represents the user's email that he/she inputs.
     * @param password: Represents the user's password that he/she inputs. */
    public void loginUsers(String email, String password) {
        if (email == null || password == null) {
            Toast.makeText(this, "Please enter your email and password!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please make sure the email and password fields aren't empty!", Toast.LENGTH_LONG).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("FirebaseDebugging", "status: " + task.isSuccessful());
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("FirebaseDebugging", "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("FirebaseDebugging", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(LoginActivity.this, "Incorrect email or password.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    /** Switches to ExploreActivity.class. */
    public void updateUI() {
        // Intent i = new Intent(this, ExploreActivity.class);
        Intent i = new Intent(this, SetupActivity.class);
        startActivity(i);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Attempts to login the user.
            case R.id.signInButton:
                loginUsers(emailText.getText().toString(), passwordText.getText().toString());
                break;
            // Navigates to the sign-up page.
            case R.id.signUpButton:
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
                break;
        }
    }
}
