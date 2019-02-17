package com.example.l3umb.ver3.Fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {
    private TextView tvTimer;
    private Button btnBack, btnReset;
    private EditText edEmail, edCode, edPassword;
    String email;
    private ProgressBar progress;
    private Fragment fragmentLogin;
    private CountDownTimer countDownTimer;
    private boolean isResetInitiated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forgotpassword,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBack:
                goToFragment(fragmentLogin);
                break;
            case R.id.btnReset:
                if(!isResetInitiated) {
                email = edEmail.getText().toString();

                    if (!email.isEmpty()) {
                        progress.setVisibility(View.VISIBLE);
                        initiateResetPasswordProcess(email);
                    } else {
                        Snackbar.make(getView(), "Fields are empty!", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    String code = edCode.getText().toString();
                    String password = edPassword.getText().toString();

                    if(!code.isEmpty() && !password.isEmpty()){
                        finishResetPasswordProcess(email, code, password);
                    } else {
                        Snackbar.make(getView(), "Fields are empty!", Snackbar.LENGTH_LONG).show();
                    }
                } break;
        }
    }

    private void initLayout(View v) {
        //init View
        tvTimer = (TextView) v.findViewById(R.id.tvTimer);
        edEmail = (EditText)v.findViewById(R.id.edEmail);
        edCode = (EditText)v.findViewById(R.id.edCode);
        edPassword = (EditText)v.findViewById(R.id.edPassword);
        btnBack = (Button)v.findViewById(R.id.btnBack);
        btnReset = (Button)v.findViewById(R.id.btnReset);
        progress = (ProgressBar)v.findViewById(R.id.progress);

        edPassword.setVisibility(View.GONE);
        edCode.setVisibility(View.GONE);
        //set listener
        btnBack.setOnClickListener(this);
        btnReset.setOnClickListener(this);

        //init Fragment
        fragmentLogin = new LoginFragment();
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }

    private void initiateResetPasswordProcess(String email) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET_PASSWORD_INITIATE);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if (resp.getResult().equals(Constants.SUCCESS)) {
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    edEmail.setVisibility(View.GONE);
                    edCode.setVisibility(View.VISIBLE);
                    edPassword.setVisibility(View.VISIBLE);
                    tvTimer.setVisibility(View.VISIBLE);
                    btnReset.setText("Change Password");
                    isResetInitiated = true;
                    startCountdownTimer();
                } else {
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG, "failed");
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void finishResetPasswordProcess(String email, String code, String password){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        user.setCode(code);
        user.setPassword(password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.RESET_PASSWORD_FINISH);
        request.setUser(user);
        Call<ServerResponse> response = requestInterface.operation(request);

        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();

                if(resp.getResult().equals(Constants.SUCCESS)){
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    countDownTimer.cancel();
                    isResetInitiated = false;
                    goToFragment(fragmentLogin);
                } else {
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                }
                progress.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progress.setVisibility(View.INVISIBLE);
                Log.d(Constants.TAG,"failed");
                Snackbar.make(getView(), t.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void startCountdownTimer(){
        countDownTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                tvTimer.setText("Time remaining : " + millisUntilFinished / 1000);
            }
            public void onFinish() {
                Snackbar.make(getView(), "Time Out ! Request again to reset password.", Snackbar.LENGTH_LONG).show();
                goToFragment(fragmentLogin);
            }
        }.start();
    }
}
