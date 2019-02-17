package com.example.l3umb.ver3.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.l3umb.ver3.Function.Constants;
import com.example.l3umb.ver3.Function.ImageDocumentHelper;
import com.example.l3umb.ver3.Object.ImageResponse;
import com.example.l3umb.ver3.Object.ImgurService;
import com.example.l3umb.ver3.Object.Product;
import com.example.l3umb.ver3.Object.RequestInterface;
import com.example.l3umb.ver3.Object.ServerRequest;
import com.example.l3umb.ver3.Object.ServerResponse;
import com.example.l3umb.ver3.R;

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
 * Created by Dev on 10/29/2018.
 */

public class PostProductDialogFragment extends DialogFragment implements View.OnClickListener{
    private SharedPreferences pref;
    private ImageView ivImage;
    private EditText edTitle, edDescription;
    private Spinner spinnerPoint;
    private Button btnSelectImage, btnCancel, btnPost;
    private ProgressBar progressBar;
    private CheckedTextView ctvDigital2D, ctvDigital3D, ctvConceptArt, ctvPhotorealism, ctvIllustration, ctvOther;
    private String user_id, product_title = "", product_image_url = "", product_description = "", product_tag = "";
    private int product_points = -1;
    private String[] pointsRange = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private Uri returnUri;
    private File chosenFile;

    public static PostProductDialogFragment newInstance() {
        PostProductDialogFragment dialog = new PostProductDialogFragment();
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        setCancelable(false);

        final View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_post_product, null);
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
        btnCancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        btnPost = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user_id = pref.getString(Constants.UNIQUE_ID, "");
                product_title = edTitle.getText().toString();
                product_description = edDescription.getText().toString();
                product_points = spinnerPoint.getSelectedItemPosition() + 1;

                if (!product_title.isEmpty() && !product_description.isEmpty() && product_points != -1) {
                    joinTag();
                    progressBar.setVisibility(View.VISIBLE);
                    onUpload(v);
                } else {
                    Snackbar.make(v, "Invalid parameter!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ctvDigital2D:
                if (ctvDigital2D.isChecked()) {
                    ctvDigital2D.setChecked(false);
                    ctvDigital2D.setBackgroundColor(Color.BLACK);
                    ctvDigital2D.setTextColor(Color.WHITE);
                } else {
                    ctvDigital2D.setChecked(true);
                    ctvDigital2D.setBackgroundColor(Color.WHITE);
                    ctvDigital2D.setTextColor(Color.BLACK);
                }
                break;
            case R.id.ctvDigital3D:
                if (ctvDigital3D.isChecked()) {
                    ctvDigital3D.setChecked(false);
                    ctvDigital3D.setBackgroundColor(Color.BLACK);
                    ctvDigital3D.setTextColor(Color.WHITE);
                } else {
                    ctvDigital3D.setChecked(true);
                    ctvDigital3D.setBackgroundColor(Color.WHITE);
                    ctvDigital3D.setTextColor(Color.BLACK);
                }
                break;
            case R.id.ctvConceptArt:
                if (ctvConceptArt.isChecked()) {
                    ctvConceptArt.setChecked(false);
                    ctvConceptArt.setBackgroundColor(Color.BLACK);
                    ctvConceptArt.setTextColor(Color.WHITE);
                } else {
                    ctvConceptArt.setChecked(true);
                    ctvConceptArt.setBackgroundColor(Color.WHITE);
                    ctvConceptArt.setTextColor(Color.BLACK);
                }
                break;
            case R.id.ctvPhotorealism:
                if (ctvPhotorealism.isChecked()) {
                    ctvPhotorealism.setChecked(false);
                    ctvPhotorealism.setBackgroundColor(Color.BLACK);
                    ctvPhotorealism.setTextColor(Color.WHITE);
                } else {
                    ctvPhotorealism.setChecked(true);
                    ctvPhotorealism.setBackgroundColor(Color.WHITE);
                    ctvPhotorealism.setTextColor(Color.BLACK);
                }
                break;
            case R.id.ctvIllustration:
                if (ctvIllustration.isChecked()) {
                    ctvIllustration.setChecked(false);
                    ctvIllustration.setBackgroundColor(Color.BLACK);
                    ctvIllustration.setTextColor(Color.WHITE);
                } else {
                    ctvIllustration.setChecked(true);
                    ctvIllustration.setBackgroundColor(Color.WHITE);
                    ctvIllustration.setTextColor(Color.BLACK);
                }
                break;
            case R.id.ctvOther:
                if (ctvOther.isChecked()) {
                    ctvOther.setChecked(false);
                    ctvOther.setBackgroundColor(Color.BLACK);
                    ctvOther.setTextColor(Color.WHITE);
                } else {
                    ctvOther.setChecked(true);
                    ctvOther.setBackgroundColor(Color.WHITE);
                    ctvOther.setTextColor(Color.BLACK);
                }
                break;
            case R.id.btnSelectImage:
                onChoose(v);
                break;
        }
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
                    product_image_url = response.body().data.link;

                    if (!product_tag.isEmpty()) {
                        postProductProcess();
                    } else {
                        Snackbar.make(v, "Fields are empty!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "An unknown error has occured. Failed to upload image!", Toast.LENGTH_LONG).show();
                PostProductDialogFragment.this.getDialog().cancel();
                t.printStackTrace();
                Log.d(Constants.TAG, "failure");
            }
        });
    }

    private void initLayout(View v) {
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

        ivImage = (ImageView) v.findViewById(R.id.ivImage);

        edTitle = (EditText) v.findViewById(R.id.edTitle);
        edDescription = (EditText) v.findViewById(R.id.edDescription);

        spinnerPoint = (Spinner) v.findViewById(R.id.spinnerPoint);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, pointsRange);
        spinnerPoint.setAdapter(spinnerAdapter);

        btnSelectImage = (Button) v.findViewById(R.id.btnSelectImage);

        ctvDigital2D = (CheckedTextView) v.findViewById(R.id.ctvDigital2D);
        ctvDigital3D = (CheckedTextView) v.findViewById(R.id.ctvDigital3D);
        ctvConceptArt = (CheckedTextView) v.findViewById(R.id.ctvConceptArt);
        ctvPhotorealism = (CheckedTextView) v.findViewById(R.id.ctvPhotorealism);
        ctvIllustration = (CheckedTextView) v.findViewById(R.id.ctvIllustration);
        ctvOther = (CheckedTextView) v.findViewById(R.id.ctvOther);

        ctvDigital2D.setOnClickListener(this);
        ctvDigital3D.setOnClickListener(this);
        ctvConceptArt.setOnClickListener(this);
        ctvPhotorealism.setOnClickListener(this);
        ctvIllustration.setOnClickListener(this);
        ctvOther.setOnClickListener(this);

        btnSelectImage.setOnClickListener(this);
    }

    private void joinTag() {
        int i = 1;
        switch (i) {
            case 1:
                if (!ctvDigital2D.isChecked()) {
                    product_tag += "_" + ctvDigital2D.getText().toString();
                }
            case 2:
                if (!ctvDigital3D.isChecked()) {
                    product_tag += "_" + ctvDigital3D.getText().toString();
                }
            case 3:
                if (!ctvConceptArt.isChecked()) {
                    product_tag += "_" + ctvConceptArt.getText().toString();
                }
            case 4:
                if (!ctvPhotorealism.isChecked()) {
                    product_tag += "_" + ctvPhotorealism.getText().toString();
                }
            case 5:
                if (!ctvIllustration.isChecked()) {
                    product_tag += "_" + ctvIllustration.getText().toString();
                }
            case 6:
                if (!ctvOther.isChecked()) {
                    product_tag += "_" + ctvOther.getText().toString();
                }
            default:
                product_tag = product_tag.substring(1);
        }
    }

    private void postProductProcess() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        RequestInterface requestInterface = retrofit.create(RequestInterface.class);

        Product product = new Product();
        product.setUser_id(user_id);
        product.setTitle(product_title);
        product.setTag(product_tag);
        product.setPoints(product_points);
        product.setDescription(product_description);
        product.setImage_url(product_image_url);

        ServerRequest request = new ServerRequest();
        request.setOperation(Constants.POST_PRODUCT_OPERATION);
        request.setProduct(product);

        Call<ServerResponse> response = requestInterface.operation(request);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse resp = response.body();
                if (response.body() == null) {
                    Toast.makeText(getActivity(), "null", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(getActivity(), resp.getMessage(), Toast.LENGTH_SHORT)
                            .show();
                    dismiss();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Log.d(Constants.TAG,"failed");
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
