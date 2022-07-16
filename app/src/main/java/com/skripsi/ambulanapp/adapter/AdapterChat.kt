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

class AdapterChat (
    type: String,
    private val list: List<Model.DataModel>,
) :
    RecyclerView.Adapter<AdapterChat.ListViewHolder>() {
    val _type = type

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val myCardMessage: CardView by lazy { itemView.findViewById(R.id.myCardChat) }
        private val yourCardMessage: CardView by lazy { itemView.findViewById(R.id.yourCardChat) }
        private val myMessage: TextView by lazy { itemView.findViewById(R.id.myChat) }
        private val yourMessage: TextView by lazy { itemView.findViewById(R.id.yourChat) }

        fun bindData(result: Model.DataModel) {
            myCardMessage.visibility = View.GONE
            yourCardMessage.visibility = View.GONE

            if (_type == result.from_user_type){
                myCardMessage.visibility = View.VISIBLE
                yourCardMessage.visibility = View.GONE
                myMessage.text= result.message
            } else if (_type == result.to_user_type) {
                yourCardMessage.visibility = View.VISIBLE
                myCardMessage.visibility = View.GONE
                yourMessage.text = result.message
            } else {
                myCardMessage.visibility = View.GONE
                yourCardMessage.visibility = View.GONE
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

}