package com.agnidating.agni.model

import com.google.gson.annotations.SerializedName

open class BaseResponse{
    @SerializedName("status")
    var status:Int?=null
    @SerializedName("message")
    var message:String?=null
    @SerializedName("isAuthorized")
    var isAuthorized:Int?=null

    @SerializedName("count_otp")
    var reques_count:Int?=null


    @SerializedName("created_time")
    var created_time:String?=null

}
