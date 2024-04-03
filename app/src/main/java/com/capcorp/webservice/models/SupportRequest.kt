package com.capcorp.webservice.models

data class SupportRequest(
    var name: String = "",
    var email: String = "",
    var fullNumber: String = "",
    var description: String = "",
    var reasonPicUrl: ArrayList<ItemImageModel> = ArrayList(),
    var orderId: String = "",
    var reasonId: String = "",
    var refundIssue: String = "false"
)