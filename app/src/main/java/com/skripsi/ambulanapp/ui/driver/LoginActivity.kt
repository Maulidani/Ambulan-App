package com.skripsi.ambulanapp.ui.driver

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiCLient
import com.skripsi.ambulanapp.ui.admin.LoginAdminActivity
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var progressDialog: ProgressDialog

    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLogin) }
    private val tvLoginAdmin: TextView by lazy { findViewById(R.id.tvLoginAdmin) }
    private val inputUsername: TextInputEditText by lazy { findViewById(R.id.inputUsername) }
    private val inputPassword: TextInputEditText by lazy { findViewById(R.id.inputPassword) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        sharedPref = PreferencesHelper(this)
        progressDialog = ProgressDialog(this)

        btnLogin.setOnClickListener {

            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString().toString()
            val type = "driver"
            if (username.isNotEmpty() && password.isNotEmpty()) {

                login(username, password, type)

            } else {
                Toast.makeText(this, "Lengkapi data untuk login", Toast.LENGTH_SHORT).show()
            }
        }

        tvLoginAdmin.setOnClickListener {
            startActivity(Intent(this, LoginAdminActivity::class.java))
        }

    }

    private fun login(username: String, password: String, type: String) {
        progressDialog.setMessage("Loading...")
        progressDialog.show()

        ApiCLient.instances.loginUser(username, password, type)
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

                            saveSession(data!!)
                        } else {

                            Toast.makeText(this@LoginActivity, "gagal", Toast.LENGTH_SHORT).show()

                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "gagal", Toast.LENGTH_SHORT).show()

                    }

                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    progressDialog.dismiss()

                    Toast.makeText(this@LoginActivity, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun saveSession(data: Model.DataModel) {

        sharedPref.put(Constant.PREF_ID_USER, data.id.toString())
        sharedPref.put(Constant.PREF_TYPE, data.type)
        sharedPref.put(Constant.PREF_IS_LOGIN, true)

        startActivity(Intent(this, MainDriverActivity::class.java))

        finish()
    }

    override fun onResume() {
        super.onResume()

        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            finish()
        }
    }
}