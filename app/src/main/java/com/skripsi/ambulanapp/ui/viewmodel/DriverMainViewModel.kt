package com.skripsi.ambulanapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.repository.Repository
import com.skripsi.ambulanapp.util.ScreenState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverMainViewModel(private val repository: Repository = Repository(ApiClient.instances)) :
    ViewModel() {

    private var _characterLivedata = MutableLiveData<ScreenState<Model.ResponseModel?>>()

    val driverLiveData: MutableLiveData<ScreenState<Model.ResponseModel?>>
        get() = _characterLivedata

    init {
        getDriver()
    }

    private fun getDriver() {

        _characterLivedata.postValue(ScreenState.Loading(null))

       ApiClient.instances.getDriverUser().enqueue(object : Callback<Model.ResponseModel> {
            override fun onResponse(
                call: Call<Model.ResponseModel>,
                response: Response<Model.ResponseModel>
            ) {
                val responseBody = response.body()
                val message = response.body()?.message
                val data = response.body()?.data

                if (response.isSuccessful) {
                    Log.e("driver", "$message: $data")
                    _characterLivedata.postValue(ScreenState.Success(responseBody))

                } else {
                    Log.e("character", "not success: " + response.code().toString())
                    _characterLivedata.postValue(ScreenState.Error(response.code().toString(),null))
                }
            }

            override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                Log.e("character", "failure: " + t.message.toString())
                _characterLivedata.postValue(ScreenState.Error(t.message.toString(),null))
            }

        })
    }
}