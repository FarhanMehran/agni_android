package com.capcorp.webservice.models

data class Account(
    var image: Int,
    var title: String = "",
    var isLanguage: Boolean = false,
    var isNotification: Boolean = false
)
