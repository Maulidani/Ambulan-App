package com.skripsi.ambulanapp.ui.driver

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
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
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.directionModules.DirectionFinder
import com.skripsi.ambulanapp.directionModules.DirectionFinderListener
import com.skripsi.ambulanapp.directionModules.Route
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.GoogleMapHelper.getDottedPolylines
import com.skripsi.ambulanapp.util.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.UnsupportedEncodingException


class MainDriverActivity : AppCompatActivity(), DirectionFinderListener, OnMapReadyCallback {
    private lateinit var sharedPref: PreferencesHelper

    private lateinit var mMap: GoogleMap
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

    var loop = true

    private lateinit var progressDialog: ProgressDialog

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResultCallback: LocationResult) {
            super.onLocationResult(locationResultCallback)

            locationResult = locationResultCallback

            val myLocation =
                LatLng(
                    locationResult.locations[0].latitude,
                    locationResult.locations[0].longitude
                )


            // direction from myLocation to clientLocation/clientDestination
            if (isReady) {
                if (!cameraZoom) {
                    progressDialog.dismiss()

                    cameraUpdate = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder().target(myLocation).zoom(13.5f).build()
                    )
                    mMap.animateCamera(cameraUpdate)
                    cameraZoom = true
                }

                val idUser = sharedPref.getString(Constant.PREF_ID_USER)

                CoroutineScope(Dispatchers.Main).launch {

                    isReady = true

                    while (loop) {
                        getOrder(idUser)
                        delay(300000)

                    }
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        isReady = true

        mMap.isMyLocationEnabled = true

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_driver)
        supportActionBar?.title = "Driver"

        sharedPref = PreferencesHelper(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)

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
                R.id.navLogout -> {
                    sharedPref.logout()
                    finish()
                }
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
        Log.e("direction", "Route : " + routes.toString())

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

    override fun onResume() {
        super.onResume()
        progressDialog.show()

        loop = true

        if (!sharedPref.getBoolean(Constant.PREF_IS_LOGIN)) {
            finish()
        } else if (isReady) {
            val idUser = sharedPref.getString(Constant.PREF_ID_USER)
            getOrder(idUser)
        }
    }

    private fun getOrder(idUser: String?) {

        ApiClient.instances.showOrder("loading", idUser.toString())
            .enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {

                    val message = response.body()?.message
                    val error = response.body()?.errors
                    val data = response.body()?.data

                    if (response.isSuccessful) {
                        if (error == false) {

                            setMarker(data)

                            Toast.makeText(this@MainDriverActivity, "Sedang Ada Orderan", Toast.LENGTH_SHORT).show()

                        }

                    } else {
                        Toast.makeText(this@MainDriverActivity, "gagal", Toast.LENGTH_SHORT).show()
                    }
                    progressDialog.dismiss()

                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@MainDriverActivity,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }

    private fun setMarker(data: List<Model.DataModel>?) {
        mMap.clear()

        if (data != null) {
            for (i in data) {
                val pickUpLocation =
                    LatLng(i.pick_up_latitude.toDouble(), i.pick_up_longitude.toDouble())
                val dropOffLocation =
                    LatLng(i.drop_off_latitude.toDouble(), i.drop_off_longitude.toDouble())

                mMap.addMarker(
                    MarkerOptions().position(pickUpLocation).title("Lokasi Pick Up / Jemput")
                )

                mMap.addMarker(
                    MarkerOptions().position(dropOffLocation).title("Lokasi Drop Off")
                )

                loop = false

            }
        }
    }
}