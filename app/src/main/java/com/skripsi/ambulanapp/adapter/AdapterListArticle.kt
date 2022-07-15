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

class AdapterListArticle (
    type: String,
    private val list: List<Model.DataModel>,
    private val mListener: IUserRecycler
) :
    RecyclerView.Adapter<AdapterListArticle.ListViewHolder>() {
    val _type = type

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgArticle: ImageView by lazy { itemView.findViewById(R.id.imgArticle) }
        private val articleName: TextView by lazy { itemView.findViewById(R.id.tvArticleName) }
        private val articleDescription: TextView by lazy { itemView.findViewById(R.id.tvArticleDescription) }
        private val itemArticle: CardView by lazy { itemView.findViewById(R.id.itemArticle) }

        fun bindData(result: Model.DataModel) {
            var linkImage = ""
            linkImage = "${Constant.BASE_URL}${result.image}"
            imgArticle.load(linkImage)

            articleName.text = result.title
            articleDescription.text = result.description

            itemArticle.setOnClickListener {
                if (_type == "admin") {
                    optionAlert(result)

                } else if (_type == "customer"){
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(itemView.context, AdminAddArticleActivity::class.java)
                            .putExtra("action", "show")
                            .putExtra("user_type", _type)
                            .putExtra("id", result.id)
                            .putExtra("title", result.title)
                            .putExtra("description", result.description)
                            .putExtra("image", result.image),null,
                    )
                } else {
//                    lihat detail
                }
            }
        }

        private fun optionAlert(result: Model.DataModel) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)

            val options = arrayOf("Lihat detail", "Edit artikel", "Hapus artikel")
            builder.setItems(
                options
            ) { _, which ->
                when (which) {
                    0 -> {
                        ContextCompat.startActivity(
                            itemView.context,
                            Intent(itemView.context, AdminAddArticleActivity::class.java)
                                .putExtra("action", "show")
                                .putExtra("user_type", _type)
                                .putExtra("id", result.id)
                                .putExtra("title", result.title)
                                .putExtra("description", result.description)
                                .putExtra("image", result.image),null,
                        )
                    }
                    1 -> {
                        ContextCompat.startActivity(
                            itemView.context,
                            Intent(itemView.context, AdminAddArticleActivity::class.java)
                                .putExtra("action", "edit")
                                .putExtra("user_type", _type)
                                .putExtra("id", result.id)
                                .putExtra("title", result.title)
                                .putExtra("description", result.description)
                                .putExtra("image", result.image),null,
                        )
                    }
                    2 -> deleteAlert(result)
                }
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        private fun deleteAlert(result: Model.DataModel) {

            tvHead?.text = "Hapus artikel ${result.title} ?"

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

            ApiClient.instances.deleteArticle(id)
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
            LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
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