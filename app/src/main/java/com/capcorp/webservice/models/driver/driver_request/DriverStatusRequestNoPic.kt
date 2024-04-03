package com.capcorp.webservice.models.driver.driver_request


data class DriverStatusRequestNoPic(
    var orderId: String,
    var driverStatus: String,
    var arrivedLatLong: ArrayList<Float>,
    var orderCode: String,
    var arrivedAddress: String,
    var lat: Double,
    var long: Double,
    var oppositionId: String
)