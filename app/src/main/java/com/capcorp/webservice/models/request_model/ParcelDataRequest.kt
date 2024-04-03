package com.capcorp.webservice.models.request_model

import com.capcorp.webservice.models.ItemDemension
import com.capcorp.webservice.models.ItemImageModel
import com.capcorp.webservice.models.parceloffices.ParcelId


data class ParcelDataRequest(
    var dimensionArray: ArrayList<ItemDemension> = ArrayList(),
    var description: String = "",
    var innerParcelImagesURL: ArrayList<ItemImageModel> = ArrayList(),
    var outerParcelImagesURL: ArrayList<ItemImageModel> = ArrayList(),
    var pickUpLocation: ArrayList<Double> = ArrayList(),
    var dropDownLocation: ArrayList<Double> = ArrayList(),
    var tempBranchOffices: ArrayList<ParcelId> = ArrayList(),
    var parcelBranchId: String = "",
    var pickUpAddress: String = "",
    var dropDownAddress: String = "",
    var pickUpAdditionalNotes: String = "",
    var dropDownAdditionalNotes: String = "",
    var pickUpDate: String = "",
    var payment: String = ""
)
