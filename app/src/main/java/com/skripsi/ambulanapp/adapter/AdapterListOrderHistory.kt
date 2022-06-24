package com.skripsi.ambulanapp.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterListOrderHistory(
    private val list: List<Model.DataModel>,
) :
    RecyclerView.Adapter<AdapterListOrderHistory.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val nameOrder: TextView by lazy { itemView.findViewById(R.id.tvOrderByName) }
        private val hospitalDestination: TextView by lazy { itemView.findViewById(R.id.tvHospitalDestination) }
        private val itemAccount: CardView by lazy { itemView.findViewById(R.id.itemAccount) }

        fun bindData(result: Model.DataModel) {

            nameOrder.text = result.name
            getHospital(result.id)
        }

        fun getHospital(id: Int?) {
            CoroutineScope(Dispatchers.IO).launch {

                ApiClient.instances.getHospital(id!!,"")
                    .enqueue(object : Callback<Model.ResponseModel> {
                        override fun onResponse(
                            call: Call<Model.ResponseModel>,
                            response: Response<Model.ResponseModel>
                        ) {
                            val responseBody = response.body()
                            val message = response.body()?.message
                            val hospital = response.body()?.hospital

                            if (response.isSuccessful && message == "Success") {

                                Log.e("onResponse: ", hospital.toString())
                                hospitalDestination.text = hospital?.name

                            } else {
                                Toast.makeText(itemView.context, message, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                        override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {
                            Toast.makeText(
                                itemView.context,
                                t.message.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })
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

