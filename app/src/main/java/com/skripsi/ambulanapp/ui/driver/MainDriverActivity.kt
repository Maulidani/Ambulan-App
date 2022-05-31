package com.skripsi.ambulanapp.ui.driver

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.directionModules.DirectionFinder
import com.skripsi.ambulanapp.directionModules.DirectionFinderListener
import com.skripsi.ambulanapp.directionModules.Route
import com.skripsi.ambulanapp.util.GoogleMapHelper.*
import java.io.UnsupportedEncodingException
import java.util.function.Consumer


class MainDriverActivity : AppCompatActivity(), DirectionFinderListener, OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var myMap: GoogleMap
    private var isReady = false
    private var polyline: Polyline? = null

    private val locationRequestCode = 1001

    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationResult: LocationResult
    lateinit var cameraUpdate: CameraUpdate
    private var cameraZoom: Boolean = false

    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

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
                myMap.clear()
                myMap.addMarker(MarkerOptions().position(myLocation).title("Lokasi Saya"))
            }

            //clientLocation, clientDestination
            val origin = LatLng(-5.139740893754784, 119.45001968809942)
            val destination = LatLng(-5.1347215426505946, 119.43516105772933)

            // direction from clientLocation to clientDestination
            if (isReady) {
//                if (!cameraZoom) {
//                    cameraUpdate = CameraUpdateFactory.newCameraPosition(
//                        CameraPosition.builder().target(origin).zoom(13.5f).build()
//                    )
//                    mMap.animateCamera(cameraUpdate)
//                    cameraZoom = true
//                }
                mMap.addMarker(MarkerOptions().position(origin).title("Lokasi Jemput"))
                mMap.addMarker(MarkerOptions().position(destination).title("Lokasi Drop Off"))
//                fetchDirections(origin, destination)
            }

            // direction from myLocation to clientLocation/clientDestination
            if (isReady) {
                if (!cameraZoom) {
                    cameraUpdate = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder().target(myLocation).zoom(13.5f).build()
                    )
                    mMap.animateCamera(cameraUpdate)
                    cameraZoom = true
                }

//                fetchDirections(myLocation, origin)

            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        myMap = p0
        isReady = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_driver)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navProfile -> startActivity(
                    Intent(
                        applicationContext,
                        ProfileActivity::class.java
                    )
                )
                R.id.navOrderHistory -> startActivity(
                    Intent(
                        applicationContext,
                        OrderHistoryActivity::class.java
                    )
                )
                R.id.navLogout -> finish()
            }
            true
        }
    }

    private fun fetchDirections(origin: LatLng, destination: LatLng) {

        try {
            DirectionFinder(
                this,
                "${origin.latitude},${origin.longitude}",
                "${destination.latitude},${destination.longitude}"
            ).execute()

            Log.e("direction", "FetchDirection")

        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            Log.e("direction", "FetchDirection Failed")

            Toast.makeText(this, e.message.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDirectionFinderStart() {
        //loading here
        Log.e("direction", "FinderStart...")

    }

    override fun onDirectionFinderSuccess(routes: MutableList<Route>?) {

        Log.e("direction", "FinderSuccess")
        Log.e("direction", "Route : "+routes.toString())

        if (routes != null) {
            if (routes.isNotEmpty() && polyline != null) {
                polyline?.remove()
            }

        }
        try {
            if (routes != null) {
                for (route in routes) {
                    val polylineOptions = getDottedPolylines(route.points)
                    polyline = mMap.addPolyline(polylineOptions!!)
                    Log.e("direction", "Add Polyline")
                }


            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error occurred on finding the directions...", Toast.LENGTH_SHORT)
                .show()
        }
//        mMap.animateCamera(buildCameraUpdate(routes?.get(0)?.endLocation), 10, null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
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