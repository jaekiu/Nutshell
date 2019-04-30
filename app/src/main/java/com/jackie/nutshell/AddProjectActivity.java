package com.jackie.nutshell;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jackie.nutshell.Login.LoginActivity;
import com.jackie.nutshell.Searching.DataHelper;
import com.jackie.nutshell.Searching.SkillSuggestion;
import com.jackie.nutshell.Utils.FirebaseUtils;
import com.jackie.nutshell.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AddProjectActivity extends AppCompatActivity {

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;

    private EditText editName;
    private EditText editDesc;
    private DatabaseReference projsDBRef;
    private DatabaseReference usersRef;
    private SkillsAdapter skillsAdapter;
    private ArrayList<String> skills = new ArrayList<>();
    private FloatingSearchView mSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        // UI stuff
        mSearchView = findViewById(R.id.search);
        editName = findViewById(R.id.editName);
        editDesc = findViewById(R.id.editDesc);
        projsDBRef = FirebaseUtils.getProjsDatabaseRef();
        usersRef = FirebaseUtils.getUsersDatabaseRef();

        // Handling GridView for skills
        GridView gridView = findViewById(R.id.skillGridView);
        skillsAdapter = new SkillsAdapter(getApplicationContext(), skills);
        gridView.setAdapter(skillsAdapter);

        // Change color of status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        // Action bar
        ActionBar actionBar = this.getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark)));

        // Setup search
        setupSearch();
    }

    /** Creates all the menu options for the toolbar (the add button). */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        menu.findItem(R.id.nav_addProj).setVisible(false);
        menu.findItem(R.id.nav_submitProj).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent i = new Intent(AddProjectActivity.this, ExploreActivity.class);
                startActivity(i);
                return true;
            case R.id.nav_submitProj:
                String name = editName.getText().toString();
                String desc = editDesc.getText().toString();
                if (checkFields(name, desc)) {
                    Intent added = new Intent(AddProjectActivity.this, ExploreActivity.class);
                    startActivity(added);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    boolean checkFields(String name, String desc) {
        if (skills == null || name == null || desc == null) {
            Toast.makeText(this, "Please fill out all the sections!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (skills.size() == 0 || name.equals("") || desc.equals("")) {
            Toast.makeText(this, "Please fill out all the sections!", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            sendToProjsDatabase(name, desc);
            return true;
        }
    }

    private void sendToProjsDatabase(String name, String desc) {
        final String key = projsDBRef.push().getKey();
        FirebaseUser user = FirebaseUtils.getFirebaseUser();
        projsDBRef.child(key).child("name").setValue(name);
        projsDBRef.child(key).child("description").setValue(desc);
        projsDBRef.child(key).child("user").setValue(user.getUid());
        projsDBRef.child(key).child("skills").setValue(skills);
        sendToUsersDatabase(key);
    }
    private void sendToUsersDatabase(final String key) {
        final FirebaseUser user = FirebaseUtils.getFirebaseUser();

        usersRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (key.equals("karma")) {
                        Integer karma = dataSnapshot.getValue(Integer.class);
                        karma += 5;
                        usersRef.child(user.getUid()).child("karma").setValue(karma);
                    } else if (key.equals("postedProjects")) {
                        ArrayList<String> postedProjects = (ArrayList<String>) dataSnapshot.getValue();
                        if (postedProjects == null) {
                            postedProjects = new ArrayList<>();
                        }
                        postedProjects.add(key);
                        usersRef.child(user.getUid()).child("postedProjects").setValue(postedProjects);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
}
