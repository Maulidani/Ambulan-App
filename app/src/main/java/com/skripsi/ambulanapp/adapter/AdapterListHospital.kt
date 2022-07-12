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
import com.skripsi.ambulanapp.ui.admin.AdminAddHospitalActivity
import com.skripsi.ambulanapp.util.Constant
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterListHospital(
    type: String,
    private val list: List<Model.DataModel>,
    private val mListener: IUserRecycler
) :
    RecyclerView.Adapter<AdapterListHospital.ListViewHolder>() {

    val _type = type

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgHospital: ImageView by lazy { itemView.findViewById(R.id.imgHospital) }
        private val nameHospital: TextView by lazy { itemView.findViewById(R.id.tvHospitalName) }
        private val addressHospital: TextView by lazy { itemView.findViewById(R.id.tvHospitalAddress) }
        private val itemHospital: CardView by lazy { itemView.findViewById(R.id.itemHospital) }

        fun bindData(result: Model.DataModel) {
            var linkImage = ""
            linkImage = "${Constant.BASE_URL}${result.image}"
            imgHospital.load(linkImage)

            nameHospital.text = result.name
            addressHospital.text = result.address

            itemHospital.setOnClickListener {
                if (_type == "admin") {
                    optionAlert(result)

                } else if (_type == "customer"){
//                    mListener.refreshView(true, result)
//                    lihat detail
                } else {
//                    lihat detail
                }
            }
        }

        private fun optionAlert(result: Model.DataModel) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)

            val options = arrayOf("Lihat detail", "Edit rumah sakit", "Hapus rumah sakit")
            builder.setItems(
                options
            ) { _, which ->
                when (which) {
                    0 -> {
                        ContextCompat.startActivity(
                            itemView.context,
                            Intent(itemView.context, AdminAddHospitalActivity::class.java)
                                .putExtra("action", "show")
                                .putExtra("user_type", _type)
                                .putExtra("id", result.id)
                                .putExtra("name", result.name)
                                .putExtra("address", result.address)
                                .putExtra("image", result.image)
                                .putExtra("latitude", result.latitude)
                                .putExtra("longitude", result.longitude),null,
                        )
                    }
                    1 -> {
                        ContextCompat.startActivity(
                            itemView.context,
                            Intent(itemView.context, AdminAddHospitalActivity::class.java)
                                .putExtra("action", "edit")
                                .putExtra("user_type", _type)
                                .putExtra("id", result.id)
                                .putExtra("name", result.name)
                                .putExtra("address", result.address)
                                .putExtra("image", result.image)
                                .putExtra("latitude", result.latitude)
                                .putExtra("longitude", result.longitude),null,
                        )
                    }
                    2 -> deleteAlert(result)
                }
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        private fun deleteAlert(result: Model.DataModel) {

            tvHead?.text = "Hapus rumah sakit ${result.name} ?"

            dialog?.setCancelable(false)
            dialog?.setContentView(view!!)
            dialog?.show()

            btnNo?.setOnClickListener {
                dialog?.dismiss()
            }

            btnYes?.setOnClickListener {
                dialog?.dismiss()
                delete(result.id)
            }
        }

        private fun delete(id: String) {

            ApiClient.instances.deleteHospital(id)
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = responseBody?.message

                        if (response.isSuccessful && message == "Success") {

                            mListener.refreshView(true, null)
                            notifyDataSetChanged()

                        } else {
                            Toast.makeText(itemView.context, "Gagal", Toast.LENGTH_SHORT)
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

    private var dialog: BottomSheetDialog? = null
    private var view: View? = null
    private var tvHead: TextView? = null
    private var btnYes: MaterialButton? = null
    private var btnNo: MaterialButton? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        dialog = BottomSheetDialog(parent.context)
        view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_delete_item, null)
        tvHead = view?.findViewById<TextView>(R.id.tvHead)
        btnYes = view?.findViewById<MaterialButton>(R.id.btnYes)
        btnNo = view?.findViewById<MaterialButton>(R.id.btnNo)

        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_hospital, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size


    interface IUserRecycler {
        fun refreshView(onUpdate: Boolean, result: Model.DataModel?)
    }
}