package com.valle.resturantfoodieapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.base.BaseActivity;
import com.valle.resturantfoodieapp.models.OTPResendModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.valle.resturantfoodieapp.utils.OtpEditText;
import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class OTPVerificationChangePasswordActivity extends BaseActivity implements NetworkResponceListener {

    @BindView(R.id.etOtp)
    OtpEditText etOtp;

    @BindView(R.id.tvPhoneNumber)
    AppCompatTextView tvPhoneNumber;


    @BindView(R.id.llMain)
    LinearLayoutCompat llMain;

    private String validationCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        bindView(this);
        validationCode = getIntent().getStringExtra("validationCode");
        tvPhoneNumber.setText(!TextUtils.isEmpty(getIntent().getStringExtra("phoneNumber")) ? getIntent().getStringExtra("phoneNumber") : "");
        //etOtp.setText(validationCode);
        etOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 4) {
                    CommonUtils.hideKeyboard(OTPVerificationChangePasswordActivity.this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @OnClick(R.id.tvVerifyOtp)
    void OnClicktvVerifyOtp() {
        String OTP = etOtp.getText().toString();

        if (TextUtils.isEmpty(OTP)) {
            Toast.makeText(OTPVerificationChangePasswordActivity.this, getResources().getString(R.string.enter_otp), Toast.LENGTH_SHORT).show();
            return;
        }

        if (!OTP.equalsIgnoreCase(validationCode)) {
            Toast.makeText(OTPVerificationChangePasswordActivity.this, getResources().getString(R.string.enter_code), Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(OTPVerificationChangePasswordActivity.this, NewPasswordActivity.class);
        intent.putExtra("phoneNumber", getIntent().getStringExtra("phoneNumber"));
        startActivity(intent);
    }

    @OnClick(R.id.tvResendOTP)
    void OnClicktvResendOTP() {

        if (CommonUtils.isNetworkAvailable(this)) {
            showProgressDialog(OTPVerificationChangePasswordActivity.this);
            makeHttpCall(OTPVerificationChangePasswordActivity.this, Apis.SEND_OTP, getRetrofitInterface().sendOTPtoUser(getIntent().getStringExtra("phoneNumber")));
        } else {
            showSnakBar(llMain);
        }
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(OTPVerificationChangePasswordActivity.this);

        switch (url) {
            case Apis.SEND_OTP:
                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        OTPResendModel otpResendModel = new Gson().fromJson(responce, OTPResendModel.class);
                        Toast.makeText(OTPVerificationChangePasswordActivity.this, otpResendModel.response.msg, Toast.LENGTH_SHORT).show();
                        validationCode = otpResendModel.response.validationCode;
                    } else {
                        Toast.makeText(OTPVerificationChangePasswordActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(OTPVerificationChangePasswordActivity.this);
    }

}
