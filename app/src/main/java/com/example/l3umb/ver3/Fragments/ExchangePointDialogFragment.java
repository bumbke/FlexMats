package com.example.l3umb.ver3.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.Currency;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.R;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 10/30/2018.
 */

public class ExchangePointDialogFragment extends DialogFragment {
    private SharedPreferences pref;
    private Button btnCancel, btnPost;
    private EditText edPoint, edMessage, edRecipient;
    private TextView tvRecipient;
    private String content, from_user, to_user;
    private int points;

    public static ExchangePointDialogFragment newInstance() {
        ExchangePointDialogFragment dialog = new ExchangePointDialogFragment();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        setCancelable(false);

        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_exchange_point, null);
        initLayout(v);

        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.dialog_post, new DialogInterface.OnClickListener() {
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
        if(getArguments().getBoolean("isInDetailPage")) {
            edRecipient.setVisibility(View.GONE);
            tvRecipient.setVisibility(View.GONE);
        }

        btnCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnPost = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content = edMessage.getText().toString();
                points = Integer.parseInt(edPoint.getText().toString());
                from_user = pref.getString(Constants.UNIQUE_ID, "");

                if(edPoint.equals("")){
                    Toast.makeText(getActivity(), "Invalid input!", Toast.LENGTH_SHORT).show();
                } else if (content.isEmpty()){
                    Toast.makeText(getActivity(), "Message is empty!", Toast.LENGTH_SHORT).show();
                    edMessage.setFocusable(true);
                } else {
                    if (getArguments().getBoolean("isInDetailPage")) {
                        to_user = getArguments().getString("user_id");
                        sendPointProcess();
                    } else if(edRecipient.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "Recipient is empty!", Toast.LENGTH_SHORT).show();
                        edRecipient.setFocusable(true);
                    } else {
                        to_user = edRecipient.getText().toString();
                        sendPointProcess();
                    }
                }
            }
        });
        return dialog;
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        edRecipient = (EditText) v.findViewById(R.id.edRecipient);
        edPoint = (EditText) v.findViewById(R.id.edPoint);
        edMessage = (EditText) v.findViewById(R.id.edMessage);

        tvRecipient = (TextView) v.findViewById(R.id.tvRecipient);
    }

    private void sendPointProcess() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Currency currency = new Currency();
        currency.setFrom_user_id(from_user);
        currency.setTo_user_id(to_user);
        currency.setAmounts(points);
        currency.setContent(content);
        currency.setType(1);

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
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG, "failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
