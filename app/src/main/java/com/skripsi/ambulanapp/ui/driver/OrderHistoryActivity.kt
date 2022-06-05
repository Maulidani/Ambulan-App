package com.skripsi.ambulanapp.ui.driver

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterOrders
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var progressDialog: ProgressDialog
    private val rv: RecyclerView by lazy { findViewById(R.id.rvOrder) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        supportActionBar?.title = "Order"

        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(true)
        progressDialog.setCancelable(true)
        getOrderHistory()

    }

    private fun getOrderHistory() {
        sharedPref = PreferencesHelper(this)
        val idDriver = sharedPref.getString(Constant.PREF_ID_USER)
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        ApiClient.instances.showOrder("finish", idDriver.toString())
            .enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {

                    val message = response.body()?.message
                    val error = response.body()?.errors
                    val data = response.body()?.data

                    if (response.isSuccessful) {
                        if (error == false) {

                            val adapter =
                                data?.let { AdapterOrders(it) }
                            rv.layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
                            rv.adapter = adapter

                        } else {

                            Toast.makeText(this@OrderHistoryActivity, "gagal", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(this@OrderHistoryActivity, "gagal", Toast.LENGTH_SHORT)
                            .show()
                    }
                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@OrderHistoryActivity,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }
}