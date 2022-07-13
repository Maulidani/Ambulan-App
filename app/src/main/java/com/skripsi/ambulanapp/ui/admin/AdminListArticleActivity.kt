package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterListArticle
import com.skripsi.ambulanapp.adapter.AdapterListHospital
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminListArticleActivity : AppCompatActivity(), AdapterListArticle.IUserRecycler {
    private val TAG = "AdminListArticleActvty"
    private val userType = "admin"

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    private val fabAddArticle: FloatingActionButton by lazy { findViewById(R.id.fabAddArticle) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rvArticle) }
    private val etSearchArticle: EditText by lazy { findViewById(R.id.etSearchArticle) }
    private val loading: SpinKitView by lazy { findViewById(R.id.spin_kit_loading_article) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_article)

        onClick()
    }

    private fun onClick() {
        imgBack.setOnClickListener { finish() }

        fabAddArticle.setOnClickListener {
            startActivity(
                Intent(this, AdminAddArticleActivity::class.java)
                    .putExtra("action", "add")
            )
        }

        etSearchArticle.addTextChangedListener {
            val search = it.toString()
            getArticle(search)
        }
    }

    private fun getArticle(search:String) {
        loading.visibility = View.VISIBLE

        ApiClient.instances.getArticle(search)
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
                                AdapterListArticle(
                                    userType,
                                    it,
                                    this@AdminListArticleActivity
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
        getArticle("")
    }

    override fun onResume() {
        super.onResume()

        getArticle("")
    }
}