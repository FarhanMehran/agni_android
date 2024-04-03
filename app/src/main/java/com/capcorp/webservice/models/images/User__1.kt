package com.capcorp.webservice.models.images

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class User__1 {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("username")
    @Expose
    var username: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("first_name")
    @Expose
    var firstName: String? = null

    @SerializedName("last_name")
    @Expose
    var lastName: String? = null

    @SerializedName("twitter_username")
    @Expose
    var twitterUsername: String? = null

    @SerializedName("portfolio_url")
    @Expose
    var portfolioUrl: String? = null

    @SerializedName("bio")
    @Expose
    var bio: String? = null

    @SerializedName("location")
    @Expose
    var location: String? = null

    @SerializedName("links")
    @Expose
    var links: Links__3? = null

    @SerializedName("profile_image")
    @Expose
    var profileImage: ProfileImage__1? = null

    @SerializedName("instagram_username")
    @Expose
    var instagramUsername: String? = null

    @SerializedName("total_collections")
    @Expose
    var totalCollections: Int? = null

    @SerializedName("total_likes")
    @Expose
    var totalLikes: Int? = null

    @SerializedName("total_photos")
    @Expose
    var totalPhotos: Int? = null

    @SerializedName("accepted_tos")
    @Expose
    var acceptedTos: Boolean? = null

    @SerializedName("for_hire")
    @Expose
    var forHire: Boolean? = null

    @SerializedName("social")
    @Expose
    var social: Social__1? = null
}