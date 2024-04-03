package com.capcorp.webservice.models.request_model

import com.capcorp.webservice.models.ItemImageModel


class TransportDataRequset {
    var type: String = ""
    var description: String = ""
    var itemImages: ArrayList<ItemImageModel> = ArrayList()
    var isDismantleNeeded: Boolean = false
    var isElevated: Boolean = false
    var isFragile: Boolean = false
    var pickUpLocation: ArrayList<Double> = ArrayList()
    var dropDownLocation: ArrayList<Double> = ArrayList()
    var pickUpAddress: String = ""
    var dropDownAddress: String = ""
    var pickUpAdditionalNotes: String = ""
    var dropDownAdditionalNotes: String = ""
    var pickUpDate: String = ""
    var payment: String = ""
}