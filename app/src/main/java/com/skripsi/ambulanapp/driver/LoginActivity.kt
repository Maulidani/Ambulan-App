package com.skripsi.ambulanapp.driver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R

class LoginActivity : AppCompatActivity() {

    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLogin) }
    private val tvRegistration: TextView by lazy { findViewById(R.id.tvRegistration) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.hide()

        btnLogin.setOnClickListener {
            startActivity(Intent(this, MainDriverActivity::class.java))
        }

        tvRegistration.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}