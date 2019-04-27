package com.jackie.nutshell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        back = findViewById(R.id.backarrow);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backarrow:
                Intent i = new Intent(ViewActivity.this, ExploreFragment.class);
                startActivity(i);
                break;
        }
    }


}
