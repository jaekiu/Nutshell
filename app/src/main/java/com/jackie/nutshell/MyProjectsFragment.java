package com.jackie.nutshell;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.Models.Project;
import com.jackie.nutshell.Utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProjectsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private DatabaseReference usersDBRef;
    private DatabaseReference projsDBRef;
    private ArrayList<Project> projects;
    private ProjectsAdapter adapter;

    public MyProjectsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_my_projects, container, false);

        usersDBRef = FirebaseUtils.getUsersDatabaseRef();
        projsDBRef = FirebaseUtils.getProjsDatabaseRef();
        projects = new ArrayList<>();


        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ProjectsAdapter(projects, getContext(), getActivity());
        mRecyclerView.setAdapter(adapter);

        // Realtime database retrieval.
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Project> newProjs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ArrayList<String> applied = (ArrayList<String>) snapshot.child("applied").getValue();
                    if (applied == null) {
                        applied = new ArrayList<String>();
                    }
                    String poster = snapshot.child("user").getValue(String.class);
                    String userId = FirebaseUtils.getFirebaseUser().getUid();
                    if (poster.equals(userId) || applied.contains(userId)) {
                        String name = snapshot.child("name").getValue(String.class);
                        String description = snapshot.child("description").getValue(String.class);
                        ArrayList<String> skills = (ArrayList<String>) snapshot.child("skills").getValue();
                        Project p = new Project(name, description, skills, poster);
                        newProjs.add(p);
                    }
                }
                projects.clear();
                projects.addAll(newProjs);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
            }
        };
        projsDBRef.orderByKey().addValueEventListener(postListener);


        return v;
    }
}
