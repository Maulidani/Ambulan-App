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
import com.skripsi.ambulanapp.ui.ChatActivity
import com.skripsi.ambulanapp.ui.admin.AdminAddAccountActivity
import com.skripsi.ambulanapp.ui.admin.AdminAddArticleActivity
import com.skripsi.ambulanapp.ui.admin.AdminAddHospitalActivity
import com.skripsi.ambulanapp.util.Constant
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdapterUserChat (
    type: String,
    private val list: List<Model.DataModel>,
) :
    RecyclerView.Adapter<AdapterUserChat.ListViewHolder>() {
    val _type = type

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val imgUserChat: CircleImageView by lazy { itemView.findViewById(R.id.imgUserChat) }
        private val nameUserChat: TextView by lazy { itemView.findViewById(R.id.tvNameUserChat) }
        private val itemUser: CardView by lazy { itemView.findViewById(R.id.itemUserChat) }

        fun bindData(result: Model.DataModel) {

            var linkImage = ""
            linkImage = "${Constant.BASE_URL}${result.image}"
            imgUserChat.load(linkImage)
            nameUserChat.text = result.name

            itemUser.setOnClickListener {

                if (_type == "admin") {
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(itemView.context, ChatActivity::class.java)
                            .putExtra("your_user_id",result.id)
                            .putExtra("your_user_type","driver")
                            .putExtra("your_user_name",result.name)
                            .putExtra("your_user_image",result.image),
                        null,
                    )
                }
            }


        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        return ListViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_user_chat, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bindData(list[position])
    }

    override fun getItemCount(): Int = list.size

}