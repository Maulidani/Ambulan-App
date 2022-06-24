package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterListAccount
import com.skripsi.ambulanapp.adapter.AdapterListOrderHistory
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminListOrderHistoryActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rvOrderHistory) }
    private val loading: SpinKitView by lazy { findViewById(R.id.spin_kit_loading_account) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_order_history)

        onClick()
    }

    private fun onClick() {
        imgBack.setOnClickListener { finish() }
    }

    private fun getAccountUserList() {
        CoroutineScope(Dispatchers.IO).launch {

            ApiClient.instances.getOrder()
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = response.body()?.message
                        val data = response.body()?.data

                        if (response.isSuccessful && message == "Success") {

                            Log.e("onResponse: ", data.toString())

                            val adapter =
                                data?.let {
                                    AdapterListOrderHistory(
                                        it,
                                    )
                                }
                            rv.layoutManager = LinearLayoutManager(applicationContext)
                            rv.adapter = adapter

                            if ("${data?.size}" == "0") {
                                Toast.makeText(
                                    applicationContext,
                                    "Tidak ada data akun driver",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                        } else {
                            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                                .show()

                        }

                        loading.visibility = View.GONE
                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            t.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                        loading.visibility = View.GONE
                    }

                })
        }
    }

    override fun onResume() {
        super.onResume()
        loading.visibility = View.VISIBLE
        getAccountUserList()

    }

}