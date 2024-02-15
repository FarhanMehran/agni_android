package com.agnidating.agni.model


import com.agnidating.agni.model.home.User
import com.google.gson.annotations.SerializedName

data class UserMessage(
    @SerializedName("data")
    var `data`: List<DataX>,
    @SerializedName("recieverData")
    var receiver: User,
    @SerializedName("blocked_by")
    var blockedBy: String?="",
    @SerializedName("Response")
    var response: String,
):BaseResponse()