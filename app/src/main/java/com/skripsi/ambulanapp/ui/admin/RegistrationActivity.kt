package com.skripsi.ambulanapp.ui.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skripsi.ambulanapp.R

class RegistrationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.hide()
    }
}