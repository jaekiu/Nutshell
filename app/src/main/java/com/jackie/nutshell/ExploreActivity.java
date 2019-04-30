package com.jackie.nutshell;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.Login.LoginActivity;
import com.jackie.nutshell.Models.Project;
import com.jackie.nutshell.Utils.FirebaseUtils;

import java.util.Arrays;
import java.util.List;

public class ExploreActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private FirebaseUser user;
    private String usersName = "";
    private DatabaseReference usersDb;
    private TextView helloMenuText;
    private ImageView profilePicMenu;
    private Toolbar toolbar;
    private List<String> messages = Arrays.asList("Explore", "Logout", "Profile", "Projects", "New Project");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        // Change color of status bar
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        user = FirebaseUtils.getFirebaseUser();
        usersDb = FirebaseUtils.getUsersDatabaseRef();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Explore");
        //***
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);

        // View vi = getLayoutInflater().inflate(R.layout.nav_header, null);
        helloMenuText = header.findViewById(R.id.helloTextMenu);
        profilePicMenu = header.findViewById(R.id.profilePicMenu);
        setUserInfoInMenu();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();



        if (savedInstanceState == null) {
            toolbar.setTitle("Explore");
            invalidateOptionsMenu();
            ExploreFragment explore = new ExploreFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, explore).commit();

            navigationView.setCheckedItem(R.id.nav_explore);
        }
    }

    private void setUserInfoInMenu() {
        String uid = user.getUid();
        DatabaseReference userSpecificDb = usersDb.child(uid).child("name");
        userSpecificDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersName = dataSnapshot.getValue(String.class);
                helloMenuText.setText("Hello " + usersName + "!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        StorageReference storageRef = FirebaseUtils.getFirebaseStorage().getReference();
        StorageReference imgRef = storageRef.child("users").child(user.getUid() + ".jpeg");
        // Handling images
        Glide.with(this).load(imgRef).centerCrop().into(profilePicMenu);
    }

    /** Creates all the menu options for the toolbar (the add button). */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /** Handles selection of options for the Drawer. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_addProj:
                Intent i = new Intent(ExploreActivity.this, AddProjectActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
            switch (messages.indexOf(toolbar.getTitle())) {
                case 0: //explore
                    System.out.println("case 0 reached");
                    invalidateOptionsMenu();
                    menu.findItem(R.id.nav_submitProj).setVisible(false);
                    menu.findItem(R.id.nav_addProj).setVisible(true);
                    break;
                case 1: //logout
                    break;
                case 2: //profile
                    System.out.println("case 1 reached");
                    invalidateOptionsMenu();
                    menu.findItem(R.id.nav_addProj).setVisible(false);
                    menu.findItem(R.id.nav_submitProj).setVisible(false);
                    break;
                case 3: //projects
                    invalidateOptionsMenu();
                    menu.findItem(R.id.nav_addProj).setVisible(false);
                    menu.findItem(R.id.nav_submitProj).setVisible(false);
                    break;
                case 4: //new project
                    invalidateOptionsMenu();
                    menu.findItem(R.id.nav_addProj).setVisible(false);
                    menu.findItem(R.id.nav_submitProj).setVisible(true);
                    break;
            }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_explore:
                toolbar.setTitle("Explore");
                invalidateOptionsMenu();
                ExploreFragment explore = new ExploreFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, explore);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.nav_logout:
                toolbar.setTitle("Logout");
                invalidateOptionsMenu();
                Intent i2 = new Intent(this, LoginActivity.class);
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(this, "You are now signed out.", Toast.LENGTH_SHORT).show();
                startActivity(i2);
                break;
            case R.id.nav_profile:
                toolbar.setTitle("Profile");
                invalidateOptionsMenu();
                Fragment fragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("posterId", user.getUid());
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();
                break;
            case R.id.nav_projects:
                toolbar.setTitle("Projects");
                invalidateOptionsMenu();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProjectsFragment()).commit();
                break;
            case R.id.nav_addProj:
                Intent i = new Intent(ExploreActivity.this, AddProjectActivity.class);
                startActivity(i);
                break;

        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
