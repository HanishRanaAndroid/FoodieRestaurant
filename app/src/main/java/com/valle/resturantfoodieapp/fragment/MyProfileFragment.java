package com.valle.resturantfoodieapp.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.base.BaseFragment;
import com.valle.resturantfoodieapp.utils.CommonUtils;

public class MyProfileFragment extends BaseFragment {

    FrameLayout frameMain;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);

        frameMain   = view.findViewById(R.id.frameMain);

        if (CommonUtils.isNetworkAvailable(getActivity())) {

        } else {
            showSnakBar(frameMain);
        }
    }

}
