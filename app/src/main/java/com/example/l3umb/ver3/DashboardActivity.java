package com.example.l3umb.ver3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.example.l3umb.ver3.Fragments.LoginFragment;
import com.example.l3umb.ver3.Function.Constants;


public class DashboardActivity extends AppCompatActivity {
    protected FrameLayout fragmentBlank;
    protected Fragment fragmentLogin;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initLayout();
    }

    private void initLayout() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);
        if(pref.getBoolean(Constants.IS_LOGGED_IN,false)){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        fragmentBlank = (FrameLayout)findViewById(R.id.fragmentBlank);
        fragmentLogin = new LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentBlank, fragmentLogin);
        transaction.commit();
    }
}
