package com.capcorp.webservice.models.images

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CoverPhoto {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("promoted_at")
    @Expose
    var promotedAt: Any? = null

    @SerializedName("width")
    @Expose
    var width: Int? = null

    @SerializedName("height")
    @Expose
    var height: Int? = null

    @SerializedName("color")
    @Expose
    var color: String? = null

    @SerializedName("blur_hash")
    @Expose
    var blurHash: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("alt_description")
    @Expose
    var altDescription: Any? = null

    @SerializedName("urls")
    @Expose
    var urls: Urls__1? = null

    @SerializedName("links")
    @Expose
    var links: Links__2? = null

    @SerializedName("likes")
    @Expose
    var likes: Int? = null

    @SerializedName("liked_by_user")
    @Expose
    var likedByUser: Boolean? = null

    @SerializedName("current_user_collections")
    @Expose
    var currentUserCollections: List<Any>? = null

    @SerializedName("sponsorship")
    @Expose
    var sponsorship: Any? = null

    @SerializedName("topic_submissions")
    @Expose
    var topicSubmissions: TopicSubmissions__1? = null

    @SerializedName("premium")
    @Expose
    var premium: Boolean? = null

    @SerializedName("user")
    @Expose
    var user: User__1? = null
}