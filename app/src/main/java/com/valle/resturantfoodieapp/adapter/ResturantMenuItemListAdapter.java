package com.valle.resturantfoodieapp.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.activity.HomeTabActivity;
import com.valle.resturantfoodieapp.fragment.AddMenuItemFragment;
import com.valle.resturantfoodieapp.models.MenuItemListModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.utils.RoundRectCornerImageView;

import java.util.ArrayList;
import java.util.List;

public class ResturantMenuItemListAdapter extends RecyclerView.Adapter<ResturantMenuItemListAdapter.ViewHolder> {

    private Context context;
    private MenuItemListModel menuItemListModel;
    private ArrayList<MenuItemListModel.responseData.ItemsData> itemsDataList;
    private final String STOCK_AVALIABLE = "In Stock";
    private final String OUT_OF_STOCK = "Out Of Stock";
    private ItemStockAvaliablity itemStockAvaliablity;

    public ResturantMenuItemListAdapter(Context context, MenuItemListModel menuItemListModel, ItemStockAvaliablity itemStockAvaliablity) {
        this.context = context;
        this.menuItemListModel = menuItemListModel;
        this.itemStockAvaliablity = itemStockAvaliablity;
        itemsDataList = new ArrayList<>();
        itemsDataList.addAll(menuItemListModel.response.Items);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_layout_menu_list_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MenuItemListModel.responseData.ItemsData itemsData = itemsDataList.get(position);
        holder.tvOrderName.setText(itemsData.Item_Name);
        holder.tvOrderCategory.setText(itemsData.Item_Category);
        holder.tvOrderValue.setText("$" + itemsData.Item_Price);

        try {

            Glide.with(context).load(Apis.API_URL_FILE + itemsData.Item_Image).into(holder.ivOrderImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (itemsData.Item_Status.equalsIgnoreCase(OUT_OF_STOCK)) {
            holder.swStock.setChecked(false);
        } else if (itemsData.Item_Status.equalsIgnoreCase(STOCK_AVALIABLE)) {
            holder.swStock.setChecked(true);
        }

        holder.swStock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                itemStockAvaliablity.ItemInStock(itemsData.Id, STOCK_AVALIABLE);
            } else {
                itemStockAvaliablity.ItemOutStock(itemsData.Id, OUT_OF_STOCK);
            }
        });

        holder.ivEditItem.setOnClickListener(v -> {

            ((HomeTabActivity) context).replaceFragmentWithBackStack(new AddMenuItemFragment(itemsData), null);

        });
    }

    public interface ItemStockAvaliablity {
        void ItemInStock(String itemId, String status);

        void ItemOutStock(String itemId, String status);
    }

    public void filterData(String data) {
        itemsDataList.clear();
        if (!TextUtils.isEmpty(data)) {

            List<MenuItemListModel.responseData.ItemsData> items = menuItemListModel.response.Items;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).Item_Name.toLowerCase().contains(data.toLowerCase()) || items.get(i).Item_Category.toLowerCase().contains(data.toLowerCase())) {
                    itemsDataList.add(items.get(i));
                }
            }
        } else {
            itemsDataList.addAll(menuItemListModel.response.Items);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return itemsDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvOrderName, tvOrderCategory, tvOrderValue;
        private RatingBar rating;
        private AppCompatImageView ivEditItem;
        private RoundRectCornerImageView ivOrderImage;
        private Switch swStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderName = itemView.findViewById(R.id.tvOrderName);
            tvOrderCategory = itemView.findViewById(R.id.tvOrderCategory);
            tvOrderValue = itemView.findViewById(R.id.tvOrderValue);
            ivEditItem = itemView.findViewById(R.id.ivEditItem);
            ivOrderImage = itemView.findViewById(R.id.ivOrderImage);
            swStock = itemView.findViewById(R.id.swStock);
        }
    }

}
