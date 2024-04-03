package com.capcorp.webservice.models.notifications.notificationdata

import com.google.gson.annotations.SerializedName

data class ProfilePicURL(

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("original")
    val original: String? = null
)