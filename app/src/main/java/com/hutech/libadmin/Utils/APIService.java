package com.hutech.libadmin.Utils;

import com.hutech.libadmin.Models.AccessToken;
import com.hutech.libadmin.Models.Author;
import com.hutech.libadmin.Models.Customer;
import com.hutech.libadmin.Models.Order;
import com.hutech.libadmin.Models.Statistic;

import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIService {
    @POST("login")
    @FormUrlEncoded
    Call<AccessToken> login(@Field("email") String email, @Field("password") String password);

    @POST("refresh")
    @FormUrlEncoded
    Call<AccessToken> refresh(@Field("refresh_token") String refreshToken);

    @POST("admin/get-order")
    @FormUrlEncoded
    Call<Order> scan(@Field("token") String token, @Field("order_code") String order_code);

    @POST("get-user")
    @FormUrlEncoded
    Call<Customer> get_data_customer(@Field("token") String token);

    @POST("admin/get-statistic")
    @FormUrlEncoded
    Call<Statistic> get_statistic(@Field("token") String token);

    @GET("list-all-author")
    Call<ArrayList<Author>> list_all_author();

    @POST("admin/create-book")
    @FormUrlEncoded
    Call<ResponseBody> create_book(@Field("token") String token, @Field("name_book") String name_book, @Field("author_book") int author_book, @Field("image_book") String image_book, @Field("description_book") String description_book, @Field("publish_date") String publish_date, @Field("price_book") float price_book);

    @POST("admin/create-author")
    @FormUrlEncoded
    Call<ResponseBody> create_author(@Field("token") String token, @Field("name_author") String author_book, @Field("avatar") String avatar, @Field("description_author") String description_author);
}
