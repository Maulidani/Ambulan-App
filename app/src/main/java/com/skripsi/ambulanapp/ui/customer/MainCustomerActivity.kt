package com.skripsi.ambulanapp.ui.customer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.customer.fragment.AmbulanceFragment
import com.skripsi.ambulanapp.ui.customer.fragment.HomeFragment
import com.skripsi.ambulanapp.ui.customer.fragment.ProfileFragment

class MainCustomerActivity : AppCompatActivity() {

    private val bottomNavigation: BottomNavigationView by lazy { findViewById(R.id.bottomNavigation) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_customer)

        loadFragment(HomeFragment())
        bottomNavigation.setOnItemSelectedListener { id ->
            when (id.itemId) {
                R.id.navHome -> loadFragment(HomeFragment())
                R.id.navAmbulance -> loadFragment(AmbulanceFragment())
                R.id.navProfile -> loadFragment(ProfileFragment())
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