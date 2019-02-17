package com.example.l3umb.ver3.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.ItemClickListener;
import com.example.l3umb.ver3.Object.ItemNewsAdapter;
import com.example.l3umb.ver3.Object.News;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 11/2/2018.
 */

public class NewsFragment extends Fragment implements View.OnClickListener{
    private TextView tvAlert, tvHeader;
    private ImageView ivBack;
    private HomeFragment fragmentHome;
    private RecyclerView rvItem;
    private ItemNewsAdapter itemNewsAdapter;
    private int news_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mailbox,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        news_type = getArguments().getInt("type");
        switch (news_type) {
            case 1:
                tvHeader.setText(R.string.events_title);
                break;
            case 2:
                tvHeader.setText(R.string.news_title);
                break;
        }
        getNewsProcess();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                goToFragment(fragmentHome);
        }
    }

    private void initLayout(View v) {
        tvAlert = (TextView) v.findViewById(R.id.tvAlert);
        tvHeader = (TextView) v.findViewById(R.id.tvHeader);

        ivBack = (ImageView) v.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);

        fragmentHome = new HomeFragment();

        rvItem = (RecyclerView) v.findViewById(R.id.rvItem);

        LinearLayoutManager vertical = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rvItem.setLayoutManager(vertical);
        rvItem.setItemAnimator(new DefaultItemAnimator());
    }

    private void getNewsProcess() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        final News news = new News();
        news.setType(news_type);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_NEWS_OPERATION);
        request.setNews(news);

        Log.d(Constants.TAG,"?");
        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                final ServerResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)) {
                    final List<News> newsList = resp.getNewsList();

                    if (newsList.size()>0) {
                        tvAlert.setVisibility(View.GONE);
                    }

                    itemNewsAdapter = new ItemNewsAdapter(getActivity(), newsList, newsList.size(), new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Bundle bundle = new Bundle();
                            bundle.putString("news_id", newsList.get(position).getUnique_id());
                            bundle.putString("title", newsList.get(position).getTitle());
                            bundle.putString("content", newsList.get(position).getContent());
                            bundle.putString("image_url", newsList.get(position).getImage_url());
                            bundle.putString("created_date", newsList.get(position).getCreated_date());
                            bundle.putString("expired_date", newsList.get(position).getExpired_date());
                            bundle.putInt("type", newsList.get(position).getType());
                            bundle.putString("view_count", newsList.get(position).getView_count());

                            FragmentManager fm = getFragmentManager();
                            NewsDetailDialogFragment newsDetailDialogFragment = NewsDetailDialogFragment.newInstance();
                            newsDetailDialogFragment.setArguments(bundle);
                            newsDetailDialogFragment.show(fm, null);
                        }
                    });
                    rvItem.setAdapter(itemNewsAdapter);
                } else {
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }
}
