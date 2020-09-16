package com.valle.resturantfoodieapp.adapter;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.activity.HomeTabActivity;
import com.valle.resturantfoodieapp.fragment.CustomerOrderDetailFragment;
import com.valle.resturantfoodieapp.models.OrderPlacedModel;
import com.valle.resturantfoodieapp.utils.CommonUtils;

import java.util.ArrayList;

public class OrderReadyAndPickedUpLIstAdapter extends RecyclerView.Adapter<OrderReadyAndPickedUpLIstAdapter.ViewHolder> {

    private Context context;
    private ArrayList<OrderPlacedModel.responseData.orders_InfoData> orderPlacedModelList;

    public OrderReadyAndPickedUpLIstAdapter(Context context, ArrayList<OrderPlacedModel.responseData.orders_InfoData> orderPlacedModelList) {
        this.context = context;
        this.orderPlacedModelList = orderPlacedModelList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_layout_pickedup_order_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderPlacedModel.responseData.orders_InfoData ordersInfoData = orderPlacedModelList.get(position);
        holder.tvOrderNumber.setText(context.getResources().getString(R.string.order_number) + ordersInfoData.Order_Number);

        try {

            if (holder.llOrderedItems.getChildCount() > 0) {
                holder.llOrderedItems.removeAllViews();
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
                holder.llOrderedItems.addView(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvOrderDate.setText(ordersInfoData.createdDtm);
        holder.tvOrderAmount.setText(context.getResources().getString(R.string.order_amount) + ordersInfoData.Grand_Total);
        holder.tvOrderStatus.setText(ordersInfoData.Order_Status.equalsIgnoreCase("Order Ready") ? "Orden lista" : "El orden está en camino");
        holder.ivOrderStatus.setImageDrawable(ordersInfoData.Order_Status.equalsIgnoreCase("Order Ready") ? context.getResources().getDrawable(R.drawable.time) : context.getResources().getDrawable(R.drawable.delivery_man));

        holder.llOrderDetails.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("isFrom", "OrderReadyAndPickedUp");
            ((HomeTabActivity) context).replaceFragmentWithBackStack(new CustomerOrderDetailFragment(ordersInfoData), bundle);
        });

    }

    @Override
    public int getItemCount() {
        return orderPlacedModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvOrderNumber, tvOrderDate, tvOrderAmount, tvOrderStatus;
        private LinearLayoutCompat llOrderedItems, llOrderDetails;
        private AppCompatImageView ivOrderStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderNumber = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvOrderAmount = itemView.findViewById(R.id.tvOrderAmount);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            llOrderedItems = itemView.findViewById(R.id.llOrderedItems);
            llOrderDetails = itemView.findViewById(R.id.llOrderDetails);
            ivOrderStatus = itemView.findViewById(R.id.ivOrderStatus);
        }
    }
}
