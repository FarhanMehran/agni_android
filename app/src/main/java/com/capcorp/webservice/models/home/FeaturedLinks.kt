package com.capcorp.webservice.models.home

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.capcorp.webservice.models.home.Esp__2
import com.capcorp.webservice.models.home.Eng__2
import com.capcorp.webservice.models.home.Esp
import com.capcorp.webservice.models.home.Eng
import com.capcorp.webservice.models.home.Esp__1
import com.capcorp.webservice.models.home.Eng__1

class FeaturedLinks {
    @SerializedName("title_esp")
    @Expose
    var titleEsp: String? = null

    @SerializedName("title_en")
    @Expose
    var titleEn: String? = null

    @SerializedName("esp")
    @Expose
    var esp: List<Esp__2>? = null

    @SerializedName("eng")
    @Expose
    var eng: List<Eng__2>? = null
}