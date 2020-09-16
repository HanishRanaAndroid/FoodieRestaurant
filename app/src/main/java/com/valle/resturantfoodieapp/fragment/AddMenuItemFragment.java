package com.valle.resturantfoodieapp.fragment;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.valle.resturantfoodieapp.R;
import com.valle.resturantfoodieapp.activity.HomeTabActivity;
import com.valle.resturantfoodieapp.cutomclasses.MenuItemCutomClass;
import com.valle.resturantfoodieapp.models.AddItemModel;
import com.valle.resturantfoodieapp.models.CategoryListModel;
import com.valle.resturantfoodieapp.models.LoginModel;
import com.valle.resturantfoodieapp.models.MenuItemListModel;
import com.valle.resturantfoodieapp.network.Apis;
import com.valle.resturantfoodieapp.network.NetworkResponceListener;
import com.valle.resturantfoodieapp.prefs.SharedPrefModule;
import com.valle.resturantfoodieapp.utils.CommonUtils;
import com.valle.resturantfoodieapp.utils.ImageFilePath;
import com.valle.resturantfoodieapp.utils.RoundRectCornerImageView;
import com.valle.resturantfoodieapp.utils.RoundedImageView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import id.zelory.compressor.Compressor;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AddMenuItemFragment extends MenuItemCutomClass implements NetworkResponceListener {

    public File itemFile;

    @BindView(R.id.ivItemImage)
    RoundedImageView ivItemImage;

    @BindView(R.id.spnItemCategory)
    AppCompatSpinner spnItemCategory;

    @BindView(R.id.etitemName)
    AppCompatEditText etitemName;

    @BindView(R.id.etItemPrice)
    AppCompatEditText etItemPrice;

    @BindView(R.id.etItemDescription)
    AppCompatEditText etItemDescription;

    private String category = "";

    private List<String> categoryList;

    private MenuItemListModel.responseData.ItemsData itemsData;

    @BindView(R.id.ivCoverPhoto)
    AppCompatImageView ivCoverPhoto;

    @BindView(R.id.ivRestaurantImage)
    RoundRectCornerImageView ivRestaurantImage;

    @BindView(R.id.tvRestName)
    AppCompatTextView tvRestName;

    @BindView(R.id.tvRestaurantName)
    AppCompatTextView tvRestaurantName;

    @BindView(R.id.tvRestAddress)
    AppCompatTextView tvRestAddress;

    @BindView(R.id.tvSubmitItem)
    AppCompatTextView tvSubmitItem;

    @BindView(R.id.llMain)
    LinearLayout llMain;

    @BindView(R.id.tvRating)
    AppCompatTextView tvRating;

    @BindView(R.id.tvReview)
    AppCompatTextView tvReview;

    @BindView(R.id.ivAddFlavour)
    AppCompatImageView ivAddFlavour;

    @BindView(R.id.llFlavour)
    LinearLayoutCompat llFlavour;

    @BindView(R.id.llViewForFlavours)
    LinearLayoutCompat llViewForFlavours;

    @BindView(R.id.rYes)
    RadioButton rYes;

    @BindView(R.id.rNo)
    RadioButton rNo;

    private ArrayList<AppCompatEditText> appCompatEditTexts = new ArrayList<>();

    public AddMenuItemFragment(MenuItemListModel.responseData.ItemsData itemsData) {
        this.itemsData = itemsData;
    }

    public AddMenuItemFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_menu_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setOnClickListener(null);
        bindView(this, view);
        if (CommonUtils.isNetworkAvailable(getActivity())) {
            showProgressDialog(getActivity());
            makeHttpCall(this, Apis.CATEGORY_LIST, getRetrofitInterface().getCategoryList());
        } else {
            showSnakBar(llMain);
        }
        setProfileData();

        setData();
        getUpdatedRating();
    }

    @OnClick({R.id.rYes, R.id.rNo})
    public void onRadioButtonClicked(RadioButton radioButton) {
        // Is the button now checked?
        boolean checked = radioButton.isChecked();

        // Check which radio button was clicked
        switch (radioButton.getId()) {
            case R.id.rYes:
                if (checked) {
                    llViewForFlavours.setVisibility(View.VISIBLE);
                    llFlavour.setVisibility(View.VISIBLE);
                    View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_add_flavour, llFlavour, false);
                    AppCompatEditText etFlavourName = view.findViewById(R.id.etFlavourName);
                    appCompatEditTexts.add(etFlavourName);
                    llFlavour.addView(view);
                }
                break;
            case R.id.rNo:
                if (checked) {
                    llViewForFlavours.setVisibility(View.GONE);

                    if (llFlavour.getChildCount() > 0) {
                        llFlavour.removeAllViews();
                        appCompatEditTexts.clear();
                    }

                    llFlavour.setVisibility(View.GONE);
                }
                break;
        }
    }

    @OnClick(R.id.ivAddFlavour)
    void onClickivAddFlavour() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_add_flavour, llFlavour, false);
        AppCompatEditText etFlavourName = view.findViewById(R.id.etFlavourName);
        AppCompatImageView ivRemove = view.findViewById(R.id.ivRemove);
        ivRemove.setVisibility(View.VISIBLE);
        ivRemove.setOnClickListener(v -> {
            appCompatEditTexts.remove(etFlavourName);
            llFlavour.removeView(view);
        });
        appCompatEditTexts.add(etFlavourName);
        llFlavour.addView(view);
    }

    private void setProfileData() {
        String userLoginResponseData = new SharedPrefModule(getActivity()).getUserLoginResponseData();
        if (TextUtils.isEmpty(userLoginResponseData)) {
            return;
        }

        LoginModel.responseData.UserInfoData userInfoData = new Gson().fromJson(userLoginResponseData, LoginModel.responseData.UserInfoData.class);

        try {

            if (!TextUtils.isEmpty(userInfoData.Profile_Image)) {
                Glide.with(getActivity()).load(Apis.API_URL_FILE + userInfoData.Profile_Image).into(ivRestaurantImage);
            }
            if (!TextUtils.isEmpty(userInfoData.Cover_Image)) {
                Glide.with(getActivity()).load(Apis.API_URL_FILE + userInfoData.Cover_Image).into(ivCoverPhoto);
            }

            tvRestName.setText(userInfoData.Restaurant_Name);
            tvRestaurantName.setText(userInfoData.Restaurant_Name);
            tvRestAddress.setText(userInfoData.Address);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUpdatedRating() {

        try {
            String restId = new SharedPrefModule(getActivity()).getUserId();
            makeHttpCall(this, Apis.REST_RATING, getRetrofitInterface().getRestRating(restId));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setData() {

        try {
            if (itemsData == null) {
                tvSubmitItem.setText(getResources().getString(R.string.add));
                return;
            }

            try {
                tvSubmitItem.setText(getResources().getString(R.string.update));
                if (itemsData.Item_Image != null) {
                    Glide.with(getActivity()).load(Apis.API_URL_FILE + itemsData.Item_Image).placeholder(getResources().getDrawable(R.drawable.dish)).into(ivItemImage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            etitemName.setText(itemsData.Item_Name);
            etItemPrice.setText(itemsData.Item_Price);
            etItemDescription.setText(itemsData.Item_Description);

            String itemFlavor = itemsData.Item_Flavor;
            rYes.setChecked(!TextUtils.isEmpty(itemFlavor));
            rNo.setChecked(TextUtils.isEmpty(itemFlavor));

            llViewForFlavours.setVisibility(TextUtils.isEmpty(itemFlavor)?View.GONE:View.VISIBLE);

            if (TextUtils.isEmpty(itemFlavor)) {
                return;
            }

            String[] listFlavor = itemFlavor.split(",");
            if (listFlavor.length > 0) {
                llFlavour.setVisibility(View.VISIBLE);
                for (String s : listFlavor) {
                    addFlavorView(s);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addFlavorView(String text) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_add_flavour, llFlavour, false);
        AppCompatEditText etFlavourName = view.findViewById(R.id.etFlavourName);
        AppCompatImageView ivRemove = view.findViewById(R.id.ivRemove);
        ivRemove.setVisibility(View.VISIBLE);
        etFlavourName.setText(text);
        ivRemove.setOnClickListener(v -> {
            appCompatEditTexts.remove(etFlavourName);
            llFlavour.removeView(view);
        });
        appCompatEditTexts.add(etFlavourName);
        llFlavour.addView(view);
    }

    @OnClick(R.id.ivEdit)
    void OnCllickivEdit() {
        ((HomeTabActivity) getActivity()).replaceFragmentWithBackStack(new UpdateProfileFragment(), null);
    }

    @OnClick(R.id.tvSubmitItem)
    void OnClicktvSubmitItem() {
        String strFlavours = "";
        for (int i = 0; i < appCompatEditTexts.size(); i++) {
            strFlavours = strFlavours + appCompatEditTexts.get(i).getText().toString() + ",";
        }

        if (!TextUtils.isEmpty(strFlavours)) {
            strFlavours = strFlavours.substring(0, strFlavours.length() - 1);
        }

        Log.d("check", "OnClicktvSubmitItem: " + strFlavours);
        String itemName = etitemName.getText().toString();
        String itemPrice = etItemPrice.getText().toString();
        String itemDescription = etItemDescription.getText().toString();


        if (TextUtils.isEmpty(category)) {
            Toast.makeText(getActivity(), getResources().getString(R.string.select_catg), Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(itemName)) {
            etitemName.setError(getResources().getString(R.string.enter_item_name));
            etitemName.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(itemPrice)) {
            etItemPrice.setError(getResources().getString(R.string.enter_item_price));
            etItemPrice.setFocusable(true);
            return;
        }

        if (TextUtils.isEmpty(itemDescription)) {
            etItemDescription.setError(getResources().getString(R.string.enter_item_desc));
        }

        MultipartBody.Part body = null;
        if (itemFile != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), itemFile);
            body = MultipartBody.Part.createFormData("Item_Image", itemFile.getName(), requestFile);
        }

        RequestBody userId = RequestBody.create(MediaType.parse("multipart/form-data"), new SharedPrefModule(getActivity()).getUserId());
        RequestBody Item_Name = RequestBody.create(MediaType.parse("multipart/form-data"), itemName);
        RequestBody Item_Category = RequestBody.create(MediaType.parse("multipart/form-data"), category);
        RequestBody Item_Price = RequestBody.create(MediaType.parse("multipart/form-data"), itemPrice);
        RequestBody Item_Description = RequestBody.create(MediaType.parse("multipart/form-data"), itemDescription);
        RequestBody Item_Flavour = RequestBody.create(MediaType.parse("multipart/form-data"), strFlavours);

        if (CommonUtils.isNetworkAvailable(getActivity())) {
            showProgressDialog(getActivity());
            if (itemsData == null) {
                makeHttpCallToUploadImageData(this, Apis.ADD_ITEM, getRetrofitInterfaceToUploadImageData().addMenuItem(body, userId, Item_Name, Item_Category, Item_Price, Item_Description, Item_Flavour));
            } else {
                RequestBody Item_Id = RequestBody.create(MediaType.parse("multipart/form-data"), itemsData.Id);
                makeHttpCallToUploadImageData(this, Apis.UPDATE_ITEM, getRetrofitInterfaceToUploadImageData().updateMenuItem(body, userId, Item_Id, Item_Name, Item_Category, Item_Price, Item_Description, Item_Flavour));
            }
        } else {
            showSnakBar(llMain);
        }

    }

    @OnClick(R.id.tvUploadImage)
    void OnClicktvUploadImage() {
        final CharSequence[] items = {"Tomar foto", "Elige de la biblioteca",
                "Cancelar"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir foto!");
        builder.setItems(items, (dialog, item) -> {

            if (items[item].equals("Tomar foto")) {
                userChoosenTask = "Take Photo";
                cameraIntent();
            } else if (items[item].equals("Elige de la biblioteca")) {
                userChoosenTask = "Choose from Library";
                galleryIntent();
            } else if (items[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if (userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA) {
                onCaptureImageResult(data);
            }
        }
    }

    @Override
    public void onSuccess(String url, String responce) {
        hideProgressDialog(getActivity());

        switch (url) {
            case Apis.CATEGORY_LIST:
                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        CategoryListModel categoryListModel = new Gson().fromJson(responce, CategoryListModel.class);
                        categoryList = new ArrayList<>();

                        for (CategoryListModel.responseData.ItemsList catName : categoryListModel.response.Items) {
                            categoryList.add(catName.Category_Name);
                        }

                        ArrayAdapter<String> categorySpinnerAdapter = new ArrayAdapter<String>(getActivity(),
                                android.R.layout.simple_spinner_item, categoryList);

                        spnItemCategory.setAdapter(categorySpinnerAdapter);

                        if (itemsData != null) {
                            for (int i = 0; i < categoryList.size(); i++) {
                                if (itemsData.Item_Category.toLowerCase().equalsIgnoreCase(categoryList.get(i).toLowerCase())) {
                                    spnItemCategory.setSelection(i);
                                }
                            }
                        }
                        spnItemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                category = categoryList.get(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    } else
                        {
                        Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case Apis.ADD_ITEM:
                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        AddItemModel addItemModel = new Gson().fromJson(responce, AddItemModel.class);
                        Toast.makeText(getActivity(), addItemModel.response.msg, Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case Apis.UPDATE_ITEM:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        Toast.makeText(getActivity(), getResources().getString(R.string.updated_successfully), Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case Apis.REST_RATING:

                try {

                    if (TextUtils.isEmpty(responce)) {
                        return;
                    }

                    JSONObject jsonObject = new JSONObject(responce);
                   /* {
                        "status": "success",
                            "response": {
                        "msg": "",
                                "total_rating": 3
                    }
                    }*/

                    if (jsonObject.getString(Apis.STATUS).equalsIgnoreCase(Apis.SUCCESS)) {
                        String totalRating = jsonObject.getJSONObject("response").getString("total_rating");

                        tvRating.setText("★" + totalRating);
                        tvReview.setText(jsonObject.getJSONObject("response").getString("Count") + " revisión");
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

    @SuppressLint("CheckResult")
    private void compressImage(File file) {
        new Compressor(getActivity())
                .compressToFileAsFlowable(file)
                .subscribeOn(Schedulers.io())
                .subscribe(file1 -> itemFile = file1, throwable -> throwable.printStackTrace());
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String picturePath = ImageFilePath.getPath(getActivity().getApplicationContext(), selectedImageUri);
        itemFile = new File(picturePath);
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                ivItemImage.setImageBitmap(bm);
                compressImage(itemFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) BitmapFactory.decodeFile(imageFilePath);
        itemFile = new File(imageFilePath);
        try {
            ivItemImage.setImageBitmap(thumbnail);
            compressImage(itemFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
