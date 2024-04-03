package com.capcorp.webservice.models.orders

import java.io.Serializable

class MyOffers(
    var fullName: String?,
    var firstName: String?,
    var lastName: String?,
    var type: String?,
    var driverCardId: String?,
    var price: Float?,
    var driverArrivalDate: Long?,
    var shippingCharge: Int?,
    var totalPrice: Int?,
    var _id: String?,
    var phoneNo: String?,
    var createdDate: Long?,
    var totalRating: Double?,
    var averageRating: Double?,
    var driverTotalRating: Double?,
    var driverAverageRating: Double?
) : Serializable
