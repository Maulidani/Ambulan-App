package com.skripsi.ambulanapp.network.model

class Model {
    data class ResponseModel(
        val message: String,
        val errors: Boolean,
        val data: List<DataModel>, // user,hospital,article,order,chat,userChat,
        val user: UserModel, // add,edit,login
        val hospital: HospitalModel, //add,edit,
        val article: ArticleModel, //add,edit,
        val order: OrderModel, //add
        val chat: ChatModel,
    )

    data class DataModel(
        val id : String,

        val name : String, // user,hospital , getUserAdminChat
        val phone : String, // user
        val password : String, // user
        val image : String, // user,hospital,article
        val latitude : String, // user,hospital
        val longitude : String, // user,hospital
        val status : String, // user,order, order history

        val address : String, // hospital

        val title : String, // article
        val description : String, // article

        val user_customer_id : String, // order
        val user_driver_id : String, // order
        val hospital_id : String, // order, order history
        val pick_up_latitude : String, // order, order history
        val pick_up_longitude : String, // order, order history

        val from_user_type : String, // chat
        val to_user_type : String, // chat
        val from_user_id : String, // chat
        val to_user_id : String, // chat
        val message : String, // chat

        val customer_id:String, // order history
        val customer_name:String, // order history
        val customer_phone:String, // order history
        val customer_image:String, // order history
        val driver_name:String, // order history
        val driver_id:String, // order history
        val driver_phone:String, // order history
        val driver_image:String, // order history
        val hospital_name:String, // order history
        val hospital_latitude:String, // order history
        val hospital_longitude:String, // order history

        val updated_at : String,
        val created_at : String,
    )

    data class UserModel(
        val id : String,
        val name : String,
        val phone : String,
        val password : String,
        val image : String,
        val latitude : String,
        val longitude : String,
        val status : String,
        val updated_at : String,
        val created_at : String,
    )

    data class HospitalModel(
        val id : String,

        val name : String,
        val address : String,
        val image : String,
        val latitude : String,
        val longitude : String,

        val updated_at : String,
        val created_at : String,
    )

    data class ArticleModel(
        val id : String,

        val title : String,
        val description : String,
        val image : String,

        val updated_at : String,
        val created_at : String,
    )

    data class OrderModel(
        val id : String,

        val user_customer_id : String,
        val user_driver_id : String,
        val hospital_id : String,
        val pick_up_latitude : String,
        val pick_up_longitude : String,
        val status : String,

        val updated_at : String,
        val created_at : String,
    )

    data class ChatModel(
        val id : String,

        val from_user_type : String,
        val to_user_type : String,
        val from_user_id : String,
        val to_user_id : String,
        val message : String,

        val updated_at : String,
        val created_at : String,
    )



}