package com.example.l3umb.ver3.Fragments;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordDialogFragment extends DialogFragment {
    private TextView tvEmail;
    private EditText edOldpass, edNewpass, edConfirm;
    private SharedPreferences pref;

    public static ChangePasswordDialogFragment newInstance() {
        ChangePasswordDialogFragment dialog = new ChangePasswordDialogFragment();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        setCancelable(false);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        initLayout(v);

        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChangePasswordDialogFragment.this.getDialog().cancel();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String old_password = edOldpass.getText().toString();
                String new_password = edNewpass.getText().toString();
                String confirm_password = edConfirm.getText().toString();

                if (!old_password.isEmpty() && !new_password.isEmpty()) {
                    if (confirm_password.equals(new_password)) {
                        changePasswordProcess(pref.getString(Constants.EMAIL, ""), old_password, new_password, v);
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                },
                                1000);
                    } else {
                        Snackbar.make(v, "Password not matched!", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(v, "Fields are empty!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        return dialog;
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //init View
        tvEmail = (TextView)v.findViewById(R.id.tvEmail);
        tvEmail.setText(pref.getString(Constants.EMAIL,""));
        edOldpass = (EditText)v.findViewById(R.id.edOldpass);
        edNewpass = (EditText)v.findViewById(R.id.edNewpass);
        edConfirm = (EditText)v.findViewById(R.id.edConfirm);
    }

    private void changePasswordProcess(String email, String old_password, String new_password, final View v) {
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();

        RequestInterface requestInterface = retrofit.create(RequestInterface.class);
        User user = new User();
        user.setEmail(email);
        user.setOld_password(old_password);
        user.setNew_password(new_password);
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_PASSWORD_OPERATION);
        request.setUser(user);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp.getResult().equals(Constants.SUCCESS)) {
                    Snackbar.make(v, resp.getMessage(), Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(v, resp.getMessage(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG, "failed "+t.getMessage());
                Snackbar.make(v, t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
