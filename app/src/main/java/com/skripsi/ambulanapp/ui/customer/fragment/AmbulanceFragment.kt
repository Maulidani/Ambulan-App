package com.skripsi.ambulanapp.ui.customer.fragment

import android.annotation.SuppressLint
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
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

    private val btnPesan: MaterialButton by lazy { requireActivity().findViewById(R.id.btnPesan) }

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
                    } else if (isOrdering) {
                        getDriver("add_driver_order")
                    } else {

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

        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        locationRequest = LocationRequest.create()
        locationRequest.interval = 4000
        locationRequest.fastestInterval = 2000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

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
        mMap.clear()

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

                        if (type == "add_driver_order") {
                            val idOrder = sharedPref.getString(Constant.PREF_ID_ORDER).toString()
                            mMap.clear()
                            sortJarak(data, idOrder)
                        } else if (type == "loading_order") {
//                            val idOrder = sharedPref.getString(Constant.PREF_ID_ORDER).toString()
//
//                            myOrderLoading(data, idOrder)

                            mMap.clear()
                            Toast.makeText(requireContext(), "driver menuju kelokasi pick up", Toast.LENGTH_SHORT).show()
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
        progressDialog.show()
//
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


    private fun addOrderDriver(idOrder: ArrayList<Array<String>>) {
        if (isAdded) {
            progressDialog.setMessage("Loading...")
            progressDialog.show()

            ApiClient.instances.addOrder(
                idOrder[0][0],
                idOrder[0][1],
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "loading"
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
                                "Berhasil : mendapat driver",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                            sharedPref.put(Constant.PREF_ORDER_STATUS, "loading")
                            Toast.makeText(requireContext(), order?.name+ "sedang kelokasi anda", Toast.LENGTH_SHORT).show()
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
                        small[0].compareTo(big[0])
                    }
                    .forEach { dataSort: Array<String> ->
                        list.add(dataSort)
                        Log.e("jarak: hasil sort", list[0][0])
                    }

                addOrderDriver(list)

            }
        }

    }

//    private fun myOrderLoading(data: List<Model.DataModel>?, idOrder: String) {
//    }
}