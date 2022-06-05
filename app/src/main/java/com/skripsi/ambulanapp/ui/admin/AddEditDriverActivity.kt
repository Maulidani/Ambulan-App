package com.skripsi.ambulanapp.ui.admin

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddEditDriverActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog

    private val btnNext: MaterialButton by lazy { findViewById(R.id.btnNext) }
    private val inputName: TextInputEditText by lazy { findViewById(R.id.inputName) }
    private val inputPhone: TextInputEditText by lazy { findViewById(R.id.inputPhone) }
    private val inputUsername: TextInputEditText by lazy { findViewById(R.id.inputUsername) }
    private val inputPassword: TextInputEditText by lazy { findViewById(R.id.inputPassword) }
    private val imgProfile: ImageView by lazy { findViewById(R.id.imgProfile) }

    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_registration)
        supportActionBar?.hide()

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)

        btnNext.setOnClickListener {
            val name = inputName.text.toString()
            val phone = inputPhone.text.toString()
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()

            if (partImage == null) {
                Toast.makeText(this, "Pilih foto profil!", Toast.LENGTH_SHORT).show()
            } else if (name.isNotEmpty() && phone.isNotEmpty() && username.isNotEmpty() && password.isNotEmpty()) {
                addAkun(name, phone, username, password)

            } else {
                Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show()

            }
        }

        imgProfile.setOnClickListener {
            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }
    }

    private fun addAkun(
        name: String,
        phone: String,
        username: String,
        password: String,
    ) {
        progressDialog.show()

        val partName: RequestBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
        val partPhone: RequestBody = phone.toRequestBody("text/plain".toMediaTypeOrNull())
        val partUsername: RequestBody = username.toRequestBody("text/plain".toMediaTypeOrNull())
        val partPassword: RequestBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
        val partType: RequestBody = "driver".toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.instances.addUser(
            partName,
            partPhone,
            partImage!!,
            partUsername,
            partPassword,
            partType
        )
            .enqueue(object :
                Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {
                    val message = response.body()?.message
                    val dataUser = response.body()?.user

                    if (response.isSuccessful) {
                        if (message == "Success") {
                            Toast.makeText(
                                applicationContext,
                                "Berhasil menambah akun",
                                Toast.LENGTH_SHORT
                            ).show()

                            startActivity(
                                Intent(
                                    this@AddEditDriverActivity,
                                    AddEditCarActivity::class.java
                                )
                                    .putExtra("id", dataUser?.id.toString())
                                    .putExtra("name", dataUser?.name)
                            )

                            finish()
                        } else if (message == "Exist") {
                            Toast.makeText(
                                applicationContext,
                                "Akun username ini sudah ada",
                                Toast.LENGTH_SHORT
                            ).show()


                        } else {
                            Toast.makeText(applicationContext, "gagal", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "gagal", Toast.LENGTH_SHORT).show()
                    }

                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    progressDialog.dismiss()

                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })

    }

    private val startForProfileImageResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {

                val fileUri = data?.data!!
//                    imageView.setImageURI(fileUri)

                val image = File(fileUri.path!!)
                imgProfile.setImageBitmap(BitmapFactory.decodeFile(image.absolutePath))

                reqBody = image.asRequestBody("image/*".toMediaTypeOrNull())

                partImage = MultipartBody.Part.createFormData("image", image.name, reqBody!!)

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}