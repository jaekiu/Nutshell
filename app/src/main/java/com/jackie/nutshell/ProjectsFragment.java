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

public class ProjectsFragment extends Fragment {

    TextView watch;
    TextView apply;
    TextView posted;

    public ProjectsFragment() { }

    @NonNull
    public static ProjectsFragment newInstance() { return new ProjectsFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_projects, container, false);

        watch = rootview.findViewById(R.id.watchtext);
        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watch.setTypeface(Typeface.DEFAULT_BOLD);
                apply.setTypeface(Typeface.DEFAULT);
                posted.setTypeface(Typeface.DEFAULT);
                // here you set what you want to do when user clicks your button,

            }
        });

        apply = rootview.findViewById(R.id.applytext);
        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apply.setTypeface(Typeface.DEFAULT_BOLD);
                watch.setTypeface(Typeface.DEFAULT);
                posted.setTypeface(Typeface.DEFAULT);
                // here you set what you want to do when user clicks your button,

            }
        });

        posted = rootview.findViewById(R.id.postedtext);
        posted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                posted.setTypeface(Typeface.DEFAULT_BOLD);
                apply.setTypeface(Typeface.DEFAULT);
                watch.setTypeface(Typeface.DEFAULT);
                // here you set what you want to do when user clicks your button,

            }
        });

        return rootview;
    }
}
