package com.valle.resturantfoodieapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.base.BaseActivity;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.utils.CommonUtils;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.valle.resturantfoodieapp.utils.CommonUtils.MY_PERMISSIONS_REQUEST_LOCATION;

public class RegisterActivity extends BaseActivity implements NetworkResponceListener {

    @BindView(R.id.etRestaurantName)
    AppCompatEditText etRestaurantName;

    @BindView(R.id.etPhoneNumber)
    AppCompatEditText etPhoneNumber;

    @BindView(R.id.etEmail)
    AppCompatEditText etEmail;

    @BindView(R.id.etAddress)
    AppCompatTextView etAddress;

    @BindView(R.id.etPassword)
    AppCompatEditText etPassword;

    @BindView(R.id.etConfirmPassword)
    AppCompatEditText etConfirmPassword;

    @BindView(R.id.ivPasswordVisi)
    AppCompatImageView ivPasswordVisi;

    @BindView(R.id.ivConfPassVisi)
    AppCompatImageView ivConfPassVisi;

    @BindView(R.id.rlMain)
    RelativeLayout rlMain;

    private String longitude = "", latitude = "";

    private String Yes = "Y";
    private String No = "N";

    private String strAdministrater = "";
    private String deviceToken = "";


    @OnClick(R.id.ivPasswordVisi)
    void OnClickivPasswordVisibility() {
        if (etPassword.getTag().toString().equalsIgnoreCase(Yes)) {
            etPassword.setTag(No);
            etPassword.setTransformationMethod(null);
            ivPasswordVisi.setImageDrawable(getResources().getDrawable(R.drawable.pass_show));
        } else {
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            ivPasswordVisi.setImageDrawable(getResources().getDrawable(R.drawable.pass_hide));
            etPassword.setTag(Yes);
        }
    }

    @OnClick(R.id.ivConfPassVisi)
    void OnClickivConfPassVisi() {
        if (etConfirmPassword.getTag().toString().equalsIgnoreCase(Yes)) {
            etConfirmPassword.setTag(No);
            etConfirmPassword.setTransformationMethod(null);
            ivConfPassVisi.setImageDrawable(getResources().getDrawable(R.drawable.pass_show));
        } else {
            etConfirmPassword.setTransformationMethod(new PasswordTransformationMethod());
            ivConfPassVisi.setImageDrawable(getResources().getDrawable(R.drawable.pass_hide));
            etConfirmPassword.setTag(Yes);
        }
    }

    @OnClick(R.id.etAddress)
    void chooseAddress() {
        if (CommonUtils.checkLocationPermission(RegisterActivity.this)) {
            startActivityForResult(new Intent(RegisterActivity.this, AddAddressActivity.class), 786);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 786) {
                etAddress.setText(data.getStringExtra("address"));
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                strAdministrater = data.getStringExtra("administrater");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        try {
                            if (CommonUtils.checkLocationPermission(RegisterActivity.this)) {
                                startActivityForResult(new Intent(RegisterActivity.this, AddAddressActivity.class), 786);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                } else {

                    try {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            if (CommonUtils.checkLocationPermission(RegisterActivity.this)) {
                                startActivityForResult(new Intent(RegisterActivity.this, AddAddressActivity.class), 786);
                            }
                        } else {

                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle(getResources().getString(R.string.plz_allow_loc_per))
                                    .setMessage(getResources().getString(R.string.plz_allow_for_best_results))
                                    .setPositiveButton(getResources().getString(R.string.ok), (dialogInterface, i) -> {
                                        //Prompt the user once explanation has been shown
                                        Intent intent = new Intent();
                                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                                        intent.setData(uri);
                                        startActivity(intent);
                                    })
                                    .create()
                                    .show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        bindView(this);
        etPassword.setTag(No);
        etConfirmPassword.setTag(No);

        try {

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
                deviceToken = instanceIdResult.getToken();
                Log.d("LoginScreen", "findViewId: " + deviceToken);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.tvSignup)
    void OnClicktvDeliveryBoySignup() {

        String restaurantName = etRestaurantName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String email = etEmail.getText().toString();
        String address = etAddress.getText().toString();
        String password = etPassword.getText().toString();
        String confPassword = etConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(restaurantName)) {
            etRestaurantName.setError(getResources().getString(R.string.plz_enter_restaurant_name));
            etRestaurantName.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            etPhoneNumber.setError(getResources().getString(R.string.plz_enter_phone_number));
            etPhoneNumber.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError(getResources().getString(R.string.plz_enter_your_email));
            etEmail.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(address) || address.equalsIgnoreCase(getResources().getString(R.string.address))) {
            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.plz_enter_restaurant_address), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError(getResources().getString(R.string.plz_enter_your_password));
            etPassword.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(confPassword)) {
            etConfirmPassword.setError(getResources().getString(R.string.plz_enter_conf_password));
            etConfirmPassword.setFocusable(true);
            return;
        }

        if (!password.equalsIgnoreCase(confPassword)) {
            etConfirmPassword.setError(getResources().getString(R.string.password_not_matched));
            etConfirmPassword.setFocusable(true);
            return;
        }

        if (CommonUtils.isNetworkAvailable(this)) {
            showProgressDialog(RegisterActivity.this);
            makeHttpCall(RegisterActivity.this, Apis.REGISTER, getRetrofitInterface().register(strAdministrater, restaurantName, phoneNumber, email, address, password, deviceToken, "Restaurant", latitude, longitude));
        } else {
            showSnakBar(rlMain);
        }
    }

    @OnClick(R.id.tvAlreadyHaveAAccount)
    void OnClicktvAlreadyHaveAAccount() {
        super.onBackPressed();
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(RegisterActivity.this);

        switch (url) {
            case Apis.REGISTER:
                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        Intent intent = new Intent(RegisterActivity.this, OTPVerificationActivity.class);
                        intent.putExtra("responce", responce);
                        startActivity(intent);
                    } else if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.ERROR)) {
                        new android.app.AlertDialog.Builder(RegisterActivity.this).setTitle(getResources().getString(R.string.alert)).setMessage(jsonObject.getString("response").toLowerCase().contains("email") ? "ID de correo electrónico ya registrado. Por favor, elija otro" : jsonObject.getString("response").toLowerCase().contains("mobile") ? "Número de móvil ya registrado, elija otro" : jsonObject.getString("response")).setPositiveButton(getResources().getString(R.string.ok), (dialog, which) -> dialog.dismiss()).create().show();
                        // Toast.makeText(RegisterActivity.this, jsonObject.getString("response"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(RegisterActivity.this);
    }
}
