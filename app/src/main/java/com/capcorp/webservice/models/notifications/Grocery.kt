package com.capcorp.webservice.models.notifications

import com.capcorp.webservice.models.orders.GroceryItem

data class Grocery(
    val storeId: String?,
    val totalItem: Int?,
    val _id: String,
    val storeDetails: List<GroceryItem>
)