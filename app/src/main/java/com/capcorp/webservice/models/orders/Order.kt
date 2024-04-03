package com.capcorp.webservice.models.orders

data class Order(
    var orderListing: List<OrderListing>?,
    var orderListingCount: Int?
)