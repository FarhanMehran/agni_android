package com.capcorp.webservice.models.home

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.capcorp.webservice.models.home.Esp__2
import com.capcorp.webservice.models.home.Eng__2
import com.capcorp.webservice.models.home.Esp
import com.capcorp.webservice.models.home.Eng
import com.capcorp.webservice.models.home.Esp__1
import com.capcorp.webservice.models.home.Eng__1

class Video {
    @SerializedName("video_thumbnail")
    @Expose
    var videoThumbnail: String? = null

    @SerializedName("esp")
    @Expose
    var esp: Esp__1? = null

    @SerializedName("eng")
    @Expose
    var eng: Eng__1? = null
}