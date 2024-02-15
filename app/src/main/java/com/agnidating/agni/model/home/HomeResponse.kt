package com.agnidating.agni.model.home


import com.agnidating.agni.model.BaseResponse
import com.google.gson.annotations.SerializedName

data class HomeResponse(
    @SerializedName("count")
    var count: Int,
    @SerializedName("data")
    var `data`: List<User>,
    @SerializedName("rejectedData")
    var rejectedData: List<User>,
    @SerializedName("loginData")
    var flowerData:FlowerData,
    @SerializedName("unreadNotification")
    var unreadNotification:Int
):BaseResponse()

data class FlowerData(
    @SerializedName("total_flowers")
    var totalFlowers:String
)