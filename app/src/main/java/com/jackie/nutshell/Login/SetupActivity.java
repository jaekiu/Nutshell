package com.jackie.nutshell.Login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jackie.nutshell.ExploreActivity;
import com.jackie.nutshell.R;
import com.jackie.nutshell.Searching.DataHelper;
import com.jackie.nutshell.Searching.SkillSuggestion;
import com.jackie.nutshell.SingleInputDialogListener;
import com.jackie.nutshell.SkillsAdapter;
import com.jackie.nutshell.Utils.FirebaseUtils;
import com.jackie.nutshell.Utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** @author jackie
 * Created on 4/6/19.
 * Represents the SetupActivity Screen. */

public class SetupActivity extends AppCompatActivity implements View.OnClickListener {

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
    private DatabaseReference usersDb;
    private SkillsAdapter skillsAdapter;
    private ArrayList<String> skills = new ArrayList<>(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // Prevents keyboard from popping up when activity launches.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        // Initializing variables
        uploadImage = findViewById(R.id.uploadImgBtn);
        skip = findViewById(R.id.skipBtn);
        linkedIn = findViewById(R.id.linkedinBtn);
        github = findViewById(R.id.githubBtn);
        phoneNum = findViewById(R.id.phoneNumText);
        submit = findViewById(R.id.submitBtn);
        mSearchView = findViewById(R.id.floating_search_view);
        storage = FirebaseUtils.getFirebaseStorage();
        usersDb = FirebaseUtils.getUsersDatabaseRef();

        // Setting buttons
        uploadImage.setOnClickListener(this);
        skip.setOnClickListener(this);
        linkedIn.setOnClickListener(this);
        github.setOnClickListener(this);
        submit.setOnClickListener(this);

        // Handling images
        Glide.with(this).load(R.drawable.default_profile_pic).fitCenter().into(uploadImage);

        // Handling GridView for skills
        GridView gridView = findViewById(R.id.skillGridView);
        skillsAdapter = new SkillsAdapter(this, skills);
        gridView.setAdapter(skillsAdapter);

        // Handling searching
        setupSearch();
    }

    /** Sets up searching. */
    private void setupSearch() {
        mSearchView.setShowSearchKey(true);
        mSearchView.setCloseSearchOnKeyboardDismiss(true);

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
                int pixelsH = Utils.dpToPx(408, getResources());
                int pixelsR = Utils.dpToPx(28, getResources());
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
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                String lastQuery = searchSuggestion.getBody();
                mSearchView.setSearchText(lastQuery);
            }

            @Override
            public void onSearchAction(String currentQuery) {
                String query = "";
                switch (currentQuery) {
                    case "Machine Learning":
                        query = "ML";
                        break;
                    case "Artificial Intelligence":
                        query = "AI";
                        break;
                    case "Graphic Design":
                        query = "Graphic Design";
                        break;
                    default:
                        query = currentQuery;
                }
                if (query.length() > 8 && !query.toLowerCase().equals("javascript")) {
                    query = query.substring(0, 6) + "...";
                }
                addSkill(query);
            }
        });
    }

    /** Adds skill to skills ArrayList (used for populating GridView). */
    private void addSkill(String skill) {
        if (skills.size() == 5) {
            Toast.makeText(this, "You can only have 5 skills!", Toast.LENGTH_SHORT).show();
        } else if (skills.contains(skill)) {
            Toast.makeText(this, "Skill already exists!", Toast.LENGTH_SHORT).show();
        } else {
            skills.add(skill);
            skillsAdapter.notifyDataSetChanged();
        }
    }

    /** Dialogs for uploading LinkedIn and Github links. */
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
                        if (type.equals("LinkedIn") && inputText.toLowerCase().contains("linkedin.com")) {
                            linkedInLink = inputText;
                        } else if (type.equals("Github") && inputText.toLowerCase().contains("github.com")) {
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

    /** Checks if phone number is in correct format (xxx-xxx-xxxx). */
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
                if (linkedInLink.equals("")) {
                    Toast.makeText(this, "Please enter a link to your LinkedIn!", Toast.LENGTH_SHORT).show();
                    break;
                } else if (githubLink.equals("")) {
                    Toast.makeText(this, "Please enter a link to your Github!", Toast.LENGTH_SHORT).show();
                    break;
                } else if (skills.size() == 0) {
                    Toast.makeText(this, "Please include at least one skill!", Toast.LENGTH_SHORT).show();
                    break;
                } else if (!checkValidPhoneNum(phoneNum.getText().toString())) {
                    Toast.makeText(this, "Please enter your phone number in xxx-xxx-xxxx format!", Toast.LENGTH_SHORT).show();
                    break;
                }

                final String imgKey = FirebaseUtils.getFirebaseUser().getUid();
                // Create a storage reference from our app
                StorageReference storageRef = storage.getReference();
                // Create a reference to "GUID.jpg"
                StorageReference imgRef = storageRef.child("users").child(imgKey + ".jpeg");
                imgRef.putFile(profileImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        usersDb.child(imgKey).child("linkedIn").setValue(linkedInLink);
                        usersDb.child(imgKey).child("github").setValue(githubLink);
                        usersDb.child(imgKey).child("skills").setValue(skills);
                        usersDb.child(imgKey).child("phone").setValue(phoneNum.getText().toString());
                        Intent i = new Intent(SetupActivity.this, ExploreActivity.class);
                        startActivity(i);

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
