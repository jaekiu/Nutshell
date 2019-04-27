package com.jackie.nutshell.Models;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Project {
    public String _name;
    public String _desc;
    public String[] _skills;
    public String _poster;


    public Project(String name, String desc, String[] skills, String poster) {
        _name = name;
        _desc = desc;
        _skills = skills;
        _poster = poster;
    }

    public String getName() {
        return _name;
    }

    public String getDesc() {
        return _desc;
    }

    public String[] getSkills() {
        return _skills;
    }

    public String getPoster() {
        return _poster;
    }

}
