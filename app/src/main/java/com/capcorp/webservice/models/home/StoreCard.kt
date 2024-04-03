package com.capcorp.webservice.models.home

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.capcorp.webservice.models.home.Esp__2
import com.capcorp.webservice.models.home.Eng__2
import com.capcorp.webservice.models.home.Esp
import com.capcorp.webservice.models.home.Eng
import com.capcorp.webservice.models.home.Esp__1
import com.capcorp.webservice.models.home.Eng__1
import java.util.ArrayList

class StoreCard {
    @SerializedName("nameEsp")
    @Expose
    var nameEsp: String? = null

    @SerializedName("nameEn")
    @Expose
    var nameEn: String? = null

    @SerializedName("store")
    @Expose
    var store: ArrayList<Store>? = null
}