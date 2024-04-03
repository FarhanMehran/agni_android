package com.capcorp.webservice.models

import com.capcorp.webservice.models.orders.GroceryItem


data class GroceryStore(
    var _id: String?,
    var name: String?,
    var address: String?,
    var startDay: Int?,
    var endDay: Int?,
    var startTime: Long?,
    var endTime: Long?,
    var shopStatus: String?,
    var groceryItems: ArrayList<GroceryItem>? = ArrayList(),
    var selectedItemsCount: Int = 0
)