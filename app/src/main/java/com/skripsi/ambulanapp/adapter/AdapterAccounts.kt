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
import com.skripsi.ambulanapp.ui.admin.AddEditDriverActivity
import com.skripsi.ambulanapp.util.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterAccounts(
    private val list: List<Model.DataModel>,
    private val mListener: IUserRecycler
) :
    RecyclerView.Adapter<AdapterAccounts.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val name: TextView by lazy { itemView.findViewById(R.id.tvAccountName) }
        private  val imgDriver: ImageView by lazy { itemView.findViewById(R.id.imgItem) }
        private val carName: TextView by lazy { itemView.findViewById(R.id.tvCarName) }
        private val carNumber: TextView by lazy { itemView.findViewById(R.id.tvCarNumber) }
        private  val item: CardView by lazy { itemView.findViewById(R.id.itemAccount) }

        fun bindData(result: Model.DataModel) {

            var linkImage = "${Constant.URL_IMAGE_USER}${result.image}"
            imgDriver.load(linkImage)
            linkImage = ""

            name.text = result.name
            carName.text = result.car_type
            carNumber.text = result.car_number

            item.setOnClickListener {
                optionAlert(itemView,result)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_account, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

    private fun optionAlert(itemView: View, result: Model.DataModel) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Aksi")

        val options = arrayOf( "Edit Akun", "Hapus Akun")
        builder.setItems(
            options
        ) { _, which ->
            when (which) {
                1 -> {
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(itemView.context, AddEditDriverActivity::class.java)
                            .putExtra("add_edit", "edit")
                            .putExtra("id", result.id.toString())
                            .putExtra("name", result.name)
                            .putExtra("phone", result.phone)
                            .putExtra("image", result.image)
                            .putExtra("username", result.username)
                            .putExtra("password", result.password)
                            .putExtra("car_type", result.car_type)
                            .putExtra("car_number", result.car_number), null
                    )
                }
                2 -> deleteAlert(itemView, result.id, result.name)
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteAlert(itemView: View, id: Int, name: String) {
        val builder = AlertDialog.Builder(itemView.context)
        builder.setTitle("Hapus")
        builder.setMessage("Hapus Akun $name ?")

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
            instances.deleteuser(id).enqueue(object :
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