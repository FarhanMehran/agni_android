package com.agnidating.agni.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    var `data`: Data,
    @SerializedName("token")
    var token: String?
) : BaseResponse()