package com.jackie.nutshell;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.jackie.nutshell.Utils.FirebaseUtils;

public class AddProjFragment extends Fragment {

    private EditText search;
    private EditText editName;
    private EditText editDesc;
    private DatabaseReference usersDBRef;
    private DatabaseReference projsDBRef;


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

        setHasOptionsMenu(true);
        return v;
    }

    private void checkFields(String skills, String name, String desc, View v) {
        if (skills == null || name == null || desc == null) {
            Toast.makeText(v.getContext(), "Please fill out all the sections!", Toast.LENGTH_SHORT);
        } else if (skills.equals("") || name.equals("") || desc.equals("")) {
            Toast.makeText(v.getContext(), "Please fill out all the sections!", Toast.LENGTH_SHORT);
        } else {
            sendToProjsDatabase(skills, name, desc);
        }
    }

    private void sendToProjsDatabase(String skills, String name, String desc) {
        final String key = projsDBRef.push().getKey();
        projsDBRef.child(key).child("name").setValue(name);
        projsDBRef.child(key).child("description").setValue(desc);
        projsDBRef.child(key).child("skills").setValue(skills);


    }

}
