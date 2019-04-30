package com.jackie.nutshell;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.Models.Project;
import com.jackie.nutshell.Utils.FirebaseUtils;

import java.util.List;

/** @author jackie
 * Created on 4/28/19.
 * Represents the Projects Adapter for populating projects. */

public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectsViewHolder> {

    private List<Project> mlist;
    private Context context;
    private Activity activity;

    public ProjectsAdapter(List<Project> list, Context context, Activity activity) {

        this.mlist = list;
        this.context = context;
        this.activity = activity;
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
            if (poster.equals(FirebaseUtils.getFirebaseUser().getUid())) {
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
}
