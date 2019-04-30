package com.jackie.nutshell;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jackie.nutshell.Projects.ViewPagerAdapter;

public class ProjectsFragment extends Fragment {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabLayout t;


    public ProjectsFragment() { }

    @NonNull
    public static ProjectsFragment newInstance() { return new ProjectsFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_projects, container, false);

        viewPager = rootview.findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        t = rootview.findViewById(R.id.tabs);
        t.setupWithViewPager(viewPager);

        return rootview;
    }


}
