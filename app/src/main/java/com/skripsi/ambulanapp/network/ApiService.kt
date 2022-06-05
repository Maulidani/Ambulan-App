package com.skripsi.ambulanapp.network

import com.skripsi.ambulanapp.model.Model
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //user
    @FormUrlEncoded
    @POST("login-users")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("type") type: String,
    ): Call<Model.ResponseModel>

    @Multipart
    @POST("add-users")
    fun addUser(
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part parts: MultipartBody.Part,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
        @Part("type") type: RequestBody,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("edit-users")
    fun editUser(
        @Field("id_user") idUser: Int,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("username") username: String,
        @Field("password") password: String,
    ): Call<Model.ResponseModel>

    @Multipart
    @POST("edit-image-users")
    fun editImageUser(
        @Part("id_user") name: RequestBody,
        @Part parts: MultipartBody.Part,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("delete-users")
    fun deleteuser(
        @Field("id_user") idUser: Int,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("delete-users")
    fun addLatlngDriverUser(
        @Field("id_user") idUser: Int,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
    ): Call<Model.ResponseModel>

    @GET("get-driver-users")
    fun getDriverUser(
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("add-status-users")
    fun addStatusDriverUser(
        @Field("id_user") idUser: Int,
        @Field("status") latitude: Int,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("add-edit-car-users")
    fun addEditCarUser(
        @Field("id_user") iduser: String,
        @Field("car_type") car_type: String,
        @Field("car_number") car_number: String,
    ): Call<Model.ResponseModel>


    //order
    @FormUrlEncoded
    @POST("show-orders")
    fun showOrder(
        @Field("status") statusOrder: String,
        @Field("id_driver") idDriver: String,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("add-orders")
    fun addOrder(
        @Field("id_orders") idOrder: String, //status = loading,finish,cancel
        @Field("id_user_driver") idDriverUser: String, //status = loading
        @Field("note") note: String,
        @Field("pick_up") pickUp: String,
        @Field("drop_off") dropOff: String,
        @Field("pick_up_latitude") pickUpLatitude: String,
        @Field("pick_up_longitude") pickUpLongitude: String,
        @Field("drop_off_latitude") dropOffLatitude: String,
        @Field("drop_off_longitude") dropOffLongitude: String,
        @Field("status") status: String, //status = loading,finish,cancel
    ): Call<Model.ResponseModel>

}