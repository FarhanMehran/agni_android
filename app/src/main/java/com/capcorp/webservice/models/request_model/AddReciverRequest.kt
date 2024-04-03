package com.capcorp.webservice.models.request_model

data class AddReciverRequest(
    var orderId: String?,
    var emailId: String?,
    var phoneNo: String?,
    var employeeName: String?
)