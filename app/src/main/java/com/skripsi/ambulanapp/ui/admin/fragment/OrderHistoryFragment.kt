package com.skripsi.ambulanapp.ui.admin.fragment

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterOrders
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryFragment : Fragment() {
    private lateinit var progressDialog: ProgressDialog
    private val rv: RecyclerView by lazy { requireActivity().findViewById(R.id.rvOrders) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setCanceledOnTouchOutside(true)
        progressDialog.setCancelable(true)

        getOrderHistory()
    }

    private fun getOrderHistory() {
        if (isAdded) {
            progressDialog.setMessage("Loading...")
            progressDialog.show()

            ApiClient.instances.showOrder("finish", "0")
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {

                        val message = response.body()?.message
                        val error = response.body()?.errors
                        val data = response.body()?.data

                        if (response.isSuccessful) {
                            if (error == false) {

                                val adapter =
                                    data?.let { AdapterOrders(it) }
                                rv.layoutManager = LinearLayoutManager(requireContext())
                                rv.adapter = adapter

                            } else {

//                                Toast.makeText(requireContext(), "kosong", Toast.LENGTH_SHORT)
//                                    .show()
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
}