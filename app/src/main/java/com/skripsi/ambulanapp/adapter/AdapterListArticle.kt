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
import com.skripsi.ambulanapp.network.Link
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.ui.admin.AdminAddArticleActivity
import com.skripsi.ambulanapp.ui.admin.AdminAddHospitalActivity
import com.skripsi.ambulanapp.ui.customer.DetailArticleActivity
import com.skripsi.ambulanapp.util.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterListArticle(
    private val list: List<Model.DataModel>,
    private val mListener: IUserRecycler
) :
    RecyclerView.Adapter<AdapterListArticle.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private lateinit var sharedPref: PreferencesHelper

        private val imgArticle: ImageView by lazy { itemView.findViewById(R.id.imgArticle) }
        private val titleArticlel: TextView by lazy { itemView.findViewById(R.id.tvArticleName) }
        private val descArticle: TextView by lazy { itemView.findViewById(R.id.tvArticleDescription) }
        private val itemArticle: CardView by lazy { itemView.findViewById(R.id.itemArticle) }

        fun bindData(result: Model.DataModel) {
            sharedPref = PreferencesHelper(itemView.context)

            var linkImage = ""
            linkImage = "${Link.URL_IMAGE_ARTICLE}${result.image}"
            imgArticle.load(linkImage)

            titleArticlel.text = result.title
            descArticle.text = result.description

            itemArticle.setOnClickListener {

                if (sharedPref.getString(PreferencesHelper.PREF_TYPE).toString() == "admin") {
                    optionAlert(itemView, result)
                } else {
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(itemView.context, DetailArticleActivity::class.java)
                            .putExtra("action", "show")
                            .putExtra("user", "admin")
                            .putExtra("image", result.image)
                            .putExtra("id", result.id.toString())
                            .putExtra("title", result.title)
                            .putExtra("description", result.description), null
                    )
                }
            }
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

    private fun optionAlert(itemView: View, result: Model.DataModel) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(itemView.context)

        val options = arrayOf("Lihat detail", "Edit artikel", "Hapus artikel")
        builder.setItems(
            options
        ) { _, which ->
            when (which) {
                0 -> {
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(itemView.context, DetailArticleActivity::class.java)
                            .putExtra("action", "show")
                            .putExtra("user", "admin")
                            .putExtra("image", result.image)
                            .putExtra("id", result.id.toString())
                            .putExtra("title", result.title)
                            .putExtra("description", result.description), null
                    )
                }
                1 -> {
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(itemView.context, AdminAddArticleActivity::class.java)
                            .putExtra("action", "edit")
                            .putExtra("user", "admin")
                            .putExtra("image", result.image)
                            .putExtra("id", result.id.toString())
                            .putExtra("title", result.title)
                            .putExtra("description", result.description), null
                    )
                }
                2 -> deleteAlert(itemView, result.id!!, result.title!!)
            }
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun deleteAlert(itemView: View, idArticle: Int, title: String) {

        tvHead?.text = "Hapus artikel $title ?"

        dialog?.setCancelable(false)
        dialog?.setContentView(view!!)
        dialog?.show()

        btnNo?.setOnClickListener {
            dialog?.dismiss()
        }

        btnYes?.setOnClickListener {
            dialog?.dismiss()
            delete(itemView, idArticle)
        }
    }

    private fun delete(itemView: View, idArticle: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            ApiClient.instances.deleteArticle(idArticle)
                .enqueue(object : Callback<Model.ResponseModel> {
                    override fun onResponse(
                        call: Call<Model.ResponseModel>,
                        response: Response<Model.ResponseModel>
                    ) {
                        val responseBody = response.body()
                        val message = response.body()?.message

                        if (response.isSuccessful && message == "Success") {

                            mListener.refreshView(true, null)
                            notifyDataSetChanged()

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

    interface IUserRecycler {
        fun refreshView(onUpdate: Boolean, result: Model.DataModel?)
    }

}

