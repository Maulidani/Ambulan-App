package com.skripsi.ambulanapp.ui.driver

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.skripsi.ambulanapp.R

class OrderHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        supportActionBar?.title = "Order"
    }
}