package com.skripsi.ambulanapp.ui.admin

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.skripsi.ambulanapp.R

class AdminListChatActivity : AppCompatActivity() {

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_list_chat)

        loadFragment(AdminListChatContainerFragment())

        imgBack.setOnClickListener { finish() }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frameUserAccount, fragment)
            commit()
        }

    }

    override fun onResume() {
        super.onResume()
    }
}
