package com.capcorp.webservice.models.request_model

data class DriverStatusRequest(
    var orderId: String,
    var driverStatus: String,
    var arrivedLatLong: ArrayList<Float>,
    var orderCode: String
)