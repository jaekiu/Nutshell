package com.jackie.nutshell;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] childFragments;
    private String[] titles;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
        childFragments = new Fragment[] {
                new ProjectsFragment(), //0
                new PostedFragment(), //1
                new AppliedFragment() //2
        };
        titles = new String[] {
                "My Projects", "Posted", "Applied"
        };
    }

    @Override
    public Fragment getItem(int position) {

//        position = position + 1;
//        Bundle bundle = new Bundle();
//        bundle.putString("message", "Fragment : " + position);
//        demoFragment.setArguments(bundle);
        return childFragments[position];
    }

    @Override
    public int getCount() {
        return childFragments.length; //for three tabs
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
