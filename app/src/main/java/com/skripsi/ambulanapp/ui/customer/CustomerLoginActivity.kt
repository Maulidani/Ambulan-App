package com.skripsi.ambulanapp.ui.customer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class CustomerLoginActivity : AppCompatActivity() {
    private val TAG = "CustomerLoginActivity"
    private val userType = "customer"
    private lateinit var sharedPref: PreferencesHelper

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    private val inputPhone: TextInputEditText by lazy { findViewById(R.id.inputPhone) }
    private val inputPassword: TextInputEditText by lazy { findViewById(R.id.inputPassword) }
    private val tvRegister: TextView by lazy { findViewById(R.id.tvRegister) }
    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLogin) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_login)

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

        tvRegister.setOnClickListener {
        startActivity(Intent(applicationContext, CustomerRegisterAccountActivity::class.java))
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

//        startActivity(Intent(applicationContext, CustomerMainActivity::class.java))
//        finish()
    }

}