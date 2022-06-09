package com.skripsi.ambulanapp.ui.admin

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddEditCarActivity : AppCompatActivity() {
    private lateinit var progressDialog: ProgressDialog

    private val btnAdd: MaterialButton by lazy { findViewById(R.id.btnAddCarInfo) }
    private val inputName: TextInputEditText by lazy { findViewById(R.id.inputName) }
    private val inputNameCar: TextInputEditText by lazy { findViewById(R.id.inputNameCar) }
    private val inputNumber: TextInputEditText by lazy { findViewById(R.id.inputNumber) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_car)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)

        val intentId = intent.getStringExtra("id").toString()
        val intentName = intent.getStringExtra("name").toString()
        val intentCarName = intent.getStringExtra("car_name").toString()
        val intentCarNumber = intent.getStringExtra("car_number").toString()

        inputName.setText(intentName)
        inputNameCar.setText(intentCarName)
        inputNumber.setText(intentCarNumber)

        btnAdd.setOnClickListener {
            val car = inputNameCar.text.toString()
            val number = inputNumber.text.toString()

            if (car.isNotEmpty() && number.isNotEmpty()) {
                addCarInfo(intentId, intentName, car, number)
            } else {
                Toast.makeText(this, "Lengkapi data", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addCarInfo(id: String, name: String, car: String, number: String) {

        progressDialog.setMessage("Loading...")
        progressDialog.show()

        ApiClient.instances.addEditCarUser(id, car, number)
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
                            Toast.makeText(
                                applicationContext,
                                "Berhasil menambah info kendaraan $name",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {

                            Toast.makeText(this@AddEditCarActivity, "gagal", Toast.LENGTH_SHORT)
                                .show()

                        }
                    } else {
                        Toast.makeText(this@AddEditCarActivity, "gagal", Toast.LENGTH_SHORT).show()

                    }

                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    progressDialog.dismiss()

                    Toast.makeText(
                        this@AddEditCarActivity,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }
}