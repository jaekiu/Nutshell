package com.jackie.nutshell;

import android.content.Intent;
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

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ExploreFragment extends Fragment implements View.OnClickListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private String[] myStringArray = new String[]{"a", "b", "c"};


    public ExploreFragment() { }

    @NonNull
    public static ExploreFragment newInstance() { return new ExploreFragment(); }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore, container, false);

        Button viewbutton = getView().findViewById(R.id.viewbtn);
        viewbutton.setOnClickListener(this);

        return v;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.viewbtn:
                Intent intent = new Intent(getActivity(), ViewActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRecyclerView = getView().findViewById(R.id.recycler_view);

        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myStringArray);
        mRecyclerView.setAdapter(mAdapter);



    }

}
