package com.skripsi.ambulanapp.ui.customer

import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.util.SetIconMarkerMap
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class CustomerMainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var isReady = false
    private val locationRequestCode = 1001
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationResult: LocationResult
    lateinit var cameraUpdate: CameraUpdate
    private var cameraZoom: Boolean = false
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private val parentAddOrder: ConstraintLayout by lazy { findViewById(R.id.parentAddOrder) }
    private val parentOrderan: ConstraintLayout by lazy { findViewById(R.id.parentOrderan) }

    private val loadingMap: LinearLayoutCompat by lazy { findViewById(R.id.view_loading) }
    private val slideUp: SlidingUpPanelLayout by lazy { findViewById(R.id.sliding_layout) }
    private val btnOrder: MaterialButton by lazy { findViewById(R.id.btnOrder) }
    private val tvChoosePickUp: TextView by lazy { findViewById(R.id.tv_pick_up) }

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
                if (!cameraZoom) {

                    cameraUpdate = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder().target(myLocation).zoom(13.5f).build()
                    )
                    mMap.animateCamera(cameraUpdate)

                    cameraZoom = true

                    loadingMap.visibility = View.GONE
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        mMap.isMyLocationEnabled = true
        isReady = true

        onClickMap()

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        onClick()

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

        //action start
        loadingMap.visibility = View.VISIBLE

        parentAddOrder.visibility = View.VISIBLE
        parentOrderan.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    private fun onClickMap() {

        mMap.setOnMapClickListener {

            setMarker(it, "Driver")

            val dialogOrder = BottomSheetDialog(this)
            val viewOrder = layoutInflater.inflate(R.layout.item_dialog_add_order, null)
            val btnOrderAmbulance = viewOrder.findViewById<MaterialButton>(R.id.btnOrderAmbulance)
            val btnCancel = viewOrder.findViewById<MaterialButton>(R.id.btnCancelOrder)

            dialogOrder.setCancelable(false)
            dialogOrder.setContentView(viewOrder)
            dialogOrder.show()

            btnCancel.setOnClickListener {
                dialogOrder.dismiss()
            }

            btnOrderAmbulance.setOnClickListener {
                dialogOrder.dismiss()
            }

        }

        mMap.setOnMarkerClickListener {

            Toast.makeText(this, it.title, Toast.LENGTH_SHORT).show()

            true
        }

    }

    private fun onClick() {

        tvChoosePickUp.setOnClickListener {

            val dialogPickUp = BottomSheetDialog(this)
            val viewPickUp = layoutInflater.inflate(R.layout.item_dialog_pickup, null)
            val btnChooseLocation = viewPickUp.findViewById<MaterialButton>(R.id.btnChooseLocation)
            val btnCurrentLocation =
                viewPickUp.findViewById<MaterialButton>(R.id.btnCurrentLocation)

            dialogPickUp.setCancelable(false)
            dialogPickUp.setContentView(viewPickUp)
            dialogPickUp.show()

            btnChooseLocation.setOnClickListener {
                dialogPickUp.dismiss()
                tvChoosePickUp.text = "Titik lokasi"
                tvChoosePickUp.setTextColor(Color.RED)
            }
            btnCurrentLocation.setOnClickListener {
                dialogPickUp.dismiss()
                tvChoosePickUp.text = "Lokasi saat ini"
                tvChoosePickUp.setTextColor(Color.RED)
            }

        }

        btnOrder.setOnClickListener {
            val dialoOrder = BottomSheetDialog(this)
            val view = layoutInflater.inflate(R.layout.item_dialog_add_order, null)
            val btnCancelOrder = view.findViewById<MaterialButton>(R.id.btnCancelOrder)

            dialoOrder.setCancelable(false)
            dialoOrder.setContentView(view)
            dialoOrder.show()

            btnCancelOrder.setOnClickListener {
                dialoOrder.dismiss()
            }

//            startActivity(Intent(this, CustomerMapsActivity::class.java))
        }

    }


    private fun setMarker(latlng: LatLng, title: String) {
        val setIconMarkerMap = SetIconMarkerMap()

        mMap.addMarker(
            MarkerOptions()
                .position(latlng)
                .title("Driver")
                .icon(
                    BitmapDescriptorFactory
                        .fromBitmap(
                            setIconMarkerMap
                                .generateSmallIcon(applicationContext, R.drawable.ic_ambulance)
                        )
                )
        )

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

    private fun stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}