package com.jackie.nutshell;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class ExploreActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Explore");
        //***
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ExploreFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_explore);
        }
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
                toolbar.setTitle("New Project");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AddProjFragment()).commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_explore:
                toolbar.setTitle("Explore");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ExploreFragment()).commit();
                break;
            case R.id.nav_logout:
                toolbar.setTitle("Logout");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new LogoutFragment()).commit();
                break;
            case R.id.nav_profile:
                toolbar.setTitle("Profile");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileFragment()).commit();
                break;
            case R.id.nav_projects:
                toolbar.setTitle("Projects");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProjectsFragment()).commit();
                break;
            case R.id.nav_addProj:
                toolbar.setTitle("New Project");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AddProjFragment()).commit();
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
