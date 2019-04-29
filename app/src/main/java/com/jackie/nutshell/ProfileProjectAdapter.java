package com.jackie.nutshell;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileProjectAdapter extends RecyclerView.Adapter<ProfileProjectAdapter.MyViewHolder> {
    private String[] list;
    private Context _c;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View textView;

        public MyViewHolder(View v) {
            super(v);
            textView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public ProfileProjectAdapter(String[] myDataset, Context c) {
        list = myDataset;
        _c = c;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ProfileProjectAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview, parent, false);
        ProfileProjectAdapter.MyViewHolder vh = new ProfileProjectAdapter.MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ProfileProjectAdapter.MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        // holder.textView.setText(list[position]);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.length;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
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
//                    Intent intent = new Intent(itemView.getContext(), ViewActivity.class);
//                    startActivity(intent);
                }
            });
            mApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(_c);
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
    }
}
