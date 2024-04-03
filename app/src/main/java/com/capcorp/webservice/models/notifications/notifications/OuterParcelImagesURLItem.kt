package com.capcorp.webservice.models.notifications.notifications

import com.google.gson.annotations.SerializedName

data class OuterParcelImagesURLItem(

    @field:SerializedName("thumbnail")
    val thumbnail: String? = null,

    @field:SerializedName("original")
    val original: String? = null,

    @field:SerializedName("_id")
    val id: String? = null
)