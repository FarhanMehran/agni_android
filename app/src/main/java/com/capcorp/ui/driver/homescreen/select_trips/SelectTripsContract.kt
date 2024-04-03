package com.capcorp.ui.driver.homescreen.select_trips

import android.widget.ImageView
import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.images.Result
import com.capcorp.webservice.models.select_trips.AllTrips
import com.capcorp.webservice.models.select_trips.Data

interface SelectTripsContract {
    interface View : BaseView {
        fun apiTripsListing(data: AllTrips)
        fun responseImages(data: List<Result>, ivImage: ImageView)
    }

    interface Presenter : BasePresenter<View> {
        fun tripsListing(accessToken: String, skip: String, pageNo: String)
        fun getImages(dropdownCountry: String, imageview: ImageView)
    }
}