package com.capcorp.webservice.models

data class ParcelStoreReq(
    var storeName: String = "",
    var storeLocation: String = "",
    var storeImage: String = "",

    var transpoterName: String = "",
    var transpoterImage: String = "",
    var tranpoterReview: String = ""
)