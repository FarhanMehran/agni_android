package com.capcorp.webservice.models

data class FilterDataRequest(
    var priceRange: Double = 20000.0,
    var rewardRange: Double = 5000.0,
    var StartDate: String = "",
    var EndDate: String = "",
    var filterType: String = "",
    var mStartDateTimeStamp: Long = 0,
    var mEndDateTimeStamp: Long = 0
)