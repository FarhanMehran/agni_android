package com.capcorp.webservice.models

data class UserId(
    val _id: String,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val emailId: String,
    val profilePicURL: ProfilePicUr,
    var phoneNo: String,
    var price: String,
    var totalPrice: String?,
    var badge: String,
    var isApproved: Int?,
    var averageRating: Double,
    var driverAverageRating: Double,
    var driverArrivalDate: Long,
    var isRated: String,
    var location: ArrayList<Double>?
)