package com.jackie.nutshell;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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




}
