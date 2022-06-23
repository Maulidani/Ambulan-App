package com.skripsi.ambulanapp.ui.driver

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R
import com.skripsi.ambulanapp.network.model.Model
import com.skripsi.ambulanapp.ui.viewmodel.DriverMainViewModel
import com.skripsi.ambulanapp.util.ScreenState
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DriverMainActivity : AppCompatActivity() {
    val slideUp: SlidingUpPanelLayout by lazy { findViewById(R.id.sliding_layout) }
    val btnPickUp: MaterialButton by lazy { findViewById(R.id.btnPickUp) }
    val btnDropOff: MaterialButton by lazy { findViewById(R.id.btnDropOff) }

    val parentSlideUp: ConstraintLayout by lazy { findViewById(R.id.parentSlideUp) }
    val fabMore: FloatingActionButton by lazy { findViewById(R.id.fabMore) }
    val fabProfile: FloatingActionButton by lazy { findViewById(R.id.fabProfile) }
    val fabHistory: FloatingActionButton by lazy { findViewById(R.id.fabHistory) }
    var isFABOpen = false

    private val viewModel: DriverMainViewModel by lazy {
        ViewModelProvider(this).get(DriverMainViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)

//        parentSlideUp.visibility = View.GONE

        viewModel.orderLiveData.observe(this) {
            processGetOrderResponse(it)
        }

        onClick()
    }

    override fun onBackPressed() {
        if (!isFABOpen) {
            super.onBackPressed()
        } else {
            closeFABMenu()
        }
    }

    private fun showFABMenu() {
        isFABOpen = true
        fabMore.animate().translationY(0f)
        fabProfile.animate().translationY(112f)
        fabHistory.animate().translationY(224f)
    }

    private fun closeFABMenu() {
        isFABOpen = false
        fabMore.animate().translationY(0f)
        fabProfile.animate().translationY(0f)
        fabHistory.animate().translationY(0f)
    }


    private fun onClick() {
        fabMore.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }

        btnPickUp.setOnClickListener {
            slideUp.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }

        btnDropOff.setOnClickListener {
            slideUp.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }

    }

    private fun processGetOrderResponse(state: ScreenState<List<Model.DataModel>?>) {
        CoroutineScope(Dispatchers.Main).launch {

//        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

            when (state) {
                is ScreenState.Loading -> {
//                progressBar.visibility = View.VISIBLE
                }
                is ScreenState.Success -> {
//                progressBar.visibility = View.GONE

                    if (state.data != null) {

                        for (i in state.data) {
                            if (i.id_driver == 3 && i.status == 0) {
                                //ordering
                                Toast.makeText(
                                    applicationContext,
                                    "lagi ada orderan guys",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                                parentSlideUp.visibility = View.VISIBLE

                            } else if (i.id_driver == 3 && i.status == 1) {
                                Toast.makeText(
                                    applicationContext,
                                    "lagi tidak ada",
                                    Toast.LENGTH_SHORT
                                ).show()
                                parentSlideUp.visibility = View.GONE

                            }
                        }
                    }
                }
                is ScreenState.Error -> {
//                progressBar.visibility = View.GONE
                }
            }
        }
    }
}