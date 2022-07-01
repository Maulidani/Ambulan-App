package com.skripsi.ambulanapp.ui.admin

import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
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

class AdminAddArticleActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }

    private val imgArticle: ImageView by lazy { findViewById(R.id.imgArticle) }
    private val inputTitle: TextInputEditText by lazy { findViewById(R.id.inputTitle) }
    private val inputDescription: TextInputEditText by lazy { findViewById(R.id.inputDescription) }
    private val btnAdd: MaterialButton by lazy { findViewById(R.id.btnAdd) }

    private var intentAction = ""
    private var intentUser = ""
    private var intentId = ""
    private var intentTitle = ""
    private var intentDescription = ""
    private var intentImage = ""

    private var imgNewSource = false
    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_article)

        intentAction = intent.getStringExtra("action").toString()
        intentUser = intent.getStringExtra("user").toString()
        intentId = intent.getStringExtra("id").toString()
        intentImage = intent.getStringExtra("image").toString()
        intentTitle = intent.getStringExtra("title").toString()
        intentDescription = intent.getStringExtra("description").toString()

        if (intentAction == "show" || intentAction == "edit") {

            val linkImage = "${Link.URL_IMAGE_ARTICLE}${intentImage}"
            imgArticle.load(linkImage)

            inputTitle.setText(intentTitle)
            inputDescription.setText(intentDescription)

            if (intentAction == "show") {
                btnAdd.visibility = View.GONE
                inputTitle.isEnabled = false
                inputDescription.isEnabled = false

            } else if (intentAction == "edit") {
                btnAdd.text = "Edit artikel"
            }
        }

        onClick()
    }

    private fun onClick() {

        imgBack.setOnClickListener { finish() }

        imgArticle.setOnClickListener {
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

            val title = inputTitle.text.toString()
            val description = inputDescription.text.toString()

            if (title.isNotEmpty() && description.isNotEmpty()) {
                //do
                if (intentAction == "add") {
                    if (partImage != null) {
                        addEditArticle(title, description)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Foto tidak boleh kosong",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else { // intentAction == "edit"
                    if (title == intentTitle && description == intentDescription &&
                        !imgNewSource
                    ) {

                        Toast.makeText(
                            applicationContext,
                            "Tidak ada data yang berubah",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    } else {
                        addEditArticle(title, description)
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
        text = if (showProgress == true) "" else "Login driver"

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

    private fun addEditArticle(
        title: String,
        description: String,
    ) {
        btnAdd.setShowProgress(true)

        val partId: RequestBody = intentId.toRequestBody("text/plain".toMediaTypeOrNull())
        val partTitle: RequestBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val partDescription: RequestBody =
            description.toRequestBody("text/plain".toMediaTypeOrNull())

        if (intentAction == "add") {

            CoroutineScope(Dispatchers.IO).launch {

                ApiClient.instances.addArticle(
                    partTitle,
                    partDescription,
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
                                Log.e(applicationContext.toString(), "onResponse: "+response.message().toString(), )

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
                    ApiClient.instances.editArticle(
                        partId,
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
                    ApiClient.instances.editWithoutImgArticle(
                        intentId,
                        title,
                        description,
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
                imgArticle.setImageBitmap(BitmapFactory.decodeFile(image.absolutePath))

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