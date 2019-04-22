package com.jackie.nutshell.Searching;

import android.content.Context;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataHelper {

    private static List<SkillSuggestion> sSkillSuggestions =
            new ArrayList<>(Arrays.asList(
                    new SkillSuggestion("green"),
                    new SkillSuggestion("blue"),
                    new SkillSuggestion("pink"),
                    new SkillSuggestion("purple"),
                    new SkillSuggestion("brown"),
                    new SkillSuggestion("gray"),
                    new SkillSuggestion("Granny Smith Apple"),
                    new SkillSuggestion("Indigo"),
                    new SkillSuggestion("Periwinkle"),
                    new SkillSuggestion("Mahogany"),
                    new SkillSuggestion("Maize"),
                    new SkillSuggestion("Mahogany"),
                    new SkillSuggestion("Outer Space"),
                    new SkillSuggestion("Melon"),
                    new SkillSuggestion("Yellow"),
                    new SkillSuggestion("Orange"),
                    new SkillSuggestion("Red"),
                    new SkillSuggestion("Orchid")));

    public interface OnFindSuggestionsListener {
        void onResults(List<SkillSuggestion> results);
    }

    public static List<SkillSuggestion> getHistory(Context context, int count) {

        List<SkillSuggestion> suggestionList = new ArrayList<>();
        SkillSuggestion SkillSuggestion;
        for (int i = 0; i < sSkillSuggestions.size(); i++) {
            SkillSuggestion = sSkillSuggestions.get(i);
            suggestionList.add(SkillSuggestion);
            if (suggestionList.size() == count) {
                break;
            }
        }
        return suggestionList;
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
