package com.skripsi.ambulanapp.ui.customer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
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
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterListHospital
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.ui.ChatActivity
import com.skripsi.ambulanapp.ui.SplashScreenActivity
import com.skripsi.ambulanapp.ui.admin.AdminAddAccountActivity
import com.skripsi.ambulanapp.ui.admin.AdminListHospitalActivity
import com.skripsi.ambulanapp.ui.admin.AdminListOrderHistoryActivity
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.Constant.setShowProgress
import com.skripsi.ambulanapp.util.PreferencesHelper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.stream.Stream

@RequiresApi(Build.VERSION_CODES.N)
class CustomerMainActivity : AppCompatActivity(), AdapterListHospital.IUserRecycler,
    OnMapReadyCallback {
    private val TAG = "CustomerMainActvty"
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
    private var orderUserDriverId: String? = null
    private var orderUserDriverName: String? = null
    private var orderUserDriverImage: String? = null
    private var pickUpForOrderLatLng: LatLng? = null
    private var choosePickUp: Boolean = false
    private var chooseDropOff: Boolean = false
    private var chooseDropOffId: String? = null
    private var chooseDropOffName: String? = null
    private var forChooseInMapLatlng: Boolean = false
    private var myLocation: LatLng? = null
    var dataForSortDistanceDriver = ArrayList<Array<String>>()

    private val loadingMap: LinearLayoutCompat by lazy { findViewById(R.id.view_loading) }
    private val loadingHospitalDest: SpinKitView by lazy { findViewById(R.id.spin_kit_loading_hospital) }
    private val tvHeadOrdering: TextView by lazy { findViewById(R.id.tvHeadAsk) }
    private val tvDriverNameOrder: TextView by lazy { findViewById(R.id.tvDriverName) }
    private val tvDriverPhoneOrder: TextView by lazy { findViewById(R.id.tvDriverPhone) }
    private val tvStatusOrder: TextView by lazy { findViewById(R.id.tvStatusOrder) }
    private val icChat: ImageView by lazy { findViewById(R.id.imgChat) }
    private val parentOrdering: ConstraintLayout by lazy { findViewById(R.id.parentOrdering) }
    private val parentNotOrdering: ConstraintLayout by lazy { findViewById(R.id.parentNotOrdering) }
    private val fabProfile: FloatingActionButton by lazy { findViewById(R.id.fabProfile) }
    private val fabOrderHistory: FloatingActionButton by lazy { findViewById(R.id.fabHistory) }
    private val fabCallCenter: FloatingActionButton by lazy { findViewById(R.id.fabCallCenter) }
    private val fablogout: FloatingActionButton by lazy { findViewById(R.id.fabLogout) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rvHospital) }
    private val showAllHospital: TextView by lazy { findViewById(R.id.tvShowAll) }
    private val btnOrder: MaterialButton by lazy { findViewById(R.id.btnOrder) }
    private val tvChoosePickUp: TextView by lazy { findViewById(R.id.tv_pick_up) }
    private val etHospital: EditText by lazy { findViewById(R.id.searchDropoff) }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResultCallback: LocationResult) {
            super.onLocationResult(locationResultCallback)

            locationResult = locationResultCallback

            myLocation =
                LatLng(
                    locationResult.locations[0].latitude,
                    locationResult.locations[0].longitude
                )

            if (isReady) {

                runBlocking { getIsOrdering() }

                if (!cameraZoom) {

                    cameraUpdate = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder().target(myLocation!!).zoom(13.2f).build()
                    )
                    mMap.animateCamera(cameraUpdate)

                    cameraZoom = true
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_main)

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

        parentOrdering.visibility = View.GONE
        parentNotOrdering.visibility = View.VISIBLE
//        loadingMap.visibility = View.GONE
//        loadingHospitalDest.visibility = View.GONE

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
            getUserDetail("user_detail")
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

        mMap.setOnMapClickListener {
            if (forChooseInMapLatlng) {
//                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
                mMap.addMarker(
                    MarkerOptions().position(it).title("Lokasi pick up")
                )
                tvChoosePickUp.setTextColor(Color.RED)
                tvChoosePickUp.text = "${it.latitude}, ${it.longitude}"
                pickUpForOrderLatLng = it
                tvChoosePickUp.setTextColor(Color.RED)
                choosePickUp = true

                forChooseInMapLatlng = false
            }
        }

        tvChoosePickUp.setOnClickListener {

            if (myLocation != null) {

                val dialogPickUp = BottomSheetDialog(this)
                val viewPickUp = layoutInflater.inflate(R.layout.item_dialog_pickup, null)
                val btnChooseLocation =
                    viewPickUp.findViewById<MaterialButton>(R.id.btnChooseLocation)
                val btnCurrentLocation =
                    viewPickUp.findViewById<MaterialButton>(R.id.btnCurrentLocation)

                dialogPickUp.setCancelable(false)
                dialogPickUp.setContentView(viewPickUp)
                dialogPickUp.show()

                btnChooseLocation.setOnClickListener {
                    dialogPickUp.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "Pilih titik di map untuk lokasi pick up",
                        Toast.LENGTH_SHORT
                    ).show()

                    forChooseInMapLatlng = true

                }

                btnCurrentLocation.setOnClickListener {
                    dialogPickUp.dismiss()
                    pickUpForOrderLatLng = myLocation
                    tvChoosePickUp.text = "Lokasi saat ini"
                    tvChoosePickUp.setTextColor(Color.RED)
                    choosePickUp = true
                }

            }
        }

        btnOrder.setOnClickListener {
            if (choosePickUp && chooseDropOff && chooseDropOffId != null) {

                val dialogOrder = BottomSheetDialog(this)
                val view = layoutInflater.inflate(R.layout.item_dialog_add_order, null)
                val inputPickUp = view.findViewById<TextInputEditText>(R.id.inputPickUp)
                val inputDropOff = view.findViewById<TextInputEditText>(R.id.inputDropOff)
                val btnOrderAmbulance =
                    view.findViewById<MaterialButton>(R.id.btnOrderAmbulance)
                val btnCancelOrder = view.findViewById<MaterialButton>(R.id.btnCancelOrder)

                dialogOrder.setCancelable(false)
                dialogOrder.setContentView(view)
                dialogOrder.show()

                inputPickUp.setText(tvChoosePickUp.text)
                inputPickUp.setTextColor(Color.BLACK)
                inputDropOff.setText(chooseDropOffName)
                inputDropOff.setTextColor(Color.BLACK)
                btnOrderAmbulance.setOnClickListener {
                    btnOrderAmbulance.setShowProgress(true)
                    getUserForOrder(null)
                    btnOrderAmbulance.setShowProgress(false)
                    dialogOrder.dismiss()

                }

                btnCancelOrder.setOnClickListener {
                    dialogOrder.dismiss()
                }

            } else {
                Toast.makeText(
                    applicationContext,
                    "Pilih lokasi pick up dan tujuan rumah sakit!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        etHospital.setOnClickListener {
            etHospital.setTextColor(Color.BLACK)
            etHospital.setText("")
            chooseDropOff = false
        }

        etHospital.addTextChangedListener {
            loadingHospitalDest.visibility = View.VISIBLE
            getHospital(it.toString())
        }

        showAllHospital.setOnClickListener {
            startActivity(Intent(applicationContext, AdminListHospitalActivity::class.java))
        }

        icChat.setOnClickListener {
//            intentYourUserId = intent.getStringExtra("your_user_id").toString()
//            intentYourUserType = intent.getStringExtra("your_user_type").toString()
//            intentYourUserName = intent.getStringExtra("your_user_name").toString()
//            intentYourUserImage = intent.getStringExtra("your_user_image").toString()
            startActivity(Intent(applicationContext, ChatActivity::class.java)
                .putExtra("your_user_id",orderUserDriverId)
                .putExtra("your_user_type","driver")
                .putExtra("your_user_name",orderUserDriverName)
                .putExtra("your_user_image",orderUserDriverImage)
            )

//            Toast.makeText(applicationContext, "chat user_customer_id : $orderUserCustomerId, my_user_id : $userId my_user_type : $userType", Toast.LENGTH_SHORT).show()
        }

        fabCallCenter.setOnClickListener {
            Toast.makeText(applicationContext, "Chat dengan Call Center / Admin", Toast.LENGTH_SHORT).show()
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
                ApiClient.instances.getOrder(userType!!, "ordering", userId!!, "", "")
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

                                    setMarker(order, null, null, "ordering")
                                    parentOrdering.visibility = View.VISIBLE
                                    parentNotOrdering.visibility = View.GONE

                                    tvDriverNameOrder.text = order?.driver_name
                                    tvDriverPhoneOrder.text = order?.driver_phone

                                    if (order?.status == "to_pick_up") {
                                        tvHeadOrdering.text = "Driver sedang dijalan"
                                        tvStatusOrder.text = "Menuju lokasi pick up"
                                    } else if (order?.status == "to_drop_off") {
                                        tvHeadOrdering.text = "Driver sedang dijalan"
                                        tvStatusOrder.text =
                                            "Menuju drop off : ${order.hospital_name}"
                                    } else {
                                        tvStatusOrder.text = "tunggu . . ."
                                        parentOrdering.visibility = View.GONE
                                        parentNotOrdering.visibility = View.VISIBLE
                                    }

                                    orderId = order?.id
                                    orderUserDriverId = order?.driver_id
                                    orderUserDriverName = order?.driver_name
                                    orderUserDriverImage = order?.driver_image

                                } else {
                                    getHospital("")
                                    parentOrdering.visibility = View.GONE
                                    parentNotOrdering.visibility = View.VISIBLE
                                }

                                Log.e(TAG, "onResponse: $responseBody")

                            } else {
                                Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                                    .show()

                                parentOrdering.visibility = View.GONE
                                parentNotOrdering.visibility = View.VISIBLE
                            }

                            loadingMap.visibility = View.GONE
                        }

                        override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                t.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()

                            parentOrdering.visibility = View.GONE
                            parentNotOrdering.visibility = View.VISIBLE

                            loadingMap.visibility = View.GONE
                        }
                    })
            }
//            cancel()
        }
    }

    private fun getHospital(search: String) {
//        loadingMap.visibility = View.VISIBLE

        ApiClient.instances.getHospital(search)
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

//                        setMarker(null, data, "hospital")

                        val adapter = data?.let {
                            AdapterListHospital(
                                "hospital_destination",
                                it,
                                this@CustomerMainActivity
                            )
                        }

                        getUserForOrder(data)

                        rv.layoutManager = LinearLayoutManager(applicationContext)
                        rv.adapter = adapter

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

    private fun getUserForOrder(dataHospitalMarker: List<Model.DataModel>?) {
        ApiClient.instances.getUser("", "", "for_order")
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

                        if (dataHospitalMarker != null) {
                            setMarker(null, dataHospitalMarker, data, "hospital_driver")
                        } else {
                            calculateDistance(data)
                        }

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
                    tvChoosePickUp.isEnabled = true
                    etHospital.isEnabled = true

                }

            })
    }


    private fun calculateDistance(data: List<Model.DataModel>?) {
        var exist = false

        if (data != null) {
            for (i in data) {

                if (i.status.toIntOrNull() == 1) {

                    var lat = i.latitude.toDoubleOrNull()
                    var long = i.longitude.toDoubleOrNull()

                    if (lat == null) {
                        lat = 0.0
                    }
                    if (long == null) {
                        long = 0.0
                    }

                    var distance = haversine(
                        pickUpForOrderLatLng!!.latitude,
                        pickUpForOrderLatLng!!.longitude,
                        lat,
                        long
                    )

                    dataForSortDistanceDriver.add(
                        arrayOf(
                            distance.toString(),
                            i.id.toString(),
                        )
                    )
                    exist = true
                }
            }

            if (exist) {
                val tes1: Array<Array<String>?> = arrayOfNulls(dataForSortDistanceDriver.size)
                for (i in dataForSortDistanceDriver.indices) {
                    tes1[i] = dataForSortDistanceDriver[i]
                }
                sortMintoMax(tes1)
            } else {
                Toast.makeText(applicationContext, "Tidak ada driver aktif", Toast.LENGTH_SHORT)
                    .show()

            }

        } else {
            Toast.makeText(applicationContext, "Tidak ada driver aktif", Toast.LENGTH_SHORT).show()
        }

    }

    private fun haversine(
        latPickUp: Double, longPickUp: Double,
        latDriver: Double, longDriver: Double
    ): Double {
        // distance between latitudes and longitudes
        var lat1 = latPickUp
        var lat2 = latDriver
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(longDriver - longPickUp)

        // convert to radians
        lat1 = Math.toRadians(lat1)
        lat2 = Math.toRadians(lat2)

        // apply formulae
        val a = Math.pow(Math.sin(dLat / 2), 2.0) +
                Math.pow(Math.sin(dLon / 2), 2.0) *
                Math.cos(lat1) *
                Math.cos(lat2)
        val rad = 6371.0
        val c = 2 * Math.asin(Math.sqrt(a))

        return rad * c
    }


    private fun sortMintoMax(
        value: Array<Array<String>?>,
    ) {

        val list = ArrayList<Array<String>>()

        Stream.of<Array<String>>(*value)
            .sorted { small: Array<String>, big: Array<String> ->
                small[0].compareTo(big[0])
            }
            .forEach { dataSort: Array<String> ->
                list.add(dataSort)
            }

        Log.e(TAG, "sortMintoMax: ${list[0][1]}")

        addOrder(list[0][1])
    }

    private fun addOrder(driverId: String) {
        tvHeadOrdering.text = "Driver ambulan melihat orderan anda"
        tvStatusOrder.text = "tunggu . . ."
        tvDriverNameOrder.text = "tunggu . . ."
        tvDriverPhoneOrder.text = "tunggu . . ."


        ApiClient.instances.addOrder(
            userId!!,
            driverId,
            chooseDropOffId.toString(),
            pickUpForOrderLatLng?.latitude.toString(),
            pickUpForOrderLatLng?.longitude.toString(),
            "to_pick_up"
        )
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

                        parentOrdering.visibility = View.VISIBLE
                        parentNotOrdering.visibility = View.GONE

                        choosePickUp = false
                        forChooseInMapLatlng = false
                        chooseDropOff = false
                        tvChoosePickUp.text = "Lokasi pick up"
                        etHospital.setText("")
                        tvChoosePickUp.setTextColor(Color.BLACK)
                        tvChoosePickUp.setTextColor(Color.BLACK)
                        Toast.makeText(
                            applicationContext,
                            "Berhasil order ambulan",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                            .show()
                    }

//                    loadingMap.visibility = View.GONE
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

    private fun setMarker(
        ordering: Model.OrderModel?,
        data: List<Model.DataModel>?,
        dataDriverAktive: List<Model.DataModel>?,
        type: String
    ) {
        if (isReady) {
            mMap.clear()

            if (type == "hospital_driver") {
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

                        if (dataDriverAktive != null) {
                            for (i in dataDriverAktive) {
                                var lat = i.latitude.toDoubleOrNull()
                                var long = i.longitude.toDoubleOrNull()

                                if (lat == null) lat = 0.0
                                if (long == null) long = 0.0

                                val driverLatlng = LatLng(lat, long)

                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(driverLatlng)
                                        .title(i.name)
                                        .icon(
                                            BitmapDescriptorFactory
                                                .fromBitmap(
                                                    Constant.generateSmallIcon(
                                                        applicationContext,
                                                        R.drawable.ic_ambulance
                                                    )
                                                )
                                        )
                                )
                            }
                        }
                    }
                }

            } else if (type == "ordering") {

                if (ordering != null) {
                    var latPickUp = ordering.pick_up_latitude.toDoubleOrNull()
                    var longPickUp = ordering.pick_up_longitude.toDoubleOrNull()
                    var latDropOff = ordering.hospital_latitude.toDoubleOrNull()
                    var longDropOff = ordering.hospital_longitude.toDoubleOrNull()
                    var latDriver = ordering.driver_latitude.toDoubleOrNull()
                    var longDriver = ordering.driver_longitude.toDoubleOrNull()

                    if (latPickUp == null) latPickUp = 0.0
                    if (longPickUp == null) longPickUp = 0.0
                    if (latDropOff == null) latDropOff = 0.0
                    if (longDropOff == null) longDropOff = 0.0
                    if (latDriver == null) latDriver = 0.0
                    if (longDriver == null) longDriver = 0.0

                    val pickUp = LatLng(
                        latPickUp, longPickUp
                    )
                    val dropOff = LatLng(
                        latDropOff,
                        longDropOff
                    )
                    val driver = LatLng(
                        latDriver,
                        longDriver
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
                    mMap.addMarker(
                        MarkerOptions()
                            .position(driver)
                            .title("Driver : ${ordering.driver_name}")
                            .icon(
                                BitmapDescriptorFactory
                                    .fromBitmap(
                                        Constant.generateSmallIcon(
                                            applicationContext,
                                            R.drawable.ic_ambulance
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

    private fun getUserDetail(type: String) {

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

                        startActivity(
                            Intent(applicationContext, AdminAddAccountActivity::class.java)
                                .putExtra("action", "edit")
                                .putExtra("user_type", "customer")
                                .putExtra("id", user?.id)
                                .putExtra("name", user?.name)
                                .putExtra("phone", user?.phone)
                                .putExtra("password", user?.password)
                                .putExtra("image", user?.image),
                        )
                    } else {
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                            .show()

                    }

//                    loadingMap.visibility = View.GONE
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

//                    loadingMap.visibility = View.GONE
                }

            })
    }

    override fun refreshView(onUpdate: Boolean, result: Model.DataModel?) {
        getHospital("")

        chooseDropOffId = result?.id
        chooseDropOffName = result?.name
        etHospital.setText(result?.name)
        etHospital.setTextColor(Color.RED)
        chooseDropOff = true
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