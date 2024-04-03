package com.capcorp.webservice.models

data class Users(
    var _id: String,
    var users: ArrayList<UserDataDto>,
    var userslength: Int,
    var fullName: String,
    var type: String,
    var lastName: String,
    var firstName: String,
    var isChecked: Boolean = false,
    var profilePicURL: ProfileData
)