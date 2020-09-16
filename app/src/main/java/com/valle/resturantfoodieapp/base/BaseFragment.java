package com.valle.resturantfoodieapp.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.network.APIClient;
import com.valle.resturantfoodieapp.network.APIClientUpdateImage;
import com.valle.resturantfoodieapp.network.APIInterface;
import com.valle.resturantfoodieapp.network.NetworkManager;
import com.valle.resturantfoodieapp.network.NetworkManagerToUploadImageData;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.google.android.material.snackbar.Snackbar;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseFragment extends Fragment {

    public ProgressDialog progressDialog;
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void bindView(Fragment activity, View view) {
        unbinder = ButterKnife.bind(activity, view);
    }

    public void unBindView() {
        unbinder.unbind();
    }

    public void showProgressDialog(Context activity) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage(getResources().getString(R.string.please_wait_msg));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    public void hideProgressDialog(Context context) {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void makeHttpCall(NetworkResponceListener context, String url, Object o) {
        new NetworkManager(context).getNetworkResponce(url, o);
    }

    public void makeHttpCallToUploadImageData(NetworkResponceListener context, String url, Object o) {
        new NetworkManagerToUploadImageData(context).getNetworkResponce(url, o);
    }

    public APIInterface getRetrofitInterface() {
        return APIClient.getClient().create(APIInterface.class);
    }

    public APIInterface getRetrofitInterfaceToUploadImageData() {
        return APIClientUpdateImage.getClient().create(APIInterface.class);
    }

    public void showSnakBar(View viewLayout) {
        Snackbar snackbar = Snackbar
                .make(viewLayout, getResources().getString(R.string.internet_error), Snackbar.LENGTH_INDEFINITE)
                .setAction(getResources().getString(R.string.restry), view -> {
                    if (!CommonUtils.isNetworkAvailable(getActivity())) {
                        showSnakBar(viewLayout);
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

}
