package com.skripsi.ambulanapp.ui.driver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.admin.AdminLoginActivity

class DriverLoginActivity : AppCompatActivity() {
    private val btnLogin: MaterialButton by lazy{ findViewById(R.id.btnLoginDriver)}
    private val tvLoginAdmin: TextView by lazy{ findViewById(R.id.tvLoginAdmin)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_login)

        onClick()
    }

    private fun onClick(){

        btnLogin.setOnClickListener {
            startActivity(Intent(this,DriverMainActivity::class.java))
        }

        tvLoginAdmin.setOnClickListener {
            startActivity(Intent(this,AdminLoginActivity::class.java))
        }
    }
}