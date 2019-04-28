package com.jackie.nutshell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ViewActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageButton back;
    private TextView name;
    private TextView desc;
    private TextView poster;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        back = findViewById(R.id.backarrow);
        name = findViewById(R.id.titletext);
        desc = findViewById(R.id.descText);

        Intent in= getIntent();
        Bundle b = in.getExtras();

        if(b!=null)
        {
            Proj j = (Proj) b.get("Proj");
            name.setText(j.getName());
            desc.setText(j.getDesc());
            poster.setText(j.getPoster());

        }
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
