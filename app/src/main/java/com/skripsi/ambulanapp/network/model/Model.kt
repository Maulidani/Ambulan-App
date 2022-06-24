package com.skripsi.ambulanapp.network.model

class Model {
    data class ResponseModel(
        val message: String,
        val errors: Boolean,
        val data: List<DataModel>?,
        val user: DataModel?,
        val order: DataOrderModel?,
        val hospital: DataHospitalModel?
    )

    data class DataModel(
        val id: Int?,
        val type: String?,
        val name: String?,
        val phone: String?,
        val username: String?,
        val password: String?,
        val latitude: String?,
        val longitude: String?,
        val image: String?,
        val status: Int?,

        val id_driver: Int?,
        val pick_up_latitude: String?,
        val pick_up_longitude: String?,
        val id_hospital: Int?,

        val address: String?,

        val updated_at: String?,
        val created_at: String?,

        )

    data class DataOrderModel(
        val id: Int,
        val id_driver: String,
        val name: String,
        val phone: String,
        val pick_up_latitude: String,
        val pick_up_longitude: String,
        val id_hospital: String,

        val updated_at: String,
        val created_at: String,
        )

    data class DataHospitalModel(
        val id: Int,
        val name: String,
        val address: String,
        val latitude: String,
        val longitude: String,
        val image: String,

        val updated_at: String,
        val created_at: String,
        )
}