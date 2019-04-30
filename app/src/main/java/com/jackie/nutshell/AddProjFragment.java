package com.jackie.nutshell;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.Login.LoginActivity;
import com.jackie.nutshell.Searching.DataHelper;
import com.jackie.nutshell.Searching.SkillSuggestion;
import com.jackie.nutshell.Utils.FirebaseUtils;
import com.jackie.nutshell.Utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddProjFragment extends Fragment  {

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;

    EditText editName;
    EditText editDesc;
    private DatabaseReference projsDBRef;
    private SkillsAdapter skillsAdapter;
    ArrayList<String> skills = new ArrayList<>();
    private FloatingSearchView mSearchView;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private TextView helloMenuText;
    private ImageView profilePicMenu;
    private FirebaseUser user;
    private DatabaseReference usersDb;
    private String usersName = "";
    private List<String> messages = Arrays.asList("Explore", "Logout", "Profile", "Projects", "New Project");

    public AddProjFragment() { }

    @NonNull
    public static AddProjFragment newInstance() { return new AddProjFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addproj, container, false);

        mSearchView = v.findViewById(R.id.search);
        this.editName = v.findViewById(R.id.editName);
        this.editDesc = v.findViewById(R.id.editDesc);
        projsDBRef = FirebaseUtils.getProjsDatabaseRef();
        user = FirebaseUtils.getFirebaseUser();
        usersDb = FirebaseUtils.getUsersDatabaseRef();

        // Handling GridView for skills
        GridView gridView = v.findViewById(R.id.skillGridView);
        skillsAdapter = new SkillsAdapter(getContext(), skills);
        gridView.setAdapter(skillsAdapter);

        // Other
        ExploreActivity actionBarActivity = (ExploreActivity) getActivity();
//        drawer = v.findViewById(R.id.drawer_layout);
//        NavigationView navigationView = v.findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//        View header = navigationView.getHeaderView(0);
//        helloMenuText = header.findViewById(R.id.helloTextMenu);
//        profilePicMenu = header.findViewById(R.id.profilePicMenu);
//        setUserInfoInMenu();

//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(actionBarActivity, drawer, toolbar,
//                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.addDrawerListener(toggle);
//        toggle.syncState();


//        Toolbar toolbar = actionBarActivity.findViewById(R.id.toolbar);
//        actionBarActivity.setSupportActionBar(toolbar);
//        toolbar.setTitle("Add Project");
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                switch (item.getItemId()) {
//                    case R.id.nav_submitProj:
//                        Log.d("nani", "haha does this work");
//                        // do what ever you want here
//                }
//                return true;
//            }
//        });


        // Handling searching
        setupSearch();

        setHasOptionsMenu(true);
        return v;
    }

    boolean checkFields(String skills, String name, String desc) {
        if (skills == null || name == null || desc == null) {
            Toast.makeText(getContext(), "Please fill out all the sections!", Toast.LENGTH_SHORT);
            return false;
        } else if (skills.equals("") || name.equals("") || desc.equals("")) {
            Toast.makeText(getContext(), "Please fill out all the sections!", Toast.LENGTH_SHORT);
            return false;
        } else {
            sendToProjsDatabase(skills, name, desc);
            return true;
        }
    }

    private void sendToProjsDatabase(String skills, String name, String desc) {
        final String key = projsDBRef.push().getKey();
        projsDBRef.child(key).child("name").setValue(name);
        projsDBRef.child(key).child("description").setValue(desc);
        projsDBRef.child(key).child("skills").setValue(skills);


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
                    DataHelper.findSuggestions(getContext(), newQuery, 5,
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
            Toast.makeText(getContext(), "You can only have 5 skills!", Toast.LENGTH_SHORT).show();
        } else if (skills.contains(skill)) {
            Toast.makeText(getContext(), "Skill already exists!", Toast.LENGTH_SHORT).show();
        } else {
            skills.add(skill);
            skillsAdapter.notifyDataSetChanged();
        }
    }
//
//
//    private void setUserInfoInMenu() {
//        String uid = user.getUid();
//        DatabaseReference userSpecificDb = usersDb.child(uid).child("name");
//        userSpecificDb.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                usersName = dataSnapshot.getValue(String.class);
//                helloMenuText.setText("Hello " + usersName + "!");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//
//        StorageReference storageRef = FirebaseUtils.getFirebaseStorage().getReference();
//        StorageReference imgRef = storageRef.child("users").child(user.getUid() + ".jpeg");
//        // Handling images
//        Glide.with(this).load(imgRef).centerCrop().into(profilePicMenu);
//    }
//
//    /** Creates all the menu options for the toolbar (the add button). */
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getActivity().getMenuInflater().inflate(R.menu.toolbar_menu, menu);
//        return true;
//    }
//
//    /** Handles selection of options for the Drawer. */
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.nav_addProj:
//                toolbar.setTitle("New Project");
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new AddProjFragment()).commit();
//                return true;
//            case R.id.nav_submitProj:
//                toolbar.setTitle("Explore");
//                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ExploreFragment()).commit();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        switch (messages.indexOf(toolbar.getTitle())) {
//            case 0: //explore
//                System.out.println("case 0 reached");
//                invalidateOptionsMenu();
//                menu.findItem(R.id.nav_submitProj).setVisible(false);
//                menu.findItem(R.id.nav_addProj).setVisible(true);
//                break;
//            case 1: //logout
//                break;
//            case 2: //profile
//                System.out.println("case 1 reached");
//                invalidateOptionsMenu();
//                menu.findItem(R.id.nav_addProj).setVisible(false);
//                menu.findItem(R.id.nav_submitProj).setVisible(false);
//                break;
//            case 3: //projects
//                invalidateOptionsMenu();
//                menu.findItem(R.id.nav_addProj).setVisible(false);
//                menu.findItem(R.id.nav_submitProj).setVisible(false);
//                break;
//            case 4: //new project
//                invalidateOptionsMenu();
//                menu.findItem(R.id.nav_addProj).setVisible(false);
//                menu.findItem(R.id.nav_submitProj).setVisible(true);
//                break;
//        }
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//        switch (menuItem.getItemId()) {
//            case R.id.nav_explore:
//                toolbar.setTitle("Explore");
//                invalidateOptionsMenu();
//                ExploreFragment explore = new ExploreFragment();
//                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//                transaction.replace(R.id.fragment_container, explore);
//                transaction.addToBackStack(null);
//                transaction.commit();
//
////                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
////                        new ExploreFragment()).commit();
//                break;
//            case R.id.nav_logout:
//                toolbar.setTitle("Logout");
//                invalidateOptionsMenu();
//                Intent i2 = new Intent(this, LoginActivity.class);
//                FirebaseAuth.getInstance().signOut();
//                Toast.makeText(this, "You are now signed out.", Toast.LENGTH_SHORT).show();
//                startActivity(i2);
//                break;
//            case R.id.nav_profile:
//                toolbar.setTitle("Profile");
//                invalidateOptionsMenu();
//                Fragment fragment = new ProfileFragment();
//                Bundle bundle = new Bundle();
//                bundle.putString("posterId", user.getUid());
//                fragment.setArguments(bundle);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        fragment).commit();
//                break;
//            case R.id.nav_projects:
//                toolbar.setTitle("Projects");
//                invalidateOptionsMenu();
////                Intent i3 = new Intent(this, ProjectActivity.class);
////                startActivity(i3);
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ProjectsFragment()).commit();
//                break;
//            case R.id.nav_addProj:
//                toolbar.setTitle("New Project");
//                invalidateOptionsMenu();
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new AddProjFragment()).commit();
//                break;
//            case R.id.nav_submitProj:
//                toolbar.setTitle("Explore");
//                invalidateOptionsMenu();
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                        new ExploreFragment()).commit();
//                break;
//
//        }
//
//        drawer.closeDrawer(GravityCompat.START);
//
//        return true;
//    }
//
//    @Override
//    public void onBackPressed() {
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
}
