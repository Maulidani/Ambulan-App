package com.skripsi.ambulanapp.ui.customer

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.view.textservice.TextInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.skripsi.ambulanapp.R

class AddLatLngOrderActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var isReady = false
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationResult: LocationResult
    lateinit var cameraUpdate: CameraUpdate
    private var cameraZoom: Boolean = false
    private val locationRequestCode = 1001

    private lateinit var progressDialog: ProgressDialog
    private var pickUp = ""
    private val cardParentDetailOrder: CardView by lazy { findViewById(R.id.carOrderDetail) }
    private val imgPickUp: ImageView by lazy { findViewById(R.id.imgAddPickUp) }
    private val imgDropOff: ImageView by lazy { findViewById(R.id.imgAddDropOff) }
    private val inputPickUp: TextInputEditText by lazy { findViewById(R.id.inputPickUp) }
    private val inputDropOff: TextInputEditText by lazy { findViewById(R.id.inputDropoff) }
    private val inputNameOrderBy: TextInputEditText by lazy { findViewById(R.id.inputOrderBy) }
    private val inputOrderNote: TextInputEditText by lazy { findViewById(R.id.inputNote) }
    private val btnOrder: MaterialButton by lazy { findViewById(R.id.btnOrder) }

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

            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_lat_lng_order)

        supportActionBar?.title = "Pesan Ambulan"

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Loading...")
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.show()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        imgPickUp.setOnClickListener{
            cardParentDetailOrder.visibility = View.GONE
            mMap.setOnMapClickListener {
                mMap.clear()
                val latLngPickUp = LatLng(it.latitude, it.longitude)
//                mMap.addMarker(
//                    MarkerOptions().position(latLngPickUp).title("Lokasi Jemput")
//                )
                inputPickUp.setText("${it.latitude}, ${it.longitude}")
                cardParentDetailOrder.visibility = View.VISIBLE
            }
        }

        imgDropOff.setOnClickListener{
            cardParentDetailOrder.visibility = View.GONE
            mMap.setOnMapClickListener {
                mMap.clear()
                val latLngDropOff = LatLng(it.latitude, it.longitude)
//                mMap.addMarker(
//                    MarkerOptions().position(latLngDropOff).title("Lokasi Drop Off")
//                )
                inputDropOff.setText("${it.latitude}, ${it.longitude}")
                cardParentDetailOrder.visibility = View.VISIBLE
            }
        }

        btnOrder.setOnClickListener {
            val orderBy = inputNameOrderBy.text.toString()
            val note = inputOrderNote.text.toString()
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        isReady = true
        mMap.isMyLocationEnabled = true
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

}