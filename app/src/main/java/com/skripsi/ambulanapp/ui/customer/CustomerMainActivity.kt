package com.skripsi.ambulanapp.ui.customer

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Color.RED
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterListHospital
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.ui.admin.AdminListArticleActivity
import com.skripsi.ambulanapp.ui.viewmodel.CustomerMainViewModel
import com.skripsi.ambulanapp.util.PreferencesHelper
import com.skripsi.ambulanapp.util.ScreenState
import com.skripsi.ambulanapp.util.SetIconMarkerMap
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.stream.Stream


@RequiresApi(Build.VERSION_CODES.N)
class CustomerMainActivity : AppCompatActivity(), OnMapReadyCallback,
    AdapterListHospital.IUserRecycler {
    private lateinit var sharedPref: PreferencesHelper

    private lateinit var mMap: GoogleMap
    private var isReady = false
    private val locationRequestCode = 1001
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationResult: LocationResult
    lateinit var cameraUpdate: CameraUpdate
    private var cameraZoom: Boolean = false
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var myLocation: LatLng? = null

    private val nestedScrollView: NestedScrollView by lazy { findViewById(R.id.nestedScrollView) }
    private val parentAddOrder: ConstraintLayout by lazy { findViewById(R.id.parentAddOrder) }
    private val parentOrderan: ConstraintLayout by lazy { findViewById(R.id.parentOrderan) }
    private val tvHospitalNotFound: TextView by lazy { findViewById(R.id.tvHospitalNotFound) }

    private val btnArticle: ExtendedFloatingActionButton by lazy { findViewById(R.id.fabArticle) }
    private val tvDriverName: TextView by lazy { findViewById(R.id.tvDriverName) }
    private val tvDriverPhone: TextView by lazy { findViewById(R.id.tvDriverPhone) }
    private val fabBack: FloatingActionButton by lazy { findViewById(R.id.fabBack) }
    private val loadingMap: LinearLayoutCompat by lazy { findViewById(R.id.view_loading) }
    private val slideUp: SlidingUpPanelLayout by lazy { findViewById(R.id.sliding_layout) }
    private val btnOrder: MaterialButton by lazy { findViewById(R.id.btnOrder) }
    private val btnFinish: MaterialButton by lazy { findViewById(R.id.btnFinish) }
    private val tvChoosePickUp: TextView by lazy { findViewById(R.id.tv_pick_up) }
    private val etHospital: EditText by lazy { findViewById(R.id.searchDropoff) }
    private val rv: RecyclerView by lazy { findViewById(R.id.rvHospital) }
    private val loadingHospital: SpinKitView by lazy { findViewById(R.id.spin_kit_loading_hospital) }

    private var myOrderId = ""
    private var myOrderIdHospital = ""
    private var pickUpOrderLatLng: LatLng? = null
    private var choosePickUp = false
    private var chooseHospital = false
    private var driverList: List<Model.DataModel>? = null
    private var hospitalList: List<Model.DataModel>? = null
    var dataForSortDistanceDriver = ArrayList<Array<String>>()

//    private dataHospitalForOrder =

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
                if (!cameraZoom) {

                    cameraUpdate = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder().target(myLocation!!).zoom(13.5f).build()
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

        //
        if (isReady) {
            mMap.setOnMapClickListener {
//                Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_SHORT).show()
                mMap.addMarker(
                    MarkerOptions().position(it).title("Lokasi jemput / pick up : pasien")
                )
                tvChoosePickUp.setTextColor(RED)
                tvChoosePickUp.text = "${it.latitude}, ${it.longitude}"
                pickUpOrderLatLng = it
            }
        }
    }

    private val viewModel: CustomerMainViewModel by lazy {
        ViewModelProvider(this).get(CustomerMainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_main)

        sharedPref = PreferencesHelper(this)

        viewModel.liveData.observe(this) {
            processGetOrderResponse(it)
        }

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

    private fun processGetOrderResponse(state: ScreenState<List<Model.DataModel>?>) {

//        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        when (state) {
            is ScreenState.Loading -> {
//                progressBar.visibility = View.VISIBLE
            }
            is ScreenState.Success -> {
//                progressBar.visibility = View.GONE

                if (state.data != null) {

                    mMap.clear()

                    Log.e(application.toString(), "data : " + state.data.toString())

                    myOrderId =
                        sharedPref.getString(PreferencesHelper.PREF_ID_ORDER_CUSTOMER).toString()

                    for (i in state.data) {

                        if (myOrderId == "null") {
                            myOrderId = "0"
                        }

                        Log.e(this.toString(), "processGetOrderResponse: myId: " + myOrderId)
                        Log.e(this.toString(), "processGetOrderResponse:idFor: " + i.id)

                        if (myOrderId.toInt() == i.id!! && i.status!! == 0) {

                            parentAddOrder.visibility = View.GONE
                            parentOrderan.visibility = View.VISIBLE

                            getDriver(i.id_driver)
                            break
                        } else {

                            getDriver(null)
                            setMarker(hospitalList, null, "hospital", null)

                            parentAddOrder.visibility = View.VISIBLE
                            parentOrderan.visibility = View.GONE
                        }
                    }
                }
            }
            is ScreenState.Error -> {
//                progressBar.visibility = View.GONE

                parentAddOrder.visibility = View.VISIBLE
                parentOrderan.visibility = View.GONE

            }
        }
    }

    private fun getDriver(idDriver: Int?) {
        CoroutineScope(Dispatchers.IO).launch {

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

                            driverList = data

                            if (data != null && idDriver != null) {
                                for (i in data) {
                                    if (idDriver == i.id) {
                                        setMarker(data, null, "driver_ordering", idDriver)
                                    }
                                }
                            } else {
                                setMarker(data, null, "driver", null)
                            }


                        } else {
                            Log.e(this.toString(), "onResponse: Failed")
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

        getHospital("")
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }

    private fun onClick() {

        fabBack.setOnClickListener { finish() }

        btnArticle.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    AdminListArticleActivity::class.java
                ).putExtra("type", "customer")
            )
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
                        "Pilih di map titik lokasi jemput / pick up : pasien",
                        Toast.LENGTH_SHORT
                    ).show()

                    choosePickUp = true

                }

                btnCurrentLocation.setOnClickListener {
                    dialogPickUp.dismiss()
                    pickUpOrderLatLng = myLocation
                    tvChoosePickUp.text = "Lokasi saat ini"
                    tvChoosePickUp.setTextColor(RED)
                    choosePickUp = true
                }
            }
        }


        btnFinish.setOnClickListener {
            if (myOrderId != "null" && tvDriverName.text != "Tunggu . . .") {

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
                    if (myOrderId.isNotEmpty()) {
                        finishOrder(myOrderId.toInt(), dialogOrder)
                    }
                }

            }
        }

        etHospital.setOnClickListener {
            slideUp.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
        }

        etHospital.addTextChangedListener {
            chooseHospital = false
            etHospital.setTextColor(Color.BLACK)
            slideUp.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            getHospital(it.toString())
        }

        btnOrder.setOnClickListener {

            if (isReady && myLocation != null) {

                if (choosePickUp && pickUpOrderLatLng != null && chooseHospital) {
                    val dialogOrder = BottomSheetDialog(this)
                    val view = layoutInflater.inflate(R.layout.item_dialog_add_order, null)
                    val inputOrderByName = view.findViewById<TextInputEditText>(R.id.inputOrderBy)
                    val inputOrderByPhone = view.findViewById<TextInputEditText>(R.id.inputPhone)
                    val btnOrderAmbulance =
                        view.findViewById<MaterialButton>(R.id.btnOrderAmbulance)
                    val btnCancelOrder = view.findViewById<MaterialButton>(R.id.btnCancelOrder)

                    dialogOrder.setCancelable(false)
                    dialogOrder.setContentView(view)
                    dialogOrder.show()

                    btnOrderAmbulance.setOnClickListener {
                        val name = inputOrderByName.text.toString()
                        val phone = inputOrderByPhone.text.toString()

                        if (name.isNotEmpty() && phone.isNotEmpty()) {

                            btnOrderAmbulance.setShowProgress(true)
                            calculateDistance(btnOrderAmbulance, dialogOrder, name, phone)

                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Lengkapi data pemesan!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
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

//            startActivity(Intent(this, CustomerMapsActivity::class.java))
            } else {
                Toast.makeText(applicationContext, "Sedang memuat lokasi...", Toast.LENGTH_SHORT)
                    .show()
            }

        }

    }

    private fun calculateDistance(
        btnOrderAmbulance: MaterialButton,
        dialogOrder: BottomSheetDialog,
        name: String,
        phone: String
    ) {

        for (i in driverList!!) {
            if (i.status == 1) {
                var distance = haversine(
                    pickUpOrderLatLng!!.latitude,
                    pickUpOrderLatLng!!.longitude,
                    i.latitude!!.toDouble(),
                    i.longitude!!.toDouble()
                )

                dataForSortDistanceDriver.add(
                    arrayOf(
                        distance.toString(),
                        i.name.toString(),
                        i.id.toString(),
                    )
                )
            }
        }
        val tes1: Array<Array<String>?> = arrayOfNulls(dataForSortDistanceDriver.size)
        for (i in dataForSortDistanceDriver.indices) {
            tes1[i] = dataForSortDistanceDriver[i]
        }
        sortMintoMax(tes1, btnOrderAmbulance, dialogOrder, name, phone)


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

                            sharedPref.logout()
                            parentAddOrder.visibility = View.VISIBLE
                            parentOrderan.visibility = View.GONE
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


    private fun setMarker(
        dataList: List<Model.DataModel>?,
        driverOrdering: Model.DataModel?,
        s: String,
        id: Int?
    ) {
        val setIconMarkerMap = SetIconMarkerMap()

        if (s == "driver") {

            if (dataList != null) {
                for (i in dataList) {
                    if (i.status == 1) {

                        var lat = i.latitude!!.toDoubleOrNull()
                        var long = i.longitude!!.toDoubleOrNull()

                        if (lat == null) {
                            lat = 0.0
                        }
                        if (long == null) {
                            long = 0.0
                        }

                        val driverLatLng = LatLng(
                            lat,
                            long
                        )
                        mMap.addMarker(
                            MarkerOptions()
                                .position(driverLatLng)
                                .title(i.name)
                                .icon(
                                    BitmapDescriptorFactory
                                        .fromBitmap(
                                            setIconMarkerMap
                                                .generateSmallIcon(
                                                    applicationContext,
                                                    R.drawable.ic_ambulance
                                                )
                                        )
                                )
                        )
                    }
                }
            }

        } else if (s == "hospital") {
            if (dataList != null) {
                for (i in dataList) {

                    var lat = i.latitude!!.toDoubleOrNull()
                    var long = i.longitude!!.toDoubleOrNull()

                    if (lat == null) {
                        lat = 0.0
                    }
                    if (long == null) {
                        long = 0.0
                    }

                    val hospitalLatLng = LatLng(
                        lat,
                        long
                    )
                    mMap.addMarker(
                        MarkerOptions()
                            .position(hospitalLatLng)
                            .title(i.name)
                            .icon(
                                BitmapDescriptorFactory
                                    .fromBitmap(
                                        setIconMarkerMap
                                            .generateSmallIcon(
                                                applicationContext,
                                                R.drawable.ic_hospital
                                            )
                                    )
                            )
                    )
                }
            }
        } else if (s == "driver_ordering") {
            if (dataList != null) {
                for (i in dataList) {

                    if (i.id == id) {

                        tvDriverName.text = i.name
                        tvDriverPhone.text = i.phone

                        var lat = i.latitude!!.toDoubleOrNull()
                        var long = i.longitude!!.toDoubleOrNull()

                        if (lat == null) {
                            lat = 0.0
                        }
                        if (long == null) {
                            long = 0.0
                        }
                        val driverLatlng = LatLng(
                            lat,
                            long
                        )
                        mMap.addMarker(
                            MarkerOptions()
                                .position(driverLatlng)
                                .title(i.name)
                                .icon(
                                    BitmapDescriptorFactory
                                        .fromBitmap(
                                            setIconMarkerMap
                                                .generateSmallIcon(
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

    }

    private fun getHospital(nameHospitalSearch: String) {
        loadingHospital.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {

            ApiClient.instances.getHospital(null, nameHospitalSearch)
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = response.body()?.message
                        val data = response.body()?.data

                        if (response.isSuccessful && message == "Success") {

                            hospitalList = data

                            val adapter = data?.let {
                                AdapterListHospital(
                                    "add_order",
                                    it,
                                    this@CustomerMainActivity
                                )
                            }

                            rv.layoutManager = LinearLayoutManager(applicationContext)
                            rv.adapter = adapter

                            if (data?.size == 0) {
                                tvHospitalNotFound.visibility = View.VISIBLE
//
//                                Toast.makeText(
//                                    applicationContext,
//                                    "Tidak ada data akun driver",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            }

                            Log.e("onResponse: ", data.toString())


                        } else {
                            Log.e("onResponse: ", "Failed")

                            tvHospitalNotFound.visibility = View.GONE

                        }

                        loadingHospital.visibility = View.GONE
                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                        Toast.makeText(
                            applicationContext,
                            t.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                        tvHospitalNotFound.visibility = View.GONE
                        loadingHospital.visibility = View.GONE

                    }

                })

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
        btnOrderAmbulance: MaterialButton,
        dialogOrder: BottomSheetDialog,
        name: String,
        phone: String
    ) {

        val list = ArrayList<Array<String>>()

        Stream.of<Array<String>>(*value)
            .sorted { small: Array<String>, big: Array<String> ->
                small[0].compareTo(big[0])
            }
            .forEach { dataSort: Array<String> ->
                list.add(dataSort)
            }


        addOrder(btnOrderAmbulance, dialogOrder, name, phone, list[0][2])
    }

    private fun addOrder(
        btnOrderAmbulance: MaterialButton,
        dialogOrder: BottomSheetDialog,
        name: String,
        phone: String,
        idDriver: String
    ) {

        ApiClient.instances.addOrder(
            idDriver.toInt(),
            name,
            phone,
            pickUpOrderLatLng?.latitude.toString(),
            pickUpOrderLatLng?.longitude.toString(),
            myOrderIdHospital.toInt()
        )
            .enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {
                    val responseBody = response.body()
                    val message = response.body()?.message
                    val order = response.body()?.order

                    if (response.isSuccessful && message == "Success") {

                        myOrderId = sharedPref.put(
                            PreferencesHelper.PREF_ID_ORDER_CUSTOMER,
                            order?.id.toString()
                        ).toString()

                        Log.e("onResponse: ", order.toString())


                        parentAddOrder.visibility = View.GONE
                        parentOrderan.visibility = View.VISIBLE

                    } else {
                        Log.e("onResponse: ", "Failed")
                        Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                        chooseHospital = false
                        etHospital.setTextColor(Color.BLACK)
                        slideUp.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
                        getHospital("")
                    }

                    dialogOrder.dismiss()
                    btnOrderAmbulance.setShowProgress(false)
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                    dialogOrder.dismiss()
                    btnOrderAmbulance.setShowProgress(false)
                }

            })


    }


    fun MaterialButton.setShowProgress(showProgress: Boolean?) {

        iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        isCheckable = showProgress == false
        text = if (showProgress == true) "" else "Login admin"

        icon = if (showProgress == true) {
            CircularProgressDrawable(context!!).apply {
                setStyle(CircularProgressDrawable.DEFAULT)
                setColorSchemeColors(ContextCompat.getColor(context!!, R.color.white))
                start()
            }
        } else null

        if (icon != null) { // callback to redraw button icon
            icon.callback = object : Drawable.Callback {
                override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                }

                override fun invalidateDrawable(who: Drawable) {
                    this@setShowProgress.invalidate()
                }

                override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
                }
            }
        }
    }

    override fun refreshView(onUpdate: Boolean, result: Model.DataModel?) {

        myOrderIdHospital = result?.id.toString()
        etHospital.setText(result?.name)
        etHospital.setTextColor(RED)
        chooseHospital = true
//        etHospital.isEnabled = false
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

//    private fun getOrder():Boolean{
//        var myOrderExist = false
//
//
//        Toast.makeText(applicationContext, myOrderExist.toString(), Toast.LENGTH_SHORT).show()
//
//        return myOrderExist
//    }
}