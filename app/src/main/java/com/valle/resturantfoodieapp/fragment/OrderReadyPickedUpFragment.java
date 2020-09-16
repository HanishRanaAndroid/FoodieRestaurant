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

import com.google.gson.Gson;
import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.adapter.OrderReadyAndPickedUpLIstAdapter;
import com.valle.resturantfoodieapp.base.BaseFragment;
import com.valle.resturantfoodieapp.models.OrderPlacedModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.prefs.SharedPrefModule;
import com.valle.resturantfoodieapp.utils.CommonUtils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;

public class OrderReadyPickedUpFragment extends BaseFragment implements NetworkResponceListener {

    @BindView(R.id.rvOrderPreparing)
    RecyclerView rvOrderPreparing;

    @BindView(R.id.llNoOrderYet)
    LinearLayoutCompat llNoOrderYet;

    @BindView(R.id.rlMain)
    RelativeLayout rlMain;

    private OrderReadyAndPickedUpLIstAdapter orderPreparingLIstAdapter;
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
        getPickedOrdersList();
    }

    private void getPickedOrdersList() {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Rest_Id", new SharedPrefModule(getActivity()).getUserId());

            if (CommonUtils.isNetworkAvailable(getActivity())) {
                showProgressDialog(getActivity());
                makeHttpCall(this, Apis.GET_PICKEPUP_READY_ORDERS, getRetrofitInterface().getREadyOrders(jsonObject.toString()));
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
            case Apis.GET_PICKEPUP_READY_ORDERS:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        OrderPlacedModel orderPlacedModel = new Gson().fromJson(responce, OrderPlacedModel.class);
                        orderPlacedModelList = new ArrayList<>();
                        orderPlacedModelList.addAll(orderPlacedModel.response.orders_Info);
                        orderPreparingLIstAdapter = new OrderReadyAndPickedUpLIstAdapter(getActivity(),orderPlacedModelList);
                        rvOrderPreparing.setAdapter(orderPreparingLIstAdapter);
                        handleVisiblity(orderPlacedModelList.size() > 0);
                    } else {
                        handleVisiblity(false);
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


    private void handleVisiblity(boolean isVisible) {
        llNoOrderYet.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        rvOrderPreparing.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }


}
