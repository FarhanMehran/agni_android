package com.capcorp.webservice.models.notifications.notifications

import com.google.gson.annotations.SerializedName

data class UserId(

    @field:SerializedName("lastName")
    val lastName: String? = null,

    @field:SerializedName("firstName")
    val firstName: String? = null,

    @field:SerializedName("profilePicURL")
    val profilePicURL: ProfilePicURL? = null,

    @field:SerializedName("fullName")
    val fullName: String? = null,

    @field:SerializedName("emailId")
    val emailId: String? = null,

    @field:SerializedName("_id")
    val id: String? = null,

    @field:SerializedName("phoneNo")
    val phoneNo: String? = null
)