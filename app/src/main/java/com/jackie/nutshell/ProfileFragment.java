package com.jackie.nutshell;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.Models.Project;
import com.jackie.nutshell.Models.User;
import com.jackie.nutshell.Utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

    TextView _about;
    TextView _projects;
    private ImageView _pic;
    private TextView _karma;
    private TextView _name;
    private Button _linkedin;
    private Button _github;
    private GridView _gridview;
    private DatabaseReference _userRef;
    private String _id;
    private Context _c;
    private Activity _activity;
    private SkillsAdapterReg _skillsAdapterReg;
    private ArrayList<String> skills = new ArrayList<>(5);

    public ProfileFragment() { }

    @NonNull
    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_profile, container, false);

        // Retrieves information from whatever was clicked if something was even clicked lol
        Bundle bundle = this.getArguments();
        _id = "";
        if (bundle != null) {
            _id = bundle.getString("posterId", "");
        }

        // Initialize UI elements
        _pic = rootview.findViewById(R.id.pic);
        _name = rootview.findViewById(R.id.name);
        _karma = rootview.findViewById(R.id.karma);
        _linkedin = rootview.findViewById(R.id.linkedin);
        _github = rootview.findViewById(R.id.github);
        _gridview = rootview.findViewById(R.id.gridView);
        _userRef = FirebaseUtils.getUsersDatabaseRef().child(_id);
        _c = getContext();
        _activity = getActivity();

        _skillsAdapterReg = new SkillsAdapterReg(getContext(), skills);
        _gridview.setAdapter(_skillsAdapterReg);

        getUserInfo();

        // Configures the tabs
        _about = rootview.findViewById(R.id.about);
        _about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _about.setTypeface(Typeface.DEFAULT_BOLD);
                _projects.setTypeface(Typeface.DEFAULT);
            }
        });

        _projects = rootview.findViewById(R.id.projects);
        _projects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _about.setTypeface(Typeface.DEFAULT_BOLD);
                _projects.setTypeface(Typeface.DEFAULT);
                Intent i = new Intent(getContext(), ProjectProfile.class);
                startActivity(i);
            }
        });

        return rootview;
    }

    void getUserInfo() {
        // Realtime database retrieval.
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> attributes = new HashMap<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String key = snapshot.getKey();
                    if (key.equals("name")) {
                        _name.setText(snapshot.getValue(String.class));
                    } else if (key.equals("karma")) {
                        Integer karma = snapshot.getValue(Integer.class);
                        _karma.setText(karma + " Karma");
                    } else if (key.equals("linkedIn")) {
                        String linkedin = snapshot.getValue(String.class);
                        if (linkedin != null && !linkedin.equals("") && !linkedin.startsWith("http://") && !linkedin.startsWith("http:s//")) {
                            linkedin = "http://" + linkedin;
                        }
                        final String finalized = linkedin;
                        Log.d("nani", "what is my linkedin: " + finalized);

                        if (linkedin == null && linkedin.equals("")) {
                            _linkedin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(_c);
                                    builder.setCancelable(true);
                                    builder.setTitle("Error!");
                                    builder.setMessage("User has not linked his or her LinkedIn!");
                                    builder.setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        } else {
                            _linkedin.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("nani", "what is my linkedin: " + finalized);

                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalized));
                                    startActivity(browserIntent);
                                }
                            });
                        }
                    } else if (key.equals("github")) {
                        String github = snapshot.getValue(String.class);
                        if (github != null && !github.equals("") && !github.startsWith("http://") && !github.startsWith("http:s//")) {
                            github = "http://" + github;
                        }
                        final String finalized = github;
                        if (github == null && github.equals("")) {
                            _github.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(_c);
                                    builder.setCancelable(true);
                                    builder.setTitle("Error!");
                                    builder.setMessage("User has not linked his or her Github!");
                                    builder.setPositiveButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            });

                                    AlertDialog dialog = builder.create();
                                    dialog.show();
                                }
                            });
                        } else {
                            _github.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalized));
                                    startActivity(browserIntent);
                                }
                            });
                        }


                    } else if (key.equals("skills")) {
                        ValueEventListener postListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int i = 0;
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    // category is an ArrayList you can declare above
                                    String skill = dataSnapshot.child(String.valueOf(i)).getValue(String.class);
                                    switch (skill) {
                                        case "Machine Learning":
                                            skill = "ML";
                                            break;
                                        case "Artificial Intelligence":
                                            skill = "AI";
                                            break;
                                        case "Graphic Design":
                                            skill = "GFX";
                                            break;
                                    }
                                    if (skill.length() > 10 ) {
                                        skill = skill.substring(0, 8) + "...";
                                    }
                                    skills.add(skill);
                                    _skillsAdapterReg.notifyDataSetChanged();
                                    i++;
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
                            }
                        };
                        _userRef.child("skills").addValueEventListener(postListener);

                    }
//                    else {
//                        String value = snapshot.getValue(String.class);
//                        attributes.put(key, value);
//                    }
                }
//                String name = (String) attributes.get("name");
//                int karma = (Integer) attributes.get("karma");
//                String linkedin = (String) attributes.get("linkedin");
//                String github = (String) attributes.get("github");
//                String number = (String) attributes.get("number");
                // ArrayList<String> skills = attributes.get("skills");
//                _name.setText(name);
//                _karma.setText(karma + " Karma");

                StorageReference storageRef = FirebaseUtils.getFirebaseStorage().getReference();
                StorageReference imgRef = storageRef.child("users").child(_id + ".jpeg");
                // Handling images
                Glide.with(_c).load(imgRef).centerCrop().into(_pic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
            }
        };
        _userRef.addValueEventListener(postListener);
    }

}
