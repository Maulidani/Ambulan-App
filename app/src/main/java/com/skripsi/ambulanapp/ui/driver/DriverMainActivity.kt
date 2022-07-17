package com.skripsi.ambulanapp.ui.driver

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.ui.ChatActivity
import com.skripsi.ambulanapp.ui.SplashScreenActivity
import com.skripsi.ambulanapp.ui.admin.AdminAddAccountActivity
import com.skripsi.ambulanapp.ui.admin.AdminListOrderHistoryActivity
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.PreferencesHelper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@RequiresApi(Build.VERSION_CODES.N)
class DriverMainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val TAG = "AdminMainActivity"
    private var userType: String? = null
    private var userId: String? = null
    private lateinit var sharedPref: PreferencesHelper

    private lateinit var mMap: GoogleMap
    private var isReady = false
    lateinit var cameraUpdate: CameraUpdate
    private var cameraZoom: Boolean = false
    private val locationRequestCode = 1001
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationResult: LocationResult

    private var orderId: String? = null
    private var orderUserCustomerId: String? = null
    private var orderUserCustomerName: String? = null
    private var orderUserCustomerImage: String? = null
    private var orderPickUpLatlng: LatLng? = null
    private var orderDropOffLatlng: LatLng? = null

    private val loadingMap: LinearLayoutCompat by lazy { findViewById(R.id.view_loading) }
    private val btnUpdateStatus: MaterialButton by lazy { findViewById(R.id.btnChangeStatus) }
    private val btnFinish: MaterialButton by lazy { findViewById(R.id.btnFinish) }
    private val icChat: ImageView by lazy { findViewById(R.id.imgChat) }
    private val tvOrderByName: TextView by lazy { findViewById(R.id.tvOrderByName) }
    private val tvOrderByPhone: TextView by lazy { findViewById(R.id.tvOrderByPhone) }
    private val tvStatusOrder: TextView by lazy { findViewById(R.id.tvStatusOrder) }
    private val parentOrdering: ConstraintLayout by lazy { findViewById(R.id.parentOrdering) }
    private val parentNotOrdering: ConstraintLayout by lazy { findViewById(R.id.parentNotOrdering) }
    private val switchStatusDriver: SwitchCompat by lazy { findViewById(R.id.switchDriverStatus) }
    private val tvStatusDriver: TextView by lazy { findViewById(R.id.tvStatusDriver) }
    private val fabProfile: FloatingActionButton by lazy { findViewById(R.id.fabProfile) }
    private val fabOrderHistory: FloatingActionButton by lazy { findViewById(R.id.fabHistory) }
    private val fablogout: FloatingActionButton by lazy { findViewById(R.id.fabLogout) }
    private val fabDropOffOrder: ExtendedFloatingActionButton by lazy { findViewById(R.id.fabDropOff) }
    private val fabPickUpOrder: ExtendedFloatingActionButton by lazy { findViewById(R.id.fabPickUp) }
    private val chatAdmin: TextView by lazy { findViewById(R.id.tvChatAdmin) }

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

                runBlocking { getIsOrdering() }

                runBlocking {
                    addlatlngDriver(
                        myLocation.latitude.toString(),
                        myLocation.longitude.toString()
                    )
                }
//                switchStatusDriver.isEnabled = true
//                loadingMap.visibility = View.GONE

                getStatusDriver("for_driver_status")

                if (!cameraZoom) {

                    cameraUpdate = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder().target(myLocation).zoom(13.2f).build()
                    )
                    mMap.animateCamera(cameraUpdate)

                    cameraZoom = true
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)

        sharedPref = PreferencesHelper(applicationContext)
        userType = sharedPref.getString(PreferencesHelper.PREF_USER_TYPE)
        userId = sharedPref.getString(PreferencesHelper.PREF_USER_ID)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create()
        locationRequest.interval = 7000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        switchStatusDriver.isEnabled = false
        parentOrdering.visibility = View.GONE
        parentNotOrdering.visibility = View.VISIBLE
        fabDropOffOrder.visibility = View.GONE
        fabPickUpOrder.visibility = View.GONE

        runBlocking { getIsOrdering() }

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        mMap = p0
        isReady = true
        mMap.isMyLocationEnabled = true
        onClick()
    }

    private fun onClick() {
        fabProfile.setOnClickListener {
            getStatusDriver("user_detail")
        }

        fabOrderHistory.setOnClickListener {
            startActivity(Intent(applicationContext, AdminListOrderHistoryActivity::class.java))
        }

        fablogout.setOnClickListener {
            val dialog = BottomSheetDialog(this)
            val view =
                layoutInflater.inflate(R.layout.item_dialog_logout, null)
            val btnYes = view.findViewById<MaterialButton>(R.id.btnYes)
            val btnNo = view.findViewById<MaterialButton>(R.id.btnNo)

            dialog.setCancelable(false)
            dialog.setContentView(view)
            dialog.show()

            btnNo.setOnClickListener {
                dialog.dismiss()
            }

            btnYes.setOnClickListener {
                dialog.dismiss()
                sharedPref.logout()

                startActivity(Intent(applicationContext, SplashScreenActivity::class.java))
                finish()
            }
        }

        fabDropOffOrder.setOnClickListener {
            if (orderDropOffLatlng != null) {
                cameraUpdate = CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder().target(orderDropOffLatlng!!).zoom(13.2f).build()
                )
                mMap.animateCamera(cameraUpdate)
            }

        }

        fabPickUpOrder.setOnClickListener {
            if (orderPickUpLatlng != null) {
                cameraUpdate = CameraUpdateFactory.newCameraPosition(
                    CameraPosition.builder().target(orderPickUpLatlng!!).zoom(13.2f).build()
                )
                mMap.animateCamera(cameraUpdate)
            }
        }

        btnUpdateStatus.setOnClickListener {
            if (orderId != null) {
                val dialogOrder = BottomSheetDialog(this)
                val viewStatus =
                    layoutInflater.inflate(R.layout.item_dialog_update_status_order, null)
                val btnDropOff = viewStatus.findViewById<MaterialButton>(R.id.btntoDropOff)
                val btnPickUp = viewStatus.findViewById<MaterialButton>(R.id.btntoPickUp)

                dialogOrder.setContentView(viewStatus)
                dialogOrder.show()

                btnDropOff.setOnClickListener {
                    dialogOrder.dismiss()
                    tvStatusOrder.text = "tunggu . . ."
                    runBlocking { changeStatusOrder(orderId!!, "to_drop_off") }
                }
                btnPickUp.setOnClickListener {
                    dialogOrder.dismiss()
                    tvStatusOrder.text = "tunggu . . ."
                    runBlocking { changeStatusOrder(orderId!!, "to_pick_up") }
                }
            }
        }

        btnFinish.setOnClickListener {
            if (orderId != null) {
                val dialogOrder = BottomSheetDialog(this)
                val viewStatus =
                    layoutInflater.inflate(R.layout.item_dialog_finish_order, null)
                val btnYes = viewStatus.findViewById<MaterialButton>(R.id.btnYes)
                val btnNo = viewStatus.findViewById<MaterialButton>(R.id.btnNo)

                dialogOrder.setContentView(viewStatus)
                dialogOrder.show()

                btnYes.setOnClickListener {
                    dialogOrder.dismiss()
                    runBlocking { changeStatusOrder(orderId!!, "finish") }
                }
                btnNo.setOnClickListener {
                    dialogOrder.dismiss()
                }
            }
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
                    runBlocking { addStatusDriver(" 0") }
                    switchStatusDriver.isChecked = false
                }
            } else {
                tvStatusDriver.text = "Mengaktifkan..."
                runBlocking { addStatusDriver(" 1") }
            }
        }

        icChat.setOnClickListener {
//            intentYourUserId = intent.getStringExtra("your_user_id").toString()
//            intentYourUserType = intent.getStringExtra("your_user_type").toString()
//            intentYourUserName = intent.getStringExtra("your_user_name").toString()
//            intentYourUserImage = intent.getStringExtra("your_user_image").toString()
            startActivity(Intent(applicationContext,ChatActivity::class.java)
                .putExtra("your_user_id",orderUserCustomerId)
                .putExtra("your_user_type","customer")
                .putExtra("your_user_name",orderUserCustomerName)
                .putExtra("your_user_image",orderUserCustomerImage)
            )

//            Toast.makeText(applicationContext, "chat user_customer_id : $orderUserCustomerId, my_user_id : $userId my_user_type : $userType", Toast.LENGTH_SHORT).show()
        }

        chatAdmin.setOnClickListener {
            val adminId = "1" //static
            startActivity(Intent(applicationContext,ChatActivity::class.java)
                .putExtra("your_user_id",adminId)
                .putExtra("your_user_type","admin")
                .putExtra("your_user_name","Admin")
                .putExtra("your_user_image","")
            )
        }

    }

    private suspend fun getIsOrdering() {
        coroutineScope {
            launch {

                ApiClient.instances.getOrder(userType!!, "ordering", "", userId!!, "")
                    .enqueue(object : Callback<Model.ResponseModel> {
                        override fun onResponse(
                            call: Call<Model.ResponseModel>,
                            response: Response<Model.ResponseModel>
                        ) {
                            val responseBody = response.body()
                            val message = responseBody?.message
                            val order = responseBody?.order

                            if (response.isSuccessful) {

                                if (message == "Success") {

                                    runBlocking { addStatusDriver("0") }

                                    setMarker(order, null, "ordering")
                                    parentOrdering.visibility = View.VISIBLE
                                    parentNotOrdering.visibility = View.GONE
                                    fabDropOffOrder.visibility = View.VISIBLE
                                    fabPickUpOrder.visibility = View.VISIBLE

                                    tvOrderByName.text = order?.customer_name
                                    tvOrderByPhone.text = order?.customer_phone

                                    if (order?.status == "to_pick_up") {
                                        tvStatusOrder.text = "Menuju lokasi pick up"
                                    } else if (order?.status == "to_drop_off") {
                                        tvStatusOrder.text =
                                            "Menuju drop off : ${order.hospital_name}"
                                    } else {
                                        tvStatusOrder.text = "tunggu . . ."

                                        switchStatusDriver.isEnabled = false
                                        tvStatusDriver.text = "Loading..."
                                        parentOrdering.visibility = View.GONE
                                        parentNotOrdering.visibility = View.VISIBLE
                                        fabDropOffOrder.visibility = View.GONE
                                        fabPickUpOrder.visibility = View.GONE
                                    }

                                    orderId = order?.id
                                    orderUserCustomerId = order?.customer_id
                                    orderUserCustomerName = order?.customer_name
                                    orderUserCustomerImage = order?.customer_image

                                    var latPickUp = order?.pick_up_latitude?.toDoubleOrNull()
                                    var longPickUp = order?.pick_up_longitude?.toDoubleOrNull()
                                    var latDropOff = order?.hospital_latitude?.toDoubleOrNull()
                                    var longDropOff = order?.hospital_longitude?.toDoubleOrNull()

                                    if (latPickUp == null) latPickUp = 0.0
                                    if (longPickUp == null) longPickUp = 0.0
                                    if (latDropOff == null) latDropOff = 0.0
                                    if (longDropOff == null) longDropOff = 0.0

                                    orderPickUpLatlng = LatLng(
                                        latPickUp, longPickUp
                                    )
                                    orderDropOffLatlng = LatLng(
                                        latDropOff,
                                        longDropOff
                                    )

                                } else {
                                    getHospital()
                                    parentOrdering.visibility = View.GONE
                                    parentNotOrdering.visibility = View.VISIBLE
                                    fabDropOffOrder.visibility = View.GONE
                                    fabPickUpOrder.visibility = View.GONE
                                }

                                Log.e(TAG, "onResponse: $responseBody")

                            } else {
                                Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                                    .show()

                                switchStatusDriver.isEnabled = false
                                tvStatusDriver.text = "Loading..."
                                parentOrdering.visibility = View.GONE
                                parentNotOrdering.visibility = View.VISIBLE
                                fabDropOffOrder.visibility = View.GONE
                                fabPickUpOrder.visibility = View.GONE
                            }

                            loadingMap.visibility = View.GONE
                        }

                        override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                t.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                            switchStatusDriver.isEnabled = false
                            tvStatusDriver.text = "Loading..."
                            parentOrdering.visibility = View.GONE
                            parentNotOrdering.visibility = View.VISIBLE

                            loadingMap.visibility = View.GONE
                        }
                    })
            }
//            cancel()
        }
    }

    private suspend fun changeStatusOrder(orderId: String, statusOrder: String) {
        coroutineScope {
            launch {

                ApiClient.instances.editStatusOrder(orderId, statusOrder)
                    .enqueue(object : Callback<Model.ResponseModel> {
                        override fun onResponse(
                            call: Call<Model.ResponseModel>,
                            response: Response<Model.ResponseModel>
                        ) {
                            val responseBody = response.body()
                            val message = responseBody?.message

                            if (response.isSuccessful && message == "Success") {

                                Log.e(TAG, "onResponse: $responseBody")

                                //update status order

                                if (statusOrder == "to_drop_off") {
                                    tvStatusOrder.text = "Menuju lokasi drop off"
                                } else if (statusOrder == "to_pick_off") {
                                    tvStatusOrder.text = "Menuju lokasi pick up"
                                } else if (statusOrder == "finish") {

                                    tvStatusDriver.text = "Loading..."
                                    runBlocking { addStatusDriver("1") }

                                    switchStatusDriver.isEnabled = false
                                    parentOrdering.visibility = View.GONE
                                    parentNotOrdering.visibility = View.VISIBLE
                                    fabDropOffOrder.visibility = View.GONE
                                    fabPickUpOrder.visibility = View.GONE
                                    mMap.clear()

                                } else {
                                    //
                                }

                            } else {
                                Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                                    .show()

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
//            cancel()
        }
    }

    private suspend fun addStatusDriver(status: String) {
        coroutineScope {
            launch {

                ApiClient.instances.addStatusDriverUser(userId!!, status)
                    .enqueue(object : Callback<Model.ResponseModel> {
                        override fun onResponse(
                            call: Call<Model.ResponseModel>,
                            response: Response<Model.ResponseModel>
                        ) {
                            val responseBody = response.body()
                            val message = responseBody?.message

                            if (response.isSuccessful && message == "Success") {

                                Log.e(TAG, "onResponse: $responseBody")

                                if (status.toIntOrNull() == 0) {
                                    tvStatusDriver.text = "Akun anda tidak aktif"
                                    switchStatusDriver.isChecked = false
                                } else if (status.toIntOrNull() == 1) {
                                    tvStatusDriver.text = "Akun anda sudah aktif"
                                    switchStatusDriver.isChecked = true
                                } else {
                                    switchStatusDriver.isEnabled = false
                                    tvStatusDriver.text = "Loading..."
                                }

                            } else {
                                Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                                    .show()
                                switchStatusDriver.isEnabled = false
                                tvStatusDriver.text = "Loading..."
                            }

                            loadingMap.visibility = View.GONE
                        }

                        override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                t.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                            switchStatusDriver.isEnabled = false
                            tvStatusDriver.text = "Loading..."

                            loadingMap.visibility = View.GONE
                        }
                    })
            }
//            cancel()
        }
    }

    private suspend fun addlatlngDriver(latitude: String, longitude: String) {
        coroutineScope {
            launch {

                if (latitude.toDoubleOrNull() != null || longitude.toDoubleOrNull() != null) {
                    ApiClient.instances.addLatlngDriverUser(
                        userType!!,
                        userId!!,
                        latitude,
                        longitude
                    )
                        .enqueue(object : Callback<Model.ResponseModel> {
                            override fun onResponse(
                                call: Call<Model.ResponseModel>,
                                response: Response<Model.ResponseModel>
                            ) {
                                val responseBody = response.body()
                                val message = responseBody?.message

                                if (response.isSuccessful && message == "Success") {

                                    Log.e(TAG, "onResponse: $responseBody")


                                } else {
                                    Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                                        .show()

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
//            cancel()
        }
    }

    private fun getHospital() {
//        loadingMap.visibility = View.VISIBLE

        ApiClient.instances.getHospital("")
            .enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {
                    val responseBody = response.body()
                    val message = responseBody?.message
                    val data = responseBody?.data

                    if (response.isSuccessful && message == "Success") {

                        Log.e(TAG, "onResponse: $responseBody")

                        setMarker(null, data, "hospital")
                        getStatusDriver("for_driver_status")

                    } else {
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                            .show()

                        loadingMap.visibility = View.GONE

                    }

//                    loadingMap.visibility = View.GONE
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    loadingMap.visibility = View.GONE
                }

            })
    }

    private fun getStatusDriver(type: String) {
//        loadingMap.visibility = View.VISIBLE

        ApiClient.instances.getUser(userId!!, userType!!, type)
            .enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {
                    val responseBody = response.body()
                    val message = responseBody?.message
                    val user = responseBody?.user

                    if (response.isSuccessful && message == "Success") {

                        Log.e(TAG, "onResponse: $responseBody")

                        if (type == "for_driver_status") {
                            switchStatusDriver.isEnabled = true
                            if (user?.status?.toIntOrNull() == 1) {
                                tvStatusDriver.text = "Akun anda sudah aktif"
                                switchStatusDriver.isChecked = true
                            } else {
                                tvStatusDriver.text = "Akun anda tidak aktif"
                                switchStatusDriver.isChecked = false
                            }
                        } else if (type == "user_detail") {
                            startActivity(
                                Intent(applicationContext, AdminAddAccountActivity::class.java)
                                    .putExtra("action", "show")
                                    .putExtra("user_type", userType)
                                    .putExtra("name", user?.name)
                                    .putExtra("phone", user?.phone)
                                    .putExtra("password", user?.password)
                                    .putExtra("image", user?.image),
                            )
                        } else {
                            //
                        }

                    } else {
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                            .show()

                    }

                    loadingMap.visibility = View.GONE
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    loadingMap.visibility = View.GONE
                }

            })
    }

    private fun setMarker(ordering: Model.OrderModel?, data: List<Model.DataModel>?, type: String) {
        if (isReady) {
            mMap.clear()

            if (type == "hospital") {
                if (data != null) {
                    for (i in data) {
                        var lat = i.latitude.toDoubleOrNull()
                        var long = i.longitude.toDoubleOrNull()

                        if (lat == null) lat = 0.0
                        if (long == null) long = 0.0

                        val hospitaLatlng = LatLng(lat, long)

                        mMap.addMarker(
                            MarkerOptions()
                                .position(hospitaLatlng)
                                .title(i.name)
                                .icon(
                                    BitmapDescriptorFactory
                                        .fromBitmap(
                                            Constant.generateSmallIcon(
                                                applicationContext,
                                                R.drawable.ic_hospital
                                            )
                                        )
                                )
                        )
                    }
                }

            } else if (type == "ordering") {

                if (ordering != null) {
                    var latPickUp = ordering.pick_up_latitude.toDoubleOrNull()
                    var longPickUp = ordering.pick_up_longitude.toDoubleOrNull()
                    var latDropOff = ordering.hospital_latitude.toDoubleOrNull()
                    var longDropOff = ordering.hospital_longitude.toDoubleOrNull()

                    if (latPickUp == null) latPickUp = 0.0
                    if (longPickUp == null) longPickUp = 0.0
                    if (latDropOff == null) latDropOff = 0.0
                    if (longDropOff == null) longDropOff = 0.0

                    val pickUp = LatLng(
                        latPickUp, longPickUp
                    )
                    val dropOff = LatLng(
                        latDropOff,
                        longDropOff
                    )

                    mMap.addMarker(
                        MarkerOptions()
                            .position(pickUp)
                            .title("Pick up")
                            .icon(
                                BitmapDescriptorFactory
                                    .fromBitmap(
                                        Constant.generateSmallIcon(
                                            applicationContext,
                                            R.drawable.ic_hospital
                                        )
                                    )
                            )
                    )
                    mMap.addMarker(
                        MarkerOptions()
                            .position(dropOff)
                            .title("Drop off : ${ordering.hospital_name}")
                            .icon(
                                BitmapDescriptorFactory
                                    .fromBitmap(
                                        Constant.generateSmallIcon(
                                            applicationContext,
                                            R.drawable.ic_hospital
                                        )
                                    )
                            )
                    )
                }

            } else {
                //
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