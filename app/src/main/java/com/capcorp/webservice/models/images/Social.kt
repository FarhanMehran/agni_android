package com.capcorp.webservice.models.images

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Social {
    @SerializedName("instagram_username")
    @Expose
    var instagramUsername: Any? = null

    @SerializedName("portfolio_url")
    @Expose
    var portfolioUrl: Any? = null

    @SerializedName("twitter_username")
    @Expose
    var twitterUsername: Any? = null

    @SerializedName("paypal_email")
    @Expose
    var paypalEmail: Any? = null
}