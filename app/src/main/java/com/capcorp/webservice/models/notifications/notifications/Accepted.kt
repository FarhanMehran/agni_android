package com.capcorp.webservice.models.notifications.notifications

import com.google.gson.annotations.SerializedName

data class Accepted(

    @field:SerializedName("createdDate")
    val createdDate: Long? = null,

    @field:SerializedName("driverId")
    val driverId: String? = null,

    @field:SerializedName("totalPrice")
    val totalPrice: Int? = null,

    @field:SerializedName("driverCardId")
    val driverCardId: String? = null
)