package com.example.l3umb.ver3.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.l3umb.ver3.DashboardActivity;
import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Function.LocaleHelper;
import com.example.l3umb.ver3.MainActivity;
import com.example.l3umb.ver3.R;

/**
 * Created by Dev on 10/28/2018.
 */

public class SettingFragment extends Fragment implements View.OnClickListener {
    private Button btnPassword, btnAboutApp, btnContact, btnAvatar, btnInformation, btnTheme, btnLanguage;
    private ImageView ivBack;
    private Fragment fragmentUser;
    private Dialog dialog;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting,container,false);
        initLayout(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder mBuilder;
        AlertDialog mDialog;

        switch (v.getId()) {
            case R.id.btnAvatar:
                FragmentManager fm = getFragmentManager();
                ChangeUserAvatarDialogFragment changeUserAvatarDialogFragment = ChangeUserAvatarDialogFragment.newInstance();
                changeUserAvatarDialogFragment.show(fm, null);
                break;

            case R.id.btnPassword:
                FragmentManager fm1 = getFragmentManager();
                ChangePasswordDialogFragment changePasswordDialogFragment = ChangePasswordDialogFragment.newInstance();
                changePasswordDialogFragment.show(fm1, null);
                break;

            case R.id.btnInformation:
                FragmentManager fm2 = getFragmentManager();
                ChangeUserInfoDialogFragment changeUserInfoDialogFragment = ChangeUserInfoDialogFragment.newInstance();
                changeUserInfoDialogFragment.show(fm2, null);
                break;

            case R.id.btnTheme:
                final String[] listTheme = {"Lilac", "Raspberry"};
                mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle(R.string.general_choose_language);
                mBuilder.setSingleChoiceItems(listTheme, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                SharedPreferences.Editor editor = pref.edit();

                                editor.putString(Constants.SELECTED_THEME, "lilac");
                                editor.apply();

                                getActivity().recreate();
                                break;

                            case 1:
                                SharedPreferences.Editor editor1 = pref.edit();

                                editor1.putString(Constants.SELECTED_THEME, "black");
                                editor1.apply();

                                getActivity().recreate();
                                break;
                        }

                        dialog.dismiss();
                    }
                });

                mDialog = mBuilder.create();
                mDialog.show();
                break;

            case R.id.btnLanguage:
                final String[] listLanguage = {"English", "Tiếng Việt"};
                mBuilder = new AlertDialog.Builder(getActivity());
                mBuilder.setTitle(R.string.general_choose_language);
                mBuilder.setSingleChoiceItems(listLanguage, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                LocaleHelper.setLocale(getActivity(), "en");
                                getActivity().recreate();
                                break;

                            case 1:
                                LocaleHelper.setLocale(getActivity(), "vi");
                                getActivity().recreate();
                                break;
                        }

                        dialog.dismiss();
                    }
                });

                mDialog = mBuilder.create();
                mDialog.show();
                break;

            case R.id.ivBack:
                goToFragment(fragmentUser);
                break;

            case R.id.btnAboutApp:
                getSettingDialog("about");
                break;

            case R.id.btnContact:
                getSettingDialog("contact");
                break;
        }
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        btnAvatar = v.findViewById(R.id.btnAvatar);
        btnPassword = v.findViewById(R.id.btnPassword);
        btnInformation = v.findViewById(R.id.btnInformation);
        btnTheme = v.findViewById(R.id.btnTheme);
        btnLanguage = v.findViewById(R.id.btnLanguage);
        btnAboutApp = v.findViewById(R.id.btnAboutApp);
        btnContact = v.findViewById(R.id.btnContact);
        ivBack = v.findViewById(R.id.ivBack);
        //set listener
        btnAvatar.setOnClickListener(this);
        btnPassword.setOnClickListener(this);
        btnInformation.setOnClickListener(this);
        btnTheme.setOnClickListener(this);
        btnLanguage.setOnClickListener(this);
        btnAboutApp.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        //
        fragmentUser = new UserFragment();
    }

    private void goToFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.fragmentBlank, fragment);
        transaction.commit();
    }

    private void getSettingDialog(String s) {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        switch (s){
            case "about":
                dialog.setContentView(R.layout.dialog_about_app);
                break;
            case "contact":
                dialog.setContentView(R.layout.dialog_contact_info);
                break;
        }

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
