package com.example.quickdropapp.utils

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil

object PolylineDecoder {
    fun decode(encoded: String): List<LatLng> {
        try {
            val decodedPoints = PolyUtil.decode(encoded)
            decodedPoints.forEachIndexed { index, latLng ->
                Log.d("PolylineDecoder", "Decoded point $index: lat=${latLng.latitude}, lng=${latLng.longitude}")
            }
            Log.d("PolylineDecoder", "Total decoded points: ${decodedPoints.size}")
            return decodedPoints
        } catch (e: Exception) {
            Log.e("PolylineDecoder", "Error decoding polyline: ${e.message}")
            return emptyList()
        }
    }
}