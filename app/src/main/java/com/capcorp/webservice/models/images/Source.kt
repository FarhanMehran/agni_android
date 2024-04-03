package com.capcorp.webservice.models.images

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Source {
    @SerializedName("ancestry")
    @Expose
    var ancestry: Ancestry? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("subtitle")
    @Expose
    var subtitle: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("meta_title")
    @Expose
    var metaTitle: String? = null

    @SerializedName("meta_description")
    @Expose
    var metaDescription: String? = null

    @SerializedName("cover_photo")
    @Expose
    var coverPhoto: CoverPhoto? = null
}