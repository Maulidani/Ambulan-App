package com.skripsi.ambulanapp.ui.admin.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterAccounts
import com.skripsi.ambulanapp.adapter.AdapterArtikels
import com.skripsi.ambulanapp.adapter.AdapterOrders
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.ui.admin.AddArtikelActivity
import com.skripsi.ambulanapp.ui.admin.AddEditDriverActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArtikelFragment(type: String) : Fragment(), AdapterArtikels.IUserRecycler {
    private lateinit var progressDialog: ProgressDialog
    private val rv: RecyclerView by lazy { requireActivity().findViewById(R.id.rvArtikel) }
    private val btnAdd: FloatingActionButton by lazy { requireActivity().findViewById(R.id.fabAdd) }
    private val tvTitle: TextView by lazy { requireActivity().findViewById(R.id.tvArtikel) }

    var _type = type

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_artikel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(requireContext())
        progressDialog.setCanceledOnTouchOutside(true)
        progressDialog.setCancelable(true)

        if (_type == "rumah_sakit_terdekat"){
           getArtikel(_type)
            tvTitle.setText("Rumah Sakit Terdekat")

        } else if(_type == "pertolongan_pertama") {
            getArtikel(_type)
            tvTitle.setText("Pertolongan Pertama")

        } else {

        }

        btnAdd.setOnClickListener {
            startActivity(
                Intent(requireContext(), AddArtikelActivity::class.java)
                    .putExtra("add_edit", "add")
            )
        }

    }

    private fun getArtikel(_type: String) {
        if (isAdded) {
            progressDialog.setMessage("Loading...")
            progressDialog.show()

            ApiClient.instances.getArtikel(_type)
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
                                    data?.let { AdapterArtikels(it,this@ArtikelFragment) }
                                rv.layoutManager = GridLayoutManager(requireContext(), 2)
                                rv.adapter = adapter

                            } else {

                                Toast.makeText(requireContext(), "Kosong", Toast.LENGTH_SHORT)
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

    override fun refreshView(onUpdate: Boolean) {
        getArtikel(_type)
    }

    override fun onResume() {
        super.onResume()
        getArtikel(_type)
    }
}