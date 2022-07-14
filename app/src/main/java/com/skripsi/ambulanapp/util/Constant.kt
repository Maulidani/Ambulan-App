package com.skripsi.ambulanapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton
import com.skripsi.ambulanapp.R

object Constant {
            const val BASE_URL = "http://192.168.42.5:8000"
//    const val BASE_URL = "http://skripsi-andi-jul.my.id/ambulan-app/public"

    fun MaterialButton.setShowProgress(showProgress: Boolean?) {

        iconGravity = MaterialButton.ICON_GRAVITY_TEXT_START
        isCheckable = showProgress == false
        text = if (showProgress == true) "" else "Coba lagi"

        icon = if (showProgress == true) {
            CircularProgressDrawable(context!!).apply {
                setStyle(CircularProgressDrawable.DEFAULT)
                setColorSchemeColors(ContextCompat.getColor(context!!, R.color.white))
                start()
            }
        } else null

        if (icon != null) { // callback to redraw button icon
            icon.callback = object : Drawable.Callback {
                override fun unscheduleDrawable(who: Drawable, what: Runnable) {
                }

                override fun invalidateDrawable(who: Drawable) {
                    this@setShowProgress.invalidate()
                }

                override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {
                }
            }
        }
    }

    fun generateSmallIcon(context: Context, vectorResId: Int): Bitmap {
        val height = 100
        val width = 100
        val bitmap = BitmapFactory.decodeResource(context.resources, vectorResId)
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }
}