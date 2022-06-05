package com.skripsi.ambulanapp.ui.driver

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.admin.LoginAdminActivity

class LoginActivity : AppCompatActivity() {
    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLogin) }
    private val tvLoginAdmin: TextView by lazy { findViewById(R.id.tvLoginAdmin) }
    private val inputUsername: TextInputEditText by lazy { findViewById(R.id.inputUsername) }
    private val inputPassword: TextInputEditText by lazy { findViewById(R.id.inputPassword) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        btnLogin.setOnClickListener {
            startActivity(Intent(this, MainDriverActivity::class.java))

//            if (inputUsername.text.toString().isNotEmpty() && inputPassword.text.toString().isNotEmpty()) {
//                startActivity(Intent(this, MainDriverActivity::class.java))
//            } else {
//                Toast.makeText(this, "Lengkapi data untuk login", Toast.LENGTH_SHORT).show()
//            }
        }

        tvLoginAdmin.setOnClickListener {
            startActivity(Intent(this, LoginAdminActivity::class.java))
        }

    }
}