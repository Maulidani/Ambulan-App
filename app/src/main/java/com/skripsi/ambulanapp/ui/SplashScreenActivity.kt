package com.skripsi.ambulanapp.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.ui.admin.AdminLoginActivity
import com.skripsi.ambulanapp.ui.admin.AdminMainActivity
import com.skripsi.ambulanapp.ui.customer.CustomerLoginActivity
import com.skripsi.ambulanapp.ui.driver.DriverLoginActivity
import com.skripsi.ambulanapp.ui.driver.DriverMainActivity
import com.skripsi.ambulanapp.util.PreferencesHelper


class SplashScreenActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = "SplashScreenActivity" 
    private lateinit var sharedPref: PreferencesHelper

    private val icLogin: ImageView by lazy { findViewById(R.id.imgLoginAs) }
    private val btnOrderAmbulance: MaterialButton by lazy { findViewById(R.id.btnOrderAmbulance) }
    private val btnArticle: MaterialButton by lazy { findViewById(R.id.btnArticle) }

    private lateinit var mMap: GoogleMap
    private var isReady = false
    private val locationRequestCode = 1001
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationResult: LocationResult

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        sharedPref = PreferencesHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        icLogin.visibility = View.GONE
        btnOrderAmbulance.visibility = View.GONE
        btnArticle.visibility = View.GONE

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResultCallback: LocationResult) {
            super.onLocationResult(locationResultCallback)

            locationResult = locationResultCallback

            val myLocation =
                LatLng(
                    locationResult.locations[0].latitude,
                    locationResult.locations[0].longitude
                )

            if (isReady) {
                icLogin.visibility = View.VISIBLE
                btnOrderAmbulance.visibility = View.VISIBLE
                btnArticle.visibility = View.VISIBLE
            }

        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        isReady = true
        onClick()
    }

    private fun onClick() {

        icLogin.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view =
                layoutInflater.inflate(R.layout.item_dialog_login, null)
            val btnDriver = view.findViewById<MaterialButton>(R.id.btnDriver)
            val btnAdmin = view.findViewById<MaterialButton>(R.id.btnAdmin)

//            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()

            btnDriver.setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(applicationContext, DriverLoginActivity::class.java))
            }

            btnAdmin.setOnClickListener {
                dialog.dismiss()
                startActivity(Intent(applicationContext, AdminLoginActivity::class.java))
            }
        }

        btnOrderAmbulance.setOnClickListener {
            startActivity(Intent(applicationContext, CustomerLoginActivity::class.java))
        }

        btnArticle.setOnClickListener {
//            startActivity(Intent(applicationContext,AdminListArticleActivity::class.java))
        }
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

    override fun onResume() {
        super.onResume()

        if (sharedPref.getBoolean(PreferencesHelper.PREF_IS_LOGIN)) {
            if (sharedPref.getString(PreferencesHelper.PREF_USER_TYPE) == "admin") {
                startActivity(Intent(applicationContext, AdminMainActivity::class.java))

            } else if (sharedPref.getString(PreferencesHelper.PREF_USER_TYPE) == "driver") {
                startActivity(Intent(applicationContext, DriverMainActivity::class.java))

            } else if (sharedPref.getString(PreferencesHelper.PREF_USER_TYPE) == "customer") {
//                startActivity(Intent(applicationContext, CustomerMainActivity::class.java))

            } else {
                //
            }

            finish()
        } else {
            //
        }
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()

    }

    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    //permission
    private fun checkSettingAndStartLocationUpdates() {
        val request: LocationSettingsRequest =
            LocationSettingsRequest.Builder().addLocationRequest(locationRequest).build()
        val client: SettingsClient = LocationServices.getSettingsClient(this)

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(request)

        task.addOnSuccessListener {
            startLocationUpdates()
        }

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

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        mFusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

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

}