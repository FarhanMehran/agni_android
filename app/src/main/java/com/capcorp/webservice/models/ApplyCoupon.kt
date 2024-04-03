package com.capcorp.webservice.models

data class ApplyCoupon(
    val statusCode: Int?,
    val message: String?,
    val data: List<DataApplyCoupon>?
)

data class DataApplyCoupon(
    val totalCheckout: Double?,
    val discount: Double?,
    val totalBeforeCoupon: Double?,
    val _id: String?,
    val itemGrossTotal: Double?
)

