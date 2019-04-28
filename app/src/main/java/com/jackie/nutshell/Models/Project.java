package com.jackie.nutshell.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

public class Project implements Parcelable {
    private String name;
    private String desc;
    private String[] skills;
    private String poster;


    public Project(String name, String desc, String[] skills, String poster) {
        this.name = name;
        this.desc = desc;
        this.skills = skills;
        this.poster = poster;
    }

    public String getName() {
        return this.poster;
    }

    public String getDesc() {
        return this.desc;
    }

    public String[] getSkills() {
        return this.skills;
    }

    public String getPoster() {
        return this.poster;
    }

    // Parcelling part
    public Project(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.name = data[0];
        this.desc = data[1];
        this.poster = data[3];
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        String skillstring = Arrays.toString(this.skills);
        dest.writeStringArray(new String[] {this.name, this.desc, skillstring, this.poster});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        public Project[] newArray(int size) {
            return new Project[size];
        }
    };


}
