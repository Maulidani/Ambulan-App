package com.skripsi.ambulanapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.customer.MainCustomerActivity
import com.skripsi.ambulanapp.driver.LoginActivity

class SplashScreenActivity : AppCompatActivity() {
    private val orderAmbulan: MaterialButton by lazy { findViewById(R.id.btnOrderAmbulan) }
    private val loginDriver: MaterialButton by lazy { findViewById(R.id.btnLoginDriver) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

//        CoroutineScope(Dispatchers.Main).launch {
//            delay(2500)
//
//            startActivity(Intent(this@SplashScreenActivity, LoginActivity::class.java))
//
//            finish()
//        }

        orderAmbulan.setOnClickListener {
            startActivity(Intent(this, MainCustomerActivity::class.java))
        }
        loginDriver.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}