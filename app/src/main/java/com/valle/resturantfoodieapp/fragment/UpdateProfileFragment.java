package com.valle.resturantfoodieapp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.base.BaseFragment;
import com.valle.resturantfoodieapp.models.LoginModel;
import com.valle.resturantfoodieapp.models.UpdateProfileModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.prefs.SharedPrefModule;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.valle.resturantfoodieapp.utils.ImageFilePath;
import com.valle.resturantfoodieapp.utils.RoundRectCornerImageView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UpdateProfileFragment extends BaseFragment implements NetworkResponceListener {

    private static final int REQUEST_CAMERA = 5;
    private static final int SELECT_FILE = 6;
    private String imageFilePath;
    private String userChoosenTask;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private File profileImageFile;
    private File coverImageFile;


    @BindView(R.id.etUpRestaurantName)
    AppCompatEditText etUpRestaurantName;

    @BindView(R.id.etUpRestaurantEmailId)
    AppCompatEditText etUpRestaurantEmailId;

    @BindView(R.id.etUpMobileNumber)
    AppCompatEditText etUpMobileNumber;

    @BindView(R.id.etUpAddress)
    AppCompatEditText etUpAddress;

    @BindView(R.id.tvRestName)
    AppCompatTextView tvRestName;

    @BindView(R.id.ivProfilePic)
    RoundRectCornerImageView ivProfilePic;

    @BindView(R.id.ivCoverImage)
    RoundRectCornerImageView ivCoverImage;

    @BindView(R.id.frame_layout)
    FrameLayout frame_layout;

    private String PROFILE_PIC = "PROFILE_PIC";
    private String COVER_PIC = "COVER_PIC";

    private String imageType = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_update_profile, container, false);
    }

    @OnClick(R.id.tvUpdateProfile)
    void OnClicktvUpdateProfile() {

        String restaurantName = etUpRestaurantName.getText().toString();
        String restaurantEmail = etUpRestaurantEmailId.getText().toString();
        String mobileNumber = etUpMobileNumber.getText().toString();
        String address = etUpAddress.getText().toString();

        if (TextUtils.isEmpty(restaurantName)) {
            etUpRestaurantName.setError(getResources().getString(R.string.resturent_name));
            etUpRestaurantName.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(restaurantEmail)) {
            etUpRestaurantEmailId.setError(getResources().getString(R.string.resturent_email));
            etUpRestaurantEmailId.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(mobileNumber)) {
            etUpMobileNumber.setError(getResources().getString(R.string.resturent_mobile_number));
            etUpMobileNumber.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(address)) {
            etUpAddress.setError(getResources().getString(R.string.resturent_address));
            etUpAddress.setFocusable(true);
            return;
        }

        MultipartBody.Part profileBody = null;
        MultipartBody.Part coverBody = null;
        if (profileImageFile != null) {
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), profileImageFile);
            profileBody = MultipartBody.Part.createFormData("Profile_Image", profileImageFile.getName(), requestFile);
        }

        if (coverImageFile != null) {
            RequestBody requestFile1 =
                    RequestBody.create(MediaType.parse("multipart/form-data"), coverImageFile);
            coverBody = MultipartBody.Part.createFormData("Cover_Image", coverImageFile.getName(), requestFile1);
        }
        RequestBody userId =
                RequestBody.create(MediaType.parse("multipart/form-data"), new SharedPrefModule(getActivity()).getUserId());

        RequestBody name =
                RequestBody.create(MediaType.parse("multipart/form-data"), restaurantName);

        RequestBody email =
                RequestBody.create(MediaType.parse("multipart/form-data"), restaurantEmail);

        RequestBody phone =
                RequestBody.create(MediaType.parse("multipart/form-data"), mobileNumber);

        RequestBody addressStr =
                RequestBody.create(MediaType.parse("multipart/form-data"), address);

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            showProgressDialog(getActivity());
            makeHttpCallToUploadImageData(this, Apis.EDIT_PROFILE, getRetrofitInterfaceToUploadImageData().updateProfile(profileBody, coverBody, userId, name, email, phone, addressStr));
        } else {
            showSnakBar(frame_layout);
        }
    }

    private void setData() {

        String userLoginResponseData = new SharedPrefModule(getActivity()).getUserLoginResponseData();
        if (TextUtils.isEmpty(userLoginResponseData)) {
            return;
        }

        LoginModel.responseData.UserInfoData loginModel = new Gson().fromJson(userLoginResponseData, LoginModel.responseData.UserInfoData.class);
        tvRestName.setText(loginModel.Restaurant_Name);
        etUpRestaurantName.setText(loginModel.Restaurant_Name);
        etUpMobileNumber.setText(loginModel.Mobile);
        etUpRestaurantEmailId.setText(loginModel.Email);
        etUpAddress.setText(loginModel.Address);

        try {
            if (!TextUtils.isEmpty(loginModel.Profile_Image)) {
                Glide.with(getActivity()).load(Apis.API_URL_FILE + loginModel.Profile_Image).placeholder(getResources().getDrawable(R.drawable.upload)).into(ivProfilePic);
            }
            if (!TextUtils.isEmpty(loginModel.Cover_Image)) {
                Glide.with(getActivity()).load(Apis.API_URL_FILE + loginModel.Cover_Image).placeholder(getResources().getDrawable(R.drawable.image)).into(ivCoverImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("CheckResult")
    private void compressImage(File file) {
        new Compressor(getActivity())
                .compressToFileAsFlowable(file)
                .subscribeOn(Schedulers.io())
                .subscribe(file1 -> {
                    if (imageType.equalsIgnoreCase(PROFILE_PIC)) {
                        profileImageFile = file1;
                    } else {
                        coverImageFile = file1;
                    }
                }, throwable -> throwable.printStackTrace());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);
        setData();
    }

    @OnClick({R.id.flChangeProfilePic, R.id.tvUploadImage})
    void OnClicktvUploadImage(View v) {
        if (v.getId() == R.id.flChangeProfilePic) {
            imageType = PROFILE_PIC;
        } else {
            imageType = COVER_PIC;
        }
        final CharSequence[] items = {"Tomar foto", "Elige de la biblioteca",
                "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir foto!");
        builder.setItems(items, (dialog, item) -> {

            if (items[item].equals("Tomar foto")) {
                userChoosenTask = "Take Photo";
                cameraIntent();
            } else if (items[item].equals("Elige de la biblioteca")) {
                userChoosenTask = "Choose from Library";
                galleryIntent();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals(getResources().getString(R.string.take_photo)))
                        cameraIntent();
                    else if (userChoosenTask.equals(getResources().getString(R.string.choose_from_library)))
                        galleryIntent();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else return null;
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String picturePath = ImageFilePath.getPath(getActivity().getApplicationContext(), selectedImageUri);
        if (imageType.equalsIgnoreCase(PROFILE_PIC)) {
            profileImageFile = new File(picturePath);
        } else {
            coverImageFile = new File(picturePath);
        }
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                if (imageType.equalsIgnoreCase(PROFILE_PIC)) {
                    ivProfilePic.setImageBitmap(bm);
                    compressImage(profileImageFile);
                } else {
                    ivCoverImage.setImageBitmap(bm);
                    compressImage(coverImageFile);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) BitmapFactory.decodeFile(imageFilePath);

        if (imageType.equalsIgnoreCase(PROFILE_PIC)) {
            profileImageFile = new File(imageFilePath);
            ivProfilePic.setImageBitmap(thumbnail);
            compressImage(profileImageFile);
        } else {
            coverImageFile = new File(imageFilePath);
            ivCoverImage.setImageBitmap(thumbnail);
            compressImage(coverImageFile);
        }
    }

    private void galleryIntent() {
        if (CommonUtils.checkPermission(getActivity())) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);//
            startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_file)), SELECT_FILE);
        }
    }

    private void cameraIntent() {
        if (CommonUtils.checkPermissionCamera(getActivity())) {
            Intent pictureIntent = new Intent(
                    MediaStore.ACTION_IMAGE_CAPTURE);
            if (pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                //Create a itemFile to store the image
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(), "com.valle.resturantfoodieapp.provider", photoFile);
                    pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            photoURI);
                    startActivityForResult(pictureIntent,
                            REQUEST_CAMERA);
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());

        switch (url) {

            case Apis.EDIT_PROFILE:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        UpdateProfileModel updateProfileModel = new Gson().fromJson(responce, UpdateProfileModel.class);
                        new SharedPrefModule(getActivity()).setUserLoginResponse(new Gson().toJson(updateProfileModel.response.response));
                        Toast.makeText(getActivity(), getResources().getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(getActivity());
    }
}
