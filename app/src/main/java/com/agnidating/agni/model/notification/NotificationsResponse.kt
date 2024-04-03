package com.agnidating.agni.model.notification


import android.os.Parcelable
import com.agnidating.agni.model.BaseResponse
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class NotificationsResponse(
    @SerializedName("data")
    var `data`: List<Data>,
):BaseResponse(), Parcelable