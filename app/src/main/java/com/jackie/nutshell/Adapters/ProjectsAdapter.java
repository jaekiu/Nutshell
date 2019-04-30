package com.jackie.nutshell.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.ExploreActivity;
import com.jackie.nutshell.Models.Project;
import com.jackie.nutshell.ProfileFragment;
import com.jackie.nutshell.R;
import com.jackie.nutshell.Utils.FirebaseUtils;
import com.jackie.nutshell.ViewActivity;

import java.util.ArrayList;
import java.util.List;

/** @author jackie
 * Created on 4/28/19.
 * Represents the Projects Adapter for populating projects. */

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectsViewHolder> {

    private List<Project> mlist;
    private Context context;
    private Activity activity;
    private DatabaseReference projsDBRef;
    private DatabaseReference usersRef;

    public ProjectsAdapter(List<Project> list, Context context, Activity activity) {

        this.mlist = list;
        this.context = context;
        this.activity = activity;
        projsDBRef = FirebaseUtils.getProjsDatabaseRef();
        usersRef = FirebaseUtils.getUsersDatabaseRef();
    }

    @Override
    public ProjectsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cardview, parent, false);
        return new ProjectsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProjectsViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mlist.size();
    }

    public class ProjectsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mProjName;
        private TextView mProjDes;
        private ImageView mProfile;
        private Button viewbutton;
        private Button mApply;

        public ProjectsViewHolder(final View itemView) {
            super(itemView);
            mProjName = itemView.findViewById(R.id.proj_name);
            mProjDes = itemView.findViewById(R.id.proj_des);
            mProfile = itemView.findViewById(R.id.profilePic);
            viewbutton = itemView.findViewById(R.id.viewbtn);
            mApply = itemView.findViewById(R.id.applybtn);
            mProfile.setOnClickListener(this);
            viewbutton.setOnClickListener(this);
            mApply.setOnClickListener(this);


        }
        void bind(int position) {
            Project currProj = mlist.get(position);
            String name = currProj.getName();
            String description = currProj.getDesc();
            if (description.length() > 300) {
                description = description.substring(0, 295) + "...";
            }
            String poster = currProj.getPoster();
            FirebaseUser user = FirebaseUtils.getFirebaseUser();
            if (poster != null && user != null && poster.equals(user.getUid())) {
                mApply.setVisibility(View.GONE);
            }
            StorageReference storageRef = FirebaseUtils.getFirebaseStorage().getReference();
            StorageReference imgRef = storageRef.child("users").child(poster + ".jpeg");
            // Handling images
            Glide.with(context).load(imgRef).centerCrop().into(mProfile);
            mProjName.setText(name);
            mProjDes.setText(description);

        }

        @Override
        public void onClick(final View v) {
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setTitle("Confirm application?");
                    builder.setMessage("Confirming will send your profile to the poster");
                    builder.setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Button apply = v.findViewById(R.id.applybtn);
                                    apply.setEnabled(false);
                                    apply.setText("Applied");
                                    int i = getLayoutPosition();
                                    Project currProj = mlist.get(i);
                                    updateProjectsDB(currProj.getId());
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
                    Intent intent = new Intent(activity, ViewActivity.class);
                    Project currentProj = mlist.get(getAdapterPosition());
                    intent.putExtra("Project", currentProj);
                    activity.startActivity(intent);
                    break;
            }
        }
    }

    private void updateProjectsDB(final String id) {
        final FirebaseUser user = FirebaseUtils.getFirebaseUser();
        projsDBRef.child(id).child("applied").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> applied = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    //Key exists
                    applied = (ArrayList<String>) dataSnapshot.getValue();
                    if (applied == null) {
                        applied = new ArrayList<>();
                    }
                }
                applied.add(user.getUid());
                projsDBRef.child(id).child("applied").setValue(applied);
                usersRef.child(user.getUid()).child("appliedProjects").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        ArrayList<String> appliedProjects = new ArrayList<>();
                        if (dataSnapshot.exists()) {
                            appliedProjects = (ArrayList<String>) dataSnapshot.getValue();
                            if (appliedProjects == null) {
                                appliedProjects = new ArrayList<>();
                            }
                        }
                        appliedProjects.add(id);
                        usersRef.child(user.getUid()).child("appliedProjects").setValue(appliedProjects);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
