package com.valle.resturantfoodieapp.fragment;


import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.activity.HomeTabActivity;
import com.valle.resturantfoodieapp.base.BaseFragment;
import com.valle.resturantfoodieapp.models.OrderPlacedModel;
import com.valle.resturantfoodieapp.models.OrderUpdateModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.valle.resturantfoodieapp.utils.RoundedImageView;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;

public class CustomerOrderDetailFragment extends BaseFragment implements NetworkResponceListener {

    private OrderPlacedModel.responseData.orders_InfoData orders_infoData;

    @BindView(R.id.ivCOrderImage)
    RoundedImageView ivCOrderImage;

    @BindView(R.id.tvCOrderNumber)
    AppCompatTextView tvCOrderNumber;

    @BindView(R.id.tvCOrderAmount)
    AppCompatTextView tvCOrderAmount;

    @BindView(R.id.tvCNoOfItems)
    AppCompatTextView tvCNoOfItems;

    @BindView(R.id.tvPaymentType)
    AppCompatTextView tvPaymentType;

    @BindView(R.id.tvCOrderDate)
    AppCompatTextView tvCOrderDate;

    @BindView(R.id.tvCustomerName)
    AppCompatTextView tvCustomerName;

    @BindView(R.id.tvCustomerAddress)
    AppCompatTextView tvCustomerAddress;

    @BindView(R.id.tvCustomerPhoneNumver)
    AppCompatTextView tvCustomerPhoneNumver;

    @BindView(R.id.llOrderedItems)
    LinearLayoutCompat llOrderedItems;

    @BindView(R.id.tvItemsTotal)
    AppCompatTextView tvItemsTotal;

    @BindView(R.id.tvDeliveryFees)
    AppCompatTextView tvDeliveryFees;

    @BindView(R.id.tvServiceTax)
    AppCompatTextView tvServiceTax;

    @BindView(R.id.tvDiscount)
    AppCompatTextView tvDiscount;

    @BindView(R.id.tvGrandTotal)
    AppCompatTextView tvGrandTotal;

    @BindView(R.id.tvAssistance)
    AppCompatTextView tvAssistance;

    @BindView(R.id.tvOrderReady)
    AppCompatTextView tvOrderReady;

    @BindView(R.id.llMain)
    LinearLayoutCompat llMain;

    @BindView(R.id.tvCustomerNote)
    AppCompatTextView tvCustomerNote;

    private Timer timer;

    private String adminNumber = "";

    public CustomerOrderDetailFragment(OrderPlacedModel.responseData.orders_InfoData orders_infoData) {
        this.orders_infoData = orders_infoData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customer_order_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindView(this, view);
        setData();
    }

    /*bundle.putString("isFrom", "OrderHistory");*/
    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onResume() {
        super.onResume();

        String isFrom = getArguments().getString("isFrom");
        tvAssistance.setVisibility(isFrom.equalsIgnoreCase("OrderHistory") ? View.GONE : View.VISIBLE);
        tvOrderReady.setVisibility((isFrom.equalsIgnoreCase("OrderHistory") || isFrom.equalsIgnoreCase("OrderReadyAndPickedUp")) ? View.GONE : View.VISIBLE);

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(() -> {

                    try {
                        if (CommonUtils.isNetworkAvailable(getActivity())) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("Id", orders_infoData.Id);
                            makeHttpCall(CustomerOrderDetailFragment.this, Apis.ORDER_UPDATE, getRetrofitInterface().orderUpdate(jsonObject.toString()));
                        } else {
                            showSnakBar(llMain);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 15000);

        /*if (CommonUtils.isNetworkAvailable(getActivity())) {
            makeHttpCall(this, Apis.ADMIN_CONTACT, getRetrofitInterface().getAdminContact());
        }*/
    }

    private void setData() {
        if (orders_infoData == null) {
            return;
        }

        try {
            if (!TextUtils.isEmpty(orders_infoData.Customer_Id.Profile_Image)) {
                Glide.with(getActivity()).load(Apis.API_URL_FILE + orders_infoData.Customer_Id.Profile_Image).into(ivCOrderImage);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        tvCOrderNumber.setText(getResources().getString(R.string.order_number) + orders_infoData.Order_Number);
        tvCOrderAmount.setText(getResources().getString(R.string.order_amount) + orders_infoData.Grand_Total);
        tvPaymentType.setText(getResources().getString(R.string.payment_type) + (orders_infoData.Payment_Type.equalsIgnoreCase("Cash") ? "Efectivo" : orders_infoData.Payment_Type));
        tvCOrderDate.setText(orders_infoData.createdDtm);
        tvCustomerName.setText(orders_infoData.Customer_Id.Full_Name);
        tvCustomerAddress.setText(orders_infoData.Ordered_Items.Delivery_Address.Address);
        tvCustomerPhoneNumver.setText(orders_infoData.Customer_Id.Mobile);

        if (llOrderedItems.getChildCount() > 0) {
            llOrderedItems.removeAllViews();
        }

        int itemTotal = 0;
        List<OrderPlacedModel.responseData.orders_InfoData.Ordered_ItemsData.ItemsData> items = orders_infoData.Ordered_Items.Items;
        for (int i = 0; i < items.size(); i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.cutom_view_for_ordered_items, null, false);

            AppCompatTextView tvItemNameAndCount = view.findViewById(R.id.tvItemNameAndCount);
            AppCompatTextView tvItemPrice = view.findViewById(R.id.tvItemPrice);
            AppCompatTextView tvFlavor = view.findViewById(R.id.tvFlavor);
            LinearLayoutCompat llFlavor=view.findViewById(R.id.llFlavor);

            // tvItemNameAndCount.setText(items.get(i).Item_Name + " x" + items.get(i).Quantity);
            tvItemNameAndCount.append(CommonUtils.getColoredString(getActivity(), "" + (i + 1) + ": ", getResources().getColor(R.color.foodie_color)));
            tvItemNameAndCount.append(CommonUtils.getColoredString(getActivity(), items.get(i).Item_Name, ContextCompat.getColor(getActivity(), R.color.light_black)));
            tvItemNameAndCount.append(CommonUtils.getColoredString(getActivity(), "  x" + items.get(i).Quantity, ContextCompat.getColor(getActivity(), R.color.colorAccent)));
            int itemsNo = Integer.parseInt(items.get(i).Quantity);
            int price = Integer.parseInt(items.get(i).Item_Price);

            int finalPrice = itemsNo * price;
            itemTotal = itemTotal + finalPrice;
            tvItemPrice.setText("$" + String.valueOf(finalPrice));
            String ItemFlavor = items.get(i).Item_Flavor_Type;
            llFlavor.setVisibility(!TextUtils.isEmpty(ItemFlavor) ? View.VISIBLE : View.GONE);
            if (!TextUtils.isEmpty(ItemFlavor)) {
                tvFlavor.setText("sabor: " + ItemFlavor);
            }
            llOrderedItems.addView(view);
        }

        int itemsCount = 0;
        List<OrderPlacedModel.responseData.orders_InfoData.Ordered_ItemsData.ItemsData> data = orders_infoData.Ordered_Items.Items;
        for (int i = 0; i < data.size(); i++) {
            int temp = Integer.parseInt(data.get(i).Quantity);
            itemsCount = itemsCount + temp;
        }

        tvCNoOfItems.setText(itemsCount + getResources().getString(R.string.items));

        tvItemsTotal.setText("$" + String.valueOf(itemTotal));
        tvDeliveryFees.setText("$" + orders_infoData.Ordered_Items.Delivery_Charges);
        tvServiceTax.setText("$" + orders_infoData.Ordered_Items.Tax);
        tvDiscount.setText(!TextUtils.isEmpty(orders_infoData.Ordered_Items.Discount_Amount) ? "-$" + orders_infoData.Ordered_Items.Discount_Amount : "-$0");
        tvGrandTotal.setText(getResources().getString(R.string.grand_total) + orders_infoData.Grand_Total);

        tvCustomerNote.setVisibility(!TextUtils.isEmpty(orders_infoData.Custom_Note) ? View.VISIBLE : View.GONE);
        tvCustomerNote.setText(!TextUtils.isEmpty(orders_infoData.Custom_Note) ? getResources().getString(R.string.customer_note) + " " + orders_infoData.Custom_Note : "");
    }

    @OnClick(R.id.tvOrderReady)
    void OnClicktvOrderReady() {

        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Id", orders_infoData.Id);

            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("Order_Status", "Order Ready");
            if (CommonUtils.isNetworkAvailable(getActivity())) {
                showProgressDialog(getActivity());
                makeHttpCall(this, Apis.READY_ORDER, getRetrofitInterface().readyOrder(jsonObject.toString(), jsonObject1.toString()));
            } else {
                showSnakBar(llMain);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.tvAssistance)
    void OnClicktvAssistance() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_assistance);
        dialog.setCancelable(true);
        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.setCancelable(true);
        dialog.getWindow().setLayout((6 * width) / 7, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        AppCompatImageView ivClose = dialog.findViewById(R.id.ivClose);
        AppCompatTextView tvCallDeliveryBoy = dialog.findViewById(R.id.tvCallDeliveryBoy);
        AppCompatTextView tvCallAppAdmin = dialog.findViewById(R.id.tvCallAppAdmin);
        ivClose.setOnClickListener(v -> {
            dialog.dismiss();
        });

        tvCallDeliveryBoy.setOnClickListener(v -> {

            if (TextUtils.isEmpty(orders_infoData.Delivery_Boy_Id.Mobile)) {
                Toast.makeText(getActivity(), getResources().getString(R.string.wait_delivery_msg), Toast.LENGTH_SHORT).show();
            } else {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 123);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + orders_infoData.Delivery_Boy_Id.Mobile));
                    startActivity(intent);
                }
            }

        });

        tvCallAppAdmin.setOnClickListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putString("order_id", orders_infoData.Id);
            bundle.putString("order_number", orders_infoData.Order_Number);

            ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new HelpAndSupportFragment(), bundle);

       /*     if (TextUtils.isEmpty(adminNumber)) {
                Toast.makeText(getActivity(), getResources().getString(R.string.please_wait_msg), Toast.LENGTH_SHORT).show();
            } else {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 123);
                } else {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + adminNumber));
                    startActivity(intent);
                }
            }*/

        });

        dialog.show();
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());
        switch (url) {
            case Apis.READY_ORDER:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        getActivity().onBackPressed();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.ORDER_UPDATE:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        OrderUpdateModel orderUpdateModel = new Gson().fromJson(responce, OrderUpdateModel.class);
                        orders_infoData = orderUpdateModel.response.orders_Info.get(0);
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.ADMIN_CONTACT:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    /*{
                        "status": "success",
                            "response": {
                        "msg": "",
                                "Contact_Number": {
                            "Contact_Number": "9999999999"
                        }
                    }
                    }*/

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {

                        adminNumber = jsonObject.getJSONObject("response").getJSONObject("Contact_Number").getString("Contact_Number");

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
}
