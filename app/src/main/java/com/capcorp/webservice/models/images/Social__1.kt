package com.capcorp.webservice.models.images

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Social__1 {
    @SerializedName("instagram_username")
    @Expose
    var instagramUsername: String? = null

    @SerializedName("portfolio_url")
    @Expose
    var portfolioUrl: String? = null

    @SerializedName("twitter_username")
    @Expose
    var twitterUsername: String? = null

    @SerializedName("paypal_email")
    @Expose
    var paypalEmail: Any? = null
}