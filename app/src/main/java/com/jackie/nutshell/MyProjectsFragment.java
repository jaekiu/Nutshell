package com.jackie.nutshell;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.Models.Project;
import com.jackie.nutshell.Utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProjectsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private DatabaseReference usersDBRef;
    private DatabaseReference projsDBRef;
    private ArrayList<Project> projects;
    private RecyclerViewAdapter adapter;

    public MyProjectsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_my_projects, container, false);

        usersDBRef = FirebaseUtils.getUsersDatabaseRef();
        projsDBRef = FirebaseUtils.getProjsDatabaseRef();
        projects = new ArrayList<>();


        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new RecyclerViewAdapter(projects, getContext());
        mRecyclerView.setAdapter(adapter);

        // Realtime database retrieval.
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Project> newProjs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String poster = snapshot.child("user").getValue(String.class);
                    Project p = new Project(name, description, new String[]{}, poster);
                    newProjs.add(p);
                }
                projects.clear();
                projects.addAll(newProjs);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("ERROR", "loadPost:onCancelled", databaseError.toException());
            }
        };
        projsDBRef.orderByKey().addValueEventListener(postListener);


        return v;
    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

        private List<Project> mlist;
        private Context context;

        public RecyclerViewAdapter(List<Project> list, Context context) {

            this.mlist = list;
            this.context = context;
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.cardview, parent, false);
            return new RecyclerViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            holder.bind(position);
        }

        @Override
        public int getItemCount() {
            return mlist.size();
        }

        public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private CardView mCardView;
            private TextView mProjName;
            private TextView mProjDes;
            private ImageView mProfile;
            private Button viewbutton;
            private Button mApply;

            public RecyclerViewHolder(final View itemView) {
                super(itemView);
                mCardView = itemView.findViewById(R.id.card);
                mProjName = itemView.findViewById(R.id.proj_name);
                mProjDes = itemView.findViewById(R.id.proj_des);
                mProfile = itemView.findViewById(R.id.profilePic);
                viewbutton = itemView.findViewById(R.id.viewbtn);
                mApply = itemView.findViewById(R.id.applybtn);
                mProfile.setOnClickListener(this);
                viewbutton.setOnClickListener(this);
                mApply.setOnClickListener(this);


            }
            void bind (int position) {
                Project currProj = mlist.get(position);
                String name = currProj.getName();
                String description = currProj.getDesc();
                String poster = currProj.getPoster();
                StorageReference storageRef = FirebaseUtils.getFirebaseStorage().getReference();
                StorageReference imgRef = storageRef.child("users").child(poster + ".jpeg");
                // Handling images
                Glide.with(context).load(imgRef).centerCrop().into(mProfile);
                mProjName.setText(name);
                mProjDes.setText(description);

            }

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.profilePic:
                        Fragment fragment = new ProfileFragment();
                        Bundle bundle = new Bundle();
                        int i = this.getLayoutPosition();
                        Project currProj = mlist.get(i);
                        bundle.putString("posterId", currProj.getPoster());
                        fragment.setArguments(bundle);
                        ((ExploreActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                fragment).commit();
                        break;
                    case R.id.applybtn:
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
                        builder.setCancelable(true);
                        builder.setTitle("Confirm application?");
                        builder.setMessage("Confirming will send your profile to the poster");
                        builder.setPositiveButton("Confirm",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                        break;
                    case R.id.viewbtn:
                        Intent intent = new Intent(getActivity(), ViewActivity.class);
                        Project currentProj = mlist.get(getAdapterPosition());
                        intent.putExtra("Project", currentProj);
                        startActivity(intent);
                        break;
                }
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}