package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.driver.DriverMainActivity

class AdminLoginActivity : AppCompatActivity() {
    private val btnLogin: MaterialButton by lazy{ findViewById(R.id.btnLoginAdmin)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)

        onClick()
    }

    private fun onClick(){

        btnLogin.setOnClickListener {
            startActivity(Intent(this, AdminMainActivity::class.java))
        }
    }
}