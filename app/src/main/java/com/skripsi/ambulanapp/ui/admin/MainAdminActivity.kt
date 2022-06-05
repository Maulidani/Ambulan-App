package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.customer.fragment.HomeFragment
import com.skripsi.ambulanapp.ui.customer.fragment.ProfileFragment
import com.skripsi.ambulanapp.ui.driver.OrderHistoryActivity
import com.skripsi.ambulanapp.ui.driver.ProfileActivity

class MainAdminActivity : AppCompatActivity() {

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadFragment(HomeFragment())
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navAkunDriver -> loadFragment(HomeFragment())
                R.id.navLogout ->  loadFragment(ProfileFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frame, fragment)
            commit()
        }
    }

}