package com.skripsi.ambulanapp.ui.driver

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.ui.admin.AdminLoginActivity
import com.skripsi.ambulanapp.ui.admin.AdminMainActivity
import com.skripsi.ambulanapp.util.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverLoginActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }

    private val inputUsername: TextInputEditText by lazy { findViewById(R.id.inputUsername) }
    private val inputPassword: TextInputEditText by lazy { findViewById(R.id.inputPassword) }
    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLoginDriver) }
    private val tvLoginAdmin: TextView by lazy { findViewById(R.id.tvLoginAdmin) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_login)

        sharedPref = PreferencesHelper(this)

        onClick()
    }

    override fun onResume() {
        super.onResume()

        if (sharedPref.getBoolean(PreferencesHelper.PREF_IS_LOGIN)) {
            if (sharedPref.getString(PreferencesHelper.PREF_TYPE) == "driver") {
                startActivity(Intent(applicationContext, DriverMainActivity::class.java))
                finish()
            } else {
                //
            }
        } else {
            //
        }
    }

    private fun onClick() {
        imgBack.setOnClickListener { finish() }

        btnLogin.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {

                btnLogin.setShowProgress(true)
                login(username, password, "driver")

            } else {
                Toast.makeText(applicationContext, "Lengkapi data untuk login", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        tvLoginAdmin.setOnClickListener {
            startActivity(Intent(this, AdminLoginActivity::class.java))
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


    private fun login(username: String, password: String, type: String) {
        CoroutineScope(Dispatchers.IO).launch {

            ApiClient.instances.loginUser(username, password, type)
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = response.body()?.message
                        val user = response.body()?.user

                        if (response.isSuccessful && message == "Success") {

                            saveSession(user)

                            Log.e("onResponse: ", user.toString())

                        } else {
                            Log.e(applicationContext.toString(), "onResponse: "+response.message().toString(), )
                            Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()

                        }
                        btnLogin.setShowProgress(false)

                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            t.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                        btnLogin.setShowProgress(false)

                    }

                })
        }
    }

    private fun saveSession(user: Model.DataModel?) {

        sharedPref.logout()
        sharedPref.put(PreferencesHelper.PREF_ID_USER, user?.id.toString())
        sharedPref.put(PreferencesHelper.PREF_TYPE, user?.type!!)
        sharedPref.put(PreferencesHelper.PREF_IS_LOGIN, true)
        Log.e(
            "sharedPreferences",
            "user login: " + sharedPref.getBoolean(PreferencesHelper.PREF_IS_LOGIN)
        )
        Log.e(
            "sharedPreferences",
            "user id: " + sharedPref.getString(PreferencesHelper.PREF_ID_USER)
        )
        Log.e(
            "sharedPreferences",
            "user type: " + sharedPref.getString(PreferencesHelper.PREF_TYPE)
        )

        startActivity(Intent(applicationContext, DriverMainActivity::class.java))

        finish()
    }
}