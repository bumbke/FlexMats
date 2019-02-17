package com.example.l3umb.ver3.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.ItemClickListener;
import com.example.l3umb.ver3.Object.ItemHomeAdapter;
import com.example.l3umb.ver3.Object.ItemSearchUserAdapter;
import com.example.l3umb.ver3.Object.Product;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 10/28/2018.
 */

public class SearchFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences pref;
    private TextView tvAlertUser, tvAlertProduct;
    private Button btnSearch;
    private EditText edSearch;
    private String keyword;
    private RecyclerView rvSearchProduct, rvSearchUser;
    private ItemHomeAdapter adapterVertical;
    private ItemSearchUserAdapter itemSearchUserAdapter;
    private Fragment fragmentProductDetail, fragmentUserDetail;
    private boolean uhaveItem = false, phaveItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                keyword = edSearch.getText().toString();
                searchProductProcess(keyword);
                searchUserProcess(keyword);

                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                if (!uhaveItem) {
                                    tvAlertUser.setVisibility(View.VISIBLE);
                                } else {
                                    tvAlertUser.setVisibility(View.GONE);
                                }

                                if (!phaveItem) {
                                    tvAlertProduct.setVisibility(View.VISIBLE);
                                } else {
                                    tvAlertProduct.setVisibility(View.GONE);
                                }
                            }
                        },
                        500);
                break;
        }
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        tvAlertProduct = (TextView) v.findViewById(R.id.tvAlertProduct);
        tvAlertUser = (TextView) v.findViewById(R.id.tvAlertUser);

        btnSearch = (Button) v.findViewById(R.id.btnSearch);

        edSearch = (EditText) v.findViewById(R.id.edSearch);

        btnSearch.setOnClickListener(this);

        fragmentProductDetail = new ProductDetailFragment();
        fragmentUserDetail = new UserDetailFragment();

        final LinearLayoutManager vertical = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rvSearchProduct = (RecyclerView) v.findViewById(R.id.rvSearchProduct);
        rvSearchProduct.setLayoutManager(vertical);
        rvSearchProduct.setItemAnimator(new DefaultItemAnimator());

        rvSearchProduct.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {
                    boolean loading = true;
                    int visibleItemCount = vertical.getChildCount();
                    int totalItemCount = vertical.getItemCount();
                    int pastVisiblesItems = vertical.findFirstVisibleItemPosition();

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

        final LinearLayoutManager vertical1 = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rvSearchUser = (RecyclerView) v.findViewById(R.id.rvSearchUser);
        rvSearchUser.setLayoutManager(vertical1);
        rvSearchUser.setItemAnimator(new DefaultItemAnimator());

        rvSearchUser.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy > 0) //check for scroll down
                {
                    boolean loading = true;
                    int visibleItemCount = vertical.getChildCount();
                    int totalItemCount = vertical.getItemCount();
                    int pastVisiblesItems = vertical.findFirstVisibleItemPosition();

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
    }

    private void searchProductProcess(String keyword) {
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
                        phaveItem = true;
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
                            goToFragment(fragmentProductDetail);
                        }
                    });

                    adapterVertical.notifyDataSetChanged();
                    rvSearchProduct.setAdapter(adapterVertical);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchUserProcess(String keyword) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setKeyword(keyword);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.SEARCH_USER_OPERATION);
        request.setUser(user);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                final ServerResponse resp = response.body();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG).show();
                } else {
                    final List<User> users = resp.getUsers();

                    if (users.size()>0) {
                        uhaveItem = true;
                    }

                    itemSearchUserAdapter = new ItemSearchUserAdapter(getActivity(), users, users.size(),new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Bundle bundle = new Bundle();

                            if (users.get(position).getUser_id().equals(pref.getString(Constants.UNIQUE_ID, ""))) {
                                bundle.putBoolean("self", true);
                            } else {
                                bundle.putString("user_id", users.get(position).getUser_id());
                                bundle.putBoolean("self", false);
                            }

                            fragmentUserDetail.setArguments(bundle);
                            goToFragment(fragmentUserDetail);
                        }
                    });

                    itemSearchUserAdapter.notifyDataSetChanged();
                    rvSearchUser.setAdapter(itemSearchUserAdapter);
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
}
