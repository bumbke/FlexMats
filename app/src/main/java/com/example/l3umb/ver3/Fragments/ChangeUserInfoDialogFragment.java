package com.example.l3umb.ver3.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 11/1/2018.
 */

public class ChangeUserInfoDialogFragment extends DialogFragment implements View.OnClickListener{
    private SharedPreferences pref;
    private EditText edName, edDoB, edJobs, edBio;
    private Spinner spnGender;
    private ImageButton btnPickDate;
    private String user_id, user_name, user_dob, user_jobs, user_gender, user_summary;
    private String gender_data[] = {"Male", "Female"};

    public static ChangeUserInfoDialogFragment newInstance() {
        ChangeUserInfoDialogFragment dialog = new ChangeUserInfoDialogFragment();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        setCancelable(false);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_information, null);
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
                        dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();

        onViewCreated(v, savedInstanceState);
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = pref.getString(Constants.UNIQUE_ID, "");
                user_name = edName.getText().toString();
                user_summary = edBio.getText().toString();
                user_jobs = edJobs.getText().toString();
                if(!user_name.isEmpty() && !user_summary.isEmpty() && !user_jobs.isEmpty()) {
                    if (spnGender.getSelectedItemPosition() == 0) {
                        changeUserInfoProcess(1);
                    } else {

                        changeUserInfoProcess(2);
                    }
                }
            }
        });
        return dialog;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        getUserDetailProcess(pref.getString(Constants.UNIQUE_ID, ""));

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
            case R.id.btnPickDate:
                datePicker();
                break;
        }
    }

    private void setView() {
        edName.setText(user_name);
        edBio.setText(user_summary);
        edJobs.setText(user_jobs);
        edDoB.setText(user_dob);

        if (user_gender.equals("Male")) {
            spnGender.setSelection(0);
        } else {
            spnGender.setSelection(1);
        }
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //init View
        edBio = (EditText) v.findViewById(R.id.edBio);
        edJobs = (EditText) v.findViewById(R.id.edJobs);
        edDoB = (EditText) v.findViewById(R.id.edDoB);
        edName = (EditText) v.findViewById(R.id.edName);

        btnPickDate = (ImageButton) v.findViewById(R.id.btnPickDate);
        btnPickDate.setOnClickListener(this);

        spnGender = (Spinner) v.findViewById(R.id.spinnerGender);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, gender_data);
        spnGender.setAdapter(spinnerAdapter);
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
                    user_name = resp.getUser().getName();
                    user_summary = resp.getUser().getBio();
                    user_dob = resp.getUser().getDob();
                    user_gender = resp.getUser().getGender();
                    user_jobs = resp.getUser().getJobs();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void changeUserInfoProcess(int gender) {
        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setUser_id(user_id);
        user.setName(user_name);
        user.setDoB(user_dob);
        user.setGender(gender);
        user.setJobs(user_jobs);
        user.setBio(user_summary);

        Log.d(Constants.TAG, "ec ec  " + user_name + user_dob + gender + user_jobs + user_summary);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_INFO_OPERATION);
        request.setUser(user);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp.getResult().equals(Constants.SUCCESS)) {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    dismiss();
                } else {
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG, "failed "+t.getMessage());
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void datePicker() {
        java.util.Calendar calendar =  java.util.Calendar.getInstance();
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(calendar.MONTH);
        int day = calendar.get(calendar.DAY_OF_MONTH);
        DatePickerDialog datedialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                edDoB.setText(i2 + " - " + (i1 + 1) + " - " + i);
                user_dob = i + "-" + (i1 + 1) + "-" + i2;
            }
        }, year,month,day);
        datedialog.show();
    }
}
