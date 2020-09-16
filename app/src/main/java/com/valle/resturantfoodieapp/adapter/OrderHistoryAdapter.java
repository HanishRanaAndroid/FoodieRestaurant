package com.valle.resturantfoodieapp.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.activity.HomeTabActivity;
import com.valle.resturantfoodieapp.fragment.CustomerOrderDetailFragment;
import com.valle.resturantfoodieapp.models.OrderPlacedModel;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.ViewHolder> {

    private Context context;
    private OrderPlacedModel orderPlacedModel;
    private ArrayList<OrderPlacedModel.responseData.orders_InfoData> orderPlacedModelList;

    public OrderHistoryAdapter(Context context, OrderPlacedModel orderPlacedModel) {
        this.context = context;
        this.orderPlacedModel = orderPlacedModel;
        orderPlacedModelList = new ArrayList<>();
        orderPlacedModelList.addAll(orderPlacedModel.response.orders_Info);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_layout_order_history, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderPlacedModel.responseData.orders_InfoData ordersInfoData = orderPlacedModelList.get(position);
        holder.tvOrderNumber.setText(context.getResources().getString(R.string.order_number) + ordersInfoData.Order_Number);

        try {

            if (holder.llOrderItems.getChildCount() > 0) {
                holder.llOrderItems.removeAllViews();
            }

            for (int i = 0; i < ordersInfoData.Ordered_Items.Items.size(); i++) {
                View view = LayoutInflater.from(context).inflate(R.layout.custom_item_view, null, false);
                AppCompatTextView tvOrderItems = view.findViewById(R.id.tvOrderItems);
                AppCompatTextView tvFlavor = view.findViewById(R.id.tvFlavor);
                LinearLayoutCompat llFlavor = view.findViewById(R.id.llFlavor);
                tvOrderItems.append(CommonUtils.getColoredString(context, ordersInfoData.Ordered_Items.Items.get(i).Item_Name, ContextCompat.getColor(context, R.color.light_black)));
                tvOrderItems.append(CommonUtils.getColoredString(context, "  x" + ordersInfoData.Ordered_Items.Items.get(i).Quantity, ContextCompat.getColor(context, R.color.colorAccent)));
                String ItemFlavor = ordersInfoData.Ordered_Items.Items.get(i).Item_Flavor_Type;
                llFlavor.setVisibility(!TextUtils.isEmpty(ItemFlavor) ? View.VISIBLE : View.GONE);
                if (!TextUtils.isEmpty(ItemFlavor)) {
                    tvFlavor.setText("sabor: " + ItemFlavor);
                }
                holder.llOrderItems.addView(view);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JSONObject jsonObject = new JSONObject(new Gson().toJson(ordersInfoData.Is_Rating));
            if (jsonObject.has("Rating")) {
                holder.ratingBar.setRating(Float.valueOf(ordersInfoData.Is_Rating.Rating));
            } else {
                holder.ratingBar.setRating(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvOrderDate.setText(ordersInfoData.createdDtm);
        holder.tvOrderAmount.setText(context.getResources().getString(R.string.order_amount) + ordersInfoData.Grand_Total);
        holder.llOrderHistory.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("isFrom", "OrderHistory");
            ((HomeTabActivity) context).replaceFragmentWithBackStack(new CustomerOrderDetailFragment(ordersInfoData), bundle);
        });
    }

    public void filterData(String data) {

        if (orderPlacedModel.response.orders_Info.size() == 0) {
            return;
        }

        orderPlacedModelList.clear();
        if (!TextUtils.isEmpty(data)) {

            for (int i = 0; i < orderPlacedModel.response.orders_Info.size(); i++) {
                if (orderPlacedModel.response.orders_Info.get(i).Order_Number.toLowerCase().contains(data.toLowerCase())) {
                    orderPlacedModelList.add(orderPlacedModel.response.orders_Info.get(i));
                }
            }
        } else {
            orderPlacedModelList.addAll(orderPlacedModel.response.orders_Info);
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return orderPlacedModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayoutCompat llOrderHistory, llOrderItems;
        private AppCompatTextView tvOrderNumber, tvOrderDate, tvOrderAmount;
        private RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            llOrderHistory = itemView.findViewById(R.id.llOrderHistory);
            llOrderItems = itemView.findViewById(R.id.llOrderItems);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderAmount = itemView.findViewById(R.id.tvOrderAmount);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }

}
