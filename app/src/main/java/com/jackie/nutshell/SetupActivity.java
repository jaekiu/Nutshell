package com.jackie.nutshell;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.io.IOException;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    private SearchView searchView;
    private ImageButton uploadImage;
    private Button skip;
    private ImageButton linkedIn;
    private ImageButton github;
    private Uri eventImg;
    private String linkedInLink = "";
    private String githubLink = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        uploadImage = findViewById(R.id.uploadImgBtn);
        skip = findViewById(R.id.skipBtn);
        linkedIn = findViewById(R.id.linkedinBtn);
        github = findViewById(R.id.githubBtn);

        uploadImage.setOnClickListener(this);
        skip.setOnClickListener(this);
        linkedIn.setOnClickListener(this);
        github.setOnClickListener(this);

        Glide.with(this).load(R.drawable.default_profile_pic).fitCenter().into(uploadImage);

        searchView = findViewById(R.id.searchView);
        searchView.setQueryHint("Search for skills!");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private void createDialog(final String type) {
        Utils.showSingleInputDialog(this,
                type + " Link",
                "Please insert a link to your " + type +"!",
                InputType.TYPE_CLASS_TEXT,
                "Submit",
                "Cancel",

                new SingleInputDialogListener() {
                    @Override
                    public void positiveCallback(String inputText) {
                        if (type.equals("LinkedIn")) {
                            linkedInLink = inputText;
                        } else if (type.equals("Github")) {
                            githubLink = inputText;
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid link!", Toast.LENGTH_SHORT);
                        }
                    }

                    @Override
                    public void negativeCallback() {

                        // ...

                    }
                });
    }

    /** Handles image uploading. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode == 1 && resultCode == RESULT_OK) {
            eventImg = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), eventImg);
                Glide.with(this).load(bitmap).centerCrop().into(uploadImage);
                uploadImage.setBackgroundTintList((getResources().getColorStateList(android.R.color.transparent)));

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Please upload a valid image.", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Please upload a valid image.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.skipBtn:
                Intent goToFeed = new Intent(this, ExploreActivity.class);
                startActivity(goToFeed);
                break;
            case R.id.uploadImgBtn:
                Intent upload = new Intent();
                upload.setType("image/*");
                upload.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(upload,1);
                break;
            case R.id.linkedinBtn:
                createDialog("LinkedIn");
                break;
            case R.id.githubBtn:
                createDialog("Github");
                break;
        }
    }
}
