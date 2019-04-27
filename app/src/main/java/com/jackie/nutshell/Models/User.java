package com.jackie.nutshell.Models;

import java.util.ArrayList;
import java.util.List;
/** @author jackie
 * Created on 4/27/19.
 * Represents the model of User. */

public class User {
    private String github;
    private String linkedin;
    private String karma;
    private String name;
    private String number;
    private ArrayList<String> skills;

    public User(String name, String karma, String github, String linkedin, String number, ArrayList<String> skills) {
        this.name = name;
        this.karma = karma;
        this.github = github;
        this.linkedin = linkedin;
        this.number = number;
        this.skills = skills;
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

}
