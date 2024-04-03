package com.capcorp.webservice.models.parceloffices

data class ParcelApiResponse(
    val statusCode: Int,
    val message: String,
    var data: DataParcel
)

data class DataParcel(var storesListing: ArrayList<ParcelId>)