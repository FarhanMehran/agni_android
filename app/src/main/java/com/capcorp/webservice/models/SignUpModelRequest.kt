package com.capcorp.webservice.models

data class SignUpModelRequest(
    var fullName: String? = null,
    var countryCode: String? = null,
    var fullNumber: String? = null,
    var deviceType: String? = null,
    var deviceToken: String? = null,
    var lastName: String? = null,
    var firstName: String? = null,
    var profilePic: ProfilePicUr? = null,
    var password: String? = null,
    var emailId: String? = null,
    var type: String? = null,
    var dateOfBirth: String? = null,
    var country: String? = null,
    var lat: String? = null,
    var long: String? = null,
    var languageId: String? = null,
    var countryISO: String? = null,
    var defaultLong: String? = null,
    var defaultLat: String? = null
)