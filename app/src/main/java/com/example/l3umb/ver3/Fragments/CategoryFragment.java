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

import com.example.l3umb.ver3.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dev on 10/28/2018.
 */

public class CategoryFragment extends Fragment {
    ViewPager vpCategory;
    TabLayout tlCategory;
    CategoryPagerAdapter categoryPagerAdapter;
    int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initAdapter();
    }

    private void initLayout(View v) {
        vpCategory = (ViewPager) v.findViewById(R.id.vpCategory);
        tlCategory = (TabLayout) v.findViewById(R.id.tlCategory);

        categoryPagerAdapter = new CategoryPagerAdapter(getChildFragmentManager());

        vpCategory.setAdapter(categoryPagerAdapter);
        vpCategory.setOffscreenPageLimit(3);

        tlCategory.setTabGravity(TabLayout.GRAVITY_FILL);
        tlCategory.setupWithViewPager(vpCategory);
        tlCategory.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
        ItemFragment itemFragment;
        Bundle bundle;
        switch (page) {
            case 0:
                itemFragment = new ItemFragment();
                bundle = new Bundle();
                bundle.putString("category", categoryPagerAdapter.getPageTitle(0).toString());
                itemFragment.setArguments(bundle);
                categoryPagerAdapter.addFragment(itemFragment);
                categoryPagerAdapter.notifyDataSetChanged();
            case 1:
                itemFragment = new ItemFragment();
                bundle = new Bundle();
                bundle.putString("category", categoryPagerAdapter.getPageTitle(1).toString());
                itemFragment.setArguments(bundle);
                categoryPagerAdapter.addFragment(itemFragment);
                categoryPagerAdapter.notifyDataSetChanged();
            case 2:
                itemFragment = new ItemFragment();
                bundle = new Bundle();
                bundle.putString("category", categoryPagerAdapter.getPageTitle(2).toString());
                itemFragment.setArguments(bundle);
                categoryPagerAdapter.addFragment(itemFragment);
                categoryPagerAdapter.notifyDataSetChanged();
            case 3:
                itemFragment = new ItemFragment();
                bundle = new Bundle();
                bundle.putString("category", categoryPagerAdapter.getPageTitle(3).toString());
                itemFragment.setArguments(bundle);
                categoryPagerAdapter.addFragment(itemFragment);
                categoryPagerAdapter.notifyDataSetChanged();
            case 4:
                itemFragment = new ItemFragment();
                bundle = new Bundle();
                bundle.putString("category", categoryPagerAdapter.getPageTitle(4).toString());
                itemFragment.setArguments(bundle);
                categoryPagerAdapter.addFragment(itemFragment);
                categoryPagerAdapter.notifyDataSetChanged();
            case 5:
                itemFragment = new ItemFragment();
                bundle = new Bundle();
                bundle.putString("category", categoryPagerAdapter.getPageTitle(5).toString());
                itemFragment.setArguments(bundle);
                categoryPagerAdapter.addFragment(itemFragment);
                categoryPagerAdapter.notifyDataSetChanged();
        }
    }

    protected void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }
}

class CategoryPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> listFragment = new ArrayList<>();

    public CategoryPagerAdapter(FragmentManager fm) {
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
                return "Digital 2D";
            case 1:
                return "Digital 3D";
            case 2:
                return "Concept Art";
            case 3:
                return "Characters";
            case 4:
                return "Illustration";
            case 5:
                return "Other";
        }
        return null;
    }
}
