package com.jackie.nutshell;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.Models.User;
import com.jackie.nutshell.Utils.FirebaseUtils;

import java.util.ArrayList;

public class ProfileProjectFragment extends Fragment {
    TextView _about;
    TextView _projects;
    private ImageView _pic;
    private TextView _karma;
    private TextView _name;
    private User _user;
    private Context _c;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private Activity _activity;
    private SkillsAdapterReg _skillsAdapterReg;
    private ArrayList<String> skills = new ArrayList<>(5);

    public ProfileProjectFragment() { }

    @NonNull
    public static ProfileFragment newInstance() { return new ProfileFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_profile_project, container, false);

        // Retrieves information from whatever was clicked if something was even clicked lol
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            _user = (User) bundle.getSerializable("user");
        }

        // Initialize UI elements
        _pic = rootview.findViewById(R.id.pic);
        _name = rootview.findViewById(R.id.name);
        _karma = rootview.findViewById(R.id.karma);
        _c = getContext();
        _activity = getActivity();

        // Tab controller
        _about = rootview.findViewById(R.id.about);
        _about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new ProfileFragment();
                Bundle bundle = new Bundle();
                bundle.putString("posterId", _user.getId());
                fragment.setArguments(bundle);
                ((ExploreActivity)_c).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();
            }
        });

        // Populates UI
        String id = _user.getId();
        StorageReference storageRef = FirebaseUtils.getFirebaseStorage().getReference();
        StorageReference imgRef = storageRef.child("users").child(id + ".jpeg");
        // Handling images
        Glide.with(_c).load(imgRef).centerCrop().into(_pic);
        _name.setText(_user.getName());
        _karma.setText(_user.getKarma());


        String[] list = {"Hey", "Yo", "Riding", "Horses", "Old Town Road"};
        recyclerView = rootview.findViewById(R.id.recycler_view2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ProfileProjectAdapter(list, getContext());
        recyclerView.setAdapter(mAdapter);

        return rootview;
    }
}
