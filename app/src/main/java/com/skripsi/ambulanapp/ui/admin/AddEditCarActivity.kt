package com.skripsi.ambulanapp.ui.admin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.skripsi.ambulanapp.R

class AddEditCarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_car)

        val intentId = intent.getStringExtra("id")
        val intentName = intent.getStringExtra("name")
    }
}