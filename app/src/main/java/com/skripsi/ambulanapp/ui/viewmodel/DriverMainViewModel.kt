package com.skripsi.ambulanapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.repository.Repository
import com.skripsi.ambulanapp.util.ScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverMainViewModel(private val repository: Repository = Repository(ApiClient.instances)) :
    ViewModel() {

    private var _Livedata = MutableLiveData<ScreenState<List<Model.DataModel>?>>()

    val liveData: LiveData<ScreenState<List<Model.DataModel>?>>
        get() = _Livedata

    init {
        getOrder()
    }

    private fun getOrder() {

        CoroutineScope(Dispatchers.Default).launch {
            var loop = true

            while (true) {

                _Livedata.postValue(ScreenState.Loading(null))

                val client = repository.getOrdering()
                client.enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = response.body()?.message
                        val data = response.body()?.data

                        if (response.isSuccessful && message == "Success") {
                            Log.e("order", "$message: $data")
                            _Livedata.postValue(ScreenState.Success(data))

                        } else {
                            Log.e("order", "not success: " + response.code().toString())
                            _Livedata.postValue(
                                ScreenState.Error(
                                    response.code().toString(),
                                    null
                                )
                            )

                            loop = false
                        }
                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                        Log.e("order", "failure: " + t.message.toString())
                        _Livedata.postValue(ScreenState.Error(t.message.toString(), null))

                        loop = false
                    }

                })

                delay(7000)
            }
        }
    }
}