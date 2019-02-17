package com.example.l3umb.ver3.Fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.l3umb.ver3.R;
import com.squareup.picasso.Picasso;

import java.util.Objects;

/**
 * Created by Dev on 11/3/2018.
 */

public class NewsDetailDialogFragment extends DialogFragment implements View.OnClickListener {
    private TextView tvTitle, tvDate, tvExpiredDate, tvContent;
    private ImageView ivImage, ivBack;
    private String news_id;
    private int type;

    public static NewsDetailDialogFragment newInstance() {
        NewsDetailDialogFragment dialog = new NewsDetailDialogFragment();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        setCancelable(false);

        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_news_detail, null);
        initLayout(v);

        news_id = getArguments().getString("news_id");
        type = getArguments().getInt("type");

        Picasso.with(getActivity()).load(getArguments().getString("image_url","")).placeholder(R.mipmap.ic_launcher).into(ivImage);
        tvTitle.setText(getArguments().getString("title", ""));
        tvDate.setText(getResources().getString(R.string.news_created_date) + getArguments().getString("created_date",""));
        tvContent.setText(getArguments().getString("content",""));

        if (type == 2) {
            tvExpiredDate.setVisibility(View.GONE);
        } else {
            tvExpiredDate.setText(getResources().getString(R.string.news_expired_date) + getArguments().getString("expired_date",""));
        }

        builder.setView(v);

        final AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                dismiss();
        }
    }

    private void initLayout(View v) {
        ivImage = (ImageView) v.findViewById(R.id.ivImage);
        ivBack = (ImageView) v.findViewById(R.id.ivBack);

        tvTitle = (TextView) v.findViewById(R.id.tvTitle);
        tvDate = (TextView) v.findViewById(R.id.tvDate);
        tvExpiredDate = (TextView) v.findViewById(R.id.tvExpiredDate);
        tvContent = (TextView) v.findViewById(R.id.tvContent);

        ivBack.setOnClickListener(this);
    }
}
