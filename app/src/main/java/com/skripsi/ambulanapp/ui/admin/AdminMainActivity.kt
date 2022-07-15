package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.SplashScreenActivity
import com.skripsi.ambulanapp.util.PreferencesHelper

@RequiresApi(Build.VERSION_CODES.N)
class AdminMainActivity : AppCompatActivity() {
    private val TAG = "AdminMainActivity"
    private lateinit var sharedPref: PreferencesHelper

    private val imgLogout: ImageView by lazy { findViewById(R.id.imgLogout) }
    private val cardAccount: CardView by lazy { findViewById(R.id.cardAccount) }
    private val cardHopital: CardView by lazy { findViewById(R.id.cardHospital) }
    private val cardOrderHistory: CardView by lazy { findViewById(R.id.cardHistoryOrder) }
    private val cardArticle: CardView by lazy { findViewById(R.id.cardArticle) }
    private val cardChat: CardView by lazy { findViewById(R.id.cardChat) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_main)

        sharedPref = PreferencesHelper(applicationContext)

        onClick()
    }

    private fun onClick() {
        imgLogout.setOnClickListener {

            val dialog = BottomSheetDialog(this)
            val view =
                layoutInflater.inflate(R.layout.item_dialog_logout, null)
            val btnYes = view.findViewById<MaterialButton>(R.id.btnYes)
            val btnNo = view.findViewById<MaterialButton>(R.id.btnNo)

            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()

            btnNo.setOnClickListener {
                dialog.dismiss()
            }

            btnYes.setOnClickListener {
                dialog.dismiss()
                sharedPref.logout()

                startActivity(Intent(applicationContext,SplashScreenActivity::class.java))
                finish()
            }
        }

        cardAccount.setOnClickListener {
            startActivity(Intent(this, AdminListAccountActivity::class.java))
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
        cardChat.setOnClickListener{

        }
    }
}