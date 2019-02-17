package com.example.l3umb.ver3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.l3umb.ver3.Fragments.CategoryFragment;
import com.example.l3umb.ver3.Fragments.ChangePasswordDialogFragment;
import com.example.l3umb.ver3.Fragments.HomeFragment;
import com.example.l3umb.ver3.Fragments.PostProductDialogFragment;
import com.example.l3umb.ver3.Fragments.SearchFragment;
import com.example.l3umb.ver3.Fragments.UserDetailFragment;
import com.example.l3umb.ver3.Fragments.UserFragment;
import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Function.LocaleHelper;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener{
    private SharedPreferences pref;
    private FloatingActionButton mnCenter, mnLeft, mnRight;
    public ImageView navigator;
    private Button btnHome, btnCategory, btnSearch, btnAccount;
    protected FrameLayout fragmentBlank;
    protected Fragment fragmentHome, fragmentCategory, fragmentSearch, fragmentUser, fragmentUserDetail;
    private boolean isMenuShowed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        Log.d(Constants.TAG, "?" + pref.getString(Constants.TAG, ""));
        switch (pref.getString(Constants.SELECTED_THEME, "")) {
            case "lilac":
                Log.d(Constants.TAG, "?" + pref.getString(Constants.TAG, ""));
                setTheme(R.style.Theme_App_Lilac);
                break;
            case "black":
                Log.d(Constants.TAG, "?" + pref.getString(Constants.TAG, ""));
                setTheme(R.style.Theme_App_Black);
                break;
            default:
                Log.d(Constants.TAG, "?" + pref.getString(Constants.TAG, ""));
        }

        setContentView(R.layout.activity_main);
        initLayout();


        Toast.makeText(this, "Welcome " + pref.getString(Constants.NAME, ""), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
    }

//    @Override
//    public Resources.Theme getTheme() {
//        Resources.Theme theme = super.getTheme();
//        pref = PreferenceManager.getDefaultSharedPreferences(this);
//        switch (pref.getString(Constants.SELECTED_THEME, "")) {
//            case "lilac":
//                Log.d(Constants.TAG, pref.getString(Constants.TAG, ""));
//                theme.applyStyle(R.style.Theme_App_Lilac, true);
//                break;
//            case "black":
//                theme.applyStyle(R.style.Theme_App_Black, true);
//                break;
//        }
//        // you could also use a switch if you have many themes that could apply
//        return theme;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.navigator:
                if (!isMenuShowed) {
                    showMenu(true);
                    isMenuShowed = !isMenuShowed;
                } else {
                    showMenu(false);
                    isMenuShowed = !isMenuShowed;
                } break;

            case R.id.mnCenter:
                showMenu(false);
                isMenuShowed = !isMenuShowed;
                Bundle bundle = new Bundle();
                bundle.putBoolean("self", true);
                fragmentUserDetail.setArguments(bundle);
                goToFragment(fragmentUserDetail);
                break;

            case R.id.mnLeft:
                isMenuShowed = !isMenuShowed;
                FragmentManager fm = getSupportFragmentManager();
                PostProductDialogFragment postProductDialogFragment = PostProductDialogFragment.newInstance();
                postProductDialogFragment.show(fm, null);
                break;

            case R.id.mnRight:
                showMenu(false);
                isMenuShowed = !isMenuShowed;
                break;

            case R.id.btnHome:
                goToFragment(fragmentHome);
                break;

            case R.id.btnCategory:
                goToFragment(fragmentCategory);
                break;

            case R.id.btnSearch:
                goToFragment(fragmentSearch);
                break;

            case R.id.btnAccount:
                goToFragment(fragmentUser);
                break;
        }
    }

    private void initLayout() {
        pref.registerOnSharedPreferenceChangeListener(this);

        btnHome = (Button)findViewById(R.id.btnHome);
        btnCategory = (Button)findViewById(R.id.btnCategory);
        btnSearch = (Button)findViewById(R.id.btnSearch);
        btnAccount = (Button)findViewById(R.id.btnAccount);

        navigator = (ImageView) findViewById(R.id.navigator);
        mnCenter = (FloatingActionButton)findViewById(R.id.mnCenter);
        mnLeft = (FloatingActionButton)findViewById(R.id.mnLeft);
        mnRight = (FloatingActionButton)findViewById(R.id.mnRight);

        if (!pref.getString(Constants.AVATAR, "").equals("")) {
            Picasso.with(this).load(pref.getString(Constants.AVATAR, "")).placeholder(R.mipmap.ic_launcher).resizeDimen(R.dimen.avatarXSmall, R.dimen.avatarXSmall).into(navigator);
        }

        navigator.setOnClickListener(this);
        mnCenter.setOnClickListener(this);
        mnLeft.setOnClickListener(this);
        mnRight.setOnClickListener(this);
        btnHome.setOnClickListener(this);
        btnCategory.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnAccount.setOnClickListener(this);

        fragmentBlank = (FrameLayout)findViewById(R.id.fragmentBlank);
        fragmentHome = new HomeFragment();
        fragmentCategory = new CategoryFragment();
        fragmentSearch = new SearchFragment();
        fragmentUser = new UserFragment();
        fragmentUserDetail = new UserDetailFragment();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentBlank, fragmentHome);
        transaction.commit();
    }

    private void showMenu(boolean show) {
        if (show) {
            mnCenter.show();
            mnLeft.show();
            mnRight.show();
        } else {
            mnCenter.hide();
            mnLeft.hide();
            mnRight.hide();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
        Picasso.with(this).load(pref.getString(Constants.AVATAR, "")).placeholder(R.mipmap.ic_launcher).resizeDimen(R.dimen.avatarXSmall, R.dimen.avatarXSmall).into(navigator);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MainActivity.this).setTitle("Exit")
                .setMessage("Bạn có chắc muốn thoát?")
                .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
                        startActivity(intent);
                        finish();
                        System.exit(0);
                    }
                }).setNegativeButton("Không", null).show();
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(R.id.fragmentBlank, fragment).addToBackStack(null);
        transaction.commit();
    }
}
