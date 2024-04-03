package com.capcorp.webservice.models

data class PojoSuccess(

    var statusCode: Int,
    var message: String,
    var data: UserDataDto
)