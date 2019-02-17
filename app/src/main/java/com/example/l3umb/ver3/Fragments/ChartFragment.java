package com.example.l3umb.ver3.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.l3umb.ver3.Fragments.Chart.PieChartFragment;
import com.example.l3umb.ver3.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dev on 11/2/2018.
 */

public class ChartFragment extends Fragment implements View.OnClickListener{
    private ImageView ivBack;
    private ViewPager vpChart;
    private TabLayout tlChart;
    private ChartPageAdapter chartPagerAdapter;
    private Fragment fragmentHome, itemFragment;
    int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart,container,false);
        initLayout(view);
        initAdapter();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                goToFragment(fragmentHome);
        }
    }

    private void initLayout(View v) {
        ivBack = (ImageView) v.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);

        fragmentHome = new HomeFragment();

        vpChart = (ViewPager) v.findViewById(R.id.vpChart);
        tlChart = (TabLayout) v.findViewById(R.id.tlChart);

        chartPagerAdapter = new ChartPageAdapter(getChildFragmentManager());

        vpChart.setAdapter(chartPagerAdapter);
        vpChart.setOffscreenPageLimit(3);
        tlChart.setTabGravity(TabLayout.GRAVITY_FILL);
        tlChart.setupWithViewPager(vpChart);
        tlChart.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void initAdapter() {
        Bundle bundle;
        PieChartFragment pieChartFragment;
        switch (page) {
            case 0:
                bundle = new Bundle();
                bundle.putInt("page", 1);
                pieChartFragment = new PieChartFragment();
                pieChartFragment.setArguments(bundle);
                chartPagerAdapter.addFragment(pieChartFragment);
                chartPagerAdapter.notifyDataSetChanged();

            case 1:
                bundle = new Bundle();
                bundle.putInt("page", 2);
                pieChartFragment = new PieChartFragment();
                pieChartFragment.setArguments(bundle);
                chartPagerAdapter.addFragment(pieChartFragment);
                chartPagerAdapter.notifyDataSetChanged();

            case 2:
                bundle = new Bundle();
                bundle.putString("category", "user_scoreboard");
                itemFragment = new ItemFragment();
                itemFragment.setArguments(bundle);
                chartPagerAdapter.addFragment(itemFragment);
                chartPagerAdapter.notifyDataSetChanged();
        }
    }

    protected void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }
}

class ChartPageAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> listFragment = new ArrayList<>();

    public ChartPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return listFragment.get(position);
    }

    @Override
    public int getCount() {
        return listFragment.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    public void addFragment(Fragment fragment){
        listFragment.add(fragment);
    }

    @Override
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "TOP CATEGORY";
            case 1:
                return "TOP PRODUCT";
            case 2:
                return "SCOREBOARD";
        }
        return null;
    }
}
