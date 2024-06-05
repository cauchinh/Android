package com.example.to_do_list;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class JobAdapter extends ArrayAdapter<Job> {
    public JobAdapter(@NonNull Context context, int resource, @NonNull List<Job> objects) {
        super(context, resource, objects);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.items, null);
        }
        Job jd = getItem(position);
        if (jd != null){
            TextView tv_job = (TextView) view.findViewById(R.id.tv_job);
            TextView tv_id = (TextView) view.findViewById(R.id.tv_id);
            tv_job.setText(jd.getNameJob());
            tv_id.setText(String.valueOf(jd.getId()));
        }


        return view;
    }
}
