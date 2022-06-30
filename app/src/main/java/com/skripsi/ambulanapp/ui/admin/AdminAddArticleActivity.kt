package com.skripsi.ambulanapp.ui.admin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AdminAddArticleActivity : AppCompatActivity() {
    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }

    private val inputtitle: TextInputEditText by lazy { findViewById(R.id.inputTitle) }
    private val inputDescription: TextInputEditText by lazy { findViewById(R.id.inputDescription) }
    private val btnAdd: MaterialButton by lazy { findViewById(R.id.btnAdd) }

    private var intentAction = ""
    private var intentUser = ""
    private var intentId = ""
    private var intentTitle = ""
    private var intentDescription = ""
    private var intentImage = ""

    private var imgNewSource = false
    private var reqBody: RequestBody? = null
    private var partImage: MultipartBody.Part? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_article)


        onClick()
    }

    private fun onClick() {

        imgBack.setOnClickListener { finish() }

    }
}