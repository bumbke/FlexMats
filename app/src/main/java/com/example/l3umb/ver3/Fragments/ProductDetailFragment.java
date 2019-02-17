package com.example.l3umb.ver3.Fragments;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.Currency;
import com.example.l3umb.ver3.Object.ItemCommentAdapter;
import com.example.l3umb.ver3.Object.Comment;
import com.example.l3umb.ver3.Object.ItemClickListener;
import com.example.l3umb.ver3.Object.ItemRelateAdapter;
import com.example.l3umb.ver3.Object.Product;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 10/27/2018.
 */

public class ProductDetailFragment extends Fragment implements View.OnClickListener {
    SharedPreferences pref;
    private TextView tvTitle, tvTag, tvAuthor, tvLike, tvView, tvDownload, tvDate, tvPoint, tvDescription, tvNameAuthorRelated, tvAlert;
    private ImageView ivProduct, ivAvatar;
    private ToggleButton tbLike;
    private ImageButton imbAdd;
    private Button btnPostComment;
    private EditText edComment;
    private RelativeLayout rlUserInfo;
    private ProgressBar progressBar;

    private String product_id, title, tag, user_id, user_name, date, description, product_image_url, user_avatar_url, comment_content;
    private int like_count, view_count, download_count, points, self_point, status;

    private RecyclerView rvMoreBy, rvComment;
    private ItemRelateAdapter relateAdapter;
    private ItemCommentAdapter commentAdapter;
    private Fragment fragmentUserDetail, fragmentProductDetail;
    private Drawable unlike, liked;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_detail,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        product_id = getArguments().getString("product_id");
        date = getArguments().getString("product_create_date");
        points = getArguments().getInt("product_points");
        view_count = getArguments().getInt("view_count");
        like_count = getArguments().getInt("like_count");

        user_id = getArguments().getString("user_id");
        user_name = getArguments().getString("user_name");
        user_avatar_url = getArguments().getString("user_avatar_url");

        getProductDetailProcess(pref.getString(Constants.UNIQUE_ID, ""));
        getRelateProductsProcess();
        getProductCommentsProcess();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        setView();
                    }
                },
                1000);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlUserInfo:
                Bundle bundle = new Bundle();
                if (user_id.equals(pref.getString(Constants.UNIQUE_ID, ""))) {
                    bundle.putBoolean("self", true);
                } else {
                    bundle.putString("user_id", user_id);
                    bundle.putBoolean("self", false);
                }
                fragmentUserDetail.setArguments(bundle);
                goToFragment(fragmentUserDetail);
                break;
            case R.id.btnPostComment:
                comment_content = edComment.getText().toString();
                if (!comment_content.isEmpty() && comment_content.length() > 5) {
                    postCommentProcess(pref.getString(Constants.UNIQUE_ID, ""));
                } else {
                    Toast.makeText(getActivity(), "Comment too short!", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.tbLike:
                likeProductProcess(pref.getString(Constants.UNIQUE_ID, ""));
                break;
            case R.id.imbAdd:
                if (points > self_point) {
                    Toast.makeText(getActivity(), "Not enought point!", Toast.LENGTH_SHORT).show();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    saveImage();
                                }
                            },
                            2000);
                    break;
                }
        }
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvTag = (TextView) v.findViewById(R.id.tvTag);
        tvAuthor = (TextView) v.findViewById(R.id.tvAuthor);
        tvLike = (TextView) v.findViewById(R.id.tvLike);
        tvView = (TextView) v.findViewById(R.id.tvView);
        tvDownload = (TextView) v.findViewById(R.id.tvDownload);
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        tvPoint = (TextView) v.findViewById(R.id.tvPoint);
        tvDescription = (TextView) v.findViewById(R.id.tvDescription);
        tvNameAuthorRelated = (TextView) v.findViewById(R.id.tvNameAuthorRelated);
        tvAlert = (TextView) v.findViewById(R.id.tvAlert);

        ivProduct =(ImageView) v.findViewById(R.id.ivProduct);
        ivAvatar =(ImageView) v.findViewById(R.id.ivAvatar);

        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        edComment = (EditText) v.findViewById(R.id.edComment);

        tbLike = (ToggleButton) v.findViewById(R.id.tbLike);
        imbAdd = (ImageButton)v.findViewById(R.id.imbAdd);
        btnPostComment =(Button) v.findViewById(R.id.btnPostComment);

        tbLike.setOnClickListener(this);
        imbAdd.setOnClickListener(this);
        btnPostComment.setOnClickListener(this);

        unlike = getResources().getDrawable(R.drawable.ic_like_emty);
        liked = getResources().getDrawable(R.drawable.ic_like_full);

        rlUserInfo = (RelativeLayout) v.findViewById(R.id.rlUserInfo);
        rlUserInfo.setOnClickListener(this);

        fragmentUserDetail = new UserDetailFragment();
        fragmentProductDetail = new ProductDetailFragment();

        rvMoreBy = (RecyclerView) v.findViewById(R.id.rvMoreBy);
        rvComment = (RecyclerView) v.findViewById(R.id.rvComment);

        LinearLayoutManager horizontal = new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
        rvMoreBy.setLayoutManager(horizontal);
        rvMoreBy.setItemAnimator(new DefaultItemAnimator());

        LinearLayoutManager vertical = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rvComment.setLayoutManager(vertical);
        rvComment.setItemAnimator(new DefaultItemAnimator());
    }

    private void setView() {
        if (product_image_url != null) {
            Picasso.with(getActivity()).load(product_image_url).placeholder(R.mipmap.ic_launcher).into(ivProduct);
        }
        tvTitle.setText("" + title);
        tvTag.setText("" + tag);
        tvLike.setText("" + like_count);
        tvView.setText("" + view_count);
        tvDownload.setText("" + download_count);
        tvDate.setText("" + date);
        tvDescription.setText("" + description);
        tvPoint.setText("" + points);

        if (!user_avatar_url.isEmpty()) {
            Picasso.with(getActivity()).load(user_avatar_url).placeholder(R.mipmap.ic_launcher).into(ivAvatar);
        }
        tvAuthor.setText("" + user_name);
        tvNameAuthorRelated.setText("" + user_name);

        if (status == 1 || status == 2) {
            tbLike.setCompoundDrawablesWithIntrinsicBounds(null, liked, null, null);
        }
    }

    private void getProductDetailProcess(String user_id) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Product product = new Product();
        product.setProduct_id(product_id);
        product.setUser_id(user_id);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_PRODUCT_DETAIL_OPERATION);
        request.setProduct(product);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)) {
                    title = resp.getProduct().getTitle();
                    product_image_url = resp.getProduct().getImage_url();
                    tag = resp.getProduct().getTag();
                    download_count = resp.getProduct().getDownload_count();
                    description = resp.getProduct().getDescription();
                    status = resp.getProduct().getStatus();
                    self_point = resp.getProduct().getPoints();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"getProductDetailProcess failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getRelateProductsProcess() {
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

                    relateAdapter = new ItemRelateAdapter(getActivity(), products, products.size(),new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Bundle bundle = new Bundle();
                            Log.d(Constants.TAG,products.get(position).getProduct_id());

                            bundle.putString("product_id", products.get(position).getProduct_id());
                            bundle.putString("product_create_date", date);
                            bundle.putInt("product_points", points);
                            bundle.putInt("view_count", view_count);
                            bundle.putString("user_id", user_id);
                            bundle.putString("user_name", user_name);
                            bundle.putString("user_avatar_url", user_avatar_url);
                            fragmentProductDetail.setArguments(bundle);
                            goToFragment(fragmentProductDetail);
                        }
                    });
                    rvMoreBy.setAdapter(relateAdapter);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"getRelateProductsProcess failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getProductCommentsProcess() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Product product = new Product();
        product.setProduct_id(product_id);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_PRODUCT_COMMENTS_OPERATION);
        request.setProduct(product);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();

                if(resp.getResult().equals(Constants.SUCCESS)) {
                    final List<Comment> comments = resp.getComments();
                    final List<User> users = resp.getUsers();

                    if (comments.size()>0) {
                        tvAlert.setVisibility(View.GONE);
                    }

                    commentAdapter = new ItemCommentAdapter(getActivity(), users, comments, new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {

                        }
                    });
                    rvComment.setAdapter(commentAdapter);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"getProductCommentsProcess failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void postCommentProcess(String user_id) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Comment comment = new Comment();
        comment.setProduct_id(product_id);
        comment.setUser_id(user_id);
        comment.setContent(comment_content);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.POST_COMMENT_OPERATION);
        request.setComment(comment);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    edComment.setFocusable(false);
                    edComment.setText(null);
                    getProductCommentsProcess();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void likeProductProcess(String user_id) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Product product = new Product();
        product.setProduct_id(product_id);
        product.setUser_id(user_id);
        product.setStatus(1);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LIKE_PRODUCT_OPERATION);
        request.setProduct(product);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    if (resp.getMessage().equalsIgnoreCase("liked!")) {
                        tbLike.setCompoundDrawablesWithIntrinsicBounds(null, liked, null, null);
                        like_count += 1;
                        tvLike.setText("" + like_count);
                    } else {
                        tbLike.setCompoundDrawablesWithIntrinsicBounds(null, unlike, null, null);
                        like_count -= 1;
                        tvLike.setText("" + like_count);
                    }
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"likeProductProcess failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static String randomString(int lenght) {
        String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        for(int i=0;i<lenght;++i)
            randomStringBuilder.append(ALLOWED_CHARACTERS.charAt(generator.nextInt(ALLOWED_CHARACTERS.length())));
        return "13image" + randomStringBuilder.toString();
    }

    private void saveImage() {
        BitmapDrawable drawable = (BitmapDrawable) ivProduct.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File downloadDirectory = Environment.getExternalStorageDirectory();
        File image = new File(downloadDirectory, randomString(9) +".png");

        boolean success = false;

        // Encode the file as a PNG image.
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            /* 100 to keep full quality of the image */
            outStream.flush();
            outStream.close();
            success = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        progressBar.setVisibility(View.GONE);
        if (success) {
            Toast.makeText(getContext(), "Image Saved Successfully",
                    Toast.LENGTH_LONG).show();
            if (!user_id.equals(pref.getString(Constants.UNIQUE_ID, ""))) {
                if(status != 2) {
                    calculatePointProcess(pref.getString(Constants.UNIQUE_ID, ""));
                    download_count += 1;
                    tvDownload.setText("" + download_count);
                    status = 2;
                }
            }
        } else {
            Toast.makeText(getContext(),
                    "Error During Saving Image", Toast.LENGTH_LONG).show();
        }
    }

    private void calculatePointProcess(String from_user) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Currency currency = new Currency();
        currency.setFrom_user_id(from_user);
        currency.setTo_user_id(user_id);
        currency.setAmounts(points);
        currency.setProduct_id(product_id);
        currency.setType(2);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CALCULATE_POINT_OPERATION);
        request.setCurrency(currency);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(Constants.TAG, resp.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG, "failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
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
