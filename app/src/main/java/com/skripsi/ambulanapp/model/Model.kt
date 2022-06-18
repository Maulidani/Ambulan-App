package com.skripsi.ambulanapp.model

class Model {

    data class ResponseModel(
        val message: String,
        val errors: Boolean,
        val data: List<DataModel>,
        val user: DataModel,
        val order: DataOrderModel
    )

    data class DataModel(
        val id: Int,
        val status: Int,

        //artikel
//        val type: String,
        val title: String,
        val hospital: String,
        val hospital_address: String,
        val content: String,
//        val image: String,

        // orders
        val order_by: String,
        val note: String,
        val pick_up: String,
        val drop_off: String,
        val pick_up_latitude: String,
        val pick_up_longitude: String,
        val drop_off_latitude: String,
        val drop_off_longitude: String,

        val id_orders: Int,
        val id_user_driver: Int,
        val status_users: Int,
        val status_orders: String,

        //users
        val name: String,
        val phone: String,
        val image: String,
        val username: String,
        val password: String,
        val latitude: String,
        val longitude: String,
        val type: String,

        val car_type: String,
        val car_number: String,

        val updated_at: String,
        val created_at: String,


        )

    data class DataOrderModel(
        val id: Int,
        val order_by: String,
        val note: String,
        val name: String,
        val pick_up: String,
        val drop_off: String,
        val pick_up_latitude: String,
        val pick_up_longitude: String,
        val drop_off_latitude: String,
        val drop_off_longitude: String,
        val id_user_driver: String,
        val status: String,

        )
}