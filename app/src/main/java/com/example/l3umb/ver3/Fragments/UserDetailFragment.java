package com.example.l3umb.ver3.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 10/29/2018.
 */

public class UserDetailFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences pref;
    private ViewPager vpUserItem ;
    private TabLayout tlUserItem ;
    private UserDetailPagerAdapter userDetailPagerAdapter;
    private ImageView ivAvt;
    private ImageButton imbSendMessage, imbSendPoint;
    private TextView tvLike, tvViews, tvName, tvEmail, tvLastActivity, lblAction, tvPoint;
    private String user_avatar_url, user_name, user_last_activity, user_id, user_bio,
            user_dob, user_gender, user_jobs, user_interest, user_email;
    private int like_count, view_count, user_points;
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_detail,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        boolean self = getArguments().getBoolean("self", true);
        if (self) {
            user_id = pref.getString(Constants.UNIQUE_ID, "");
            user_email = pref.getString(Constants.EMAIL, "");
            imbSendMessage.setVisibility(View.GONE);
            imbSendPoint.setVisibility(View.GONE);
            lblAction.setText(R.string.user_detail_point);
            getUserDetailProcess(user_id);
        } else {
            tvPoint.setVisibility(View.GONE);
            user_id = getArguments().getString("user_id");
            getUserDetailProcess(user_id);
        }
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        setView();
                        initAdapter();
                    }
                },
                1000);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.imbSendMessage:
                bundle = new Bundle();
                bundle.putString("user_id", user_id);

                FragmentManager fm = getFragmentManager();
                SendMessageDialogFragment sendMessageDialogFragment = SendMessageDialogFragment.newInstance();
                sendMessageDialogFragment.setArguments(bundle);
                sendMessageDialogFragment.show(fm, null);
                break;
            case R.id.imbSendPoint:
                bundle = new Bundle();
                bundle.putString("user_id", user_id);
                bundle.putBoolean("isInDetailPage", true);

                FragmentManager fm1 = getFragmentManager();
                ExchangePointDialogFragment exchangePointDialogFragment = ExchangePointDialogFragment.newInstance();
                exchangePointDialogFragment.setArguments(bundle);
                exchangePointDialogFragment.show(fm1, null);
                break;
        }
    }

    private void initAdapter() {
        Bundle bundle;
        switch (page) {
            case 0:
                ItemFragment itemFragment = new ItemFragment();
                bundle = new Bundle();
                bundle.putString("category", userDetailPagerAdapter.getPageTitle(0).toString());
                bundle.putString("user_id", user_id);
                bundle.putString("user_name", user_name);
                bundle.putString("user_avatar_url", user_avatar_url);
                itemFragment.setArguments(bundle);
                userDetailPagerAdapter.addFragment(itemFragment);
                userDetailPagerAdapter.notifyDataSetChanged();
            case 1:
                UserDetailItemFragment userDetailItemFragment =  new UserDetailItemFragment();
                bundle = new Bundle();
                bundle.putString("user_bio", user_bio);
                bundle.putString("user_dob", user_dob);
                bundle.putString("user_gender", user_gender);
                bundle.putString("user_jobs", user_jobs);
                userDetailItemFragment.setArguments(bundle);
                userDetailPagerAdapter.addFragment(userDetailItemFragment);
                userDetailPagerAdapter.notifyDataSetChanged();
        }
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        vpUserItem = (ViewPager) v.findViewById(R.id.vpUserItem);
        tlUserItem  = (TabLayout) v.findViewById(R.id.tlUserItem);
        ivAvt = (ImageView) v.findViewById(R.id.ivAvt);
        tvLike = (TextView) v.findViewById(R.id.tvLike);
        tvViews = (TextView) v.findViewById(R.id.tvViews);
        tvName = (TextView) v.findViewById(R.id.tvName);
        tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        tvLastActivity = (TextView) v.findViewById(R.id.tvLastActivity);
        tvPoint = (TextView) v.findViewById(R.id.tvPoint);
        lblAction = (TextView) v.findViewById(R.id.lblAction);

        imbSendMessage = (ImageButton) v.findViewById(R.id.imbSendMessage);
        imbSendPoint = (ImageButton) v.findViewById(R.id.imbSendPoint);
        imbSendMessage.setOnClickListener(this);
        imbSendPoint.setOnClickListener(this);

        userDetailPagerAdapter = new UserDetailPagerAdapter(getChildFragmentManager());

        vpUserItem.setAdapter(userDetailPagerAdapter);
        vpUserItem.setOffscreenPageLimit(2);

        tlUserItem.setTabGravity(TabLayout.GRAVITY_FILL);
        tlUserItem.setupWithViewPager(vpUserItem);
        tlUserItem.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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

    private void setView() {
        if (user_avatar_url != null) {
            Picasso.with(getActivity()).load(user_avatar_url).placeholder(R.mipmap.ic_launcher).into(ivAvt);
        }
        tvName.setText(user_name);
        tvEmail.setText(user_email);
        tvLastActivity.setText(user_last_activity);
        tvLike.setText(like_count+"");
        tvViews.setText(view_count+"");
        tvPoint.setText(user_points+"");
    }

    private void getUserDetailProcess(String user_id) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setUser_id(user_id);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_USER_DETAIL_OPERATION);
        request.setUser(user);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)) {
                    user_avatar_url = resp.getUser().getAvatar_url();
                    user_name = resp.getUser().getName();
                    user_email = resp.getUser().getEmail();
                    user_last_activity = resp.getUser().getLast_activity();
                    user_bio = resp.getUser().getBio();
                    user_dob = resp.getUser().getDob();
                    user_gender = resp.getUser().getGender();
                    user_jobs = resp.getUser().getJobs();
                    like_count = resp.getUser().getLike_count();
                    view_count = resp.getUser().getView_count();
                    user_points = resp.getUser().getPoints();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }
}

class UserDetailPagerAdapter extends FragmentStatePagerAdapter {
    private final List<Fragment> listFragment = new ArrayList<>();

    public UserDetailPagerAdapter(FragmentManager fm) {
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

    public void addFragment(Fragment fragment) {
        listFragment.add(fragment);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Portfolio";
            case 1:
                return "About";
        }
        return null;
    }
}
