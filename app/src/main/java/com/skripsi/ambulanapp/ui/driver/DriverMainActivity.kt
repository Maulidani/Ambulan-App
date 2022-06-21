package com.skripsi.ambulanapp.ui.driver

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.skripsi.ambulanapp.R
import com.sothree.slidinguppanel.SlidingUpPanelLayout

class DriverMainActivity : AppCompatActivity() {
    val slideUp: SlidingUpPanelLayout by lazy { findViewById(R.id.sliding_layout) }
    val btnPickUp: MaterialButton by lazy { findViewById(R.id.btnPickUp) }
    val btnDropOff: MaterialButton by lazy { findViewById(R.id.btnDropOff) }

    val parentSlideUp: ConstraintLayout by lazy { findViewById(R.id.parentSlideUp) }
    val fabMore: FloatingActionButton by lazy { findViewById(R.id.fabMore) }
    val fabProfile: FloatingActionButton by lazy { findViewById(R.id.fabProfile) }
    val fabHistory: FloatingActionButton by lazy { findViewById(R.id.fabHistory) }
    var isFABOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_main)

//        parentSlideUp.visibility = View.GONE

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
}