package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.driver.MainDriverActivity

class LoginAdminActivity : AppCompatActivity() {
    private val btnLogin: MaterialButton by lazy { findViewById(R.id.btnLogin) }
    private val inputUsername: TextInputEditText by lazy { findViewById(R.id.inputUsername) }
    private val inputPassword: TextInputEditText by lazy { findViewById(R.id.inputPassword) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_admin)
        supportActionBar?.hide()

        btnLogin.setOnClickListener {
            startActivity(Intent(this, MainAdminActivity::class.java))

//            if (inputUsername.text.toString().isNotEmpty() && inputPassword.text.toString()
//                    .isNotEmpty()
//            ) {
//                startActivity(Intent(this, MainAdminActivity::class.java))
//            } else {
//                Toast.makeText(this, "Lengkapi data untuk login", Toast.LENGTH_SHORT).show()
//            }
        }
    }
}