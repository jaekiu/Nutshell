package com.jackie.nutshell;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ExploreFragment extends Fragment {
    //public class ExploreFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mRecyclerView;


    public ExploreFragment() { }

    @NonNull
    public static ExploreFragment newInstance() { return new ExploreFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        List<String> list = new ArrayList<>();
        list.add("Black Pearl");
        list.add("Super Bowl");
        list.add("DJ");
        list.add("Developer");
        list.add("Sleep");

        mRecyclerView = v.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new RecyclerViewAdapter(list, getContext()));
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

        List<String> mlist;
        Context context;

        public RecyclerViewAdapter(List<String> list, Context context) {

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

            holder.mProjName.setText(mlist.get(position));
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
                        Intent intent = new Intent(getActivity(), ViewActivity.class);
                        startActivity(intent);
                    }
                });

            }
        }



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}
