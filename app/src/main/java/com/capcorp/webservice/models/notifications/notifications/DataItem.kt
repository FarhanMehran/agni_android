package com.capcorp.webservice.models.notifications.notifications

import com.google.gson.annotations.SerializedName

data class DataItem(

    @field:SerializedName("date")
    val date: Long? = null,

    @field:SerializedName("orderDetails")
    val orderDetails: OrderDetails? = null,

    @field:SerializedName("orderId")
    val orderId: String? = null,

    @field:SerializedName("isRead")
    val isRead: Boolean? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("userType")
    val userType: String? = null,

    @field:SerializedName("text")
    val text: String? = null
)