package com.capcorp.webservice.models

/**
 * Created by cbluser10 on 8/6/18.
 */
data class GroceryStoreResponse(
    val storesListing: List<GroceryStore>,
    val storesListingCount: Int
)