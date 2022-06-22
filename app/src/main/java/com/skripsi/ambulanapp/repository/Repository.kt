package com.skripsi.ambulanapp.repository

import com.skripsi.ambulanapp.network.ApiService

class Repository(private val apiService: ApiService) {

    fun getDriver(page: String) = apiService.getDriverUser()

}