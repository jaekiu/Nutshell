package com.jackie.nutshell.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class Project implements Serializable {
    private String id;
    private String name;
    private String desc;
    private ArrayList<String> skills;
    private String poster;


    public Project(String id, String name, String desc, ArrayList<String> skills, String poster) {
        this.id = id;
        this.name = name;
        this.desc = desc;
        this.skills = skills;
        this.poster = poster;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() {
        return this.desc;
    }

    public ArrayList<String> getSkills() {
        return this.skills;
    }

    public String getPoster() {
        return this.poster;
    }

}
