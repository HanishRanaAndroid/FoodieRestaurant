package com.valle.resturantfoodieapp.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.prefs.SharedPrefModule;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    RelativeLayout rlMain;

    private static final long SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        rlMain = findViewById(R.id.rlMain);
        setSpanishDefault();

        if (CommonUtils.isNetworkAvailable(this)) {
            openNextScreen();
        } else {
            showSnakBar(rlMain);
        }
    }

    private void openNextScreen() {
        new Handler().postDelayed(() -> {
            if (TextUtils.isEmpty(new SharedPrefModule(SplashActivity.this).getUserLoginResponseData())) {
                startActivity(new Intent(SplashActivity.this, SliderActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, HomeTabActivity.class));
            }
            this.fileList();
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void setSpanishDefault() {
        String lang = "es";
        Resources res = this.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.setLocale(new Locale(lang.toLowerCase())); // API 17+ only.
        res.updateConfiguration(conf, dm);
    }

    public void showSnakBar(View viewLayout) {
        Snackbar snackbar = Snackbar
                .make(viewLayout, getResources().getString(R.string.internet_error), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.restry), view -> {
                    if (CommonUtils.isNetworkAvailable(this)) {
                        openNextScreen();
                    } else {
                        showSnakBar(viewLayout);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
