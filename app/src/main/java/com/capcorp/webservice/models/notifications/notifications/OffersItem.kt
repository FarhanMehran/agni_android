package com.capcorp.webservice.models.notifications.notifications

import com.google.gson.annotations.SerializedName

data class OffersItem(

    @field:SerializedName("createdDate")
    val createdDate: Long? = null,

    @field:SerializedName("driverId")
    val driverId: String? = null,

    @field:SerializedName("totalPrice")
    val totalPrice: Int? = null,

    @field:SerializedName("price")
    val price: Int? = null,

    @field:SerializedName("driverCardId")
    val driverCardId: String? = null,

    @field:SerializedName("driverArrivalDate")
    val driverArrivalDate: Long? = null,

    @field:SerializedName("location")
    val location: List<Double?>? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("shippingCharge")
    val shippingCharge: Int? = null
)