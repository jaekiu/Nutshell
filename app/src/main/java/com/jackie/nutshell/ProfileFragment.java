package com.jackie.nutshell;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ProfileFragment extends Fragment {

    TextView _about;
    TextView _projects;

    public ProfileFragment() { }

    @NonNull
    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_profile, container, false);
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

}
