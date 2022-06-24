package com.skripsi.ambulanapp.network

import com.skripsi.ambulanapp.network.model.Model
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //order
    @Multipart
    @POST("add-users")
    fun addUser(
        @Part("type") type: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part parts: MultipartBody.Part,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
    ): Call<Model.ResponseModel> // response : user

    @Multipart
    @POST("edit-users")
    fun editUser(
        @Part("id_user") idUser: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part parts: MultipartBody.Part,
        @Part("username") username: RequestBody,
        @Part("password") password: RequestBody,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("delete-users")
    fun deleteUser(
        @Field("id_user") idUser: Int,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("login-users")
    fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("type") type: String,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("add-latlng-driver-users")
    fun addLatlngDriverUser(
        @Field("id_user") idUSer: Int,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
    ): Call<Model.ResponseModel>

    @GET("get-driver-users")
    fun getDriverUser(): Call<Model.ResponseModel> // response : data[]

    @FormUrlEncoded
    @POST("add-status-users")
    fun addStatusDriverUser(
        @Field("id_user") idUSer: Int,
        @Field("status") status: Int,
    ): Call<Model.ResponseModel>

    //order
    @GET("get-orders")
    fun getOrder(): Call<Model.ResponseModel> // response : data[]

    @FormUrlEncoded
    @POST("add-orders")
    fun addOrder(
        @Field("id_user") idUSer: Int,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("pick_up_latitude") pickUpLatitude: String,
        @Field("pick_up_longitude") pickUpLongitude: String,
        @Field("id_hospital") idHospital: Int,
    ): Call<Model.ResponseModel> // response : order

    @FormUrlEncoded
    @POST("add-status-orders")
    fun addStatusOrder(
        @Field("id_order") idOrder: Int,
        @Field("status") status: Int,
    ): Call<Model.ResponseModel>

    //hospital
    @Multipart
    @POST("add-hospitals")
    fun addHospital(
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part parts: MultipartBody.Part,
    ): Call<Model.ResponseModel> // response : hospital

    @FormUrlEncoded
    @POST("get-hospitals")
    fun getHospital(
        @Field("id_hospital") idhospital: Int,
        @Field("search") search: String,
    ): Call<Model.ResponseModel> // response = idHospital? hospital : data

    @Multipart
    @POST("edit-hospitals")
    fun editHospital(
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part parts: MultipartBody.Part,
        @Part("id_hospital") idHospital: RequestBody,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("delete-hospitals")
    fun deleteHospital(
        @Field("id_hospital") idHospital: Int,
    ): Call<Model.ResponseModel>


}