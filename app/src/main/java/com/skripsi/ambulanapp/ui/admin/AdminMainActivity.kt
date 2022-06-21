package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.skripsi.ambulanapp.R

class AdminMainActivity : AppCompatActivity() {
    private val cardDriver: CardView by lazy { findViewById(R.id.cardDriver) }
    private val cardHopital: CardView by lazy { findViewById(R.id.cardHospital) }
    private val cardOrderHistory: CardView by lazy { findViewById(R.id.cardHistoryOrder) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        onClick()
    }

    private fun onClick() {

        cardDriver.setOnClickListener {
            startActivity(Intent(this, AdminListAccountDriverActivity::class.java))
        }
        cardHopital.setOnClickListener {
            startActivity(Intent(this, AdminListHospitalActivity::class.java))
        }
        cardOrderHistory.setOnClickListener {
            startActivity(Intent(this, AdminListOrderHistoryActivity::class.java))
        }
    }
}