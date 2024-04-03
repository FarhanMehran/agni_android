package com.capcorp.webservice.models.images

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SummerOnFilm {
    @SerializedName("status")
    @Expose
    var status: String? = null
}