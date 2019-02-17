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
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.Mail;
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

public class SendMessageDialogFragment extends DialogFragment {
    private SharedPreferences pref;
    private Button btnCancel, btnPost;
    private EditText edTitle, edMessage;
    private String title, content, from_user, to_user;

    public static SendMessageDialogFragment newInstance() {
        SendMessageDialogFragment dialog = new SendMessageDialogFragment();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        setCancelable(false);

        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_send_message, null);
        initLayout(v);

        builder.setView(v)
                // Add action buttons
                .setPositiveButton(R.string.dialog_send, new DialogInterface.OnClickListener() {
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
        btnCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnPost = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = edTitle.getText().toString();
                content = edMessage.getText().toString();

                if (title.isEmpty()) {
                    Toast.makeText(getActivity(), "Title is empty!", Toast.LENGTH_SHORT).show();
                    edTitle.setFocusable(true);
                } else if (content.isEmpty()) {
                    Toast.makeText(getActivity(), "Message is empty!", Toast.LENGTH_SHORT).show();
                    edMessage.setFocusable(true);
                } else {
                    from_user = pref.getString(Constants.UNIQUE_ID, "");
                    to_user = getArguments().getString("user_id");
                    sendMessageProcess();
                }
            }
        });
        return dialog;
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        edTitle = (EditText) v.findViewById(R.id.edTitle);
        edMessage = (EditText) v.findViewById(R.id.edMessage);
    }

    private void sendMessageProcess() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Mail mail = new Mail();
        mail.setFrom_user_id(from_user);
        mail.setTo_user_id(to_user);
        mail.setContent(content);
        mail.setTitle(title);
        mail.setMail_type(1);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.SEND_MAIL_OPERATION);
        request.setMail(mail);

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
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
