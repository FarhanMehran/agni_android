package com.capcorp.webservice.models

data class ApiErrorModel(
    val statusCode: Int?,
    val message: String?,
    val customMessage: String?,
    val error: String?
)