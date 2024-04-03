package com.capcorp.webservice.models

data class RemoveCoupon(
    val statusCode: Int?,
    val message: String?,
    val data: List<DataRemoveCoupon>?
)

data class DataRemoveCoupon(
    val totalCheckout: Double?,
    val _id: String?,
    val itemGrossTotal: Double?
)

