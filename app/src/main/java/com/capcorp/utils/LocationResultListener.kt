package com.capcorp.utils

import android.location.Location

interface LocationResultListener {
    fun getLocation(location: Location)
}