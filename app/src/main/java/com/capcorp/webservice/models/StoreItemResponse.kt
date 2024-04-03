package com.capcorp.webservice.models

import com.capcorp.webservice.models.orders.GroceryItem

data class StoreItemResponse(
    val storesListing: List<GroceryItem>,
    val storesListingCount: Int
)