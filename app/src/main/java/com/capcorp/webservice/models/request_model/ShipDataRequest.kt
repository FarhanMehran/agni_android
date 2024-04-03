package com.capcorp.webservice.models.request_model

import com.capcorp.webservice.models.ItemImageModel
import java.io.Serializable


data class ShipDataRequest(
    var pickUpAddress: String = "",
    var pickUpLocation: ArrayList<Double> = ArrayList(),
    var dropDownAddress: String = "",
    var dropDownLocation: ArrayList<Double> = ArrayList(),
    var shipItemImages: ArrayList<ItemImageModel> = ArrayList(),
    var type: String = "",
    var description: String = "",
    var itemName: String = "",
    var itemUrl: String = "",
    var itemPrice: String = "",
    var itemQuantity: String = "",
    var pickUpDate: String = "",
    var payment: String = "",
    var send: String = "true",
    var itemSize: String = "",
    var originalPacking: String = "",
    var insurance: String = "",
    var mode: String = "",
    var pickUpCountry: String = "",
    var dropDownCountry: String = "",
    var deliveryAddress: String = "",
    var receiptImages: ArrayList<ItemImageModel> = ArrayList(),
    var trackingNumber: String = "",
    var recommendedReward: String = "",
    var cardId: String = "",
    var tax: String = "",
    var adminFee: Double = 0.0,
    var stripeFee: Double = 0.0,
    var taxInclude: String = "",
    var itemGrossTotal: Double = 0.0,
    var h2dFee: Double = 0.0
) : Serializable