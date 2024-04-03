package com.agnidating.agni.model.profile


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Images(
    @SerializedName("id")
    var id: String,
    @SerializedName("profile")
    var profile: String,
    @SerializedName("series")
    var series: String,
    @SerializedName("user_id")
    var userId: String
) : Parcelable