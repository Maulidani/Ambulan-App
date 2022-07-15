package com.skripsi.ambulanapp.ui.admin

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.Constant.setShowProgress
import com.skripsi.ambulanapp.util.PreferencesHelper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AdminAddHospitalActivity : AppCompatActivity() {
    private val TAG = "AdminAddHospitalActvty"

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    private val inputName: TextInputEditText by lazy { findViewById(R.id.inputName) }
    private val inputAddress: TextInputEditText by lazy { findViewById(R.id.inputAddress) }
    private val inputLatitude: TextInputEditText by lazy { findViewById(R.id.inputLatitude) }
    private val inputLongitude: TextInputEditText by lazy { findViewById(R.id.inputLongitude) }
    private val imgHospital: ImageView by lazy { findViewById(R.id.imgHospital) }
    private val btnAdd: MaterialButton by lazy { findViewById(R.id.btnAddHospital) }

    private var imgNewSource = false
    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

    private var intentAction = ""
    private var intentUserType = ""
    private var intentId = ""
    private var intentName = ""
    private var intentAddress = ""
    private var intentImage = ""
    private var intentLatitude = ""
    private var intentLongitude = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_hospital)

        intentAction = intent.getStringExtra("action").toString()
        intentUserType = intent.getStringExtra("user_type").toString()
        intentId = intent.getStringExtra("id").toString()
        intentName = intent.getStringExtra("name").toString()
        intentAddress = intent.getStringExtra("address").toString()
        intentImage = intent.getStringExtra("image").toString()
        intentLatitude = intent.getStringExtra("latitude").toString()
        intentLongitude = intent.getStringExtra("longitude").toString()

        if (intentAction == "show" || intentAction == "edit") {
            val linkImage = "${Constant.BASE_URL}${intentImage}"

            imgHospital.load(linkImage)
            inputName.setText(intentName)
            inputAddress.setText(intentAddress)
            inputLatitude.setText(intentLatitude)
            inputLongitude.setText(intentLongitude)

            if (intentAction == "show") {
                btnAdd.visibility = View.GONE
                inputName.setTextColor(Color.BLACK)
                inputAddress.setTextColor(Color.BLACK)
                inputLatitude.setTextColor(Color.BLACK)
                inputLongitude.setTextColor(Color.BLACK)
                inputName.isEnabled = false
                inputAddress.isEnabled = false
                inputLatitude.isEnabled = false
                inputLongitude.isEnabled = false


            } else if (intentAction == "edit") {
                btnAdd.text = "Edit rumah sakit"

            } else {
                btnAdd.text = "Tambah rumah sakit"
            }
        }

        onClick()
    }

    private fun onClick() {

        imgBack.setOnClickListener { finish() }

        imgHospital.setOnClickListener {
            if (intentAction != "show") {
                ImagePicker.with(this)
                    .crop()
                    .compress(512)         //Final image size will be less than 512 KB(Optional)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }
        }

        btnAdd.setOnClickListener {

            val name = inputName.text.toString()
            val address = inputAddress.text.toString()
            val latitude = inputLatitude.text.toString()
            val longitude = inputLongitude.text.toString()

            if (
                name.isNotEmpty() && address.isNotEmpty() &&
                latitude.isNotEmpty() && longitude.isNotEmpty()
            ) {

                if (latitude.toDoubleOrNull() == null) {
                    Toast.makeText(
                        applicationContext, "Data latitude tidak valid",
                        Toast.LENGTH_SHORT
                    ).show()

                } else if (longitude.toDoubleOrNull() == null) {
                    Toast.makeText(
                        applicationContext, "Data longitude tidak valid",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    if (intentAction == "edit") {
                        if (name == intentName && address == intentAddress &&
                            latitude == intentLatitude && longitude == intentLongitude &&
                            !imgNewSource
                        ) {
                            Toast.makeText(
                                applicationContext, "Tidak ada data yang berubah",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            editHospital(name, address, latitude, longitude)
                        }

                    } else {

                        if (partImage != null) {
                            addHospital(name, address, latitude, longitude)
                        } else {
                            Toast.makeText(
                                applicationContext, "Lengkapi data foto rumah sakit",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            } else {
                Toast.makeText(applicationContext, "Lengkapi data", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun addHospital(name: String, address: String, latitude: String, longitude: String) {
        btnAdd.setShowProgress(true)

        val partName: RequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val partAddress: RequestBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val partLatitude: RequestBody = latitude.toRequestBody("text/plain".toMediaTypeOrNull())
        val partLongitude: RequestBody = longitude.toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.instances.addHospital(
            partName,
            partAddress,
            partImage!!,
            partLatitude,
            partLongitude,
        )
            .enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {
                    val responseBody = response.body()
                    val message = responseBody?.message
                    val hospital = responseBody?.hospital

                    if (response.isSuccessful && message == "Success") {
                        Log.e(TAG, "onResponse: $responseBody")
                        Toast.makeText(
                            applicationContext,
                            "Berhasil menambah rumah sakit",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {
                        Log.e(TAG, "onResponse: $response")
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()

                    }
                    btnAdd.setShowProgress(false)

                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {

                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    btnAdd.setShowProgress(false)
                }

            })
    }

    private fun editHospital(name: String, address: String, latitude: String, longitude: String) {
        btnAdd.setShowProgress(true)

        if (imgNewSource) {
            val partHospitalId: RequestBody = intentId.toRequestBody("text/plain".toMediaTypeOrNull())
            val partName: RequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val partAddress: RequestBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
            val partLatitude: RequestBody = latitude.toRequestBody("text/plain".toMediaTypeOrNull())
            val partLongitude: RequestBody = longitude.toRequestBody("text/plain".toMediaTypeOrNull())

            ApiClient.instances.editHospital(
                partHospitalId,
                partName,
                partAddress,
                partImage!!,
                partLatitude,
                partLongitude,
            )
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = responseBody?.message
                        val hospital = responseBody?.hospital

                        if (response.isSuccessful && message == "Success") {
                            Log.e(TAG, "onResponse: $responseBody")

                            finish()

                        } else {
                            Log.e(TAG, "onResponse: $response")
                            Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()

                        }
                        btnAdd.setShowProgress(false)

                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {

                        Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                        btnAdd.setShowProgress(false)
                    }
                })

        } else {
            ApiClient.instances.editWithoutImgHospital(
                intentId,
                name,
                address,
                latitude,
                longitude,
            )
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = responseBody?.message
                        val hospital = responseBody?.hospital

                        if (response.isSuccessful && message == "Success") {
                            Log.e(TAG, "onResponse: $responseBody")

                            finish()

                        } else {
                            Log.e(TAG, "onResponse: $response")
                            Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()

                        }
                        btnAdd.setShowProgress(false)

                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {

                        Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                        btnAdd.setShowProgress(false)
                    }

                })
        }
    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
//                    imageView.setImageURI(fileUri)

                val image: File = File(fileUri.path!!)
                imgHospital.setImageBitmap(BitmapFactory.decodeFile(image.absolutePath))

                Log.e(TAG, "image format: uri = $fileUri")
                Log.e(TAG, "image format: file path = $image")
                Log.e(TAG, "image format: file absolute path = ${image.absolutePath}")

                reqBody = image.asRequestBody("image/*".toMediaTypeOrNull())
                partImage = MultipartBody.Part.createFormData("image", image.name, reqBody!!)
                imgNewSource = true

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}