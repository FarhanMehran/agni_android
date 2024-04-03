package com.capcorp.webservice.models.request_model


data class AcceptOrderRequest(
    var orderId: String = "",
    var price: String = "",
    var driverId: String = "",
    var type: String = "",
    var adminFee: Double = 0.0,
    var driverCardId: String? = null
)