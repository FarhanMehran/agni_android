package com.capcorp.webservice.models.home

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.capcorp.webservice.models.home.Esp__2
import com.capcorp.webservice.models.home.Eng__2
import com.capcorp.webservice.models.home.Esp
import com.capcorp.webservice.models.home.Eng
import com.capcorp.webservice.models.home.Esp__1
import com.capcorp.webservice.models.home.Eng__1

class Esp__2 {
    @SerializedName("link_title")
    @Expose
    var linkTitle: String? = null

    @SerializedName("link_subtitle")
    @Expose
    var linkSubtitle: String? = null

    @SerializedName("link_url")
    @Expose
    var linkUrl: String? = null

    @SerializedName("order_number")
    @Expose
    var orderNumber: Int? = null
}