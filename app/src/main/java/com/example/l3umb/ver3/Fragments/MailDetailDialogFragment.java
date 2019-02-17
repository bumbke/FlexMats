package com.example.l3umb.ver3.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class MailDetailDialogFragment extends DialogFragment implements View.OnClickListener {
    private TextView tvTitle, tvName, tvDate, tvContent, tvReply;
    private ImageView ivAvt;
    private RelativeLayout rlInfo;
    private Fragment fragmentUserDetail;
    private String user_id;
    private int mail_type, type;

    public static MailDetailDialogFragment newInstance() {
        MailDetailDialogFragment dialog = new MailDetailDialogFragment();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        setCancelable(false);

        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_mail_detail, null);
        initLayout(v);

        user_id = getArguments().getString("user_id");
        mail_type = getArguments().getInt("mail_type");

        Picasso.with(getActivity()).load(getArguments().getString("user_avatar","")).placeholder(R.mipmap.ic_launcher).into(ivAvt);
        tvTitle.setText(getArguments().getString("title", ""));
        tvDate.setText(getArguments().getString("date",""));
        tvName.setText(getArguments().getString("user_name",""));
        tvContent.setText(getArguments().getString("content",""));

        if (mail_type == 0) {
            tvReply.setVisibility(View.GONE);
        }
        builder.setView(v)
                // Add action buttons
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dismiss();
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.tvReply:
                bundle = new Bundle();
                bundle.putString("user_id", user_id);

                FragmentManager fm = getFragmentManager();
                SendMessageDialogFragment sendMessageDialogFragment = SendMessageDialogFragment.newInstance();
                sendMessageDialogFragment.setArguments(bundle);
                sendMessageDialogFragment.show(fm, null);
                break;
            case R.id.rlInfo:
                bundle = new Bundle();
                bundle.putString("user_id", user_id);
                bundle.putBoolean("self", false);

                fragmentUserDetail.setArguments(bundle);
                goToFragment(fragmentUserDetail);
                dismiss();
                break;
        }
    }

    private void initLayout(View v) {
        rlInfo = (RelativeLayout) v.findViewById(R.id.rlInfo);

        ivAvt = (ImageView) v.findViewById(R.id.ivAvt);

        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        tvName = (TextView) v.findViewById(R.id.tvName);
        tvContent = (TextView) v.findViewById(R.id.tvContent);
        tvReply = (TextView) v.findViewById(R.id.tvReply);

        tvReply.setOnClickListener(this);
        rlInfo.setOnClickListener(this);

        fragmentUserDetail = new UserDetailFragment();
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }
}
