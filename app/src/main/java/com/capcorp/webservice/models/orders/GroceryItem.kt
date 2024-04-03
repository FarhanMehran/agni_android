package com.capcorp.webservice.models.orders

data class GroceryItem(
    var storeId: String?,
    var storeItemsId: String?,
    var _id: String?,
    var quantity: String?,
    var figure: String?,
    var price: String?,
    var name: String?,
    var storeName: String?,
    var createdDate: String?,
    var itemCount: Int = 0,
    val totalItem: Int?
)
