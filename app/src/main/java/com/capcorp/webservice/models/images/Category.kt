package com.capcorp.webservice.models.images

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Category {
    @SerializedName("slug")
    @Expose
    var slug: String? = null

    @SerializedName("pretty_slug")
    @Expose
    var prettySlug: String? = null
}