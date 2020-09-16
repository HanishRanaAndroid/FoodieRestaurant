package com.valle.resturantfoodieapp.fragment;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.valle.resturantfoodieapp.activity.HomeTabActivity;
import com.valle.resturantfoodieapp.adapter.ResturantMenuItemListAdapter;
import com.valle.resturantfoodieapp.base.BaseFragment;
import com.valle.resturantfoodieapp.models.MenuItemListModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.prefs.SharedPrefModule;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

public class ResturantMenuListFragment extends BaseFragment implements NetworkResponceListener, ResturantMenuItemListAdapter.ItemStockAvaliablity {

    @BindView(R.id.rvRestaurantMenu)
    RecyclerView rvRestaurantMenu;

    @BindView(R.id.etMenuSearch)
    AppCompatEditText etMenuSearch;

    @BindView(R.id.ivMenuClearText)
    AppCompatImageView ivMenuClearText;

    private ResturantMenuItemListAdapter resturantMenuItemListAdapter;

    @BindView(R.id.tvNoMenuListFound)
    AppCompatTextView tvNoMenuListFound;

    @BindView(R.id.rlMain)
    RelativeLayout rlMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_resturant_menu_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);
        LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        rvRestaurantMenu.setLayoutManager(verticalLayoutManager);

        etMenuSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (resturantMenuItemListAdapter == null) {
                    return;
                }
                resturantMenuItemListAdapter.filterData(s.toString());
                if (TextUtils.isEmpty(s.toString())) {
                    ivMenuClearText.setVisibility(View.GONE);
                } else {
                    ivMenuClearText.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.MENU_ITEM_LIST, getRetrofitInterface().getMenuListItems(new SharedPrefModule(getActivity()).getUserId()));
        }else {
            showSnakBar(rlMain);
        }
    }

    @OnClick(R.id.fbAddMenuItem)
    void OnClickfbAddMenuItem() {
        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new AddMenuItemFragment(), null);
    }

    @OnClick(R.id.ivMenuClearText)
    void OnClickivClearText() {
        etMenuSearch.setText("");
        ivMenuClearText.setVisibility(View.GONE);
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());

        switch (url) {

            case Apis.MENU_ITEM_LIST:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {

                        MenuItemListModel menuItemListModel = new Gson().fromJson(responce, MenuItemListModel.class);
                        resturantMenuItemListAdapter = new ResturantMenuItemListAdapter(getActivity(), menuItemListModel, this);
                        rvRestaurantMenu.setAdapter(resturantMenuItemListAdapter);
                        handleVisiblity(menuItemListModel.response.Items.size() > 0);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.ITEM_STOCK:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        Toast.makeText(getActivity(), jsonObject.getJSONObject("response").getString("msg"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

        }
    }

    private void handleVisiblity(boolean isVisible) {
        tvNoMenuListFound.setVisibility(isVisible ? View.GONE : View.VISIBLE);
        rvRestaurantMenu.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onFailure(String url, Throwable throwable) {
        hideProgressDialog(getActivity());
        switch (url) {

            case Apis.MENU_ITEM_LIST:

                handleVisiblity(false);

                break;
        }
    }

    @Override
    public void ItemInStock(String itemId, String status) {

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.ITEM_STOCK, getRetrofitInterface().updateItemStock(itemId, status));
        }else {
            showSnakBar(rlMain);
        }

    }

    @Override
    public void ItemOutStock(String itemId, String status) {
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.ITEM_STOCK, getRetrofitInterface().updateItemStock(itemId, status));
        }else {
            showSnakBar(rlMain);
        }
    }
}
