package com.example.l3umb.ver3.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.MainActivity;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment implements View.OnClickListener {
    private TextView tvRegister, tvForgot;
    private EditText edEmail, edPassword;
    private Button btnLogin;
    private Fragment fragmentRegister, fragmentForgot;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);
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
            case R.id.tvRegister:
                goToFragment(fragmentRegister);
                break;

            case R.id.btnLogin:
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()) {
                    loginProcess(email, password);
                } else {
                    Snackbar.make(getView(), "Fields are empty!", Snackbar.LENGTH_LONG).show();
                } break;

            case R.id.tvForgot:
                goToFragment(fragmentForgot);
                break;
        }
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //init View
        tvRegister = (TextView)v.findViewById(R.id.tvRegister);
        tvForgot = (TextView)v.findViewById(R.id.tvForgot);
        edEmail = (EditText)v.findViewById(R.id.edEmail);
        edPassword = (EditText)v.findViewById(R.id.edPassword);
        btnLogin = (Button)v.findViewById(R.id.btnLogin);

        //set listener
        tvRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        tvForgot.setOnClickListener(this);

        //init Fragment
        fragmentRegister = new RegisterFragment();
        fragmentForgot = new ForgotPasswordFragment();
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }

    private void goToActivity(Activity activity) {
        Intent intent = new Intent(this.getActivity(), activity.getClass());
        startActivity(intent);
    }

    private void loginProcess(String email, String password){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.LOGIN_OPERATION);
        request.setUser(user);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Snackbar.make(getView(),resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putBoolean(Constants.IS_LOGGED_IN,true);
                    editor.putString(Constants.EMAIL,resp.getUser().getEmail());
                    editor.putString(Constants.NAME, resp.getUser().getName());
                    editor.putString(Constants.UNIQUE_ID,resp.getUser().getUser_id());
                    editor.putString(Constants.AVATAR, resp.getUser().getAvatar_url());
                    editor.putString(Constants.SELECTED_THEME, "lilac");
                    editor.apply();
                    goToActivity(new MainActivity());
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Snackbar.make(getView(), t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
