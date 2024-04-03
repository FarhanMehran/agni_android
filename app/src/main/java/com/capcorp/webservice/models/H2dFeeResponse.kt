package com.capcorp.webservice.models

data class H2dFeeResponse(
    val largeItemOurFee: Double?,
    val mediumItemOurFee: Double?,
    val pocketItemOurFee: Double?,
    val smallItemOurFee: Double?,
    val recommendedFee: Double?
)