package com.agnidating.agni.model.blockUser

import com.agnidating.agni.model.BaseResponse
import com.google.gson.annotations.SerializedName

/**
 * Create by AJAY ASIJA on 07/11/2022
 */
data class BlockedUser(
    @SerializedName("name")
    val name:String,
    @SerializedName("id")
    val id:String,
    @SerializedName("profileImg")
    val profilePicture:String
)

data class BlockedResponse(
   val data:ArrayList<BlockedUser>
):BaseResponse()