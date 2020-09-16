package com.valle.resturantfoodieapp.fragment;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.adapter.OrderPreparingLIstAdapter;
import com.valle.resturantfoodieapp.base.BaseFragment;
import com.valle.resturantfoodieapp.models.OrderConfirmedModel;
import com.valle.resturantfoodieapp.models.OrderPlacedModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.prefs.SharedPrefModule;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;

public class OrderPreparingFragment extends BaseFragment implements NetworkResponceListener, OrderPreparingLIstAdapter.OnReadyOrder {

    @BindView(R.id.rvOrderPreparing)
    RecyclerView rvOrderPreparing;

    @BindView(R.id.llNoOrderYet)
    LinearLayoutCompat llNoOrderYet;

    @BindView(R.id.rlMain)
    RelativeLayout rlMain;

    private OrderPreparingLIstAdapter orderPreparingLIstAdapter;
    private ArrayList<OrderPlacedModel.responseData.orders_InfoData> orderPlacedModelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_preparing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(this, view);

        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvOrderPreparing.setLayoutManager(verticalLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreparingOrdersOrders();
    }

    private void getPreparingOrdersOrders() {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Rest_Id", new SharedPrefModule(getActivity()).getUserId());
            jsonObject.put("Order_Status", "Order Confirmed");

            if (CommonUtils.isNetworkAvailable(getActivity())) {
                showProgressDialog(getActivity());
                makeHttpCall(this, Apis.UNDER_READY_ORDERS, getRetrofitInterface().getOrders(jsonObject.toString(), "0"));
            }else {
                showSnakBar(rlMain);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());
        switch (url) {
            case Apis.UNDER_READY_ORDERS:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        OrderPlacedModel orderPlacedModel = new Gson().fromJson(responce, OrderPlacedModel.class);
                        orderPlacedModelList = new ArrayList<>();
                        orderPlacedModelList.addAll(orderPlacedModel.response.orders_Info);
                        orderPreparingLIstAdapter = new OrderPreparingLIstAdapter(getActivity(), this, orderPlacedModelList);
                        rvOrderPreparing.setAdapter(orderPreparingLIstAdapter);
                        handleVisiblity(orderPlacedModelList.size() > 0);
                    } else {
                        handleVisiblity(false);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.READY_ORDER:

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

                        orderPreparingLIstAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    @Override
    public void onFailure(String url, Throwable throwable) {

    }


    private void handleVisiblity(boolean isVisible) {
        llNoOrderYet.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        rvOrderPreparing.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }


    @Override
    public void OnReady(OrderPlacedModel.responseData.orders_InfoData ordersInfoData) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", ordersInfoData.Id);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("Order_Status", "Order Ready");

            if (CommonUtils.isNetworkAvailable(getActivity())) {
                showProgressDialog(getActivity());
                makeHttpCall(this, Apis.READY_ORDER, getRetrofitInterface().readyOrder(jsonObject.toString(), jsonObject1.toString()));
            } else {
                showSnakBar(rlMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
