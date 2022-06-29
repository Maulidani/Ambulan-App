package com.skripsi.ambulanapp.ui.driver

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.ui.admin.AdminAddAccountDriverActivity
import com.skripsi.ambulanapp.ui.viewmodel.DriverMainViewModel
import com.skripsi.ambulanapp.util.PreferencesHelper
import com.skripsi.ambulanapp.util.ScreenState
import com.skripsi.ambulanapp.util.SetIconMarkerMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DriverMainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var sharedPref: PreferencesHelper

    private lateinit var mMap: GoogleMap
    private var isReady = false
    private val locationRequestCode = 1001
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationResult: LocationResult
    lateinit var cameraUpdate: CameraUpdate
    private var cameraZoom: Boolean = false
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    private val loadingMap: LinearLayoutCompat by lazy { findViewById(R.id.view_loading) }
    val btnFinish: MaterialButton by lazy { findViewById(R.id.btnFinish) }
    val tvOrderByName: TextView by lazy { findViewById(R.id.tvOrderByName) }
    val tvOrderByPhone: TextView by lazy { findViewById(R.id.tvOrderByPhone) }

    val parentOrdering: ConstraintLayout by lazy { findViewById(R.id.parentOrdering) }
    val parentNotOrdering: ConstraintLayout by lazy { findViewById(R.id.parentNotOrdering) }
    val switchStatusDriver: SwitchCompat by lazy { findViewById(R.id.switchDriverStatus) }
    val tvStatusDriver: TextView by lazy { findViewById(R.id.tvStatusDriver) }

    val fabProfile: FloatingActionButton by lazy { findViewById(R.id.fabProfile) }
    val fablogout: FloatingActionButton by lazy { findViewById(R.id.fabLogout) }

    var idUser = 0

    var idOrder = 0


    private val viewModel: DriverMainViewModel by lazy {
        ViewModelProvider(this).get(DriverMainViewModel::class.java)
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

                if (idUser != 0) {
                    addLatLngDriver(myLocation)
                }
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

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)

        sharedPref = PreferencesHelper(this)
        idUser = sharedPref.getString(PreferencesHelper.PREF_ID_USER)!!.toInt()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 7000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        viewModel.liveData.observe(this) {
            processGetOrderResponse(it)
        }

        onClick()
    }

    override fun onResume() {
        super.onResume()

        getDataStatus(idUser) //get status driver

        if (!sharedPref.getBoolean(PreferencesHelper.PREF_IS_LOGIN)) {
            finish()
        } else {
            //
        }
    }

    private fun onClick() {

        fablogout.setOnClickListener {
            val dialogLogout = BottomSheetDialog(this)
            val viewLogout =
                layoutInflater.inflate(R.layout.item_dialog_logout, null)
            val btnYes = viewLogout.findViewById<MaterialButton>(R.id.btnYes)
            val btnNo = viewLogout.findViewById<MaterialButton>(R.id.btnNo)

            dialogLogout.setCancelable(false)
            dialogLogout.setContentView(viewLogout)
            dialogLogout.show()

            btnNo.setOnClickListener {
                dialogLogout.dismiss()

            }

            btnYes.setOnClickListener {
                dialogLogout.dismiss()
                sharedPref.logout()
                finish()
            }
        }

        fabProfile.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    AdminAddAccountDriverActivity::class.java
                )
                    .putExtra("id", idUser.toString())
                    .putExtra("action", "show")
                    .putExtra("user", "driver")
            )
        }

        switchStatusDriver.setOnClickListener {
            if (!switchStatusDriver.isChecked) {
                val dialogOrder = BottomSheetDialog(this)
                val viewStatus =
                    layoutInflater.inflate(R.layout.item_dialog_switch_status_driver, null)
                val btnYes = viewStatus.findViewById<MaterialButton>(R.id.btnYes)
                val btnNo = viewStatus.findViewById<MaterialButton>(R.id.btnNo)

                dialogOrder.setCancelable(false)
                dialogOrder.setContentView(viewStatus)
                dialogOrder.show()

                btnNo.setOnClickListener {
                    dialogOrder.dismiss()
                    switchStatusDriver.isChecked = true
                }

                btnYes.setOnClickListener {
                    dialogOrder.dismiss()
                    tvStatusDriver.text = "Menonaktifkan..."
                    addStatusDriver(idUser, 0)
                    switchStatusDriver.isChecked = false
                }
            } else {
                tvStatusDriver.text = "Mengaktifkan..."
                addStatusDriver(idUser, 1)
            }
        }

        switchStatusDriver.setOnCheckedChangeListener { _, isChecked ->
            Log.e(
                "Switch State=",
                "" + isChecked
            )

            if (isChecked) {
//                tvStatusDriver.text = "Akun anda sudah aktif"

            } else {
//                tvStatusDriver.text = "Akun anda tidak aktif"
            }
        }

        btnFinish.setOnClickListener {
            if (idOrder != 0) {

                val dialogOrder = BottomSheetDialog(this)
                val viewOrder = layoutInflater.inflate(R.layout.item_dialog_finish_order, null)
                val btnYes = viewOrder.findViewById<MaterialButton>(R.id.btnYes)
                val btnNo = viewOrder.findViewById<MaterialButton>(R.id.btnNo)

                dialogOrder.setCancelable(false)
                dialogOrder.setContentView(viewOrder)
                dialogOrder.show()

                btnNo.setOnClickListener {
                    dialogOrder.dismiss()
                }

                btnYes.setOnClickListener {
                    finishOrder(idOrder, dialogOrder)
                }

            }
        }

//        btnPickUp.setOnClickListener {
//            slideUp.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
//        }
//
//        btnDropOff.setOnClickListener {
//            slideUp.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
//        }
    }



    private fun processGetOrderResponse(state: ScreenState<List<Model.DataModel>?>) {
        CoroutineScope(Dispatchers.Main).launch {

            when (state) {
                is ScreenState.Loading -> {
                    //loading
                }
                is ScreenState.Success -> {

                    if (state.data != null) {

                        for (i in state.data) {
                            if (i.id_driver == idUser && i.status == 0) {
                                idOrder = i.id!!

                                //ordering

                                parentOrdering.visibility = View.VISIBLE
                                parentNotOrdering.visibility = View.GONE

                                tvOrderByName.text = i.name
                                tvOrderByPhone.text = i.phone

                                val latlngPickUp = LatLng(
                                    i.pick_up_latitude!!.toDouble(),
                                    i.pick_up_longitude!!.toDouble()
                                )

                                mMap.clear()
                                setMarker(latlngPickUp, "Lokasi pick up / jemput", "pick_up")
                                i.id_hospital?.let { getHospital(it) }
                                break

                            } else if (i.id_driver == idUser && i.status == 1) {

                                parentOrdering.visibility = View.GONE
                                parentNotOrdering.visibility = View.VISIBLE
                            } else {

                                parentOrdering.visibility = View.GONE
                                parentNotOrdering.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        parentOrdering.visibility = View.GONE
                        parentNotOrdering.visibility = View.VISIBLE
                    }
                }
                is ScreenState.Error -> {

                    parentOrdering.visibility = View.GONE
                    parentNotOrdering.visibility = View.VISIBLE
                }
            }
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

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    private fun setMarker(latlng: LatLng, title: String, type: String) {
        val setIconMarkerMap = SetIconMarkerMap()

        if (type == "pick_up") {
            mMap.addMarker(
                MarkerOptions()
                    .position(latlng)
                    .title(title)
                    .icon(
                        BitmapDescriptorFactory
                            .fromBitmap(
                                setIconMarkerMap
                                    .generateSmallIcon(applicationContext, R.drawable.ic_hospital)
                            )
                    )
            )

        } else if (type == "hospital") {
            mMap.addMarker(
                MarkerOptions()
                    .position(latlng)
                    .title("Lokasi drop off : $title")
                    .icon(
                        BitmapDescriptorFactory
                            .fromBitmap(
                                setIconMarkerMap
                                    .generateSmallIcon(applicationContext, R.drawable.ic_hospital)
                            )
                    )
            )
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

    private fun getHospital(idHospital: Int) {
        CoroutineScope(Dispatchers.Main).launch {

            ApiClient.instances.getHospital(idHospital, "")
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = response.body()?.message
                        val hospital = response.body()?.hospital

                        if (response.isSuccessful && message == "Success") {
                            val latLngHospital =
                                hospital?.latitude?.let {
                                    LatLng(
                                        it.toDouble(),
                                        hospital.longitude.toDouble()
                                    )
                                }
                            latLngHospital?.let { setMarker(it, hospital.name, "hospital") }
                        } else {
                            mMap.clear()
                        }

                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                        Log.e("character", "failure: " + t.message.toString())

                    }

                })
        }

    }

    private fun finishOrder(idOrder: Int, dialogOrder: BottomSheetDialog) {
        CoroutineScope(Dispatchers.IO).launch {

            ApiClient.instances.addStatusOrder(idOrder, 1)
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = response.body()?.message

                        if (response.isSuccessful && message == "Success") {

                            parentOrdering.visibility = View.GONE
                            parentNotOrdering.visibility = View.VISIBLE
                            mMap.clear()

                        } else {
                            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT)
                                .show()

                        }

                        dialogOrder.dismiss()
                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            t.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                        dialogOrder.dismiss()

                    }

                })
        }

    }

    private fun addStatusDriver(idUser: Int, status: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            ApiClient.instances.addStatusDriverUser(idUser, status)
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = response.body()?.message

                        if (response.isSuccessful) {

                            if (status == 1) {
                                tvStatusDriver.text = "Akun anda sudah aktif"
                                switchStatusDriver.isChecked = true
                            } else {
                                tvStatusDriver.text = "Akun anda tidak aktif"
                                switchStatusDriver.isChecked = false
                            }

                        } else {
                            Log.e(this.toString(), "onResponse:Failed ", )
                        }

                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            t.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                })
        }
    }

    private fun addLatLngDriver(myLocation: LatLng) {
        CoroutineScope(Dispatchers.Default).launch {

            ApiClient.instances.addLatlngDriverUser(
                idUser,
                myLocation.latitude.toString(),
                myLocation.longitude.toString()
            )
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = response.body()?.message
                        val data = response.body()?.data

                        if (response.isSuccessful && message == "Success") {
                            Log.e("driver", "$message")

                        } else {
                            Log.e("driver", "not success: " + response.code().toString())

                        }
                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                        Log.e("driver", "failure: " + t.message.toString())

                    }

                })
        }
    }
    private fun getDataStatus(id: Int) : Int{
        var status = 0

        CoroutineScope(Dispatchers.Main).launch {

            tvStatusDriver.text = "Tunggu..."
            switchStatusDriver.isChecked = false

            ApiClient.instances.getDriverUser()
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = response.body()?.message
                        val data = response.body()?.data

                        if (response.isSuccessful && message == "Success") {

                            Log.e("onResponse: ", data.toString())

                            if (data != null) {
                                for (i in data){
                                    if (i.id == id.toInt()) {
                                        status = i.status!!

                                        if (i.status == 1){
                                            tvStatusDriver.text = "Akun anda sudah aktif"
                                            switchStatusDriver.isChecked = true
                                        } else {
                                            tvStatusDriver.text = "Akun anda tidak aktif"
                                            switchStatusDriver.isChecked = false
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(applicationContext, "Data tidak ada", Toast.LENGTH_SHORT)
                                    .show()
                                finish()
                            }

                        } else {
                            Log.e(this.toString(), "onResponse: Failed", )
                        }
                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            t.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        }
        return status
    }
}