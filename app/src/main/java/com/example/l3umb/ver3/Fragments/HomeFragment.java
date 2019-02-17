package com.example.l3umb.ver3.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.MainActivity;
import com.example.l3umb.ver3.Object.Banner;
import com.example.l3umb.ver3.Object.ItemHomeAdapter;
import com.example.l3umb.ver3.Object.ItemClickListener;
import com.example.l3umb.ver3.Object.Product;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;
import com.example.l3umb.ver3.SlideViewPager;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 10/27/2018.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {
    private Button btnNews, btnEvents, btnChart;
    private ItemHomeAdapter adapterVertical;
    private RecyclerView rvTrending, rvLatest;
    private Fragment fragmentProductDetail, fragmentChart, fragmentNews;
    private ViewPager vpSlide;
    private int slideCount = 4;
    private Timer timer;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        getBannerProcess();
                        getProductsProcess();
                    }
                },
                500);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.btnChart:
                goToFragment(fragmentChart);
                break;
            case R.id.btnNews:
                bundle = new Bundle();
                bundle.putInt("type", 2);
                fragmentNews.setArguments(bundle);
                goToFragment(fragmentNews);
                break;
            case R.id.btnEvents:
                bundle = new Bundle();
                bundle.putInt("type", 1);
                fragmentNews.setArguments(bundle);
                goToFragment(fragmentNews);
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        timer.cancel();
    }

    private void initLayout(View v) {
        timer = new Timer();
        fragmentProductDetail = new ProductDetailFragment();
        fragmentChart = new ChartFragment();
        fragmentNews = new NewsFragment();

        btnChart = (Button) v.findViewById(R.id.btnChart);
        btnEvents = (Button) v.findViewById(R.id.btnEvents);
        btnNews = (Button) v.findViewById(R.id.btnNews);

        btnChart.setOnClickListener(this);
        btnEvents.setOnClickListener(this);
        btnNews.setOnClickListener(this);

        rvTrending = (RecyclerView)v.findViewById(R.id.rvTrending);
//        rvLatest = (RecyclerView)v.findViewById(R.id.rvLatest);

        LinearLayoutManager vertical = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rvTrending.setLayoutManager(vertical);
        rvTrending.setItemAnimator(new DefaultItemAnimator());
        rvTrending.setNestedScrollingEnabled(false);
//        rvLatest.setLayoutManager(vertical);
//        rvLatest.setItemAnimator(new DefaultItemAnimator());

        //Slide Adapter

        vpSlide = (ViewPager) v.findViewById(R.id.vpSlide);

    }

    private void getBannerProcess() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_BANNER_OPERATION);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                final ServerResponse resp = response.body();

                if (response.body() == null) {
                    Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG).show();
                } else {
                    final List<Banner> banners = resp.getBanners();
                    if (banners.size() < 5) {
                        slideCount = banners.size();
                    }
                    SlideViewPager adapter = new SlideViewPager(getActivity(), banners, slideCount);
                    vpSlide.setAdapter(adapter);
                    //Carousel timer
                    timer.scheduleAtFixedRate(new MytimerTask(),500,5000);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getProductsProcess() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_TRENDING_PRODUCTS_OPERATION);
        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                final ServerResponse resp = response.body();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG).show();
                } else {
                    final List<Product> products = resp.getProducts();
                    final List<User> users = resp.getUsers();
                    int size = 5;
                    if (products.size() < 5) {
                        size = products.size();
                    }

                    adapterVertical = new ItemHomeAdapter(getActivity(), products, users, size, new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Bundle bundle = new Bundle();
                            bundle.putString("product_id", products.get(position).getProduct_id());
                            bundle.putString("product_create_date", products.get(position).getCreated_date());
                            bundle.putInt("product_points", products.get(position).getPoints());
                            bundle.putInt("view_count", products.get(position).getView_count() + 1);
                            bundle.putInt("like_count", products.get(position).getLike_count());
                            bundle.putString("user_id", users.get(position).getUser_id());
                            bundle.putString("user_name", users.get(position).getName());
                            bundle.putString("user_avatar_url", users.get(position).getAvatar_url());
                            fragmentProductDetail.setArguments(bundle);
                            goToFragment(fragmentProductDetail);
                        }
                    });

                    rvTrending.setAdapter(adapterVertical);
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
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }

    public class MytimerTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(vpSlide.getCurrentItem()==0) {
                        vpSlide.setCurrentItem(1, true);
                    } else if(vpSlide.getCurrentItem()==1 && vpSlide.getCurrentItem() < slideCount-1){
                        vpSlide.setCurrentItem(2, true);
                    } else  if(vpSlide.getCurrentItem()==2 && vpSlide.getCurrentItem() < slideCount-1){
                        vpSlide.setCurrentItem(3, true);
                    } else  if(vpSlide.getCurrentItem()==3 && vpSlide.getCurrentItem() < slideCount-1){
                        vpSlide.setCurrentItem(4, true);
                    } else {
                        vpSlide.setCurrentItem(0, true);
                    }
                }
            });
        }
    }
}
