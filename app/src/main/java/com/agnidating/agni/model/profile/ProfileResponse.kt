package com.agnidating.agni.model.profile

import com.agnidating.agni.model.BaseResponse
import com.google.gson.annotations.SerializedName


data class ProfileResponse(
    @SerializedName("data")
    var `data`: Data,
) : BaseResponse()