package com.example.l3umb.ver3.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Function.ImageDocumentHelper;
import com.example.l3umb.ver3.Object.ImageResponse;
import com.example.l3umb.ver3.Object.ImgurService;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.Object.User;
import com.example.l3umb.ver3.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Dev on 11/1/2018.
 */

public class ChangeUserAvatarDialogFragment extends DialogFragment implements View.OnClickListener{
    private ImageView ivImage;
    private Button btnSelectImage;
    private ProgressBar progressBar;
    private SharedPreferences pref;
    private String avatar_url;
    private Uri returnUri;
    private File chosenFile;
    private ImageView navigator;

    public static ChangeUserAvatarDialogFragment newInstance() {
        ChangeUserAvatarDialogFragment dialog = new ChangeUserAvatarDialogFragment();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        setCancelable(false);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_avatar, null);
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
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Log.d(Constants.TAG, "" + avatar_url);
                onUpload(v);
            }
        });
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSelectImage:
                onChoose(v);
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        navigator = (ImageView)getActivity().findViewById(R.id.navigator);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            returnUri = data.getData();
            ivImage.setImageURI(returnUri);

            super.onActivityResult(requestCode, resultCode, data);

            Log.d(Constants.TAG, "Before check");


            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                final List<String> permissionsList = new ArrayList<String>();
                addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE);
                addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (!permissionsList.isEmpty())
                    ActivityCompat.requestPermissions(getActivity(),
                            permissionsList.toArray(new String[permissionsList.size()]),
                            Constants.READ_WRITE_EXTERNAL);
                else
                    getFilePath();
            } else {
                getFilePath();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void addPermission(List<String> permissionsList, String permission) {
        if (getActivity().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            getActivity().shouldShowRequestPermissionRationale(permission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.READ_WRITE_EXTERNAL:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "All Permission are granted.", Toast.LENGTH_SHORT)
                            .show();
                    getFilePath();
                } else {
                    Toast.makeText(getActivity(), "Some permissions are denied", Toast.LENGTH_SHORT)
                            .show();
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void getFilePath() {
        String filePath = ImageDocumentHelper.getPath(getActivity(), this.returnUri);
        //Safety check to prevent null pointer exception
        if (filePath == null || filePath.isEmpty()) return;
        chosenFile = new File(filePath);
        Log.d(Constants.TAG, filePath);
    }

    public void onChoose(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.PICK_IMAGE_REQUEST);
    }

    public void onUpload(final View v) {
        if (chosenFile == null) {
            Toast.makeText(getActivity(), "Choose a file before upload.", Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        ImgurService imgurService = ImgurService.retrofit.create(ImgurService.class);

        final Call<ImageResponse> call =
                imgurService.postImage(
                        "Title",
                        "Description", "", "",
                        MultipartBody.Part.createFormData(
                                "image",
                                chosenFile.getName(),
                                RequestBody.create(MediaType.parse("image/*"), chosenFile)
                        ));

        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if (response == null) {
                    Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    Log.d(Constants.TAG, "this 1");
                    return;
                }
                if (response.isSuccessful()) {
                    progressBar.setVisibility(View.GONE);
                    avatar_url = response.body().data.link;
                    if (!avatar_url.isEmpty()) {
                        changeAvatarProcess(avatar_url);
                    }
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "An unknown error has occured. Failed to upload image!", Toast.LENGTH_LONG).show();
                dismiss();
                t.printStackTrace();
                Log.d(Constants.TAG, "failure");
            }
        });
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //init View
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        ivImage = (ImageView)v.findViewById(R.id.ivImage);
        btnSelectImage = (Button)v.findViewById(R.id.btnSelectImage);

        btnSelectImage.setOnClickListener(this);
    }

    private void changeAvatarProcess(final String avatar_url) {
        Gson gson = new GsonBuilder().setLenient().create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        User user = new User();
        user.setUser_id(pref.getString(Constants.UNIQUE_ID,""));
        user.setAvatar_url(avatar_url);

        Log.d(Constants.TAG, user.getAvatar_url() + user.getUser_id());
        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.CHANGE_AVATAR_OPERATION);
        request.setUser(user);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (resp.getResult().equals(Constants.SUCCESS)) {
                    Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(Constants.AVATAR, avatar_url);
                    editor.apply();
                    Picasso.with(getActivity()).load(avatar_url).placeholder(R.mipmap.ic_launcher).resizeDimen(R.dimen.avatarXSmall, R.dimen.avatarXSmall).into(navigator);
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
}
