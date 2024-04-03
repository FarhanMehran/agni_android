package com.capcorp.webservice.models

data class CancelOrderRequest(
    var orderId: String = "",
    var reason: String = ""
)
