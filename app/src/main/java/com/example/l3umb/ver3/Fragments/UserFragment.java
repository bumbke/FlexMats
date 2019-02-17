package com.example.l3umb.ver3.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.l3umb.ver3.DashboardActivity;
import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Dev on 10/28/2018.
 */

public class UserFragment extends Fragment implements View.OnClickListener {
    private ImageView ivAvt;
    private TextView tvName, tvEmail;
    private Button btnLogout, btnSetting, btnMailbox, btnExchange, btnTransactionHistory, btnUserPolicy;
    private Fragment fragmentSetting, fragmentMailbox;
    private SharedPreferences pref;
    private Dialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Picasso.with(getActivity()).load(pref.getString(Constants.AVATAR, "")).placeholder(R.mipmap.ic_launcher).resizeDimen(R.dimen.imageHeightSmall, R.dimen.imageHeightSmall).into(ivAvt);
        tvName.setText(getResources().getString(R.string.user_login_account) + " " + pref.getString(Constants.UNIQUE_ID, ""));
        tvEmail.setText(getResources().getString(R.string.user_mail) + " " + pref.getString(Constants.EMAIL, ""));
    }

    @Override
    public void onClick(View v) {
        Bundle bundle;
        switch (v.getId()) {
            case R.id.btnExchange:
                bundle = new Bundle();
                bundle.putBoolean("isInDetailPage", false);

                FragmentManager fm = getFragmentManager();
                ExchangePointDialogFragment exchangePointDialogFragment = ExchangePointDialogFragment.newInstance();
                exchangePointDialogFragment.setArguments(bundle);
                exchangePointDialogFragment.show(fm, null);
                break;

            case R.id.btnMailbox:
                bundle = new Bundle();
                bundle.putInt("mail_type", 1);
                fragmentMailbox.setArguments(bundle);
                goToFragment(fragmentMailbox);
                break;

            case R.id.btnTransactionHistory:
                bundle = new Bundle();
                bundle.putInt("mail_type", 2);
                fragmentMailbox.setArguments(bundle);
                goToFragment(fragmentMailbox);
                break;

            case R.id.btnSetting:
                goToFragment(fragmentSetting);
                break;

            case R.id.btnUserPolicy:
                getSettingDialog();
                break;

            case R.id.btnLogout:
                logout();
                break;
        }
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //init View
        ivAvt = (ImageView)v.findViewById(R.id.ivAvt);
        tvName = (TextView) v.findViewById(R.id.tvName);
        tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        btnLogout = (Button)v.findViewById(R.id.btnLogout);
        btnSetting = (Button)v.findViewById(R.id.btnSetting);
        btnMailbox = (Button)v.findViewById(R.id.btnMailbox);
        btnExchange = (Button)v.findViewById(R.id.btnExchange);
        btnTransactionHistory = (Button)v.findViewById(R.id.btnTransactionHistory);
        btnUserPolicy = (Button)v.findViewById(R.id.btnUserPolicy);

        //set listener
        btnLogout.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnMailbox.setOnClickListener(this);
        btnExchange.setOnClickListener(this);
        btnTransactionHistory.setOnClickListener(this);
        btnUserPolicy.setOnClickListener(this);

        //init Fragment
        fragmentSetting = new SettingFragment();
        fragmentMailbox = new MailboxFragment();
        fragmentMailbox = new MailboxFragment();
    }

    private void logout() {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(Constants.IS_LOGGED_IN, false);
        editor.putString(Constants.NAME, "");
        editor.putString(Constants.EMAIL, "");
        editor.putString(Constants.UNIQUE_ID, "");
        editor.commit();

        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        startActivity(intent);
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }

    private void getSettingDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_user_policy);

        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();

        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        ImageButton imbCancel = (ImageButton)dialog.findViewById(R.id.imbCancel);
        imbCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
