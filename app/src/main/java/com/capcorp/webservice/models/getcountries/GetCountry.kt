package com.capcorp.webservice.models.getcountries

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class GetCountry : Serializable {
    @SerializedName("statusCode")
    @Expose
    val statusCode: Int? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    @SerializedName("data")
    @Expose
    var data: Array<String> = emptyArray()

}