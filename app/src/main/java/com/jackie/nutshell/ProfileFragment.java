package com.jackie.nutshell;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jackie.nutshell.Utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

    TextView _about;
    TextView _projects;
    private ImageView _pic;
    private TextView _karma;
    private TextView _name;
    private Button _linkedin;
    private Button _github;
    private DatabaseReference _userRef;

    public ProfileFragment() { }

    @NonNull
    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_profile, container, false);

        // Retrieves information from whatever was clicked if something was even clicked lol
        Bundle bundle = this.getArguments();
        String userId = "";
        if (bundle != null) {
            userId = bundle.getString("posterId", "");
        }

        // Initialize UI elements
        _pic = rootview.findViewById(R.id.pic);
        _name = rootview.findViewById(R.id.name);
        _karma = rootview.findViewById(R.id.karma);
        _linkedin = rootview.findViewById(R.id.linkedin);
        _github = rootview.findViewById(R.id.github);
        _userRef = FirebaseUtils.getUsersDatabaseRef().child(userId);

        // Configures the tabs
        _about = rootview.findViewById(R.id.about);
        _about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _about.setTypeface(Typeface.DEFAULT_BOLD);
                _projects.setTypeface(Typeface.DEFAULT);
            }
        });

        _projects = rootview.findViewById(R.id.projects);
        _projects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _about.setTypeface(Typeface.DEFAULT_BOLD);
                _projects.setTypeface(Typeface.DEFAULT);
                Intent i = new Intent(getContext(), ProjectProfile.class);
                startActivity(i);
            }
        });

        return rootview;
    }

    void getUserInfo() {
        // Realtime database retrieval.
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> attributes = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (key.equals("skills")) {
                        ArrayList value = snapshot.getValue(ArrayList.class);
                        attributes.put(key, value);
                    } else {
                        String value = snapshot.getValue(String.class);
                        attributes.put(key, value);
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
            }
        };
        _userRef.addValueEventListener(postListener);
    }

}
