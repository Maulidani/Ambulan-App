package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.util.PreferencesHelper

class AdminMainActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    private val imgLogout: ImageView by lazy { findViewById(R.id.imgLogout) }

    private val cardDriver: CardView by lazy { findViewById(R.id.cardDriver) }
    private val cardHopital: CardView by lazy { findViewById(R.id.cardHospital) }
    private val cardOrderHistory: CardView by lazy { findViewById(R.id.cardHistoryOrder) }
    private val cardArticle: CardView by lazy { findViewById(R.id.cardArticle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        sharedPref = PreferencesHelper(this)

        onClick()
    }

    private fun onClick() {

        imgLogout.setOnClickListener {

            val dialogLogout = BottomSheetDialog(this)
            val viewLogout =
                layoutInflater.inflate(R.layout.item_dialog_logout, null)
            val btnYes = viewLogout.findViewById<MaterialButton>(R.id.btnYes)
            val btnNo = viewLogout.findViewById<MaterialButton>(R.id.btnNo)

            dialogLogout.setCancelable(false)
            dialogLogout.setContentView(viewLogout)
            dialogLogout.show()

            btnNo.setOnClickListener {
                dialogLogout.dismiss()
            }

            btnYes.setOnClickListener {
                dialogLogout.dismiss()
                sharedPref.logout()
                finish()
            }
        }

        cardDriver.setOnClickListener {
            startActivity(Intent(this, AdminListAccountDriverActivity::class.java))
        }
        cardHopital.setOnClickListener {
            startActivity(Intent(this, AdminListHospitalActivity::class.java))
        }
        cardOrderHistory.setOnClickListener {
            startActivity(Intent(this, AdminListOrderHistoryActivity::class.java))
        }
        cardArticle.setOnClickListener {
            startActivity(Intent(this, AdminListArticleActivity::class.java))
        }
    }
}