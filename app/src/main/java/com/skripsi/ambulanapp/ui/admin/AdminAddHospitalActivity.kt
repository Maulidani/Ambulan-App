package com.skripsi.ambulanapp.ui.admin

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.Link
import com.skripsi.ambulanapp.network.model.Model
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }

//    private val parentScrollView: ScrollView by lazy { findViewById(R.id.parentScrollView) }
//    private val loading: SpinKitView by lazy { findViewById(R.id.spin_kit_loading_account) }

    private val inputName: TextInputEditText by lazy { findViewById(R.id.inputName) }
    private val inputAddress: TextInputEditText by lazy { findViewById(R.id.inputAddress) }
    private val inputLatitude: TextInputEditText by lazy { findViewById(R.id.inputLatitude) }
    private val inputLongitude: TextInputEditText by lazy { findViewById(R.id.inputLongitude) }
    private val imgHospital: ImageView by lazy { findViewById(R.id.imgHospital) }
    private val btnAdd: MaterialButton by lazy { findViewById(R.id.btnAddHospital) }

    private var intentAction = ""
    private var intentUser = ""
    private var intentId = ""
    private var intentName = ""
    private var intentAddress = ""
    private var intentLatitude = ""
    private var intentLongitude = ""
    private var intentImage = ""

    private var imgNewSource = false
    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_hospital)

        intentAction = intent.getStringExtra("action").toString()
        intentUser = intent.getStringExtra("user").toString()
        intentId = intent.getStringExtra("id").toString()
        intentImage = intent.getStringExtra("image").toString()
        intentName = intent.getStringExtra("name").toString()
        intentAddress = intent.getStringExtra("address").toString()
        intentLatitude = intent.getStringExtra("latitude").toString()
        intentLongitude = intent.getStringExtra("longitude").toString()

        if (intentAction == "show" || intentAction == "edit") {

            val linkImage = "${Link.URL_IMAGE_HOSPITAL}${intentImage}"
            imgHospital.load(linkImage)

            inputName.setText(intentName)
            inputAddress.setText(intentAddress)
            inputLatitude.setText(intentLatitude)
            inputLongitude.setText(intentLongitude)

            if (intentAction == "show") {
                btnAdd.visibility = View.GONE
                inputName.isEnabled = false
                inputAddress.isEnabled = false
                inputLatitude.isEnabled = false
                inputLongitude.isEnabled = false

            } else if (intentAction == "edit") {
                btnAdd.text = "Edit rumah sakit"
            } else if(intentAction == "add"){
                btnAdd.text = "Tambah rumah sakit"
            }
        }

        onClick()
    }

    private fun onClick() {

        imgBack.setOnClickListener { finish() }

        imgHospital.setOnClickListener {
            if (intentAction == "add" || intentAction == "edit") {
                ImagePicker.with(this)
                    .cropSquare()
                    .compress(1024)         //Final image size will be less than 1 MB(Optional)
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
                //do
                if (intentAction == "add") {
                    if (partImage != null) {
                        addEdithospital(name, address, latitude, longitude)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Foto tidak boleh kosong",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else { // intentAction == "edit"
                    if (name == intentName && address == intentAddress &&
                        latitude == intentLatitude && longitude == intentLongitude &&
                        !imgNewSource
                    ) {

                        Toast.makeText(
                            applicationContext,
                            "Tidak ada data yang berubah",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    } else {
                        addEdithospital(name, address, latitude, longitude)
                    }
                }

            } else {

                Toast.makeText(applicationContext, "Data tidak boleh kosong", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }


    fun MaterialButton.setShowProgress(showProgress: Boolean?) {

        iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        isCheckable = showProgress == false
        text = if (showProgress == true) "" else "Coba lagi"

        icon = if (showProgress == true) {
            CircularProgressDrawable(context!!).apply {
                setStyle(CircularProgressDrawable.DEFAULT)
                setColorSchemeColors(ContextCompat.getColor(context!!, R.color.white))
                start()
            }
        } else null

        if (icon != null) { // callback to redraw button icon
            icon.callback = object : Drawable.Callback {
                override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                }

                override fun invalidateDrawable(who: Drawable) {
                    this@setShowProgress.invalidate()
                }

                override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
                }
            }
        }
    }

    private fun addEdithospital(
        name: String,
        address: String,
        latitude: String,
        longitude: String
    ) {
        btnAdd.setShowProgress(true)

        val partId: RequestBody = intentId.toRequestBody("text/plain".toMediaTypeOrNull())
        val partName: RequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val partAddress: RequestBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val partLatitude: RequestBody = latitude.toRequestBody("text/plain".toMediaTypeOrNull())
        val partLongitude: RequestBody = longitude.toRequestBody("text/plain".toMediaTypeOrNull())
//        var partImgNull: RequestBody? = "".toRequestBody("text/plain".toMediaTypeOrNull())

        if (intentAction == "add") {

            CoroutineScope(Dispatchers.IO).launch {

                ApiClient.instances.addHospital(
                    partName,
                    partAddress,
                    partLatitude,
                    partLongitude,
                    partImage!!,
                )
                    .enqueue(object : Callback<Model.ResponseModel> {
                        override fun onResponse(
                            call: Call<Model.ResponseModel>,
                            response: Response<Model.ResponseModel>
                        ) {
                            val responseBody = response.body()
                            val message = response.body()?.message

                            if (response.isSuccessful && message == "Success") {

                                finish()

                            } else {
                                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                            btnAdd.setShowProgress(false)

                        }

                        override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                t.message.toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            btnAdd.setShowProgress(false)
                        }
                    })
            }

        } else if (intentAction == "edit") {

            CoroutineScope(Dispatchers.IO).launch {

                if (imgNewSource) {
                    ApiClient.instances.editHospital(
                        partId,
                        partName,
                        partAddress,
                        partLatitude,
                        partLongitude,
                        partImage!!
                    )
                        .enqueue(object : Callback<Model.ResponseModel> {
                            override fun onResponse(
                                call: Call<Model.ResponseModel>,
                                response: Response<Model.ResponseModel>
                            ) {
                                val responseBody = response.body()
                                val message = response.body()?.message

                                if (response.isSuccessful && message == "Success") {

                                    finish()

                                } else {
                                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                                        .show()
                                }
                                btnAdd.setShowProgress(false)

                            }

                            override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                                Toast.makeText(
                                    applicationContext,
                                    t.message.toString(),
                                    Toast.LENGTH_SHORT
                                )
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
                        ""
                    )
                        .enqueue(object : Callback<Model.ResponseModel> {
                            override fun onResponse(
                                call: Call<Model.ResponseModel>,
                                response: Response<Model.ResponseModel>
                            ) {
                                val responseBody = response.body()
                                val message = response.body()?.message

                                if (response.isSuccessful && message == "Success") {

                                    finish()

                                } else {
                                    Log.e("onResponse: " , response.toString())
                                }
                                btnAdd.setShowProgress(false)

                            }

                            override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                                Toast.makeText(
                                    applicationContext,
                                    t.message.toString(),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                btnAdd.setShowProgress(false)
                            }
                        })
                }
            }
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

                Log.e("", "image format: uri = $fileUri")
                Log.e("", "image format: file path = $image")
                Log.e("", "image format: file absolute path = ${image.absolutePath}")

                reqBody = image.asRequestBody("image/*".toMediaTypeOrNull())
                partImage = MultipartBody.Part.createFormData("image", image.name, reqBody!!)
                imgNewSource = true

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onResume() {
        super.onResume()

        if (imgNewSource) {
            Log.e("", "image attached: new source")
        } else {
            Log.e("", "image attached: nothing new source")
        }
    }

}