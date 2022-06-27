package com.skripsi.ambulanapp.repository

import com.skripsi.ambulanapp.network.ApiService

class Repository(private val apiService: ApiService) {

    fun getOrdering() = apiService.getOrder()
    fun getLatLngDriver() = apiService.getDriverUser()
    fun getLatLngHospital() = apiService.getHospital(null,"")

}