package com.valle.resturantfoodieapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.models.OrderPlacedModel;
import com.valle.resturantfoodieapp.utils.CommonUtils;

import java.util.List;

public class ResturantUpcomingOrderAdapter extends RecyclerView.Adapter<ResturantUpcomingOrderAdapter.ViewHolder> {

    private Context context;
    private OnConfirmOrder onConfirmOrder;
    List<OrderPlacedModel.responseData.orders_InfoData> orderPlacedModel;

    public ResturantUpcomingOrderAdapter(Context context, OnConfirmOrder onConfirmOrder, List<OrderPlacedModel.responseData.orders_InfoData> orderPlacedModel) {
        this.context = context;
        this.onConfirmOrder = onConfirmOrder;
        this.orderPlacedModel = orderPlacedModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_layout_upcoming_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderPlacedModel.responseData.orders_InfoData ordersInfoData = orderPlacedModel.get(position);
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

        holder.tvOrderDate.setText(ordersInfoData.createdDtm);
        holder.tvOrderAmount.setText(context.getResources().getString(R.string.order_amount) + ordersInfoData.Grand_Total);
        holder.tvConfirm.setOnClickListener(v -> {
            onConfirmOrder.OnConfirm(ordersInfoData);
        });

        holder.tvRejectOrder.setOnClickListener(v -> {
            onConfirmOrder.OnOrderReject(ordersInfoData);
        });
    }

    @Override
    public int getItemCount() {
        return orderPlacedModel.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvConfirm, tvOrderNumber, tvOrderDate, tvOrderAmount, tvRejectOrder;
        private LinearLayoutCompat llOrderItems;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvConfirm = itemView.findViewById(R.id.tvConfirm);
            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderAmount = itemView.findViewById(R.id.tvOrderAmount);
            llOrderItems = itemView.findViewById(R.id.llOrderItems);
            tvRejectOrder = itemView.findViewById(R.id.tvRejectOrder);
        }
    }

    public interface OnConfirmOrder {
        void OnConfirm(OrderPlacedModel.responseData.orders_InfoData ordersInfoData);

        void OnOrderReject(OrderPlacedModel.responseData.orders_InfoData ordersInfoData);
    }
}
