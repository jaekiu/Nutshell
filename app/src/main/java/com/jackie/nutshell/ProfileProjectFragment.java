package com.jackie.nutshell;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.Models.Project;
import com.jackie.nutshell.Models.User;
import com.jackie.nutshell.Utils.FirebaseUtils;
import com.jackie.nutshell.Utils.Utils;

import java.util.ArrayList;

/** @author jackie
 * Created on 4/28/19.
 * Represents the Profile Projects Page for the Profile Fragment. */

public class ProfileProjectFragment extends Fragment {
    TextView _about;
    TextView _projects;
    private ImageView _pic;
    private TextView _karma;
    private TextView _name;
    private User _user;
    private Context _c;
    private RecyclerView recyclerView;
    private ProjectsAdapter mAdapter;
    private ArrayList<Project> projects = new ArrayList<>();

    public ProfileProjectFragment() { }

    @NonNull
    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_profile_project, container, false);

        // Retrieves information from whatever was clicked if something was even clicked lol
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _user = (User) bundle.getSerializable("user");
        }

        // Initialize UI elements
        _pic = rootview.findViewById(R.id.pic);
        _name = rootview.findViewById(R.id.name);
        _karma = rootview.findViewById(R.id.karma);
        _c = getContext();

        // Tab controller
        _about = rootview.findViewById(R.id.about);
        _about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("posterId", _user.getId());
                fragment.setArguments(bundle);
                ((ExploreActivity)_c).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();
            }
        });

        // Add line to current open tab
        _projects = rootview.findViewById(R.id.projects);
        // Generate bottom border only
        LayerDrawable bottomBorder = Utils.getBorders(
                Color.WHITE, // Background color
                getResources().getColor(R.color.colorBrownLight), // Border color
                0, // Left border in pixels
                0, // Top border in pixels
                0, // Right border in pixels
                5 // Bottom border in pixels
        );
        // Finally, apply the drawable as text view background
        _projects.setBackground(bottomBorder);

        // Populates UI
        String id = _user.getId();
        StorageReference storageRef = FirebaseUtils.getFirebaseStorage().getReference();
        StorageReference imgRef = storageRef.child("users").child(id + ".jpeg");
        // Handling images
        Glide.with(_c).load(imgRef).centerCrop().into(_pic);
        _name.setText(_user.getName());
        _karma.setText(_user.getKarma());

        // populates projects
        // Realtime database retrieval.
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Project> newProjs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String poster = snapshot.child("user").getValue(String.class);
                    Project p = new Project(name, description, new String[]{}, poster);
                    newProjs.add(p);
                }
                projects.clear();
                projects.addAll(newProjs);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
            }
        };
        FirebaseUtils.getProjsDatabaseRef().orderByKey().addValueEventListener(postListener);


        recyclerView = rootview.findViewById(R.id.recycler_view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ProjectsAdapter(projects, getContext(), getActivity());
        recyclerView.setAdapter(mAdapter);

        return rootview;
    }
}
