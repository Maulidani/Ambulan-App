package com.skripsi.ambulanapp.ui.customer.fragment

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.tasks.Task
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.ui.customer.AddLatLngOrderActivity
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.stream.Stream

class AmbulanceFragment : Fragment(), OnMapReadyCallback {
    private lateinit var sharedPref: PreferencesHelper
    private lateinit var progressDialog: ProgressDialog

    private lateinit var mMap: GoogleMap
    private var isReady = false
    private var getDriver = true

    private var polyline: Polyline? = null

    private val locationRequestCode = 1001
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationResult: LocationResult
    lateinit var cameraUpdate: CameraUpdate

    private var cameraZoom: Boolean = false

    private val cardParentOrderInfo: CardView by lazy { requireActivity().findViewById(R.id.carOrderInfo) }
    private val btnPesan: MaterialButton by lazy { requireActivity().findViewById(R.id.btnPesan) }
    private val btnFinish: MaterialButton by lazy { requireActivity().findViewById(R.id.btnFinish) }
    private val pbLoading: ProgressBar by lazy { requireActivity().findViewById(R.id.loading) }
    private val imgDriver: ImageView by lazy { requireActivity().findViewById(R.id.imgAmbulance) }
    private val tvDrivername: TextView by lazy { requireActivity().findViewById(R.id.tvNameDriver) }
    private val tvDriverCar: TextView by lazy { requireActivity().findViewById(R.id.tvNameCar) }
    private val tvDriverCarNumber: TextView by lazy { requireActivity().findViewById(R.id.tvNumberCar) }

    private var myLocation: LatLng? = null

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

                if (getDriver) {
                    val isOrdering = sharedPref.getBoolean(Constant.PREF_IS_ORDERING)
                    val isStatusOrder = sharedPref.getString(Constant.PREF_ORDER_STATUS)

                    if (isStatusOrder == "loading") {
                        getDriver("loading_order")
                        btnPesan.visibility = View.GONE
                        mMap.clear()

                    } else if (isOrdering) {
                        getDriver("add_driver_order")
                        cardParentOrderInfo.visibility = View.GONE
                        btnPesan.visibility = View.GONE

                    } else {

                        cardParentOrderInfo.visibility = View.GONE
                        btnPesan.visibility = View.VISIBLE
                    }
                    getDriver = false
                }

                if (!cameraZoom) {
//                    progressDialog.dismiss()
                    getDriver("set_marker")

                    cameraUpdate = CameraUpdateFactory.newCameraPosition(
                        CameraPosition.builder().target(myLocation!!).zoom(13.5f).build()
                    )
                    mMap.animateCamera(cameraUpdate)
                    cameraZoom = true
                }

            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        if (isAdded) {
            mMap = p0
            isReady = true
            mMap.isMyLocationEnabled = true
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ambulance, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        sharedPref = PreferencesHelper(requireContext())
        progressDialog = ProgressDialog(requireContext())
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.setCancelable(false)
        progressDialog.show()

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        btnFinish.setOnClickListener {
            deleteAlert()
        }
        btnPesan.setOnClickListener {
            startActivity(
                Intent(
                    requireContext().applicationContext,
                    AddLatLngOrderActivity::class.java
                )
            )
        }

        if (isAdded) {
            progressDialog.setMessage("Memuat lokasi...")
        }
    }


    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
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
        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())

        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(request)

        task.addOnSuccessListener {
            startLocationUpdates()
        }

        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                val apiException: ResolvableApiException = it
                try {
                    apiException.startResolutionForResult(requireActivity(), locationRequestCode)
                    askLocationPermission()
                } catch (e: IntentSender.SendIntentException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun askLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ),
                locationRequestCode
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
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

    private fun getDriver(type: String) {
//        mMap.clear()
        progressDialog.show()

        ApiClient.instances.getDriverUser().enqueue(object : Callback<Model.ResponseModel> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(
                call: Call<Model.ResponseModel>,
                response: Response<Model.ResponseModel>
            ) {

                val message = response.body()?.message
                val error = response.body()?.errors
                val data = response.body()?.data

                if (response.isSuccessful) {
                    if (error == false) {

                        val idOrder = sharedPref.getString(Constant.PREF_ID_ORDER).toString()
                        val idDriver = sharedPref.getString(Constant.PREF_DRIVER_ID).toString()

                        if (type == "add_driver_order") {

                            sortJarak(data, idOrder)

                        } else if (type == "loading_order") {
//                            val idOrder = sharedPref.getString(Constant.PREF_ID_ORDER).toString()

                            if (data != null) {
                                for (i in data) {
                                    if (i.id == idDriver.toInt()) {
                                        mMap.clear()
//                                      Toast.makeText(requireContext(), "driver menuju kelokasi pick up", Toast.LENGTH_SHORT).show()
                                        cardParentOrderInfo.visibility = View.VISIBLE

                                        tvDrivername.text = i.name
                                        tvDriverCar.text = i.car_type
                                        tvDriverCarNumber.text = i.car_number
                                        var linkImage = "${Constant.URL_IMAGE_USER}${i.image}"
                                        imgDriver.load(linkImage)

                                    }
                                }
                            }

                        } else {
                            setMarker(data)

                        }

                    } else {

                        Toast.makeText(requireContext(), "gagal", Toast.LENGTH_SHORT)
                            .show()

                    }
                } else {
                    Toast.makeText(requireContext(), "gagal", Toast.LENGTH_SHORT).show()

                }

                progressDialog.dismiss()
            }

            override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                progressDialog.dismiss()

                Toast.makeText(
                    requireContext(),
                    t.message.toString(),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    private fun setMarker(data: List<Model.DataModel>?) {
        mMap.clear()
        progressDialog.dismiss()

        if (data != null) {
            for (i in data) {
                if (i.status == 1) {
                    val driverLoc = LatLng(i.latitude.toDouble(), i.longitude.toDouble())

                    mMap.addMarker(
                        MarkerOptions().position(driverLoc).title(i.name)
                    )

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
//        progressDialog.show()

        getDriver = true
        if (getDriver) {
            val isOrdering = sharedPref.getBoolean(Constant.PREF_IS_ORDERING)
            val isStatusOrder = sharedPref.getString(Constant.PREF_ORDER_STATUS)

            if (isStatusOrder == "loading") {
                getDriver("loading_order")
                btnPesan.visibility = View.GONE

            } else if (isOrdering) {
                getDriver("add_driver_order")
                cardParentOrderInfo.visibility = View.GONE
                btnPesan.visibility = View.GONE

            } else {

                cardParentOrderInfo.visibility = View.GONE
                btnPesan.visibility = View.VISIBLE
            }
            getDriver = false
        }

//        val isOrdering = sharedPref.getBoolean(Constant.PREF_IS_ORDERING)
//        val isStatusOrder = sharedPref.getString(Constant.PREF_ORDER_STATUS)
//
//        if (isStatusOrder == "loading") {
//            getDriver("loading_order")
//        } else if (isOrdering) {
//            getDriver("add_driver_order")
//        } else {
//
//        }

    }

    private fun addOrderDriver(
        idDriver: ArrayList<Array<String>>,
        idOrder: String,
        status: String
    ) {
        if (isAdded) {
            progressDialog.setMessage("Mencari Driver Ambulan...")
            progressDialog.show()

            ApiClient.instances.addOrder(
                idOrder,
                idDriver[0][0],
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                status
            ).enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {

                    val message = response.body()?.message
                    val error = response.body()?.errors
                    val data = response.body()?.data
                    val order = response.body()?.order

                    if (response.isSuccessful) {
                        if (error == false) {
                            Toast.makeText(
                                requireContext(),
                                "Berhasil : mendapat driver ",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            Log.e("order : ", order.toString())

                            sharedPref.put(Constant.PREF_ORDER_STATUS, "loading")

                            order?.id_user_driver?.let {
                                sharedPref.put(
                                    Constant.PREF_DRIVER_ID,
                                    it
                                )
                            }

                            getDriver("loading_order")

                        } else {

                            Toast.makeText(requireContext(), "gagal", Toast.LENGTH_SHORT)
                                .show()

                        }
                    } else {
                        Toast.makeText(requireContext(), "gagal", Toast.LENGTH_SHORT).show()

                    }

                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    progressDialog.dismiss()

                    Toast.makeText(
                        requireContext(),
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun sortJarak(data: List<Model.DataModel>?, idOrder: String) {
        // distance between latitudes and longitudes

        val sizeArrayDriver = data?.let { ArrayList<Array<String>>(it.size) }

        if (data != null) {
            for (i in data) {

                if (i.status == 1) {
                    var lat1 = myLocation!!.latitude
                    var lat2 = i.latitude.toDouble()
                    val dLat = Math.toRadians(lat2 - lat1)
                    val dLon = Math.toRadians(i.longitude.toDouble() - myLocation!!.longitude)

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

                    val jarak = rad * c

                    sizeArrayDriver?.add(
                        arrayOf(
                            i.id.toString(), jarak.toString()
                        )
                    )

                }
            }

            if (sizeArrayDriver != null) {
                val dataToSort: Array<Array<String>?> = arrayOfNulls(sizeArrayDriver.size)
                for (i in sizeArrayDriver.indices) {
                    dataToSort[i] = sizeArrayDriver[i]
                }

                val list = ArrayList<Array<String>>()

                Stream.of<Array<String>>(*dataToSort)
                    .sorted { small: Array<String>, big: Array<String> ->
                        small[1].compareTo(big[1])
                        Log.e("jarak: sort", small[1] + " > " + big[1])
                    }
                    .forEach { dataSort: Array<String> ->
                        list.add(dataSort)
                        Log.e("jarak: hasil sort", list[0][0])
                        Log.e("jarak: hasil sort", list[0][1])
                    }
//                Log.e("jarak: hasil sort", list.toString())
                addOrderDriver(list, idOrder, "loading")

            }
        }

    }

    private fun deleteAlert() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Selesai")
        builder.setMessage("Orderan ini telah selesai ?")

        builder.setPositiveButton("Ya") { _, _ ->
            progressDialog.show()

            val idOrder = sharedPref.getString(Constant.PREF_ID_ORDER).toString()
            val idDriver = sharedPref.getString(Constant.PREF_DRIVER_ID).toString()

            ApiClient.instances.addOrder(
                idOrder,
                idDriver,
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "finish"
            ).enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {

                    val message = response.body()?.message
                    val error = response.body()?.errors
                    val data = response.body()?.data
                    val order = response.body()?.order

                    if (response.isSuccessful) {
                        if (error == false) {

                            cardParentOrderInfo.visibility = View.GONE
                            btnPesan.visibility = View.VISIBLE
                            sharedPref.logout()

                            getDriver("set_marker")

                        } else {

                            Toast.makeText(requireContext(), "gagal", Toast.LENGTH_SHORT)
                                .show()

                        }
                    } else {
                        Toast.makeText(requireContext(), "gagal", Toast.LENGTH_SHORT).show()

                    }

                    progressDialog.dismiss()
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    progressDialog.dismiss()

                    Toast.makeText(
                        requireContext(),
                        t.message.toString(),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
        }

        builder.setNegativeButton("Tidak") { _, _ ->
            // cancel
        }
        builder.show()
    }

}