package com.capcorp.webservice.models.home

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.capcorp.webservice.models.home.Esp__2
import com.capcorp.webservice.models.home.Eng__2
import com.capcorp.webservice.models.home.Esp
import com.capcorp.webservice.models.home.Eng
import com.capcorp.webservice.models.home.Esp__1
import com.capcorp.webservice.models.home.Eng__1

class Store {
    @SerializedName("order_number")
    @Expose
    var orderNumber: Int? = null

    @SerializedName("store_image")
    @Expose
    var storeImage: String? = null

    @SerializedName("store_link")
    @Expose
    var storeLink: String? = null

    @SerializedName("store_title")
    @Expose
    var storeTitle: String? = null
}