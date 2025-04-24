package com.example.quickdropapp.utils

import android.util.Log
import com.google.android.gms.maps.model.LatLng

object PolylineDecoder {
    fun decode(encoded: String): List<LatLng> {
        val poly = mutableListOf<LatLng>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < encoded.length) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) -(result shr 1) else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) -(result shr 1) else result shr 1
            lng += dlng

            val latLng = LatLng(lat / 1E5, lng / 1E5)
            poly.add(latLng)
            Log.d("PolylineDecoder", "Decoded point: lat=${latLng.latitude}, lng=${latLng.longitude}")
        }
        Log.d("PolylineDecoder", "Total decoded points: ${poly.size}")
        return poly
    }
}