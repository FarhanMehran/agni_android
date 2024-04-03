package com.agnidating.agni.model.user_details


import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.home.User
import com.google.gson.annotations.SerializedName

data class UserDetails(
    @SerializedName("count")
    var count: Int,
    @SerializedName("matchStatus")
    var matchStatus: Int,
    @SerializedName("data")
    var `data`: User,
):BaseResponse()