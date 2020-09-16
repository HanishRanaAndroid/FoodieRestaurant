package com.valle.resturantfoodieapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.firebase.iid.FirebaseInstanceId;
import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.base.BaseActivity;
import com.valle.resturantfoodieapp.models.LoginModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.prefs.SharedPrefModule;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity implements NetworkResponceListener {

    @BindView(R.id.etSigninNumber)
    AppCompatEditText etSigninNumber;

    @BindView(R.id.etSignInPassword)
    AppCompatEditText etSignInPassword;

    @BindView(R.id.ivPasswordVisibility)
    AppCompatImageView ivPasswordVisibility;

    @BindView(R.id.rlMain)
    RelativeLayout rlMain;

    private String Yes = "Y";
    private String No = "N";
    private String deviceToken="";


    @OnClick(R.id.ivPasswordVisibility)
    void OnClickivPasswordVisibility() {
        if (etSignInPassword.getTag().toString().equalsIgnoreCase(Yes)) {
            etSignInPassword.setTag(No);
            etSignInPassword.setTransformationMethod(null);
            ivPasswordVisibility.setImageDrawable(getResources().getDrawable(R.drawable.pass_show));
        } else {
            etSignInPassword.setTransformationMethod(new PasswordTransformationMethod());
            ivPasswordVisibility.setImageDrawable(getResources().getDrawable(R.drawable.pass_hide));
            etSignInPassword.setTag(Yes);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bindView(this);
        etSignInPassword.setTag(No);

        try {

            FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, instanceIdResult -> {
                deviceToken = instanceIdResult.getToken();
                Log.d("LoginScreen", "findViewId: " + deviceToken);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.tvLogin)
    void OnClicktvLogin() {
        String phoneNumber = etSigninNumber.getText().toString();
        String password = etSignInPassword.getText().toString();

        if (TextUtils.isEmpty(phoneNumber)) {
            etSigninNumber.setError(getResources().getString(R.string.enter_your_number_error));
            etSigninNumber.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etSignInPassword.setError(getResources().getString(R.string.enter_your_password_error));
            etSignInPassword.setFocusable(true);
            return;
        }

        if (CommonUtils.isNetworkAvailable(this)) {
            showProgressDialog(LoginActivity.this);
            makeHttpCall(LoginActivity.this, Apis.LOGIN, getRetrofitInterface().login("Mobile", phoneNumber, password, "Restaurant", deviceToken));
        } else {
            showSnakBar(rlMain);
        }
    }

    @Override
    public void onBackPressed() {
        exitFromApp(LoginActivity.this);
    }

    @OnClick(R.id.tvRegister)
    void onClicktvRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }

    @OnClick(R.id.tvForgetPassword)
    void onClicktvtvForgetPassword() {
        startActivity(new Intent(this, ForgetPasswordActivity.class));
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(LoginActivity.this);

        switch (url) {
            case Apis.LOGIN:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        LoginModel loginModel = new Gson().fromJson(responce, LoginModel.class);
                        new SharedPrefModule(LoginActivity.this).setUserLoginResponse(new Gson().toJson(loginModel.response.UserInfo));
                        new SharedPrefModule(LoginActivity.this).setUserId(loginModel.response.UserInfo.User_Id);
                        startActivity(new Intent(LoginActivity.this, HomeTabActivity.class));
                    } else if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.ERROR)) {
                        Toast.makeText(LoginActivity.this, jsonObject.getJSONObject("response").getString("msg"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(LoginActivity.this);
    }
}
