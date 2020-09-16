package com.valle.resturantfoodieapp.network;

import com.google.android.gms.common.api.Api;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIInterface {

    @GET("{coin}-usd")
    Observable<String> getCoinData(@Path("coin") String coin);

    @GET(Apis.HOME_PAGE)
    Observable<String> getHomePageData();

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.REGISTER)
    @FormUrlEncoded
    Observable<String> register(@Field("Full_Name") String Full_Name,@Field("Restaurant_Name") String Restaurant_Name, @Field("Mobile") String Mobile, @Field("Email") String Email, @Field("Address") String Address, @Field("Password") String Password, @Field("Device_Token") String Device_Token, @Field("Role") String Role, @Field("Latitude") String Latitude, @Field("Longitude") String Longitude);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.LOGIN)
    @FormUrlEncoded
    Observable<String> login(@Field("FieldType") String FieldType, @Field("Mobile") String Mobile, @Field("Password") String Password, @Field("Role_Type") String Role_Type, @Field("Device_Token") String Device_Token);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.SEND_OTP)
    @FormUrlEncoded
    Observable<String> sendOTPtoUser(@Field("Mobile") String Mobile);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.VERIFY_ACCOUNT)
    @FormUrlEncoded
    Observable<String> verifyAccount(@Field("Mobile") String Mobile, @Field("Code") String Code,@Field("Status") String Status);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.FORGET_PASSWORD)
    @FormUrlEncoded
    Observable<String> forgetPassword(@Field("Mobile") String Mobile, @Field("Password") String Password);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.CHNAGE_PASSWORD)
    @FormUrlEncoded
    Observable<String> changePassword(@Field("User_Id") String User_Id, @Field("oldPassword") String oldPassword, @Field("newPassword") String newPassword);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @Multipart
    @POST(Apis.EDIT_PROFILE)
    Call<String> updateProfile(@Part MultipartBody.Part profile, @Part MultipartBody.Part cover, @Part("User_Id") RequestBody User_Id,
                               @Part("Restaurant_Name") RequestBody Restaurant_Name, @Part("Email") RequestBody Email,
                               @Part("Mobile") RequestBody Mobile, @Part("Address") RequestBody Address);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @GET(Apis.CATEGORY_LIST)
    Observable<String> getCategoryList();

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.MENU_ITEM_LIST)
    @FormUrlEncoded
    Observable<String> getMenuListItems(@Field("Rest_Id") String Rest_Id);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @Multipart
    @POST(Apis.ADD_ITEM)
    Call<String> addMenuItem(@Part MultipartBody.Part image, @Part("Rest_Id") RequestBody Rest_Id, @Part("Item_Name") RequestBody Item_Name,
                                   @Part("Item_Category") RequestBody Item_Category, @Part("Item_Price") RequestBody Item_Price, @Part("Item_Description") RequestBody Item_Description,@Part("Item_Flavor") RequestBody Item_Flavour);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @Multipart
    @POST(Apis.UPDATE_ITEM)
    Call<String> updateMenuItem(@Part MultipartBody.Part image, @Part("Rest_Id") RequestBody Rest_Id, @Part("Item_Id") RequestBody Item_Id, @Part("Item_Name") RequestBody Item_Name,
                                      @Part("Item_Category") RequestBody Item_Category, @Part("Item_Price") RequestBody Item_Price, @Part("Item_Description") RequestBody Item_Description, @Part("Item_Flavor") RequestBody Item_Flavour);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @FormUrlEncoded
    @POST(Apis.SET_USER_PRESENCE)
    Observable<String> setUserPresense(@Field("Where") String Where, @Field("Data") String Data);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @FormUrlEncoded
    @POST(Apis.GET_ORDERS)
    Observable<String> getOrders(@Field("Where") String Where, @Field("GraterThen") String GraterThen);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @FormUrlEncoded
    @POST(Apis.GET_PICKEPUP_READY_ORDERS)
    Observable<String> getREadyOrders(@Field("Where") String Where);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @FormUrlEncoded
    @POST(Apis.CONFIRM_ORDER)
    Observable<String> confirmOrder(@Field("Where") String Where, @Field("data") String data);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @FormUrlEncoded
    @POST(Apis.READY_ORDER)
    Observable<String> readyOrder(@Field("Where") String Where, @Field("data") String data);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @FormUrlEncoded
    @POST(Apis.ORDER_HISTORY)
    Observable<String> getOrdersHistory(@Field("Where") String Where, @Field("Last_Days") String Last_Days);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @FormUrlEncoded
    @POST(Apis.ORDER_UPDATE)
    Observable<String> orderUpdate(@Field("Where") String Where);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @FormUrlEncoded
    @POST(Apis.ITEM_STOCK)
    Observable<String> updateItemStock(@Field("Item_Id") String Item_Id, @Field("Item_Status") String Item_Status);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @GET(Apis.ADMIN_CONTACT)
    Observable<String> getAdminContact();

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.TOP_DISCOUNTED_RESTAURANT)
    @FormUrlEncoded
    Observable<String> getRestaurantInfo(@Field("Where") String Where);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.GET_PICKEPUP_READY_ORDERS)
    @FormUrlEncoded
    Observable<String> getPickepUpOrder(@Field("Where") String Where);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @FormUrlEncoded
    @POST(Apis.REST_RATING)
    Observable<String> getRestRating(@Field("Rest_Id") String Rest_Id);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @POST(Apis.SUBMIT_TICKET)
    @FormUrlEncoded
    Observable<String> submitQuery(@Field("User_Id") String User_Id, @Field("Type") String Type, @Field("Order_Id") String Order_Id, @Field("Message") String Message);

    @Headers("apitoken: 813937bae1a26e2d442acec31c85c460e0a12c98")
    @GET(Apis.LIST_TICKET)
    Observable<String> getQuery();
}
