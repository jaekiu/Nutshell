package com.jackie.nutshell.Searching;

import android.os.Parcel;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

public class SkillSuggestion implements SearchSuggestion {

    private String skillName;

    public SkillSuggestion(String suggestion) {
        skillName = suggestion;
    }

    public SkillSuggestion(Parcel source) {
        skillName = source.readString();
    }

    @Override
    public String getBody() {
        return skillName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(skillName);
    }

    public static final Creator<SkillSuggestion> CREATOR = new Creator<SkillSuggestion>() {
        @Override
        public SkillSuggestion createFromParcel(Parcel in) {
            return new SkillSuggestion(in);
        }

        @Override
        public SkillSuggestion[] newArray(int size) {
            return new SkillSuggestion[size];
        }
    };
}
