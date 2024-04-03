package com.capcorp.webservice.models.home

import com.google.gson.annotations.SerializedName
import com.google.gson.annotations.Expose
import com.capcorp.webservice.models.home.StoreCard
import com.capcorp.webservice.models.home.FeaturedLinks
import java.util.ArrayList

class Data {
    @SerializedName("store_cards")
    @Expose
    var storeCards: ArrayList<StoreCard>? = null

    @SerializedName("header")
    @Expose
    var header: Header? = null

    @SerializedName("video")
    @Expose
    var video: Video? = null

    @SerializedName("featured_links")
    @Expose
    var featuredLinks: FeaturedLinks? = null
}