package com.valle.resturantfoodieapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.fragment.HomeScreenFragment;

public class TimeEstimationAdapter extends RecyclerView.Adapter<TimeEstimationAdapter.ViewHolder> {

    private Context context;
    private String[] timeEstimationList;
    private OnTimeSelectedListener onTimeSelectedListener;

    public TimeEstimationAdapter(Context context, String[] timeEstimationList, OnTimeSelectedListener onTimeSelectedListener) {
        this.context = context;
        this.timeEstimationList = timeEstimationList;
        this.onTimeSelectedListener = onTimeSelectedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.adapter_layout_time_estimation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.tvMinutes.setText(timeEstimationList[position]);

        holder.tvMinutes.setOnClickListener(v -> {
            onTimeSelectedListener.OnTimeSelected(timeEstimationList[position]);
        });

        if (HomeScreenFragment.time.equalsIgnoreCase(timeEstimationList[position])) {
            holder.ivTick.setVisibility(View.VISIBLE);
        } else {
            holder.ivTick.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return timeEstimationList.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private AppCompatTextView tvMinutes;
        private AppCompatImageView ivTick;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMinutes = itemView.findViewById(R.id.tvMinutes);
            ivTick = itemView.findViewById(R.id.ivTick);
        }
    }

    public interface OnTimeSelectedListener {
        void OnTimeSelected(String strTime);
    }
}
