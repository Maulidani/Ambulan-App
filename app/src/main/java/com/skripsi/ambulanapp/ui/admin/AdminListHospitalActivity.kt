package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R

class AdminListHospitalActivity : AppCompatActivity() {
    private val fabAddHospital : FloatingActionButton by lazy {findViewById(R.id.fabAddHospital)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_hospital)

        onClick()
    }

    private fun onClick(){

        fabAddHospital.setOnClickListener {
            startActivity(Intent(this,AdminAddHospitalActivity::class.java))
        }
    }
}