package com.capcorp.webservice.models.images

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Ancestry {
    @SerializedName("type")
    @Expose
    var type: Type? = null

    @SerializedName("category")
    @Expose
    var category: Category? = null

    @SerializedName("subcategory")
    @Expose
    var subcategory: Subcategory? = null
}