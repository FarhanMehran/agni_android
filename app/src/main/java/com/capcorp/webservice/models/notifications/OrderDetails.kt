package com.capcorp.webservice.models.notifications

data class OrderDetails(
    var _id: String?,
    var orderType: String?,
    var dimensionArray: List<Any>?,
    var groceryItems: List<Grocery>?,
    var type: String?,
    var createdDate: Long?,
    var payment: Int?
)