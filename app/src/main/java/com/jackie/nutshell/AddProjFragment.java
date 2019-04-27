package com.jackie.nutshell;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.arlib.floatingsearchview.FloatingSearchView;
import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;
import com.google.firebase.database.DatabaseReference;
import com.jackie.nutshell.Searching.DataHelper;
import com.jackie.nutshell.Searching.SkillSuggestion;
import com.jackie.nutshell.Utils.FirebaseUtils;
import com.jackie.nutshell.Utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AddProjFragment extends Fragment {

    public static final long FIND_SUGGESTION_SIMULATED_DELAY = 250;

    EditText search;
    EditText editName;
    EditText editDesc;
    private DatabaseReference usersDBRef;
    private DatabaseReference projsDBRef;
    private SkillsAdapter skillsAdapter;
    ArrayList<String> skills = new ArrayList<>();
    private FloatingSearchView mSearchView;



    public AddProjFragment() { }

    @NonNull
    public static AddProjFragment newInstance() { return new AddProjFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addproj, container, false);

        this.search = v.findViewById(R.id.search);
        this.editName = v.findViewById(R.id.editName);
        this.editDesc = v.findViewById(R.id.editDesc);
        usersDBRef = FirebaseUtils.getUsersDatabaseRef();
        projsDBRef = FirebaseUtils.getProjsDatabaseRef();
        mSearchView = v.findViewById(R.id.floating_search_view);

        // Handling GridView for skills
        GridView gridView = v.findViewById(R.id.skillGridView);
        skillsAdapter = new SkillsAdapter(getContext(), skills);
        gridView.setAdapter(skillsAdapter);

        // Handling searching
        setupSearch();

        setHasOptionsMenu(true);
        return v;
    }

    boolean checkFields(String skills, String name, String desc) {
        if (skills == null || name == null || desc == null) {
            Toast.makeText(getContext(), "Please fill out all the sections!", Toast.LENGTH_SHORT);
            return false;
        } else if (skills.equals("") || name.equals("") || desc.equals("")) {
            Toast.makeText(getContext(), "Please fill out all the sections!", Toast.LENGTH_SHORT);
            return false;
        } else {
            sendToProjsDatabase(skills, name, desc);
            return true;
        }
    }

    private void sendToProjsDatabase(String skills, String name, String desc) {
        final String key = projsDBRef.push().getKey();
        projsDBRef.child(key).child("name").setValue(name);
        projsDBRef.child(key).child("description").setValue(desc);
        projsDBRef.child(key).child("skills").setValue(skills);


    }

    /** Sets up searching. */
    private void setupSearch() {
        mSearchView.setShowSearchKey(true);
        mSearchView.setCloseSearchOnKeyboardDismiss(true);

        mSearchView.setOnFocusChangeListener(new FloatingSearchView.OnFocusChangeListener() {
            @Override
            public void onFocus() {
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mSearchView.getLayoutParams();
                p.setMargins(0, 0, 0, 0);
                mSearchView.requestLayout();
                mSearchView.getLayoutParams().height= ViewGroup.LayoutParams.MATCH_PARENT;
                mSearchView.getLayoutParams().width= ViewGroup.LayoutParams.MATCH_PARENT;
            }

            @Override
            public void onFocusCleared() {
                int pixelsH = Utils.dpToPx(408, getResources());
                int pixelsR = Utils.dpToPx(28, getResources());
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) mSearchView.getLayoutParams();
                p.setMargins(0, 0, pixelsR, 0);
                mSearchView.getLayoutParams().height= pixelsH;
                mSearchView.getLayoutParams().width = 0;
            }
        });

        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                //get suggestions based on newQuery
                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    //this shows the top left circular progress
                    //you can call it where ever you want, but
                    //it makes sense to do it when loading something in
                    //the background.
                    mSearchView.showProgress();

                    //simulates a query call to a data source
                    //with a new query.
                    DataHelper.findSuggestions(getContext(), newQuery, 5,
                            FIND_SUGGESTION_SIMULATED_DELAY, new DataHelper.OnFindSuggestionsListener() {

                                @Override
                                public void onResults(List<SkillSuggestion> results) {

                                    //this will swap the data and
                                    //render the collapse/expand animations as necessary
                                    mSearchView.swapSuggestions(results);

                                    //let the users know that the background
                                    //process has completed
                                    mSearchView.hideProgress();
                                }
                            });
                }
            }
        });

        mSearchView.setOnSearchListener(new FloatingSearchView.OnSearchListener() {
            @Override
            public void onSuggestionClicked(final SearchSuggestion searchSuggestion) {
                String lastQuery = searchSuggestion.getBody();
                mSearchView.setSearchText(lastQuery);
            }

            @Override
            public void onSearchAction(String currentQuery) {
                String query = "";
                switch (currentQuery) {
                    case "Machine Learning":
                        query = "ML";
                        break;
                    case "Artificial Intelligence":
                        query = "AI";
                        break;
                    case "Graphic Design":
                        query = "Graphic Design";
                        break;
                    default:
                        query = currentQuery;
                }
                if (query.length() > 8 && !query.toLowerCase().equals("javascript")) {
                    query = query.substring(0, 6) + "...";
                }
                addSkill(query);
            }
        });
    }

    /** Adds skill to skills ArrayList (used for populating GridView). */
    private void addSkill(String skill) {
        if (skills.size() == 5) {
            Toast.makeText(getContext(), "You can only have 5 skills!", Toast.LENGTH_SHORT).show();
        } else if (skills.contains(skill)) {
            Toast.makeText(getContext(), "Skill already exists!", Toast.LENGTH_SHORT).show();
        } else {
            skills.add(skill);
            skillsAdapter.notifyDataSetChanged();
        }
    }


}
