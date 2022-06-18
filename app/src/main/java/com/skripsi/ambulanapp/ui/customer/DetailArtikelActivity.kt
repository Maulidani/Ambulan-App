package com.skripsi.ambulanapp.ui.customer

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.util.Constant

class DetailArtikelActivity : AppCompatActivity() {
    private val tvTitle: TextView by lazy { findViewById(R.id.tvTitle) }
    private val imgArtikel: ImageView by lazy { findViewById(R.id.imgArtikel) }
    private val tvHospital: TextView by lazy { findViewById(R.id.tvHospital) }
    private val tvHospitalAddress: TextView by lazy { findViewById(R.id.tvHospitalAddresss) }
    private val tvContent: TextView by lazy { findViewById(R.id.tvContent) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_artikel)

        val intentType = intent.getStringExtra("type")
        val intentTitle = intent.getStringExtra("title")
        val intentImgArtikel = intent.getStringExtra("image")
        val intentHospital = intent.getStringExtra("hospital")
        val intentHospitalAddress = intent.getStringExtra("hospital_address")
        val intentContent = intent.getStringExtra("content")

        if (intentType == "rumah_sakit_terdekat") {
            supportActionBar?.title = "Rumah Sakit Terdekat"
        } else {
            supportActionBar?.title = "Pertolongan Pertama"
        }

        tvTitle.text = intentTitle
        tvHospital.text = intentHospital
        tvHospitalAddress.text = intentHospitalAddress
        tvContent.text = intentContent

        var linkImage = "${Constant.URL_IMAGE_ARTIKEL}${intentImgArtikel}"
        imgArtikel.load(linkImage)


    }
}