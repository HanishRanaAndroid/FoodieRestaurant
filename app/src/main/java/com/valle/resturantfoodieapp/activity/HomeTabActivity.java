package com.valle.resturantfoodieapp.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.base.BaseActivity;
import com.valle.resturantfoodieapp.fragment.HomeScreenFragment;
import com.valle.resturantfoodieapp.fragment.OrderHistoryFragment;
import com.valle.resturantfoodieapp.fragment.OrderPreparingFragment;
import com.valle.resturantfoodieapp.fragment.ResturantMenuListFragment;
import com.valle.resturantfoodieapp.fragment.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.valle.resturantfoodieapp.services.ForeGroundService;

import java.util.Objects;

import butterknife.OnClick;

public class HomeTabActivity extends BaseActivity {

    BottomNavigationView bottomNavigationView;
    public FragmentManager fragmentManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_tab);
        bindView(this);
        fragmentManager = getSupportFragmentManager();
        bottomNavigationView = findViewById(R.id.navigation);
        addFragment(new HomeScreenFragment());
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.action_item1:
                    selectedFragment = new HomeScreenFragment();
                    break;
                case R.id.action_item2:
                    selectedFragment = new ResturantMenuListFragment();
                    break;
                case R.id.action_item3:
                    selectedFragment = new OrderHistoryFragment();
                    break;
                case R.id.action_item4:
                    selectedFragment = new SettingFragment();
                    break;
            }
            replaceFragment(selectedFragment, null);
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name_refresh"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    @OnClick(R.id.tvOrderPreparing)
    void OnClicktvOrderPreparing() {
        replaceFragmentWithBackStack(new OrderPreparingFragment(), null);
    }

    public void replaceFragmentWithBackStack(Fragment fragment, Bundle bundle) {
        String backStateName = fragment.getClass().getName();
        if (!fragmentManager.popBackStackImmediate(backStateName, 0)) {
            FragmentTransaction ft = fragmentManager.beginTransaction();
            fragment.setArguments(bundle);
            ft.replace(R.id.frame_layout, fragment);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private void callExitAPP() {
        new AlertDialog.Builder(HomeTabActivity.this).setTitle(getResources().getString(R.string.exit_msg))
                .setMessage(getResources().getString(R.string.do_you_want_exit)).setPositiveButton(getResources().getString(R.string.yes), (dialog, which) -> {
            dialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }).setNegativeButton(getResources().getString(R.string.no_msg), (dialog, which) -> dialog.dismiss()).create().show();
    }

    public void addFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public void replaceFragment(Fragment fragment, Bundle bundle) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        getFragmentCallBack();
    }

    public void getFragmentCallBack() {
        Fragment fr = fragmentManager.findFragmentById(R.id.frame_layout);
        if ((fr instanceof HomeScreenFragment) || (fr instanceof ResturantMenuListFragment) || (fr instanceof OrderHistoryFragment) || (fr instanceof SettingFragment)) {
            callExitAPP();
        } else {
            fragmentManager.popBackStackImmediate();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Objects.requireNonNull(fragmentManager.findFragmentById(R.id.frame_layout)).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void alertRestaurantOnChangedOrder(String message) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_order_changed);
        dialog.setCancelable(false);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width) / 7, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        AppCompatTextView tvMessage = dialog.findViewById(R.id.tvMessage);
        tvMessage.setText(Html.fromHtml(message));
        AppCompatButton btMessOk = dialog.findViewById(R.id.btMessOk);
        btMessOk.setOnClickListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            runOnUiThread(() -> {
                alertRestaurantOnChangedOrder(message);
            });
        }
    };

}
