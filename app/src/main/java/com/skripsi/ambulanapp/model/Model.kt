package com.skripsi.ambulanapp.model

class Model {

    data class ResponseModel(
        val message: String,
        val errors: Boolean,
        val data: DataModel
    )

    data class DataModel(
        val id: Int,
        val status: Int,

        // orders
        val note: String,
        val pick_up_latitude: String,
        val pick_up_longitude: String,
        val drop_off_latitude: String,
        val drop_off_longitude: String,

        val id_orders: Int,
        val id_user_driver: Int,
        val status_users: Int,
        val status_orders: Int,

        //users
        val name: String,
        val phone: String,
        val image: String,
        val username: String,
        val password: String,
        val latitude: String,
        val longitude: String,
        val type: String,


    )
}