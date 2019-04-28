package com.jackie.nutshell;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;

public class Proj implements Parcelable{

    private String name;
    private String desc;
    private String[] skills;
    private String poster;


    public Proj(String name, String desc, String[] skills, String poster) {
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

    public Proj(Parcel in){
        name = in.readString();
        desc = in.readString();
        poster = in.readString();
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(desc);
        dest.writeString(poster);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Proj createFromParcel(Parcel in) {
            return new Proj(in);
        }

        public Proj[] newArray(int size) {
            return new Proj[size];
        }
    };


}
