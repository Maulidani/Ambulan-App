package com.skripsi.ambulanapp.ui

import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.admin.AdminListArticleActivity
import com.skripsi.ambulanapp.ui.customer.CustomerMainActivity
import com.skripsi.ambulanapp.ui.driver.DriverLoginActivity
import com.skripsi.ambulanapp.util.PreferencesHelper

class SplashScreenActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var sharedPref: PreferencesHelper

    private val btnOrderAmbulance: MaterialButton by lazy { findViewById(R.id.btnOrderAmbulance) }
    private val btnArticle: MaterialButton by lazy { findViewById(R.id.btnArticle) }
    private val tvDriverIn: TextView by lazy { findViewById(R.id.tvDriverIn) }

    private lateinit var mMap: GoogleMap
    private val locationRequestCode = 1001
    private lateinit var locationRequest: LocationRequest

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onClick() {

        val isLogin = sharedPref.getBoolean(PreferencesHelper.PREF_IS_LOGIN)
        val isCustomerOrder = sharedPref.getString(PreferencesHelper.PREF_ID_ORDER_CUSTOMER)

        btnOrderAmbulance.setOnClickListener {

            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                startActivity(Intent(this, CustomerMainActivity::class.java))

            } else {
                askLocationPermission()
            }
        }

        btnArticle.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    AdminListArticleActivity::class.java
                ).putExtra("type", "customer")
            )
        }

        tvDriverIn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (sharedPref.getString(PreferencesHelper.PREF_TYPE) == "driver") {
//                    sharedPref.logout()
                }
                startActivity(Intent(this, DriverLoginActivity::class.java))

            } else {
                askLocationPermission()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPref = PreferencesHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        onClick()

    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkSettingAndStartLocationUpdates()
        } else {
            askLocationPermission()
        }
    }

    //permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationRequestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkSettingAndStartLocationUpdates()
            }
        }
    }

    private fun checkSettingAndStartLocationUpdates() {
        val request: LocationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val client: SettingsClient = LocationServices.getSettingsClient(this)

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(request)

        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                val apiException: ResolvableApiException = it
                try {
                    apiException.startResolutionForResult(this, locationRequestCode)
                    askLocationPermission()
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun askLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                locationRequestCode
            )
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                locationRequestCode
            )
        }
    }

}