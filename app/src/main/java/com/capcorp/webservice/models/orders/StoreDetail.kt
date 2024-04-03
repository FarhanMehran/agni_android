package com.capcorp.webservice.models.orders

data class StoreDetail(

    var storeId: String?,
    var storeName: String?,
    var location: ArrayList<Float>?,
    var totalItem: Int?,
    var storeAddress: String

)