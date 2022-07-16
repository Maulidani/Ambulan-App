package com.skripsi.ambulanapp.ui

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.adapter.AdapterChat
import com.skripsi.ambulanapp.network.ApiClient
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.util.Constant
import com.skripsi.ambulanapp.util.PreferencesHelper
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatActivity : AppCompatActivity() {
    private val TAG = "ChatActivity"
    private var userId: String? = null
    private var userType: String? = null
    private var job: Job? = null
    private lateinit var sharedPref: PreferencesHelper

    private val imgBack: ImageView by lazy { findViewById(R.id.imgBack) }
    private val imgUserChat: CircleImageView by lazy { findViewById(R.id.imgProfileUserYourChat) }
    private val tvNameUserChat: TextView by lazy { findViewById(R.id.tvNameUserChat) }
    private val rvChat: RecyclerView by lazy { findViewById(R.id.rvChatMessage) }
    private val etMessage: EditText by lazy { findViewById(R.id.etChat) }
    private val btnSend: MaterialButton by lazy { findViewById(R.id.btnSendChat) }

    private var intentYourUserId = ""
    private var intentYourUserType = ""
    private var intentYourUserName = ""
    private var intentYourUserImage = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        sharedPref = PreferencesHelper(applicationContext)
        userId = sharedPref.getString(PreferencesHelper.PREF_USER_ID)
        userType = sharedPref.getString(PreferencesHelper.PREF_USER_TYPE)

        intentYourUserId = intent.getStringExtra("your_user_id").toString()
        intentYourUserType = intent.getStringExtra("your_user_type").toString()
        intentYourUserName = intent.getStringExtra("your_user_name").toString()
        intentYourUserImage = intent.getStringExtra("your_user_image").toString()

        if (intentYourUserImage != "") {
            imgUserChat.load("${Constant.BASE_URL}$intentYourUserImage")
        }

        tvNameUserChat.text = intentYourUserName

        job = CoroutineScope(Dispatchers.IO).launch {
            while (true) {
                getChat()
                delay(5000)
            }
        }

//        runBlocking { getChat()}

        onClick()
    }

    override fun onStop() {
        job?.cancel()
        job = null
        super.onStop()
    }

    private fun onClick() {
        imgBack.setOnClickListener { finish() }

        btnSend.setOnClickListener {
            val message = etMessage.text.toString()

            if (message.isNotEmpty()) {
//                btnSend.setShowProgress(true)
                sendMessage(message)
                etMessage.setText("")

            }
        }

    }

    private suspend fun getChat() {
        coroutineScope {
            launch {
                ApiClient.instances.getChat(
                    userType!!,
                    intentYourUserType,
                    userId!!,
                    intentYourUserId,
                )
                    .enqueue(object : Callback<Model.ResponseModel> {
                        override fun onResponse(
                            call: Call<Model.ResponseModel>,
                            response: Response<Model.ResponseModel>
                        ) {
                            val responseBody = response.body()
                            val message = responseBody?.message
                            val data = responseBody?.data

                            if (response.isSuccessful && message == "Success") {
                                Log.e(TAG, "onResponse: $responseBody")

                                val adapter =
                                    data?.let {
                                        AdapterChat(
                                            userType!!,
                                            it,
                                        )
                                    }
                                rvChat.layoutManager =
                                    LinearLayoutManager(applicationContext).apply {
                                        stackFromEnd = true
                                        reverseLayout = false
                                    }
                                rvChat.adapter = adapter

                                if (data != null) {
                                    if (data.isEmpty()){
                                        Toast.makeText(applicationContext, "Belum ada chat", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            } else {
                                Log.e(TAG, "onResponse: $response")
                                Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT)
                                    .show()

                            }

                        }

                        override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {

                            Toast.makeText(
                                applicationContext,
                                t.message.toString(),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    })
            }
        }
    }

    private fun sendMessage(message: String) {
        ApiClient.instances.addChat(
            userType!!,
            intentYourUserType,
            userId!!,
            intentYourUserId,
            message,
        )
            .enqueue(object : Callback<Model.ResponseModel> {
                override fun onResponse(
                    call: Call<Model.ResponseModel>,
                    response: Response<Model.ResponseModel>
                ) {
                    val responseBody = response.body()
                    val message = responseBody?.message
                    val chat = responseBody?.chat

                    if (response.isSuccessful && message == "Success") {
                        Log.e(TAG, "onResponse: $responseBody")

                        runBlocking { getChat() }

                    } else {
                        Log.e(TAG, "onResponse: $response")
                        Toast.makeText(applicationContext, "Gagal", Toast.LENGTH_SHORT).show()

                    }
//                    btnSend.setShowProgress(false)

                }

                override fun onFailure(call: Call<Model.ResponseModel>, t: Throwable) {

                    Toast.makeText(applicationContext, t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
//                    btnSend.setShowProgress(false)
                }

            })

    }

}