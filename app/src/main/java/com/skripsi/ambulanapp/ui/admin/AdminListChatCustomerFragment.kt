package com.skripsi.ambulanapp.ui.admin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.ybq.android.spinkit.SpinKitView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterListAccount
import com.skripsi.ambulanapp.adapter.AdapterUserChat
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.util.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminListChatCustomerFragment : Fragment() {
    private val TAG = "AdminListchatActvty"
    private var userId: String? = null
    private var userType: String? = null
    private lateinit var sharedPref: PreferencesHelper

    private val rv: RecyclerView by lazy { requireView().findViewById(R.id.rvUserChat) }
    private val loading: SpinKitView by lazy { requireView().findViewById(R.id.spin_kit_loading_user_chat) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_list_chat_customer, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPref = PreferencesHelper(requireContext())
        userId = sharedPref.getString(PreferencesHelper.PREF_USER_TYPE)
        userType = sharedPref.getString(PreferencesHelper.PREF_USER_TYPE)

        getUser()
    }

    private fun getUser() {
        loading.visibility = View.VISIBLE
        val chatWith = "customer"
        ApiClient.instances.getUser("",chatWith, "for_admin")
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
                                AdapterUserChat(
                                    userType!!,
                                    chatWith,
                                    it,
                                )
                            }
                        rv.layoutManager = LinearLayoutManager(requireContext())
                        rv.adapter = adapter

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