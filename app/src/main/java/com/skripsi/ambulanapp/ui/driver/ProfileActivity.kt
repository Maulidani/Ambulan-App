package com.skripsi.ambulanapp.ui.driver

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var progressDialog: ProgressDialog

    private val imgProfile: ImageView by lazy { findViewById(R.id.imgProfile) }
    private val tvName: TextView by lazy { findViewById(R.id.tvName) }
    private val tvPhone: TextView by lazy { findViewById(R.id.tvPhone) }
    private val tvUsername: TextView by lazy { findViewById(R.id.tvUsername) }
    private val tvCar: TextView by lazy { findViewById(R.id.tvCar) }
    private val tvCarNumber: TextView by lazy { findViewById(R.id.tvCarNumber) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.title = "Profile"

        sharedPref = PreferencesHelper(this)
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("memuat informasi...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.show()

        val id = sharedPref.getString(Constant.PREF_ID_USER).toString()
        getDriver(id)

    }

    private fun getDriver(id: String) {
        ApiClient.instances.getDriverUser()
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
                            setProfil(data, id)
                        }

                    } else {
                        Toast.makeText(this@ProfileActivity, "gagal", Toast.LENGTH_SHORT).show()
                    }
                    progressDialog.dismiss()

                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@ProfileActivity,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }

    private fun setProfil(data: List<Model.DataModel>?, id: String) {

        if (data != null) {
            for (i in data) {

                if (id == i.id.toString()) {
                    tvName.text = i.name
                    tvPhone.text = i.phone
                    tvUsername.text = i.username
                    tvCar.text = i.car_type
                    tvCarNumber.text = i.car_number
                    var linkImage = "${Constant.URL_IMAGE_USER}${i.image}"
                    imgProfile.load(linkImage)
                }
            }
        }

    }
}