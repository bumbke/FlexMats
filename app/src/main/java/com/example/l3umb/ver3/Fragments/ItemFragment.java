package com.example.l3umb.ver3.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.ItemClickListener;
import com.example.l3umb.ver3.Object.ItemHomeAdapter;
import com.example.l3umb.ver3.Object.ItemRelateAdapter;
import com.example.l3umb.ver3.Object.ItemScoreboardAdapter;
import com.example.l3umb.ver3.Object.Product;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.Statiscal;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 10/29/2018.
 */

public class ItemFragment extends Fragment {
    private SharedPreferences pref;
    private TextView tvAlert;
    private RecyclerView rvItem;
    private ItemHomeAdapter adapterVertical;
    private ItemRelateAdapter relateAdapter;
    private ItemScoreboardAdapter adapterScoreboard;
    private Fragment fragmentProductDetail, fragmentUserDetail;
    private String keyword, user_id, user_name, user_avatar_url;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_fragment,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (getArguments() != null) {
                            keyword = getArguments().getString("category");
                            user_id = getArguments().getString("user_id");
                            if (keyword.equals("Portfolio")) {
                                user_name = getArguments().getString("user_name");;
                                user_avatar_url = getArguments().getString("user_avatar_url");;
                                final GridLayoutManager grid = new GridLayoutManager(getActivity(), 3);
                                rvItem.setLayoutManager(grid);
                                rvItem.setItemAnimator(new DefaultItemAnimator());

                                rvItem.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        if(dy > 0) //check for scroll down
                                        {
                                            boolean loading = true;
                                            int visibleItemCount = grid.getChildCount();
                                            int totalItemCount = grid.getItemCount();
                                            int pastVisiblesItems = grid.findFirstVisibleItemPosition();

                                            if (loading) {
                                                if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                                                {
                                                    loading = false;

                                                    //Do pagination.. i.e. fetch new data
                                                }
                                            }
                                        }
                                    }
                                });

                                getRelateProductsProcess(user_id);
                            }  else {
                                final LinearLayoutManager vertical = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
                                rvItem.setLayoutManager(vertical);
                                rvItem.setItemAnimator(new DefaultItemAnimator());

                                rvItem.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                        if(dy > 0) //check for scroll down
                                        {
                                            boolean loading = true;
                                            int visibleItemCount = vertical.getChildCount();
                                            int totalItemCount = vertical.getItemCount();
                                            int pastVisiblesItems = vertical.findFirstVisibleItemPosition();

                                            if (loading)
                                            {
                                                if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                                                {
                                                    loading = false;
                                                    Log.v("...", "Last Item Wow !");
                                                    //Do pagination.. i.e. fetch new data
                                                }
                                            }
                                        }
                                    }
                                });

                                if (keyword.equals("user_scoreboard")) {
                                    getStatiscalProcess();
                                } else {
                                    getProductCategoryProcess();
                                }
                            }
                        } else {
                            tvAlert.setVisibility(View.VISIBLE);
                        }
                    }
                },
                500);

    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        fragmentProductDetail = new ProductDetailFragment();
        fragmentUserDetail = new UserDetailFragment();

        tvAlert = (TextView) v.findViewById(R.id.tvAlert);

        rvItem = (RecyclerView) v.findViewById(R.id.rvItem);
    }

    private void getRelateProductsProcess(final String user_id) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setUser_id(user_id);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_RELATE_PRODUCTS_OPERATION);
        request.setUser(user);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if(resp.getResult().equals(Constants.SUCCESS)) {
                    final List<Product> products = resp.getProducts();

                    if (products.size()>0) {
                        tvAlert.setVisibility(View.GONE);
                    }

                    relateAdapter = new ItemRelateAdapter(getActivity(), products, products.size(),new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Bundle bundle = new Bundle();
                            bundle.putString("product_id", products.get(position).getProduct_id());
                            bundle.putString("product_create_date", products.get(position).getCreated_date());
                            bundle.putInt("product_points", products.get(position).getPoints());
                            bundle.putInt("view_count", products.get(position).getView_count() + 1);
                            bundle.putString("user_id", user_id);
                            bundle.putString("user_name", user_name);
                            bundle.putString("user_avatar_url", user_avatar_url);
                            fragmentProductDetail.setArguments(bundle);
                            goToFragment(fragmentProductDetail, 2);
                        }
                    });
                    rvItem.setAdapter(relateAdapter);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getProductCategoryProcess() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setKeyword(keyword);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.SEARCH_PRODUCT_OPERATION);
        request.setUser(user);

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

                    if (products.size()>0) {
                        tvAlert.setVisibility(View.GONE);
                    }

                    adapterVertical = new ItemHomeAdapter(getActivity(), products, users, products.size(),new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Bundle bundle = new Bundle();
                            bundle.putString("product_id", products.get(position).getProduct_id());
                            bundle.putString("product_create_date", products.get(position).getCreated_date());
                            bundle.putInt("product_points", products.get(position).getPoints());
                            bundle.putInt("view_count", products.get(position).getView_count() + 1);
                            bundle.putString("user_id", users.get(position).getUser_id());
                            bundle.putString("user_name", users.get(position).getName());
                            bundle.putString("user_avatar_url", users.get(position).getAvatar_url());
                            fragmentProductDetail.setArguments(bundle);
                            goToFragment(fragmentProductDetail, 1);
                        }
                    });

                    rvItem.setAdapter(adapterVertical);
                    adapterVertical.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getStatiscalProcess() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Statiscal statiscal = new Statiscal();
        statiscal.setCategory(3);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CATEGORY_STATISCAL_OPERATION);
        request.setStatiscal(statiscal);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if (response.body() == null) {
                    Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG).show();
                } else {
                    final List<User> users = resp.getUsers();
                    int size = 10;

                    if (users.size()>0) {
                        tvAlert.setVisibility(View.GONE);
                        if (users.size() < 10) {
                            size = users.size();
                        }
                    }

                    adapterScoreboard = new ItemScoreboardAdapter(getActivity(), users, size,new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", users.get(position).getUser_id());
                            Log.d(Constants.TAG, users.get(position).getUser_id() + pref.getString(Constants.UNIQUE_ID, ""));
                            if (users.get(position).getUser_id().equals(pref.getString(Constants.UNIQUE_ID, ""))) {
                                bundle.putBoolean("self", true);
                            } else {
                                bundle.putBoolean("self", false);
                            }


                            fragmentUserDetail.setArguments(bundle);
                            goToFragment(fragmentUserDetail, 3);
                        }
                    });

                    rvItem.setAdapter(adapterScoreboard);
                    adapterScoreboard.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToFragment(Fragment fragment, int isPage) {
        switch (isPage) {
            case 1:
                CategoryFragment parentFragment = ((CategoryFragment) ItemFragment.this.getParentFragment());
                parentFragment.goToFragment(fragment);
                break;

            case 2:
                UserDetailFragment parentFragment1 = ((UserDetailFragment) ItemFragment.this.getParentFragment());
                parentFragment1.goToFragment(fragment);
                break;

            case 3:
                ChartFragment parentFragment2 = ((ChartFragment) ItemFragment.this.getParentFragment());
                parentFragment2.goToFragment(fragment);
                break;
        }
    }
}
