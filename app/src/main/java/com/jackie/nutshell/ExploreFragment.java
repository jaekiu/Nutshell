package com.jackie.nutshell;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.jackie.nutshell.Models.Project;
import com.jackie.nutshell.Utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.Collections;

public class ExploreFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private DatabaseReference projsDBRef;
    private ArrayList<Project> projects;
    private ProjectsAdapter adapter;


    public ExploreFragment() { }

    @NonNull
    public static ExploreFragment newInstance() { return new ExploreFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);
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
                    String name = snapshot.child("name").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String poster = snapshot.child("user").getValue(String.class);
                    ArrayList<String> skills = (ArrayList<String>) snapshot.child("skills").getValue();
                    Project p = new Project(name, description, skills, poster);
                    newProjs.add(p);
                }
                Collections.reverse(newProjs);
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



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}