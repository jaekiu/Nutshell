package com.jackie.nutshell;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/** skills adapter for SetupActivity */
public class SkillsAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> skills;
    private int[] colors;

    public SkillsAdapter(Context context, ArrayList<String> skills) {
        this.mContext = context;
        this.skills = skills;
        colors = new int[5];
        colors[0] = mContext.getResources().getColor(R.color.colorSkill1);
        colors[1] = mContext.getResources().getColor(R.color.colorSkill2);
        colors[2] = mContext.getResources().getColor(R.color.colorSkill3);
        colors[3] = mContext.getResources().getColor(R.color.colorSkill4);
        colors[4] = mContext.getResources().getColor(R.color.colorSkill5);
    }

    @Override
    public int getCount() {
        return skills.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String skill = skills.get(position);
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.gridview_skills, null);
        }

        CardView cardView = convertView.findViewById(R.id.skillCard);
        TextView skillName = convertView.findViewById(R.id.skillName);
        Button deleteBtn = convertView.findViewById(R.id.deleteSkillBtn);

        cardView.setCardBackgroundColor(colors[position]);
        skillName.setText(skill);

        return convertView;
    }
}
