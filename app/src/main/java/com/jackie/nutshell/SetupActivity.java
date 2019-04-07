package com.jackie.nutshell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    private Button signout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        signout = findViewById(R.id.signout);
        signout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signout:
                Intent i2 = new Intent(SetupActivity.this, LoginActivity.class);
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "You are now signed out.", Toast.LENGTH_SHORT).show();
                startActivity(i2);
        }
    }
}
