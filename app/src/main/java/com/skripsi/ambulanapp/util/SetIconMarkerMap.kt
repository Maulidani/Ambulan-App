package com.skripsi.ambulanapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.skripsi.ambulanapp.R

class SetIconMarkerMap {

    fun generateSmallIcon(context: Context, vectorResId: Int): Bitmap {
        val height = 100
        val width = 100
        val bitmap = BitmapFactory.decodeResource(context.resources, vectorResId)
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

}