package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R

class AdminListAccountDriverActivity : AppCompatActivity() {

    private val fabAddAccountDriver: FloatingActionButton by lazy { findViewById(R.id.fabAddAccountDriver) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_account_driver)

        onClick()
    }

    private fun onClick() {

        fabAddAccountDriver.setOnClickListener {
            startActivity(Intent(this, AdminAddAccountDriverActivity::class.java))
        }
    }

}