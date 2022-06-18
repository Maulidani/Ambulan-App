package com.skripsi.ambulanapp.ui.admin

import android.app.Activity
import android.app.ProgressDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.util.Constant
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddArtikelActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog

    private val btnUpload: MaterialButton by lazy { findViewById(R.id.btnUpload) }
    private val inputType: AutoCompleteTextView by lazy { findViewById(R.id.inputType) }
    private val inputTitle: TextInputEditText by lazy { findViewById(R.id.inputtitle) }
    private val inputHospital: TextInputEditText by lazy { findViewById(R.id.inputhospital) }
    private val inputHospitasAddress: TextInputEditText by lazy { findViewById(R.id.inputHospitalAddress) }
    private val inputContent: TextInputEditText by lazy { findViewById(R.id.inputContent) }
    private val imgArtikel: ImageView by lazy { findViewById(R.id.imgArtikel) }
    private val tvTextHead: TextView by lazy { findViewById(R.id.tvUpload) }

    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

    var intentTypeAction = ""
    var intentIdArtikel = ""

    var arrayTypeArtikel = arrayListOf("rumah_sakit_terdekat", "pertolongan_pertama")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_artikel)
        progressDialog = ProgressDialog(this)
        progressDialog.setCanceledOnTouchOutside(true)

        intentTypeAction = intent.getStringExtra("add_edit").toString()
        intentIdArtikel = intent.getStringExtra("id").toString()
        val intentType = intent.getStringExtra("type").toString()
        val intentTitle = intent.getStringExtra("title").toString()
        val intentHospital = intent.getStringExtra("hospital").toString()
        val intentHospitalAddress = intent.getStringExtra("hospital_address").toString()
        val intentContent = intent.getStringExtra("content").toString()
        val intentImg = intent.getStringExtra("image").toString()

        val adapterListTypeArtikel = ArrayAdapter(
            applicationContext,
            R.layout.support_simple_spinner_dropdown_item,
            arrayTypeArtikel
        )
        inputType.setAdapter(adapterListTypeArtikel)

        btnUpload.setOnClickListener {
            if (intentTypeAction == "add") {
                if (inputType.text.isNotEmpty() && inputTitle.text.toString().isNotEmpty()
                    && inputHospital.text.toString()
                        .isNotEmpty() && inputHospitasAddress.text.toString().isNotEmpty()
                    && inputContent.text.toString().isNotEmpty() && partImage != null
                ) {
                    uploadArtikel(
                        inputType.text.toString(),
                        inputTitle.text.toString(),
                        inputHospital.text.toString(),
                        inputHospitasAddress.text.toString(),
                        inputContent.text.toString(),
                        intentType
                    )
                }  else {
                    Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show()
                }

            } else if (intentTypeAction == "edit") {

                if (inputTitle.text.toString().isNotEmpty()
                    && inputHospital.text.toString().isNotEmpty() && inputHospitasAddress.text.toString().isNotEmpty()
                    && inputContent.text.toString().isNotEmpty()
                ) {

                    editArtikel(
                        inputTitle.text.toString(),
                        inputHospital.text.toString(),
                        inputHospitasAddress.text.toString(),
                        inputContent.text.toString()
                    )

                } else {
                    Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show()
                }
            }
        }

        imgArtikel.setOnClickListener {

            ImagePicker.with(this)
                .cropSquare()
                .compress(1024)         //Final image size will be less than 1 MB(Optional)
                .createIntent { intent ->
                    startForProfileImageResult.launch(intent)
                }
        }

        if (intentTypeAction == "edit") {

            btnUpload.setText("edit")

            tvTextHead.text = "Edit Artikel"

            var linkImage = "${Constant.URL_IMAGE_ARTIKEL}${intentImg}"
            imgArtikel.load(linkImage)
            linkImage = ""

            inputType.setText(intentType)
            inputType.isEnabled = false

            inputTitle.setText(intentTitle)
            inputHospital.setText(intentHospital)
            inputHospitasAddress.setText(intentHospitalAddress)
            inputContent.setText(intentContent)

        }
    }

    private fun uploadArtikel(
        type: String,
        title: String,
        hospital: String,
        hospitalAddress: String,
        content: String,
        intentType: String?
    ) {
        progressDialog.show()

        val partType: RequestBody = type.toRequestBody("text/plain".toMediaTypeOrNull())
        val partTitle: RequestBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
        val partHospital: RequestBody = hospital.toRequestBody("text/plain".toMediaTypeOrNull())
        val partHospitalAddress: RequestBody =
            hospitalAddress.toRequestBody("text/plain".toMediaTypeOrNull())
        val partContent: RequestBody = content.toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.instances.addArtikel(
            partType,
            partTitle,
            partHospital,
            partHospitalAddress,
            partContent,
            partImage!!
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
                                "Berhasil menambah artikel",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

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

    private fun editArtikel(
        title: String,
        hospital: String,
        hospitalAddress: String,
        content: String
    ) {
        progressDialog.show()

        ApiClient.instances.editArtikel(
            intentIdArtikel.toInt(),
            title,
            hospital,
            hospitalAddress,
            content,
        )
            .enqueue(object :
                Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {
                    val message = response.body()?.message

                    if (response.isSuccessful) {
                        if (message == "Success") {
                            Toast.makeText(
                                applicationContext,
                                "Berhasil ubah artikel",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

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

    private fun editImage(id: String) {
        progressDialog.show()

        val partId: RequestBody = id.toRequestBody("text/plain".toMediaTypeOrNull())

        ApiClient.instances.editImageArtikel(
            partId,
            partImage!!
        )
            .enqueue(object :
                Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {
                    val message = response.body()?.message

                    if (response.isSuccessful) {

                        if (message == "Success") {
                            Toast.makeText(
                                applicationContext,
                                "Berhasil mengubah gambat/foto",
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
                imgArtikel.setImageBitmap(BitmapFactory.decodeFile(image.absolutePath))

                reqBody = image.asRequestBody("image/*".toMediaTypeOrNull())

                partImage = MultipartBody.Part.createFormData("image", image.name, reqBody!!)

                if (intentTypeAction == "edit") {

                    editImage(intentIdArtikel)
                }

            } else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
}