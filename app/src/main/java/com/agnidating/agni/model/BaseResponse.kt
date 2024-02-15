package com.agnidating.agni.model

import com.google.gson.annotations.SerializedName

open class BaseResponse{
    @SerializedName("status")
    var status:Int?=null
    @SerializedName("message")
    var message:String?=null
    @SerializedName("isAuthorized")
    var isAuthorized:Int?=null
}
