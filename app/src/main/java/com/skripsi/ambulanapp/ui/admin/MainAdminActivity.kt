package com.skripsi.ambulanapp.ui.admin

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.admin.fragment.AccountsListFragment
import com.skripsi.ambulanapp.ui.admin.fragment.OrderHistoryFragment
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.PreferencesHelper

class MainAdminActivity : AppCompatActivity() {
    private lateinit var sharedPref: PreferencesHelper

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)
        supportActionBar?.title = "Admin"

        sharedPref = PreferencesHelper(this)

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        loadFragment(OrderHistoryFragment())
        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navOrderHistory -> loadFragment(OrderHistoryFragment())
                R.id.navAkunDriver -> loadFragment(AccountsListFragment())
                R.id.navLogout -> {
                    sharedPref.logout()
                    finish()
                }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (!sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            finish()
        }
    }


}