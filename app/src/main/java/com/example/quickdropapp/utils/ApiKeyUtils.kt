package com.example.quickdropapp.utils

import android.content.Context
import android.content.pm.PackageManager

object ApiKeyUtils {
    fun getRoutesApiKey(context: Context): String? {
        try {
            val appInfo = context.packageManager.getApplicationInfo(context.packageName, PackageManager.GET_META_DATA)
            val metaData = appInfo.metaData
            return metaData.getString("com.google.android.routes.API_KEY")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            return null
        }
    }
}