package com.capcorp.webservice.models.driver.driver_request_pic

data class DriverStatusRequestPic(
    //var array: HashMap<String, JsonArray>,
    var receiptImages: List<ReceiptImages>? = null,
    var productImages: List<ReceiptImages>? = null,
    var orderId: String? = null,
    var driverStatus: String? = null,
    var arrivedLatLong: ArrayList<Float>? = null,
    var lat: Double,
    var long: Double,
    var arrivedAddress: String,
    var oppositionId: String
)