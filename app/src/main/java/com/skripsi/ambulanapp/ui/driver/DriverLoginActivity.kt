package com.skripsi.ambulanapp.ui.driver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.util.Constant.setShowProgress
import com.skripsi.ambulanapp.util.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverLoginActivity : AppCompatActivity() {
    private val TAG = "DriverLoginActivity"
    private val userType = "driver"
    private lateinit var sharedPref: PreferencesHelper

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    private val inputPhone: TextInputEditText by lazy { findViewById(R.id.inputPhone) }
    private val inputPassword: TextInputEditText by lazy { findViewById(R.id.inputPassword) }
    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLoginDriver) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_login)

        sharedPref = PreferencesHelper(applicationContext)

        btnLogin.setOnClickListener {
            val phone = inputPhone.text.toString()
            val password = inputPassword.text.toString()

            if (phone.isNotEmpty() && password.isNotEmpty()) {
                login(phone, password, userType)
            } else {
                Toast.makeText(applicationContext, "Lengkapi data", Toast.LENGTH_SHORT).show()
            }
        }

        imgBack.setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (sharedPref.getBoolean(PreferencesHelper.PREF_IS_LOGIN)) {
            if (sharedPref.getString(PreferencesHelper.PREF_USER_TYPE) == "driver") {
                startActivity(Intent(applicationContext, DriverMainActivity::class.java))
                finish()

            } else {
                //
            }

        } else {
            //
        }
    }

    private fun login(phone: String, password: String, type: String) {
        btnLogin.setShowProgress(true)

        ApiClient.instances.loginUser(type, phone, password, "")
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
                        saveSession(user)

                    } else {
                        Log.e(TAG, "onResponse: $response")
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()

                    }
                    btnLogin.setShowProgress(false)

                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {

                    Toast.makeText(applicationContext,  t.message.toString(), Toast.LENGTH_SHORT).show()
                    btnLogin.setShowProgress(false)
                }

            })
    }

    private fun saveSession(user: Model.UserModel?) {

        sharedPref.logout()
        sharedPref.put(PreferencesHelper.PREF_USER_ID, user!!.id)
        sharedPref.put(PreferencesHelper.PREF_USER_TYPE, userType)
        sharedPref.put(PreferencesHelper.PREF_IS_LOGIN, true)

        Log.e(TAG, "saveSession: "+sharedPref.getString(PreferencesHelper.PREF_USER_ID).toString(), )
        Log.e(TAG, "saveSession: "+sharedPref.getString(PreferencesHelper.PREF_USER_TYPE).toString(), )
        Log.e(TAG, "saveSession: "+sharedPref.getBoolean(PreferencesHelper.PREF_IS_LOGIN).toString(), )

        startActivity(Intent(applicationContext, DriverMainActivity::class.java))
        finish()
    }

}