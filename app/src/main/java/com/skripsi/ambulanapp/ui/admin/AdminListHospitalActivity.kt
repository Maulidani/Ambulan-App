package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterListHospital
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.util.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminListHospitalActivity : AppCompatActivity(), AdapterListHospital.IUserRecycler {
    private val TAG = "AdminListHospitalActvty"
    private var userType: String? = null
    private lateinit var sharedPref: PreferencesHelper

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    private val fabAddHospital: FloatingActionButton by lazy { findViewById(R.id.fabAddHospital) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rvHospital) }
    private val etSearchHospital: EditText by lazy { findViewById(R.id.etSearchHospital) }
    private val loading: SpinKitView by lazy { findViewById(R.id.spin_kit_loading_hospital) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_hospital)

        sharedPref = PreferencesHelper(applicationContext)
        userType = sharedPref.getString(PreferencesHelper.PREF_USER_TYPE)

        if (userType == "customer"){
            fabAddHospital.visibility = View.GONE
        }

        onClick()
    }

    private fun onClick() {
        imgBack.setOnClickListener { finish() }

        fabAddHospital.setOnClickListener {
            startActivity(
                Intent(this, AdminAddHospitalActivity::class.java)
                    .putExtra("action", "add")
            )
        }

        etSearchHospital.addTextChangedListener {
            val search = it.toString()
            getHospitalList(search)
        }
    }

    private fun getHospitalList(search: String) {
        loading.visibility = View.VISIBLE

        ApiClient.instances.getHospital(search)
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
                                AdapterListHospital(
                                    userType!!,
                                    it,
                                    this@AdminListHospitalActivity
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

    override fun refreshView(onUpdate: Boolean, result: Model.DataModel?) {
        getHospitalList("")
    }

    override fun onResume() {
        super.onResume()

        getHospitalList("")
    }
}