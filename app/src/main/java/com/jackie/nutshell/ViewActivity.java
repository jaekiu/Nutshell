package com.jackie.nutshell;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.Models.Project;
import com.jackie.nutshell.Utils.FirebaseUtils;

public class ViewActivity extends AppCompatActivity {
    private TextView name;
    private TextView desc;
    private String poster;
    private ImageView posterpic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        // Change color of status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        // Action bar
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));

        name = findViewById(R.id.titletext);
        desc = findViewById(R.id.descText);
        posterpic = findViewById(R.id.imageView);

        Intent in= getIntent();
        Bundle b = in.getExtras();

        if(b!=null)
        {
            Project j = (Project) b.get("Project");

            name.setText(j.getName());
            desc.setText(j.getDesc());
            poster = j.getPoster();
            StorageReference storageRef = FirebaseUtils.getFirebaseStorage().getReference();
            StorageReference imgRef = storageRef.child("users").child(poster + ".jpeg");
            // Handling images
            Glide.with(getApplicationContext()).load(imgRef).centerCrop().into(posterpic);



        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent i = new Intent(ViewActivity.this, ExploreActivity.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }




}
