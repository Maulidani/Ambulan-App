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
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AdminAddAccountActivity : AppCompatActivity() {
    private val TAG = "CustomerRegistActivity"
    private val addUserType = "driver"

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    private val tvHead: TextView by lazy { findViewById(R.id.tvHead) }
    private val tvSubHead: TextView by lazy { findViewById(R.id.tvSubHead) }
    private val inputName: TextInputEditText by lazy { findViewById(R.id.inputName) }
    private val inputPhone: TextInputEditText by lazy { findViewById(R.id.inputPhone) }
    private val inputPassword: TextInputEditText by lazy { findViewById(R.id.inputPassword) }
    private val imgProfile: CircleImageView by lazy { findViewById(R.id.imgProfile) }
    private val btnAddAccount: MaterialButton by lazy { findViewById(R.id.btnAddAccount) }

    private var imgNewSource = false
    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

    private var intentAction = ""
    private var intentUserType = ""
    private var intentId = ""
    private var intentName = ""
    private var intentPhone = ""
    private var intentPassword = ""
    private var intentImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_account)

        intentAction = intent.getStringExtra("action").toString()
        intentUserType = intent.getStringExtra("user_type").toString()
        intentId = intent.getStringExtra("id").toString()
        intentName = intent.getStringExtra("name").toString()
        intentPhone = intent.getStringExtra("phone").toString()
        intentPassword = intent.getStringExtra("password").toString()
        intentImage = intent.getStringExtra("image").toString()

        if (intentUserType == "driver") {
            tvHead.text = "Buat akun driver ambulan"
        }

        if (intentAction == "show" || intentAction == "edit") {
            val linkImage = "${Constant.BASE_URL}${intentImage}"

            imgProfile.load(linkImage)
            inputName.setText(intentName)
            inputPhone.setText(intentPhone)
            inputPassword.setText(intentPassword)

            when (intentAction) {
                "show" -> {
                    if (intentUserType == "customer") {
                        tvHead.text = "Akun customer"

                    } else if (intentUserType == "driver") {
                        tvHead.text = "Akun driver ambulan"
                        tvSubHead.visibility = View.VISIBLE
                    }

                    btnAddAccount.visibility = View.GONE
                    inputName.setTextColor(Color.BLACK)
                    inputPhone.setTextColor(Color.BLACK)
                    inputPassword.setTextColor(Color.BLACK)
                    inputName.isEnabled = false
                    inputPhone.isEnabled = false
                    inputPassword.isEnabled = false

                }
                "edit" -> {
                    if (intentUserType == "driver") {
                        tvHead.text = "Edit akun driver ambulan"
                    } else if (intentUserType == "customer") {
                        tvHead.text = "Akun saya"
                        btnAddAccount.text = "Edit akun"

                    } else {
                        btnAddAccount.text = "Edit akun"
                    }

                }
                else -> {
                    btnAddAccount.text = "Buat akun"
                }
            }
        }

        onClick()
    }

    private fun onClick() {

        btnAddAccount.setOnClickListener {
            val name = inputName.text.toString()
            val phone = inputPhone.text.toString()
            val password = inputPassword.text.toString()

            if (name.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {

                if (phone.length < 9) {
                    Toast.makeText(
                        applicationContext,
                        "Nomor telepon tidak valid, terlalu pendek",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else if (password.length < 6) {
                    Toast.makeText(
                        applicationContext,
                        "Kata sandi harus 6 karakter atau lebih",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {

                    if (intentAction == "edit") {
                        if (name == intentName && phone == intentPhone && password == intentPassword &&
                            !imgNewSource
                        ) {
                            Toast.makeText(
                                applicationContext, "Tidak ada data yang berubah",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            editAccount(name, phone, password)
                        }

                    } else {

                        if (partImage != null) {

                            addAccount(name, phone, password)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Lengkapi data foto",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
                }

            } else {
                Toast.makeText(applicationContext, "Lengkapi data", Toast.LENGTH_SHORT).show()
            }
        }

        imgProfile.setOnClickListener {

            if (intentAction != "show") {
                ImagePicker.with(this)
                    .cropSquare()
                    .compress(512)//Final image size will be less than 512 KB(Optional)
                    .createIntent { intent ->
                        startForProfileImageResult.launch(intent)
                    }
            }
        }

        imgBack.setOnClickListener {
            finish()
        }
    }

    private fun addAccount(name: String, phone: String, password: String) {
        btnAddAccount.setShowProgress(true)

        val partName: RequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val partPhone: RequestBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val partPassword: RequestBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
        val partType: RequestBody = addUserType.toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.instances.addUser(partType, partName, partPhone, partPassword, partImage!!)
            .enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {
                    val responseBody = response.body()
                    val message = responseBody?.message
                    val user = responseBody?.user

                    if (response.isSuccessful && message == "Success") {
                        Log.e(TAG, "onResponse: $responseBody")

                        Toast.makeText(
                            applicationContext,
                            "Berhasil membuat akun",
                            Toast.LENGTH_SHORT
                        ).show()

                        finish()

                    } else {
                        Log.e(TAG, "onResponse: $response")
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()

                    }
                    btnAddAccount.setShowProgress(false)

                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {

                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                    btnAddAccount.setShowProgress(false)
                }

            })
    }

    private fun editAccount(name: String, phone: String, password: String) {
        btnAddAccount.setShowProgress(true)

        if (imgNewSource) {
            val partUserId: RequestBody =
                intentId.toRequestBody("text/plain".toMediaTypeOrNull())
            val partUserType: RequestBody =
                intentUserType.toRequestBody("text/plain".toMediaTypeOrNull())
            val partName: RequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val partPhone: RequestBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
            val partPassword: RequestBody = password.toRequestBody("text/plain".toMediaTypeOrNull())

            ApiClient.instances.editUser(
                partUserId,
                partUserType,
                partName,
                partPhone,
                partPassword,
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
                        btnAddAccount.setShowProgress(false)

                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {

                        Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                        btnAddAccount.setShowProgress(false)
                    }
                })
        } else {
            ApiClient.instances.editWithoutImgUser(
                intentId,
                intentUserType,
                name,
                phone,
                password,
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
                        btnAddAccount.setShowProgress(false)

                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {

                        Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT)
                            .show()
                        btnAddAccount.setShowProgress(false)
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

                Log.e(TAG, "image format: uri = $fileUri")
                Log.e(TAG, "image format: file path = $image")
                Log.e(TAG, "image format: file absolute path = ${image.absolutePath}")

                reqBody = image.asRequestBody("image/*".toMediaTypeOrNull())
                partImage = MultipartBody.Part.createFormData("image", image.name, reqBody!!)
                imgNewSource = true

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(applicationContext, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                    .show()
            } else {
//                Toast.makeText(applicationContext, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}