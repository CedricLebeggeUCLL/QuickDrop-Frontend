package com.example.quickdropapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object MapUtils {
    fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        try {
            val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
            if (vectorDrawable == null) {
                Log.e("MapUtils", "Vector drawable niet gevonden voor resource ID: $vectorResId")
                return null
            }
            val scaleFactor = 1
            val width = (vectorDrawable.intrinsicWidth * scaleFactor)
            val height = (vectorDrawable.intrinsicHeight * scaleFactor)
            vectorDrawable.setBounds(0, 0, width, height)
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            vectorDrawable.draw(canvas)
            Log.d("MapUtils", "BitmapDescriptor succesvol gemaakt voor resource ID: $vectorResId, width=$width, height=$height")
            return BitmapDescriptorFactory.fromBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("MapUtils", "Fout bij het maken van BitmapDescriptor: ${e.message}")
            return null
        }
    }
}