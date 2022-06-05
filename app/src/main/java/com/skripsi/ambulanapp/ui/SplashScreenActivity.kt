package com.skripsi.ambulanapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.admin.MainAdminActivity
import com.skripsi.ambulanapp.ui.customer.MainCustomerActivity
import com.skripsi.ambulanapp.ui.driver.LoginActivity
import com.skripsi.ambulanapp.ui.driver.MainDriverActivity
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    private val orderAmbulan: MaterialButton by lazy { findViewById(R.id.btnOrderAmbulan) }
    private val loginDriver: MaterialButton by lazy { findViewById(R.id.btnLoginDriver) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        supportActionBar?.hide()
        sharedPref = PreferencesHelper(this)

        if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN) && sharedPref.getString(Constant.PREF_TYPE) == "admin") {

            orderAmbulan.visibility = View.INVISIBLE
            loginDriver.visibility = View.INVISIBLE

            CoroutineScope(Dispatchers.Main).launch {
                delay(2500)

                startActivity(Intent(this@SplashScreenActivity, MainAdminActivity::class.java))
            }
        }

        orderAmbulan.setOnClickListener {
            startActivity(Intent(this, MainCustomerActivity::class.java))
        }

        loginDriver.setOnClickListener {
            if (sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
                startActivity(Intent(this, MainDriverActivity::class.java))
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (!sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {

            orderAmbulan.visibility = View.VISIBLE
            loginDriver.visibility = View.VISIBLE

        }

    }

}