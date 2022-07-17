package com.skripsi.ambulanapp.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterListAccount
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminListUserDriverAccountFragment : Fragment(),AdapterListAccount.IUserRecycler {
    private val TAG = "AdminListDriverFrgmnt"
    private val showUserType = "driver"
    private val actionType = "for_admin"

    private val loading: SpinKitView by lazy { requireView().findViewById(R.id.spin_kit_loading_accountDriver) }
    private val fabAddAccountDriver: FloatingActionButton by lazy { requireView().findViewById(R.id.fabAddUser) }
    private val rv: RecyclerView by lazy { requireView().findViewById(R.id.rvAccount) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_list_user_driver_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fabAddAccountDriver.setOnClickListener{
            startActivity(Intent(requireContext(),AdminAddAccountActivity::class.java))
        }

        getUser(showUserType, actionType)
    }

    private fun getUser(showUserType: String, actionType: String) {
        if (isAdded) {
            loading.visibility = View.VISIBLE

            ApiClient.instances.getUser("", showUserType, actionType)
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

                            val adapter =
                                data?.let {
                                    AdapterListAccount(
                                        showUserType,
                                        it,
                                        this@AdminListUserDriverAccountFragment
                                    )
                                }
                            if (activity != null) {
                                rv.layoutManager = LinearLayoutManager(activity?.applicationContext)
                                rv.adapter = adapter
                            }

                            if ("${data?.size}" == "0") {
//                            Toast.makeText(
//                                applicationContext, "Tidak ada data",
//                                Toast.LENGTH_SHORT
//                            ).show()
                            }

                        } else {
                            Toast.makeText(requireContext(), "Gagal", Toast.LENGTH_SHORT)
                                .show()

                        }

                        loading.visibility = View.GONE
                    }

                    override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                        Toast.makeText(
                            requireContext(),
                            t.message.toString(),
                            Toast.LENGTH_SHORT
                        ).show()

                        loading.visibility = View.GONE
                    }

                })
        }
    }

    override fun refreshView(onUpdate: Boolean, result: Model.DataModel?) {
        getUser(showUserType, actionType)
    }

    override fun onResume() {
        super.onResume()

        getUser(showUserType, actionType)
    }
}