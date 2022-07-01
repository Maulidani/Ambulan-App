package com.skripsi.ambulanapp.ui.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.network.Link

class DetailArticleActivity : AppCompatActivity() {

    private val imgBack : ImageView by lazy { findViewById(R.id.imgBack) }
    private val imgArticle : ImageView by lazy { findViewById(R.id.imgArticle) }
    private val tvTitile : TextView by lazy { findViewById(R.id.tvTitleArticle) }
    private val tvDesc : TextView by lazy { findViewById(R.id.tvDescriptionArticle) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_article)

        val intentTitle = intent.getStringExtra("title")
        val intentDesc = intent.getStringExtra("description")
        val intentImage = intent.getStringExtra("image")

        val linkImg = "${Link.URL_IMAGE_ARTICLE}${intentImage}"
        imgArticle.load(linkImg)

        tvTitile.setText(intentTitle)
        tvDesc.setText(intentDesc)

        imgBack.setOnClickListener { finish() }

    }
}