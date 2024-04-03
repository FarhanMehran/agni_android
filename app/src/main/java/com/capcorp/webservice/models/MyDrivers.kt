package com.capcorp.webservice.models

data class MyDrivers(

    var statusCode: Int,
    var message: String,
    var data: List<Users>
)