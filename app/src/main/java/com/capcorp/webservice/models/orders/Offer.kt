package com.capcorp.webservice.models.orders


data class Offer(
    var fullName: String?,
    var profilePicURL: ProfilePicURL?,
    var price: Double?,
    var totalPrice: Double? = 0.0,
    var _id: String?,
    var createdDate: Long?,
    var type: String?,
    var driverCardId: String?,
    var orderStatus: String?,
    var totalRating: String,
    var averageRating: String,
    var driverAverageRating: String,
    var driverArrivalDate: Long,
    var shippingCharge: Double,
    var detailsNote: String
)