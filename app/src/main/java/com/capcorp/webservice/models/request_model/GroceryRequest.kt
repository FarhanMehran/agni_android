package com.capcorp.webservice.models.request_model

import com.capcorp.webservice.models.orders.GroceryItem

data class GroceryRequest(
    var groceryItems: ArrayList<GroceryItem> = ArrayList(),
    var dropDownLocation: ArrayList<Double> = ArrayList(),
    var dropDownAddress: String = "",
    var dropDownAdditionalNotes: String = "",
    var pickUpDate: String = "",
    var payment: String = ""
)