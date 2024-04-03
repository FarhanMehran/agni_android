package com.capcorp.webservice.models.notifications.notifications

import com.google.gson.annotations.SerializedName

data class WrongitemPicUrl(

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("original")
    val original: String? = null
)