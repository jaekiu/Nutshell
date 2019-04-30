package com.jackie.nutshell.Adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jackie.nutshell.R;

import java.util.ArrayList;
/** @author jackie
 * Created on 4/6/19.
 * Represents the Skills Adapter for displaying the skills after searching. */

/** skills adapter for SetupActivity */
public class SkillsAdapterReg extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> skills;
    private int[] colors;

    public SkillsAdapterReg(Context context, ArrayList<String> skills) {
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
        if (convertView == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.gridview_skills_reg, null);
        }
        if (position < 5)  {
            String skill = skills.get(position);
            CardView cardView = convertView.findViewById(R.id.skillCardNoX);
            TextView skillName = convertView.findViewById(R.id.skillName);

            cardView.setCardBackgroundColor(colors[position]);
            skillName.setText(skill);
        }
        return convertView;
    }
}
