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
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.ui.admin.AdminAddArticleActivity
import com.skripsi.ambulanapp.ui.admin.AdminAddHospitalActivity
import com.skripsi.ambulanapp.util.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterListOrderHistory (
    private val list: List<Model.DataModel>,
) :
    RecyclerView.Adapter<AdapterListOrderHistory.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val orderByName: TextView by lazy { itemView.findViewById(R.id.tvOrderByName) }
        private val hospitalDestination: TextView by lazy { itemView.findViewById(R.id.tvHospitalDestination) }
        private val itemOrder: CardView by lazy { itemView.findViewById(R.id.itemOrder) }

        fun bindData(result: Model.DataModel) {

            orderByName.text = result.customer_name
            hospitalDestination.text = result.hospital_name

            itemOrder.setOnClickListener {

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {

        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order_history, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

}