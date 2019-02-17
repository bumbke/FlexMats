package com.example.l3umb.ver3.Fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.l3umb.ver3.R;

public class UserDetailItemFragment extends Fragment {
    private TextView tvSummary, tvGender, tvDoB, tvJobs;
    private Drawable male, female;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_about_user,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        tvSummary.setText(getArguments().getString("user_bio"));
        if (getArguments().getString("user_gender").equalsIgnoreCase("male")) {
            tvGender.setCompoundDrawablesWithIntrinsicBounds(male, null, null, null);
        } else {
            tvGender.setCompoundDrawablesWithIntrinsicBounds(female, null, null, null);
        }
        tvGender.setText(getArguments().getString("user_gender"));
        tvDoB.setText(getArguments().getString("user_dob"));
        tvJobs.setText(getArguments().getString("user_jobs"));
    }

    private void initLayout(View v) {
        tvSummary = (TextView) v.findViewById(R.id.tvSummary);
        tvGender = (TextView) v.findViewById(R.id.tvGender);
        tvDoB = (TextView) v.findViewById(R.id.tvDoB);
        tvJobs = (TextView) v.findViewById(R.id.tvJobs);

        male = getResources().getDrawable(R.drawable.ic_gender_male);
        female = getResources().getDrawable(R.drawable.ic_gender_female);
    }
}
