package com.skripsi.ambulanapp.ui.admin

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AdminAddArticleActivity : AppCompatActivity() {
    private val TAG = "AdminAddHospitalActvty"

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    private val tvHead: TextView by lazy { findViewById(R.id.tvHead) }
    private val inputTitle: TextInputEditText by lazy { findViewById(R.id.inputTitle) }
    private val inputDescription: TextInputEditText by lazy { findViewById(R.id.inputDescription) }
    private val imgArticle: ImageView by lazy { findViewById(R.id.imgArticle) }
    private val btnAdd: MaterialButton by lazy { findViewById(R.id.btnAddArticle) }

    private var imgNewSource = false
    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

    private var intentAction = ""
    private var intentUserType = ""
    private var intentId = ""
    private var intentTitle = ""
    private var intentDescription = ""
    private var intentImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_article)
        intentAction = intent.getStringExtra("action").toString()
        intentUserType = intent.getStringExtra("user_type").toString()
        intentId = intent.getStringExtra("id").toString()
        intentTitle = intent.getStringExtra("title").toString()
        intentDescription = intent.getStringExtra("description").toString()
        intentImage = intent.getStringExtra("image").toString()

        if (intentAction == "show" || intentAction == "edit") {
            val linkImage = "${Constant.BASE_URL}${intentImage}"

            imgArticle.load(linkImage)
            inputTitle.setText(intentTitle)
            inputDescription.setText(intentDescription)

            if (intentAction == "show") {
                tvHead.text = "Artikel, Info Pelayanan"
                btnAdd.visibility = View.GONE
                inputTitle.setTextColor(Color.BLACK)
                inputDescription.setTextColor(Color.BLACK)
                inputTitle.isEnabled = false
                inputDescription.isEnabled = false

            } else if (intentAction == "edit") {
                btnAdd.text = "Edit info pelayanan"

            } else {
                btnAdd.text = "Tambah info pelayanan"
            }
        }

        onClick()
    }

    private fun onClick() {

        imgBack.setOnClickListener { finish() }

        imgArticle.setOnClickListener {
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

            val title = inputTitle.text.toString()
            val description = inputDescription.text.toString()

            if (
                title.isNotEmpty() && description.isNotEmpty()
            ) {
                if (intentAction == "edit") {
                    if (title == intentTitle && description == intentDescription &&
                        !imgNewSource
                    ) {
                        Toast.makeText(
                            applicationContext, "Tidak ada data yang berubah",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        editArticle(title, description)
                    }

                } else {

                    if (partImage != null) {
                        addArticle(title, description)
                    } else {
                        Toast.makeText(
                            applicationContext, "Lengkapi data foto",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            } else {
                Toast.makeText(applicationContext, "Lengkapi data", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun addArticle(title: String, description: String) {
        btnAdd.setShowProgress(true)

        val partTitle: RequestBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val partDescription: RequestBody =
            description.toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.instances.addArticle(
            partTitle,
            partDescription,
            partImage!!
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
                            "Berhasil menambah artikel, info pelayanan",
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

    private fun editArticle(title: String, description: String) {
        btnAdd.setShowProgress(true)

        if (imgNewSource) {
            val partArticleId: RequestBody =
                intentId.toRequestBody("text/plain".toMediaTypeOrNull())
            val partTitle: RequestBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val partDescription: RequestBody =
                description.toRequestBody("text/plain".toMediaTypeOrNull())

            ApiClient.instances.editArticle(
                partArticleId,
                partTitle,
                partDescription,
                partImage!!
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
            ApiClient.instances.editWithoutImgArticle(
                intentId,
                title,
                description,
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
                imgArticle.setImageBitmap(BitmapFactory.decodeFile(image.absolutePath))

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