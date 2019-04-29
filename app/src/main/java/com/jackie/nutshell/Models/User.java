package com.jackie.nutshell.Models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/** @author jackie
 * Created on 4/27/19.
 * Represents the model of User. */

public class User implements Serializable {
    private String id;
    private String github;
    private String linkedin;
    private String karma;
    private String name;
    private String number;
    private ArrayList<String> skills;

    public User(String id, String name, String karma) {
        this.id = id;
        this.name = name;
        this.karma = karma;
    }

    public User(String id, String name, String karma, String github, String linkedin, String number) {
        this.id = id;
        this.name = name;
        this.karma = karma;
        this.github = github;
        this.linkedin = linkedin;
        this.number = number;
        // this.skills = skills;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getKarma() {
        return this.karma;
    }

    public String getGithub() {
        return this.github;
    }

    public String getLinkedin() {
        return this.linkedin;
    }

    public String getNumber() {
        return this.number;
    }

    public ArrayList<String> getSkills() {
        return this.skills;
    }

}
