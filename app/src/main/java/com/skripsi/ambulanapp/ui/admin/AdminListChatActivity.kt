package com.skripsi.ambulanapp.ui.admin

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterListAccount
import com.skripsi.ambulanapp.adapter.AdapterUserChat
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.util.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminListChatActivity : AppCompatActivity() {
    private val TAG = "AdminListArticleActvty"
    private var userId: String? = null
    private var userType: String? = null
    private lateinit var sharedPref: PreferencesHelper

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rvUserChat) }
    private val loading: SpinKitView by lazy { findViewById(R.id.spin_kit_loading_article) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_chat)

        sharedPref = PreferencesHelper(applicationContext)
        userId = sharedPref.getString(PreferencesHelper.PREF_USER_TYPE)
        userType = sharedPref.getString(PreferencesHelper.PREF_USER_TYPE)

        getUserDriver()

        onClick()
    }

    private fun onClick() {
        imgBack.setOnClickListener { finish() }
    }

    private fun getUserDriver() {
        loading.visibility = View.VISIBLE

        ApiClient.instances.getUser("","driver", "for_admin")
            .enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {
                    val responseBody = response.body()
                    val message = responseBody?.message
                    val data = responseBody?.data

                    if (response.isSuccessful && message == "Success") {

                        Log.e(TAG, "onResponse: $responseBody")

                        val adapter =
                            data?.let {
                                AdapterUserChat(
                                    userType!!,
                                    it,
                                )
                            }
                        rv.layoutManager = LinearLayoutManager(applicationContext)
                        rv.adapter = adapter

                        if ("${data?.size}" == "0") {
//                            Toast.makeText(
//                                applicationContext, "Tidak ada data",
//                                Toast.LENGTH_SHORT
//                            ).show()
                        }

                    } else {
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
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