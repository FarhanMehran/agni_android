package com.capcorp.webservice.models.select_trips

import com.google.gson.JsonObject
import org.json.JSONArray

data class AllTrips(
    var tripListing: List<Data>?,
    var upcomingRewards: List<Rewards>?,
    var tripListingCount: Int

)