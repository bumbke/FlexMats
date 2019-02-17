package com.example.l3umb.ver3.Fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Object.ItemClickListener;
import com.example.l3umb.ver3.Object.ItemMailboxAdapter;
import com.example.l3umb.ver3.Object.Mail;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MailboxFragment extends Fragment implements View.OnClickListener {
    private SharedPreferences pref;
    private TextView tvAlert, tvHeader;
    private ImageView ivBack;
    private UserFragment fragmentUser;
    private RecyclerView rvItem;
    private ItemMailboxAdapter mailboxAdapter;
    private int mail_type;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mailbox,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mail_type = getArguments().getInt("mail_type");
        switch (mail_type) {
            case 1:
                break;
            case 2:
                tvHeader.setText(R.string.user_transaction_history);
                break;
        }
        getMailboxProcess(pref.getString(Constants.UNIQUE_ID, ""));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ivBack:
                goToFragment(fragmentUser);
        }
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        tvAlert = (TextView) v.findViewById(R.id.tvAlert);
        tvHeader = (TextView) v.findViewById(R.id.tvHeader);

        ivBack = (ImageView) v.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(this);

        fragmentUser = new UserFragment();

        rvItem = (RecyclerView) v.findViewById(R.id.rvItem);

        LinearLayoutManager vertical = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rvItem.setLayoutManager(vertical);
        rvItem.setItemAnimator(new DefaultItemAnimator());
    }

    private void getMailboxProcess(String user_id) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Mail mail = new Mail();
        mail.setFrom_user_id(user_id);
        mail.setMail_type(mail_type);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.GET_MESSAGEBOX_OPERATION);
        request.setMail(mail);

        Log.d(Constants.TAG,"?");
        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                final ServerResponse resp = response.body();
                Log.d(Constants.TAG,"1" + mail_type);
                if(resp.getResult().equals(Constants.SUCCESS)) {
                    final List<Mail> mails = resp.getMails();
                    final List<User> users = resp.getUsers();

                    if (mails.size()>0) {
                        tvAlert.setVisibility(View.GONE);
                    }

                    Log.d(Constants.TAG,"2");
                    mailboxAdapter = new ItemMailboxAdapter(getActivity(), mails, users, mails.size(), new ItemClickListener() {
                        @Override
                        public void onItemClick(View v, int position) {
                            Bundle bundle = new Bundle();
                            bundle.putString("user_id", resp.getUsers().get(position).getUser_id());
                            bundle.putString("user_name", resp.getUsers().get(position).getName());
                            bundle.putString("user_avatar", resp.getUsers().get(position).getAvatar_url());
                            bundle.putString("title", resp.getMails().get(position).getTitle());
                            bundle.putString("date", resp.getMails().get(position).getCreated_date());
                            bundle.putString("content", resp.getMails().get(position).getContent());
                            bundle.putInt("mail_type", resp.getMails().get(position).getMail_type());
                            bundle.putInt("type", resp.getMails().get(position).getType());

                            FragmentManager fm = getFragmentManager();
                            MailDetailDialogFragment mailDetailDialogFragment = MailDetailDialogFragment.newInstance();
                            mailDetailDialogFragment.setArguments(bundle);
                            mailDetailDialogFragment.show(fm, null);
                        }
                    });
                    rvItem.setAdapter(mailboxAdapter);
                } else {
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }
}
