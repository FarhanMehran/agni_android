package com.capcorp.webservice.models.product_information

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ProductInformation : Serializable {
    @SerializedName("title")
    @Expose
    val title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("imgUrl")
    @Expose
    var imgUrl: String? = null
}