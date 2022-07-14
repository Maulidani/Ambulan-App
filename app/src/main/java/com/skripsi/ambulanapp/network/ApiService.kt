package com.skripsi.ambulanapp.network

import com.skripsi.ambulanapp.network.model.Model
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Multipart
    @POST("add-user")
    fun addUser(
        @Part("user_type") userType: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("password") password: RequestBody,
        @Part parts: MultipartBody.Part,
    ): Call<Model.ResponseModel> // response : user{}

    @Multipart
    @POST("edit-user")
    fun editUser(
        @Part("user_id") userId: RequestBody,
        @Part("user_type") userType: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("password") password: RequestBody,
        @Part parts: MultipartBody.Part,
    ): Call<Model.ResponseModel> // response : user{}

    @FormUrlEncoded
    @POST("edit-user")
    fun editWithoutImgUser(
        @Field("user_id") userId: String,
        @Field("user_type") userType: String,
        @Field("name") name: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("delete-user")
    fun deleteUser(
        @Field("user_type") userType: String,
        @Field("user_id") userId: String,
    ): Call<Model.ResponseModel> // response : user{}

    @FormUrlEncoded
    @POST("login-user")
    fun loginUser(
        @Field("user_type") userType: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("username") username: String, // for user_type = admin
    ): Call<Model.ResponseModel> // response : user{}

    @FormUrlEncoded
    @POST("add-latlng-user")
    fun addLatlngDriverUser(
        @Field("user_type") userType: String,
        @Field("user_id") userId: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("get-user")
    fun getUser(
        @Field("user_id") userDriverId: String, // only for 'for_driver_status' , 'user_detail'
        @Field("user_type") userType: String,
        @Field("get_type") getType: String, // ('for_order','for_admin','for_driver_status','user_detail')
    ): Call<Model.ResponseModel> // response : data[{}]

    @FormUrlEncoded
    @POST("add-status-user-driver")
    fun addStatusDriverUser(
        @Field("user_driver_id") userDriverId: String,
        @Field("status") status: String,
    ): Call<Model.ResponseModel>

    @Multipart
    @POST("add-article")
    fun addArticle(
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part parts: MultipartBody.Part,
    ): Call<Model.ResponseModel> // response : article{}

    @Multipart
    @POST("edit-article")
    fun editArticle(
        @Part("article_id") articleId: RequestBody,
        @Part("title") userType: RequestBody,
        @Part("description") description: RequestBody,
        @Part parts: MultipartBody.Part,
    ): Call<Model.ResponseModel> // response : article{}

    @FormUrlEncoded
    @POST("edit-article")
    fun editWithoutImgArticle(
        @Field("article_id") articleId: String,
        @Field("title") userType: String,
        @Field("description") description: String,
    ): Call<Model.ResponseModel> // response : article{}

    @FormUrlEncoded
    @POST("get-article")
    fun getArticle(
        @Field("search") search: String,
    ): Call<Model.ResponseModel> // response : data[{}]

    @FormUrlEncoded
    @POST("delete-article")
    fun deleteArticle(
        @Field("article_id") articleId: String,
    ): Call<Model.ResponseModel>

    @Multipart
    @POST("add-hospital")
    fun addHospital(
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part parts: MultipartBody.Part,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
    ): Call<Model.ResponseModel> // response : hospital{}

    @Multipart
    @POST("edit-hospital")
    fun editHospital(
        @Part("hospital_id") hospitalId: RequestBody,
        @Part("name") name: RequestBody,
        @Part("address") address: RequestBody,
        @Part parts: MultipartBody.Part,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
    ): Call<Model.ResponseModel> // response : hospital{}

    @FormUrlEncoded
    @POST("edit-hospital")
    fun editWithoutImgHospital(
        @Field("hospital_id") hospital_id: String,
        @Field("name") name: String,
        @Field("address") address: String,
        @Field("latitude") latitude: String,
        @Field("longitude") longitude: String,
    ): Call<Model.ResponseModel> // response : hospital{}

    @FormUrlEncoded
    @POST("get-hospital")
    fun getHospital(
        @Field("search") search: String,
    ): Call<Model.ResponseModel> // response : data[{}]

    @FormUrlEncoded
    @POST("delete-hospital")
    fun deleteHospital(
        @Field("hospital_id") hospitalId: String,
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("add-order")
    fun addOrder(
        @Field("user_customer_id") userCustomerId: String,
        @Field("user_driver_id") userDriverId: String,
        @Field("hospital_id") hospitalId: String,
        @Field("pick_up_latitude") pickUpLatitude: String,
        @Field("pick_up_longitude") pickUpLongitude: String,
        @Field("status") status: String, // 'to_pick_up' ('to_pick_up','to_drop_off','finish','cancel')
    ): Call<Model.ResponseModel> // response : order{}

    @FormUrlEncoded
    @POST("edit-status-order")
    fun editStatusOrder(
        @Field("order_id") orderId: String,
        @Field("status") status: String, // ('to_pick_up','to_drop_off','finish','cancel')
    ): Call<Model.ResponseModel>

    @FormUrlEncoded
    @POST("get-order")
    fun getOrder(
        @Field("user_type") userType: String, // show order by user('admin','customer','driver')
        @Field("get_type") getType: String, // ('ordering','show_list')
        @Field("user_customer_id") userCustomerId: String, // if getType = ordering-> only userCustomerID
        @Field("user_driver_id") userDriverId: String, // if getType = ordering-> only userDriverId
        @Field("status") status: String, // ('to_pick_up','to_drop_off','finish','cancel')
    ): Call<Model.ResponseModel> // if getType = ordering -> order{}, getType = show_list -> data{[]}

    @FormUrlEncoded
    @POST("add-chat")
    fun addChat(
        @Field("from_user_type") fromUserType: String, // ('admin','driver','customer')
        @Field("to_user_type") toUserType: String, // ('admin','driver','customer')
        @Field("from_user_id") fromUserId: String,
        @Field("to_user_id") toUserId: String,
        @Field("message") message: String,
    ): Call<Model.ResponseModel> // response : chat{}

    @FormUrlEncoded
    @POST("get-chat")
    fun getChat(
        @Field("from_user_type") fromUserType: String, // ('admin','driver','customer')
        @Field("to_user_type") toUserType: String, // ('admin','driver','customer')
        @Field("from_user_id") fromUserId: String,
        @Field("to_user_id") toUserId: String,
    ): Call<Model.ResponseModel> // response : data[{}]

    @FormUrlEncoded
    @POST("get-user-chat")
    fun getUserChat( // need filter don show if fromUserType && fromUserId == userTypeLogin && userIdLogin
        @Field("from_user_type") fromUserType: String, // ('admin','driver','customer')
        @Field("from_user_id") fromUserId: String,
    ): Call<Model.ResponseModel> // response : data[{}]

    @FormUrlEncoded
    @POST("get-user-admin-chat") // for driver
    fun getUserAdminChat(): Call<Model.ResponseModel> // response : data{[]}

}