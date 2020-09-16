package com.valle.resturantfoodieapp.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.activity.HomeTabActivity;
import com.valle.resturantfoodieapp.adapter.FilterListAdapter;
import com.valle.resturantfoodieapp.adapter.ResturantUpcomingOrderAdapter;
import com.valle.resturantfoodieapp.adapter.TimeEstimationAdapter;
import com.valle.resturantfoodieapp.base.BaseFragment;
import com.valle.resturantfoodieapp.models.FilterHistoryBean;
import com.valle.resturantfoodieapp.models.LoginModel;
import com.valle.resturantfoodieapp.models.OrderConfirmedModel;
import com.valle.resturantfoodieapp.models.OrderPlacedModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.prefs.SharedPrefModule;
import com.valle.resturantfoodieapp.services.ForeGroundService;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.valle.resturantfoodieapp.utils.RoundRectCornerImageView;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeScreenFragment extends BaseFragment implements ResturantUpcomingOrderAdapter.OnConfirmOrder, TimeEstimationAdapter.OnTimeSelectedListener, NetworkResponceListener {

    @BindView(R.id.rvOrdersList)
    RecyclerView rvOrdersList;

    @BindView(R.id.ivRestImage)
    RoundRectCornerImageView ivRestImage;

    @BindView(R.id.tvRestaurantName)
    AppCompatTextView tvRestaurantName;

    @BindView(R.id.tvRestaurantPhone)
    AppCompatTextView tvRestaurantPhone;

    @BindView(R.id.tvRestaurantAddress)
    AppCompatTextView tvRestaurantAddress;

    @BindView(R.id.tvCurrentDate)
    AppCompatTextView tvCurrentDate;

    @BindView(R.id.swPresense)
    Switch swPresense;

    @BindView(R.id.llNoOrderYet)
    LinearLayoutCompat llNoOrderYet;

    @BindView(R.id.llMain)
    LinearLayoutCompat llMain;

    @BindView(R.id.tvRating)
    AppCompatTextView tvRating;

    @BindView(R.id.ratingBar)
    RatingBar ratingBar;

    private String lastOrderId = "0";

    private final String ON = "on";
    private final String OFF = "off";
    private String presense = "";

    String[] timeEstimationList = {"5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60"};
    public static String time = "15";
    private TimeEstimationAdapter timeEstimationAdapter;
    private ResturantUpcomingOrderAdapter resturantUpcomingOrderAdapter;
    private List<OrderPlacedModel.responseData.orders_InfoData> orderPlacedModelList;
    private Timer timer;
    private Timer timerForPresence;
    private Timer ratingTimer;

    private boolean bPresenceOnOFF = false;

    private boolean checkPresenceThread = false;

    private boolean getResponseData = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);

        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        rvOrdersList.setLayoutManager(verticalLayoutManager);

        swPresense.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                timerForPresence.cancel();
                checkPresenceThread = false;
            } catch (Exception e) {
                e.printStackTrace();
            }

            JSONObject jsonObject = new JSONObject();
            JSONObject jsonObject1 = new JSONObject();
            try {
                jsonObject.put("User_Id", new SharedPrefModule(getActivity()).getUserId());

                if (isChecked) {
                    presense = ON;
                    jsonObject1.put("Set_Your_Presence", "ON");
                    if (CommonUtils.isNetworkAvailable(getActivity())) {
                        showProgressDialog(getActivity());
                        makeHttpCall(HomeScreenFragment.this, Apis.SET_USER_PRESENCE, getRetrofitInterface().setUserPresense(jsonObject.toString(), jsonObject1.toString()));
                    } else {
                        showSnakBar(llMain);
                    }
                } else {
                    presense = OFF;
                    jsonObject1.put("Set_Your_Presence", "OFF");
                    if (CommonUtils.isNetworkAvailable(getActivity())) {
                        showProgressDialog(getActivity());
                        makeHttpCall(HomeScreenFragment.this, Apis.SET_USER_PRESENCE, getRetrofitInterface().setUserPresense(jsonObject.toString(), jsonObject1.toString()));
                    } else {
                        showSnakBar(llMain);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        });

    }

    @OnClick(R.id.ivRestImage)
    void OnClickIvRestImage() {
        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new UpdateProfileFragment(), null);
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        ratingTimer.cancel();
        timerForPresence.cancel();
        checkPresenceThread = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        getResponseData = true;
        timer = new Timer();
        ratingTimer = new Timer();
        lastOrderId = "0";
        orderPlacedModelList = new ArrayList<>();
        resturantUpcomingOrderAdapter = new ResturantUpcomingOrderAdapter(getActivity(), this, orderPlacedModelList);
        rvOrdersList.setAdapter(resturantUpcomingOrderAdapter);
        setData();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    if (CommonUtils.isNetworkAvailable(getActivity())) {

                        String userPresence = new SharedPrefModule(getActivity()).getUserPresence();
                        if (!TextUtils.isEmpty(userPresence)) {
                            if (getResponseData) {
                                getResponseData = false;
                                getStartTakingOrders();
                            }
                        }
                    } else {
                        showSnakBar(llMain);
                    }

                });
            }
        }, 0, 10000);

        ratingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {
                    if (CommonUtils.isNetworkAvailable(getActivity())) {
                        getUpdatedRating();
                    }
                });
            }
        }, 0, 60000);
        startPresenceTimer();
    }

    private void startPresenceTimer() {
        try {

            timerForPresence = new Timer();
            timerForPresence.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (getActivity() != null)
                        getActivity().runOnUiThread(() -> {
                            checkPresenceThread = true;
                            if (CommonUtils.isNetworkAvailable(getActivity())) {
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("User_Id", new SharedPrefModule(getActivity()).getUserId());

                                    makeHttpCall(HomeScreenFragment.this, Apis.TOP_DISCOUNTED_RESTAURANT, getRetrofitInterface().getRestaurantInfo(jsonObject.toString()));

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        });
                }
            }, 0, 15000);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData() {

        String userLoginResponseData = new SharedPrefModule(getActivity()).getUserLoginResponseData();
        if (TextUtils.isEmpty(userLoginResponseData)) {
            return;
        }

        LoginModel.responseData.UserInfoData userInfoData = new Gson().fromJson(userLoginResponseData, LoginModel.responseData.UserInfoData.class);

        tvRestaurantName.setText(userInfoData.Restaurant_Name);
        tvRestaurantPhone.setText(userInfoData.Mobile);
        tvRestaurantAddress.setText(userInfoData.Address);
        tvCurrentDate.setText(CommonUtils.getCurrentDate());

        try {
            Glide.with(getActivity()).load(Apis.API_URL_FILE + userInfoData.Profile_Image).placeholder(getResources().getDrawable(R.drawable.upload)).into(ivRestImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String userPresence = new SharedPrefModule(getActivity()).getUserPresence();

        if (!TextUtils.isEmpty(userPresence)) {
            swPresense.setChecked(true);
        }
    }

    public void OnClickPreparingTime(OrderPlacedModel.responseData.orders_InfoData ordersInfoData) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_time_estimation);
        dialog.setCancelable(true);
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.setCancelable(true);
        dialog.getWindow().setLayout((6 * width) / 7, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;

        RecyclerView rvTimeEstimation = dialog.findViewById(R.id.rvTimeEstimation);
        Button btConfirm = dialog.findViewById(R.id.btConfirm);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvTimeEstimation.setLayoutManager(verticalLayoutManager);
        timeEstimationAdapter = new TimeEstimationAdapter(getActivity(), timeEstimationList, this);
        rvTimeEstimation.setAdapter(timeEstimationAdapter);
        btConfirm.setOnClickListener(v -> {
            dialog.dismiss();
            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("Id", ordersInfoData.Id);

                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("Order_Status", "Order Confirmed");
                jsonObject1.put("Delivery_Time", time);


                if (CommonUtils.isNetworkAvailable(getActivity())) {
                    showProgressDialog(getActivity());
                    makeHttpCall(this, Apis.CONFIRM_ORDER, getRetrofitInterface().confirmOrder(jsonObject.toString(), jsonObject1.toString()));
                } else {
                    showSnakBar(llMain);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        dialog.show();
    }

    @Override
    public void OnConfirm(OrderPlacedModel.responseData.orders_InfoData ordersInfoData) {
        OnClickPreparingTime(ordersInfoData);
    }

    @Override
    public void OnOrderReject(OrderPlacedModel.responseData.orders_InfoData ordersInfoData) {
        new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.alert)).
                setMessage(getResources().getString(R.string.reject_order)).setPositiveButton(
                getResources().getString(R.string.yes), (dialog, which) -> {
                    try {
                        dialog.dismiss();
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("Id", ordersInfoData.Id);

                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("Order_Status", "Order Reject");

                        if (CommonUtils.isNetworkAvailable(getActivity())) {
                            showProgressDialog(getActivity());
                            makeHttpCall(HomeScreenFragment.this, Apis.CONFIRM_ORDER, getRetrofitInterface().confirmOrder(jsonObject.toString(), jsonObject1.toString()));

                        } else {
                            showSnakBar(llMain);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).setNegativeButton(getResources().getString(R.string.no), (dialog, which) -> dialog.dismiss()).create().show();
    }

    @Override
    public void OnTimeSelected(String strTime) {
        time = strTime;
        timeEstimationAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());
        switch (url) {
            case Apis.SET_USER_PRESENCE:

                try {
                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
                        boolean checked = swPresense.isChecked();
                        if (checked) {
                            new SharedPrefModule(getActivity()).setUserPresence("ON");
                        } else {
                            new SharedPrefModule(getActivity()).setUserPresence("");
                        }
                    } else {
                        if (presense.equalsIgnoreCase(ON)) {
                            swPresense.setChecked(false);
                            new SharedPrefModule(getActivity()).setUserPresence("");
                        } else {
                            swPresense.setChecked(true);
                            new SharedPrefModule(getActivity()).setUserPresence("ON");
                        }
                        Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                    startPresenceTimer();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.GET_ORDERS:
                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        OrderPlacedModel orderPlacedModel = new Gson().fromJson(responce, OrderPlacedModel.class);

                        for (int i = 0; i < orderPlacedModel.response.orders_Info.size(); i++) {
                            boolean isAvail = false;
                            for (int j = 0; j < orderPlacedModelList.size(); j++) {
                                if (orderPlacedModel.response.orders_Info.get(i).Order_Number.equalsIgnoreCase(orderPlacedModelList.get(j).Order_Number)) {
                                    isAvail = true;
                                }
                            }
                            if (!isAvail) {
                                orderPlacedModelList.add(orderPlacedModel.response.orders_Info.get(i));
                            }
                        }

                        if (orderPlacedModelList.size() > 0) {
                            lastOrderId = orderPlacedModelList.get(orderPlacedModelList.size() - 1).Id;
                        }
                        handleVisiblity(orderPlacedModelList.size() > 0);
                        resturantUpcomingOrderAdapter.notifyDataSetChanged();
                    } else if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.ERROR)) {
                        handleVisiblity(orderPlacedModelList.size() > 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getResponseData = true;
                break;

            case Apis.CONFIRM_ORDER:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        OrderConfirmedModel orderConfirmedModel = new Gson().fromJson(responce, OrderConfirmedModel.class);

                        for (int i = 0; i < orderPlacedModelList.size(); i++) {
                            if (orderPlacedModelList.get(i).Id.equalsIgnoreCase(orderConfirmedModel.response.orders_Info.Id)) {
                                orderPlacedModelList.remove(i);
                                break;
                            }
                        }
                        resturantUpcomingOrderAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case Apis.REST_RATING:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                   /* {
                        "status": "success",
                            "response": {
                        "msg": "",
                                "total_rating": 3
                    }
                    }*/

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        String totalRating = jsonObject.getJSONObject("response").getString("total_rating");

                        tvRating.setText(totalRating + "/5");
                        ratingBar.setRating(Float.parseFloat(totalRating));
                    } else {
                        tvRating.setText("0/5");
                        ratingBar.setRating(0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.TOP_DISCOUNTED_RESTAURANT:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {

                        bPresenceOnOFF = jsonObject.getJSONObject("response").getJSONArray("response").getJSONObject(0).getString("Set_Your_Presence").equalsIgnoreCase("ON");
                        if (checkPresenceThread) {
                            if (bPresenceOnOFF) {
                                swPresense.setChecked(true);
                                new SharedPrefModule(getActivity()).setUserPresence("ON");
                            } else {
                                swPresense.setChecked(false);
                                new SharedPrefModule(getActivity()).setUserPresence("");
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private void handleVisiblity(boolean isVisible) {
        llNoOrderYet.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        rvOrdersList.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    private void getUpdatedRating() {

        try {
            String restId = new SharedPrefModule(getActivity()).getUserId();
            makeHttpCall(this, Apis.REST_RATING, getRetrofitInterface().getRestRating(restId));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStartTakingOrders() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Rest_Id", new SharedPrefModule(getActivity()).getUserId());
            jsonObject.put("Order_Status", "Order Placed");

            if (CommonUtils.isNetworkAvailable(getActivity())) {
                makeHttpCall(this, Apis.GET_ORDERS, getRetrofitInterface().getOrders(jsonObject.toString(), lastOrderId));
            } else {
                showSnakBar(llMain);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(getActivity());
        switch (url) {
            case Apis.SET_USER_PRESENCE:
                Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                if (presense.equalsIgnoreCase(ON)) {
                    swPresense.setChecked(false);
                    new SharedPrefModule(getActivity()).setUserPresence("");
                } else {
                    swPresense.setChecked(true);
                    new SharedPrefModule(getActivity()).setUserPresence("ON");
                }
                break;

            case Apis.GET_ORDERS:
                getResponseData = true;
                break;
        }
    }
}





/*

    try {
            if (!CommonUtils.isMyServiceRunning(getActivity(), ForeGroundService.class)) {
        Intent i = new Intent(getActivity(), ForeGroundService.class);
        getActivity().startService(i);
        }
        } catch (Exception e) {
        e.printStackTrace();
        }
        try {
        if (CommonUtils.isMyServiceRunning(getActivity(), ForeGroundService.class)) {
        Intent i = new Intent(getActivity(), ForeGroundService.class);
        getActivity().stopService(i);
        }
        } catch (Exception e) {
        e.printStackTrace();
        }*/
