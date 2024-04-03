package com.agnidating.agni.model.recent


import com.google.gson.annotations.SerializedName

data class RecentChatResponse(
    @SerializedName("data")
    var `data`: List<Data>,
    @SerializedName("message")
    var message: String,
    @SerializedName("Response")
    var response: String,
    @SerializedName("type")
    var type: String
)