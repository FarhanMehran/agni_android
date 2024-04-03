package com.agnidating.agni.model.addFlower


import com.agnidating.agni.model.BaseResponse
import com.google.gson.annotations.SerializedName

data class AddFlower(
    @SerializedName("data")
    var `data`: Data
):BaseResponse()