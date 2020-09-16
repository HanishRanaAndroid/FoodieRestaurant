package com.valle.resturantfoodieapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.base.BaseActivity;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;

import io.github.douglasjunior.androidSimpleTooltip.OverlayView;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class SliderActivity extends BaseActivity implements NetworkResponceListener {

    private AppCompatImageView ivIcon;
    private AppCompatTextView tvSignUp;
    private boolean isClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        ivIcon = findViewById(R.id.ivIcon);
       /* showProgressDialog(this);
        makeHttpCall(this, Apis.HOME_PAGE, getRetrofitInterface().getHomePageData());*/

        findViewById(R.id.tvLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        tvSignUp = findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(v -> {

            showTooltip();

        });

        findViewById(R.id.tvSkip).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });
    }

    private void showTooltip() {
        try {
            isClicked = true;
            SimpleTooltip build = new SimpleTooltip.Builder(this)
                    .anchorView(tvSignUp)
                    //.text("Recuerda que primero debes realizar el registro fisico para acceder a las funcionalidades de la app. Para informacion de numeros de contacto visita vallefood.co ")
                    .gravity(Gravity.TOP)
                    .dismissOnInsideTouch(false)
                    .textColor(getResources().getColor(R.color.white))
                    .animated(true)
                    .highlightShape(OverlayView.HIGHLIGHT_SHAPE_RECTANGULAR)
                    .transparentOverlay(false)
                    .contentView(R.layout.warning_message_layout)
                    .build();
            build.findViewById(R.id.btnOk).setOnClickListener(v2 -> {
                if (build.isShowing()) {
                    build.dismiss();
                }
                startActivity(new Intent(SliderActivity.this, RegisterActivity.class));
            });

            build.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(SliderActivity.this);
        Toast.makeText(SliderActivity.this, "" + responce, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(SliderActivity.this);
        Toast.makeText(SliderActivity.this, getResources().getString(R.string.failure_msg), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        exitFromApp(SliderActivity.this);
    }
}
