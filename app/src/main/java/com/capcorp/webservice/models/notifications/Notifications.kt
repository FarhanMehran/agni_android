package com.capcorp.webservice.models.notifications

import com.capcorp.webservice.models.notifications.notifications.DataItem
import com.google.gson.annotations.SerializedName

data class Notifications(
    @field:SerializedName("data")
    val data: List<DataItem?>? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("statusCode")
    val statusCode: Int? = null
)