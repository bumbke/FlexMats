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

public class RegisterFragment extends Fragment implements View.OnClickListener {
    private TextView tvLogin, tvTimer;
    private EditText edName, edEmail, edPassword, edRepeat, edCode;
    private Button btnRegister, btnAbort;
    private Fragment fragmentLogin;
    private ProgressBar progress;
    private CountDownTimer countDownTimer;
    private String email;
    private boolean isRegisterInitiated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvLogin:
                goToFragment(fragmentLogin);
                break;
            case R.id.btnRegister:
                if (!isRegisterInitiated) {
                    email = edEmail.getText().toString();
                    if (!email.isEmpty()) {
                        progress.setVisibility(View.VISIBLE);
                        initiateRegistrationProcess(email);
                    } else {
                        Snackbar.make(getView(), "Fields are empty!", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    String name = edName.getText().toString();
                    String password = edPassword.getText().toString();
                    String repeat = edRepeat.getText().toString();
                    String code = edCode.getText().toString();

                    if (!code.isEmpty() && !name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
                        if (repeat.equals(password)) {
                            finishRegistrationProcess(name, email, password, code);
                        } else {
                            Snackbar.make(getView(), "Password not matched!", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(getView(), "Fields are empty !", Snackbar.LENGTH_LONG).show();
                    }
                } break;
            case R.id.btnAbort:
                isRegisterInitiated = false;
                countDownTimer.cancel();
                edEmail.setVisibility(View.VISIBLE);
                edPassword.setVisibility(View.VISIBLE);
                edRepeat.setVisibility(View.VISIBLE);
                edName.setVisibility(View.VISIBLE);
                edCode.setVisibility(View.GONE);
                tvTimer.setVisibility(View.GONE);
                btnAbort.setVisibility(View.GONE);
                btnRegister.setText(R.string.register_register);

                Snackbar.make(getView(), "Registration abort!", Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void initLayout(View v) {
        //init View
        tvTimer = (TextView) v.findViewById(R.id.tvTimer);
        tvLogin = (TextView)v.findViewById(R.id.tvLogin);
        edName = (EditText)v.findViewById(R.id.edName);
        edEmail = (EditText)v.findViewById(R.id.edEmail);
        edPassword = (EditText)v.findViewById(R.id.edPassword);
        edRepeat = (EditText)v.findViewById(R.id.edRepeat);
        edCode = (EditText)v.findViewById(R.id.edCode);
        btnRegister = (Button)v.findViewById(R.id.btnRegister);
        btnAbort = (Button)v.findViewById(R.id.btnAbort);
        progress = (ProgressBar)v.findViewById(R.id.progress);

        edCode.setVisibility(View.GONE);
        btnAbort.setVisibility(View.GONE);
        //set listener
        tvLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnAbort.setOnClickListener(this);

        //init Fragment
        fragmentLogin = new LoginFragment();
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }

    private void initiateRegistrationProcess(String email) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setEmail(email);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REGISTRATION_INITIATE);
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
                    edPassword.setVisibility(View.GONE);
                    edRepeat.setVisibility(View.GONE);
                    edName.setVisibility(View.GONE);
                    edCode.setVisibility(View.VISIBLE);
                    tvTimer.setVisibility(View.VISIBLE);
                    btnAbort.setVisibility(View.VISIBLE);
                    btnRegister.setText(R.string.dialog_change_pass_confirm);
                    isRegisterInitiated = true;
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

    private void finishRegistrationProcess(String name, String email, String password, String code) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setCode(code);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.REGISTRATION_FINISH);
        request.setUser(user);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (response.body() == null) {
                    Snackbar.make(getView(), "null", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(getView(), resp.getMessage(), Snackbar.LENGTH_LONG).show();
                    goToFragment(fragmentLogin);
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
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
                Snackbar.make(getView(), "Time Out! Request again to registration.", Snackbar.LENGTH_LONG).show();
                goToFragment(fragmentLogin);
            }
        }.start();
    }
}
