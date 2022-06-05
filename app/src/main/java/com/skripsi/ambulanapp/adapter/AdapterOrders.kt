package com.skripsi.ambulanapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.model.Model


class AdapterOrders(
    private val list: List<Model.DataModel>,
) :
    RecyclerView.Adapter<AdapterOrders.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvIdOrder: TextView by lazy { itemView.findViewById(R.id.tvIdOrder) }
        private val tvNote: TextView by lazy { itemView.findViewById(R.id.tvNote) }
        private val item: CardView by lazy { itemView.findViewById(R.id.itemOrder) }

        fun bindData(result: Model.DataModel) {

            tvIdOrder.text = result.id_orders.toString()
            tvNote.text = result.note

            item.setOnClickListener {
//
//                ContextCompat.startActivity(
//                    itemView.context,
//                    Intent(itemView.context, DetailOrderActivity::class.java)
//                        .putExtra("id_orders", result.id_orders.toString())
//                        .putExtra("id_user_driver", result.id_user_driver.toString())
//                        .putExtra("name_driver", result.name)
//                        .putExtra("note", result.note)
//                        .putExtra("pick_up", result.pick_up)
//                        .putExtra("drop_off", result.drop_off)
//                        .putExtra("pick_up_latitude", result.pick_up_latitude)
//                        .putExtra("pick_up_longitude", result.pick_up_longitude)
//                        .putExtra("drop_off_latitude", result.drop_off_latitude)
//                        .putExtra("drop_off_longitude", result.drop_off_longitude), null
//                )
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

}