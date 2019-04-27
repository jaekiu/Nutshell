package com.jackie.nutshell;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.jackie.nutshell.Utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ExploreFragment extends Fragment {
    //public class ExploreFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private DatabaseReference usersDBRef;
    private DatabaseReference projsDBRef;
    private ArrayList<Proj> projects;
    private RecyclerViewAdapter adapter;


    public ExploreFragment() { }

    @NonNull
    public static ExploreFragment newInstance() { return new ExploreFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);


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
                ArrayList<Proj> newProjs = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String name = snapshot.child("name").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);
                    String poster = snapshot.child("user").getValue(String.class);
                    Proj p = new Proj(name, description, new String[]{}, poster);
                    newProjs.add(p);
                }
                // Need to reverse the list because Firebase doesn't have a descending ordering function.
                // https://stackoverflow.com/questions/34156996/firebase-data-desc-sorting-in-android
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


//        List<String> list = new ArrayList<>();
//        list.add("Black Pearl");
//        list.add("Super Bowl");
//        list.add("DJ");
//        list.add("Developer");
//        list.add("Sleep");

        return v;



    }

//    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
//        public CardView mCardView;
//        public TextView mProjName;
//        public TextView mProjDes;
//        public ImageView mProfile;
//        public Button viewbutton;
//        public Button mView;
//        public Button mApply;
//
//
//
//        public RecyclerViewHolder(View itemView) {
//            super(itemView);
//        }
//
//        public RecyclerViewHolder(LayoutInflater inflater, ViewGroup container) {
//            super(inflater.inflate(R.layout.cardview, container, false));
//
//            mCardView = itemView.findViewById(R.id.card);
//            mProjName = itemView.findViewById(R.id.proj_name);
//            mProjDes = itemView.findViewById(R.id.proj_des);
//            mProfile = itemView.findViewById(R.id.profilePic);
//            viewbutton = itemView.findViewById(R.id.viewbtn);
//            mApply = itemView.findViewById(R.id.applybtn);
//
//        }
//    }

    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

        private List<Proj> mlist;
        private Context context;

        public RecyclerViewAdapter(List<Proj> list, Context context) {

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

        public class RecyclerViewHolder extends RecyclerView.ViewHolder  {
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
                viewbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), ViewActivity.class);
                        startActivity(intent);
                    }
                });
                mApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setCancelable(true);
                        builder.setTitle("Title");
                        builder.setMessage("Message");
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
                    }
                });

            }
            void bind (int position) {
                Proj currProj = mlist.get(position);
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
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}
