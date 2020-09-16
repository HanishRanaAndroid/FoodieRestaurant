package com.valle.resturantfoodieapp.fragment;


import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.adapter.FilterListAdapter;
import com.valle.resturantfoodieapp.adapter.OrderHistoryAdapter;
import com.valle.resturantfoodieapp.base.BaseFragment;
import com.valle.resturantfoodieapp.models.FilterHistoryBean;
import com.valle.resturantfoodieapp.models.OrderPlacedModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.prefs.SharedPrefModule;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class OrderHistoryFragment extends BaseFragment implements FilterListAdapter.FilterListener, NetworkResponceListener {

    @BindView(R.id.rvOrderHistory)
    RecyclerView rvOrderHistory;

    @BindView(R.id.etSearch)
    AppCompatEditText etSearch;

    @BindView(R.id.ivClearText)
    AppCompatImageView ivClearText;

    @BindView(R.id.tvHistoryNotFound)
    AppCompatTextView tvHistoryNotFound;


    @BindView(R.id.rlMain)
    RelativeLayout rlMain;

    private Dialog dialog;

    public static String filterSelceted = "";
    public static String time = "-1 Days";

    private OrderHistoryAdapter orderHistoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history_adapter, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Rest_Id", new SharedPrefModule(getActivity()).getUserId());
            jsonObject.put("Order_Status", "Order Delivered");

            if (CommonUtils.isNetworkAvailable(getActivity())) {
                showProgressDialog(getActivity());
                makeHttpCall(this, Apis.ORDER_HISTORY, getRetrofitInterface().getOrdersHistory(jsonObject.toString(), time));
            } else {
                showSnakBar(rlMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvOrderHistory.setLayoutManager(verticalLayoutManager);
        filterSelceted = getActivity().getResources().getString(R.string.yesterday);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString())) {
                    ivClearText.setVisibility(View.GONE);
                } else {
                    ivClearText.setVisibility(View.VISIBLE);
                }
                if (orderHistoryAdapter == null) {
                    return;
                }

                orderHistoryAdapter.filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @OnClick(R.id.ivFilter)
    public void OnClickivFilter() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_filter_search);
        dialog.setCancelable(true);
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.setCancelable(true);
        dialog.getWindow().setLayout((6 * width) / 7, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        RecyclerView rvFilterList = dialog.findViewById(R.id.rvFilterList);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvFilterList.setLayoutManager(verticalLayoutManager);
        ArrayList<FilterHistoryBean> filterHistoryBeans = new ArrayList<>();
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.yesterday), "-1"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_five_day), "-5"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_ten_day), "-10"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_twenty_days), "-20"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_one_month), "-30"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_two_month), "-60"));
        filterHistoryBeans.add(new FilterHistoryBean(getResources().getString(R.string.last_three_month), "-90"));
        FilterListAdapter filterListAdapter = new FilterListAdapter(getActivity(), filterHistoryBeans, this);
        rvFilterList.setAdapter(filterListAdapter);
        dialog.show();
    }

    @OnClick(R.id.ivClearText)
    void OnClickivClearText() {
        etSearch.setText("");
        ivClearText.setVisibility(View.GONE);
    }

    @Override
    public void OnFilterSelect(FilterHistoryBean strFilter) {
        filterSelceted = strFilter.getName();
        dialog.dismiss();
        time = strFilter.getTime() + " Days";
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Rest_Id", new SharedPrefModule(getActivity()).getUserId());
            jsonObject.put("Order_Status", "Order Delivered");
            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.ORDER_HISTORY, getRetrofitInterface().getOrdersHistory(jsonObject.toString(), time));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());

        switch (url) {
            case Apis.ORDER_HISTORY:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        try {
                            JSONArray jsonArrayOrderedInfo = jsonObject.getJSONObject("response").getJSONArray("orders_Info");

                            for (int i = 0; i < jsonArrayOrderedInfo.length(); i++) {
                                JSONArray jsonArrayItems = jsonArrayOrderedInfo.getJSONObject(i).getJSONObject("Ordered_Items").getJSONArray("Items");
                                for (int j = 0; j < jsonArrayItems.length(); j++) {
                                    if (!jsonArrayItems.getJSONObject(j).has("Item_Flavor_Type")) {
                                        jsonArrayItems.getJSONObject(j).put("Item_Flavor_Type", "");
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        OrderPlacedModel orderPlacedModel = new Gson().fromJson(responce, OrderPlacedModel.class);

                        orderHistoryAdapter = new OrderHistoryAdapter(getActivity(), orderPlacedModel);
                        rvOrderHistory.setAdapter(orderHistoryAdapter);
                        handleVisibility(orderPlacedModel.response.orders_Info.size() > 0);
                    } else if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.ERROR)) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.order_not_found), Toast.LENGTH_SHORT).show();
                        handleVisibility(false);
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
        switch (url) {
            case Apis.ORDER_HISTORY:
                handleVisibility(false);
                break;
        }
    }

    private void handleVisibility(boolean isVisible) {
        rvOrderHistory.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        tvHistoryNotFound.setVisibility(isVisible ? View.GONE : View.VISIBLE);
    }
}
