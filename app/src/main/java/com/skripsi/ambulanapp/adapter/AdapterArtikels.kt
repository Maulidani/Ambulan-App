package com.skripsi.ambulanapp.adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.model.Model
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.ui.admin.AddArtikelActivity
import com.skripsi.ambulanapp.ui.customer.DetailArtikelActivity
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.PreferencesHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterArtikels(
    private val list: List<Model.DataModel>,
    private val mListener: IUserRecycler
) :
    RecyclerView.Adapter<AdapterArtikels.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var sharedPref: PreferencesHelper

        private val title: TextView by lazy { itemView.findViewById(R.id.tvNameItem) }
        private val imgArtikel: ImageView by lazy { itemView.findViewById(R.id.imgItem) }
        private val hospital: TextView by lazy { itemView.findViewById(R.id.tvHospital) }
        private val item: CardView by lazy { itemView.findViewById(R.id.itemArtikel) }

        fun bindData(result: Model.DataModel) {

            sharedPref = PreferencesHelper(itemView.context)

            var linkImage = "${Constant.URL_IMAGE_ARTIKEL}${result.image}"
            imgArtikel.load(linkImage)
            linkImage = ""

            title.text = result.title
            hospital.text = result.hospital

            val typeAccount = sharedPref.getString(Constant.PREF_TYPE).toString()

            if (typeAccount == "admin") {
                item.setOnClickListener {
                    optionAlert(itemView, result)
                }
            } else {
                ContextCompat.startActivity(
                    itemView.context,
                    Intent(itemView.context, DetailArtikelActivity::class.java)
                        .putExtra("add_edit", "show")
                        .putExtra("id", result.id.toString())
                        .putExtra("type", result.type)
                        .putExtra("title", result.title)
                        .putExtra("hospital", result.hospital)
                        .putExtra("hospital_address", result.hospital_address)
                        .putExtra("content", result.content)
                        .putExtra("image", result.image), null
                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_artikel, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

    private fun optionAlert(itemView: View, result: Model.DataModel) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Aksi")

        val options = arrayOf("Lihat Artikel","Edit Artikel", "Hapus Artikel")
        builder.setItems(
            options
        ) { _, which ->
            when (which) {
                0 -> {
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(itemView.context, DetailArtikelActivity::class.java)
                            .putExtra("add_edit", "show")
                            .putExtra("id", result.id.toString())
                            .putExtra("type", result.type)
                            .putExtra("title", result.title)
                            .putExtra("hospital", result.hospital)
                            .putExtra("hospital_address", result.hospital_address)
                            .putExtra("content", result.content)
                            .putExtra("image", result.image), null
                    )
                }
                1 -> {
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(itemView.context, AddArtikelActivity::class.java)
                            .putExtra("add_edit", "edit")
                            .putExtra("id", result.id.toString())
                            .putExtra("type", result.type)
                            .putExtra("title", result.title)
                            .putExtra("hospital", result.hospital)
                            .putExtra("hospital_address", result.hospital_address)
                            .putExtra("content", result.content)
                            .putExtra("image", result.image), null
                    )
                }
                2 -> deleteAlert(itemView, result.id, result.title)
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteAlert(itemView: View, id: Int, name: String) {
        val builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Hapus")
        builder.setMessage("Hapus Artikel $name ?")

        builder.setPositiveButton("Ya") { _, _ ->
            delete(itemView, id)
        }

        builder.setNegativeButton("Tidak") { _, _ ->
            // cancel
        }
        builder.show()
    }

    private fun delete(itemView: View, id: Int) {
        with(ApiClient) {
            instances.deletArtikel(id).enqueue(object :
                Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {

                    val message = response.body()?.message
                    val error = response.body()?.errors

                    if (response.isSuccessful && error == false) {
                        mListener.refreshView(true)
                        notifyDataSetChanged()
                    } else {
                        Toast.makeText(itemView.context, "gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                    Toast.makeText(itemView.context, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }

    interface IUserRecycler {
        fun refreshView(onUpdate: Boolean)
    }

}