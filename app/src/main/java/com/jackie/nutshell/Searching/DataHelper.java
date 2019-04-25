package com.jackie.nutshell.Searching;

import android.content.Context;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** @author jackie
 * Created 4/6/19.
 * Represents the data used for autocomplete in search feature. */

public class DataHelper {

    private static List<SkillSuggestion> sSkillSuggestions =
            new ArrayList<>(Arrays.asList(
                    new SkillSuggestion("C#"),
                    new SkillSuggestion("C++"),
                    new SkillSuggestion("Python"),
                    new SkillSuggestion("Objective-C"),
                    new SkillSuggestion("React"),
                    new SkillSuggestion("Ruby"),
                    new SkillSuggestion("Node.js"),
                    new SkillSuggestion("Java"),
                    new SkillSuggestion("PHP"),
                    new SkillSuggestion("Javascript"),
                    new SkillSuggestion("HTML"),
                    new SkillSuggestion("CSS"),
                    new SkillSuggestion("Azure"),
                    new SkillSuggestion("AWS"),
                    new SkillSuggestion("Mobile"),
                    new SkillSuggestion("iOS"),
                    new SkillSuggestion("Android"),
                    new SkillSuggestion("Swift"),
                    new SkillSuggestion("TensorFlow"),
                    new SkillSuggestion("Machine Learning"),
                    new SkillSuggestion("Artificial Intelligence"),
                    new SkillSuggestion("Bitcoin"),
                    new SkillSuggestion("Ripple"),
                    new SkillSuggestion("Ethereum"),
                    new SkillSuggestion("AJAX"),
                    new SkillSuggestion("ASP.Net"),
                    new SkillSuggestion("Microsoft"),
                    new SkillSuggestion("AutoCAD"),
                    new SkillSuggestion("MatLab"),
                    new SkillSuggestion("Robotics"),
                    new SkillSuggestion("macOS"),
                    new SkillSuggestion("Linux"),
                    new SkillSuggestion("Git"),
                    new SkillSuggestion("Data Science"),
                    new SkillSuggestion("NumPy"),
                    new SkillSuggestion("Scheme"),
                    new SkillSuggestion("SQL"),
                    new SkillSuggestion("VR"),
                    new SkillSuggestion("AR"),
                    new SkillSuggestion("Database"),
                    new SkillSuggestion("Firebase"),
                    new SkillSuggestion("UI/UX"),
                    new SkillSuggestion("Design"),
                    new SkillSuggestion("Graphic Design"),
                    new SkillSuggestion("Photoshop")));

    public interface OnFindSuggestionsListener {
        void onResults(List<SkillSuggestion> results);
    }

    public static void findSuggestions(Context context, String query, final int limit, final long simulatedDelay,
                                       final OnFindSuggestionsListener listener) {
        new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                try {
                    Thread.sleep(simulatedDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                List<SkillSuggestion> suggestionList = new ArrayList<>();
                if (!(constraint == null || constraint.length() == 0)) {

                    for (SkillSuggestion suggestion : sSkillSuggestions) {
                        if (suggestion.getBody().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase())) {

                            suggestionList.add(suggestion);
                            if (limit != -1 && suggestionList.size() == limit) {
                                break;
                            }
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = suggestionList;
                results.count = suggestionList.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                if (listener != null) {
                    listener.onResults((List<SkillSuggestion>) results.values);
                }
            }
        }.filter(query);

    }
}
