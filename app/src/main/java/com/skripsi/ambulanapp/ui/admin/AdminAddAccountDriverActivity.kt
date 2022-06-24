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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import coil.load
import coil.transform.CircleCropTransformation
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterListAccount
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.Link
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.util.PreferencesHelper
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

class AdminAddAccountDriverActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }

    private val parentImgNamePhone: ConstraintLayout by lazy { findViewById(R.id.parentImgNamePhone) }
    private val userNameTextField: TextInputLayout by lazy { findViewById(R.id.usernameTextField) }
    private val passwordTextField: TextInputLayout by lazy { findViewById(R.id.passwordTextField) }
    private val loading: SpinKitView by lazy { findViewById(R.id.spin_kit_loading_account) }

    private val inputName: TextInputEditText by lazy { findViewById(R.id.inputName) }
    private val inputPhone: TextInputEditText by lazy { findViewById(R.id.inputPhone) }
    private val inputUsername: TextInputEditText by lazy { findViewById(R.id.inputUsername) }
    private val inputPassword: TextInputEditText by lazy { findViewById(R.id.inputPassword) }
    private val imgProfile: ImageView by lazy { findViewById(R.id.imgProfile) }
    private val btnAdd: MaterialButton by lazy { findViewById(R.id.btnAdd) }

    private var intentAction = ""
    private var intentUser = ""
    private var intentId = ""
    private var intentName = ""
    private var intentPhone = ""
    private var intentUsername = ""
    private var intentPassword = ""
    private var intentImage = ""

    private var imgNewSource = false
    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_account_driver)

        intentAction = intent.getStringExtra("action").toString()
        intentUser = intent.getStringExtra("user").toString()
        intentId = intent.getStringExtra("id").toString()
        intentImage = intent.getStringExtra("image").toString()
        intentName = intent.getStringExtra("name").toString()
        intentPhone = intent.getStringExtra("phone").toString()
        intentUsername = intent.getStringExtra("username").toString()
        intentPassword = intent.getStringExtra("password").toString()

        if (intentAction == "show" || intentAction == "edit") {

            imgProfile.setBackgroundColor(0)
            val linkImage = "${Link.URL_IMAGE_USER}${intentImage}"
            imgProfile.load(linkImage) {
                transformations(CircleCropTransformation())
            }

            inputName.setText(intentName)
            inputPhone.setText(intentPhone)
            inputUsername.setText(intentUsername)
            inputPassword.setText(intentPassword)

            if (intentAction == "show") {
                btnAdd.visibility = View.GONE
                inputName.isEnabled = false
                inputPhone.isEnabled = false
                inputUsername.isEnabled = false
                inputPassword.isEnabled = false

                if (intentUser == "driver") {
                    loading.visibility = View.VISIBLE
                    parentImgNamePhone.visibility = View.GONE
                    userNameTextField.visibility = View.GONE
                    passwordTextField.visibility = View.GONE

                    getDataProfile(intentId)
                }

            } else if (intentAction == "edit") {
                btnAdd.text = "Edit akun driver"
            }
        }

        onClick()
    }

    private fun onClick() {

        imgBack.setOnClickListener { finish() }

        imgProfile.setOnClickListener {
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
            val phone = inputPhone.text.toString()
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()

            if (
                name.isNotEmpty() && phone.isNotEmpty() &&
                username.isNotEmpty() && password.isNotEmpty()
            ) {
                //do
                if (intentAction == "add") {
                    if (partImage != null) {
                        addEditAccount(name, phone, username, password)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Foto tidak boleh kosong",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    if (name == intentName && phone == intentPhone &&
                        username == intentUsername && password == intentPassword &&
                        !imgNewSource
                    ) {

                        Toast.makeText(
                            applicationContext,
                            "Tidak ada data yang berubah",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                    } else {
                        addEditAccount(name, phone, username, password)
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

    private fun addEditAccount(name: String, phone: String, username: String, password: String) {
        btnAdd.setShowProgress(true)

        val partId: RequestBody = intentId.toRequestBody("text/plain".toMediaTypeOrNull())
        val partName: RequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val partPhone: RequestBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val partUsername: RequestBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
        val partPassword: RequestBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
        val partType: RequestBody = "driver".toRequestBody("text/plain".toMediaTypeOrNull())
//        var partImgNull: RequestBody? = "".toRequestBody("text/plain".toMediaTypeOrNull())

        if (intentAction == "add") {

            CoroutineScope(Dispatchers.IO).launch {

                ApiClient.instances.addUser(
                    partType,
                    partName,
                    partPhone,
                    partImage!!,
                    partUsername,
                    partPassword
                )
                    .enqueue(object : Callback<Model.ResponseModel> {
                        override fun onResponse(
                            call: Call<Model.ResponseModel>,
                            response: Response<Model.ResponseModel>
                        ) {
                            val responseBody = response.body()
                            val message = response.body()?.message

                            if (response.isSuccessful) {

                                if (message == "Success") {
                                    finish()
                                } else if (message == "Exist") {
                                    Toast.makeText(
                                        applicationContext,
                                        "Username ini sudah ada",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                } else {
                                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                                        .show()
                                }

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
                    ApiClient.instances.editUser(
                        partId,
                        partName,
                        partPhone,
                        partImage!!,
                        partUsername,
                        partPassword
                    )
                        .enqueue(object : Callback<Model.ResponseModel> {
                            override fun onResponse(
                                call: Call<Model.ResponseModel>,
                                response: Response<Model.ResponseModel>
                            ) {
                                val responseBody = response.body()
                                val message = response.body()?.message

                                if (response.isSuccessful) {

                                    if (message == "Success") {
                                        finish()
                                    } else if (message == "Exist") {
                                        Toast.makeText(
                                            applicationContext,
                                            "Username ini sudah ada",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            message,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }

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
                    ApiClient.instances.editWithoutImgUser(
                        intentId,
                        name,
                        phone,
                        "",
                        username,
                        password
                    )
                        .enqueue(object : Callback<Model.ResponseModel> {
                            override fun onResponse(
                                call: Call<Model.ResponseModel>,
                                response: Response<Model.ResponseModel>
                            ) {
                                val responseBody = response.body()
                                val message = response.body()?.message

                                if (response.isSuccessful) {

                                    if (message == "Success") {
                                        finish()
                                    } else if (message == "Exist") {
                                        Toast.makeText(
                                            applicationContext,
                                            "Username ini sudah ada",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    } else {
                                        Toast.makeText(
                                            applicationContext,
                                            message,
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }

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

    private fun getDataProfile(id:String){
        CoroutineScope(Dispatchers.IO).launch {

            ApiClient.instances.getDriverUser()
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

                            if (data != null) {
                                for (i in data){
                                    if (i.id == id.toInt()) {
                                        loading.visibility = View.GONE
                                        parentImgNamePhone.visibility = View.VISIBLE
                                        userNameTextField.visibility = View.VISIBLE
                                        passwordTextField.visibility = View.VISIBLE

                                        imgProfile.setBackgroundColor(0)
                                        val linkImage = "${Link.URL_IMAGE_USER}${i.image}"
                                        imgProfile.load(linkImage) {
                                            transformations(CircleCropTransformation())
                                        }
                                        inputName.setText(i.name)
                                        inputPhone.setText(i.phone)
                                        inputUsername.setText(i.username)
                                        inputPassword.setText(i.password)

                                    }
                                }
                            } else {
                                Toast.makeText(applicationContext, "Data tidak ada", Toast.LENGTH_SHORT)
                                    .show()
                                finish()
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

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                //Image Uri will not be null for RESULT_OK
                val fileUri = data?.data!!
//                    imageView.setImageURI(fileUri)

                val image: File = File(fileUri.path!!)
                imgProfile.setImageBitmap(BitmapFactory.decodeFile(image.absolutePath))
                imgProfile.setBackgroundColor(0) // delete background

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