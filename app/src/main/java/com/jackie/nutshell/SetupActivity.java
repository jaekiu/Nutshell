package com.jackie.nutshell;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jackie.nutshell.Searching.DataHelper;
import com.jackie.nutshell.Searching.SkillSuggestion;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

    // private SearchView searchView;
    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;

    private FloatingSearchView mSearchView;
    private ImageButton uploadImage;
    private Button skip;
    private ImageButton linkedIn;
    private ImageButton github;
    private Button submit;
    private EditText phoneNum;
    private Uri profileImg;
    private String linkedInLink = "";
    private String githubLink = "";
    private FirebaseStorage storage;
    private DatabaseReference db;
    private ArrayList<String> skills = new ArrayList<>(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // Prevents keyboard from popping up when activity launches.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // Initializing variables
        uploadImage = findViewById(R.id.uploadImgBtn);
        skip = findViewById(R.id.skipBtn);
        linkedIn = findViewById(R.id.linkedinBtn);
        github = findViewById(R.id.githubBtn);
        phoneNum = findViewById(R.id.phoneNumText);
        submit = findViewById(R.id.submitBtn);
        mSearchView = findViewById(R.id.floating_search_view);
        storage = FirebaseUtils.getFirebaseStorage();
        db = FirebaseUtils.getUsersDatabaseRef();

        // Setting buttons
        uploadImage.setOnClickListener(this);
        skip.setOnClickListener(this);
        linkedIn.setOnClickListener(this);
        github.setOnClickListener(this);
        submit.setOnClickListener(this);

        // Handling images
        Glide.with(this).load(R.drawable.default_profile_pic).fitCenter().into(uploadImage);

        // Handling GridView for skills
        populateDummySkills();
        GridView gridView = findViewById(R.id.skillGridView);
        SkillsAdapter skillsAdapter = new SkillsAdapter(this, skills);
        gridView.setAdapter(skillsAdapter);

        // Handling searching
        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mSearchView.getLayoutParams();
                p.setMargins(0, 0, 0, 0);
                mSearchView.requestLayout();

                mSearchView.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
                mSearchView.getLayoutParams().width= ViewGroup.LayoutParams.MATCH_PARENT;
            }

            @Override
            public void onFocusCleared() {
                final float scale = getResources().getDisplayMetrics().density;
                int pixelsH = (int) (408 * scale + 0.5f);
                int pixelsR = (int) (28 * scale + 0.5f);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mSearchView.getLayoutParams();
                p.setMargins(0, 0, pixelsR, 0);
                mSearchView.getLayoutParams().height= pixelsH;
                mSearchView.getLayoutParams().width = 0;
            }
        });

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    DataHelper.findSuggestions(getApplicationContext(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<SkillSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    mSearchView.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    mSearchView.hideProgress();
                                }
                            });
                }
                //pass them on to the search view
                //mSearchView.swapSuggestions(newSuggestions);
            }
        });

        // searchView = findViewById(R.id.searchView);
//        searchView.setQueryHint("Search for skills!");
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                Toast.makeText(getBaseContext(), query, Toast.LENGTH_LONG).show();
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                Toast.makeText(getBaseContext(), newText, Toast.LENGTH_LONG).show();
//                return false;
//            }
//        });
    }

    private void populateDummySkills() {
        skills.add("Python");
        skills.add("Java");
        skills.add("HTML");
        skills.add("CSS");
        skills.add("Mobile");
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
                            Toast.makeText(getApplicationContext(), "Invalid link!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void negativeCallback() {

                        // ...

                    }
                });
    }

    private boolean checkValidPhoneNum(String num) {
        return num.matches("\\d{3}-\\d{3}-\\d{4}");
    }

    /** Handles image uploading. */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Detects request codes
        if(requestCode == 1 && resultCode == RESULT_OK) {
            profileImg = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profileImg);
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
            case R.id.submitBtn:
                Log.d("debugging", "what am i in");
                if (!checkValidPhoneNum(phoneNum.getText().toString())) {
                    Toast.makeText(this, "Please enter your phone number in xxx-xxx-xxxx format!", Toast.LENGTH_SHORT).show();
                    Log.d("debugging", "hello??");

                    break;
                }
                final String imgKey = FirebaseUtils.getFirebaseUser().getUid();
                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();
                // Create a reference to "GUID.jpg"
                StorageReference imgRef = storageRef.child(imgKey + ".jpg");
                imgRef.putFile(profileImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        db.child(imgKey).setValue(0);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Please upload a valid image!", Toast.LENGTH_LONG).show();
                    }
                });

        }
    }
}
