package com.capcorp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import com.capcorp.R


object CheckNetworkConnection {

    fun isOnline(context: Context?): Boolean {
        if (context != null) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (cm != null) {
                val netInfo = cm.activeNetworkInfo
                return netInfo != null && netInfo.isConnected
            }
        }
        return true
    }


    fun showNetworkError(view: View) {
        view.showSnack(R.string.network_error)
    }
}
