package com.skripsi.ambulanapp.ui.driver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skripsi.ambulanapp.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.title = "Profile"

    }
}