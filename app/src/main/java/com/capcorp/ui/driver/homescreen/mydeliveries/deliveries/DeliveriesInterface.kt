package com.capcorp.ui.driver.homescreen.mydeliveries.deliveries

interface DeliveriesInterface {

    fun onCanceled(
        reasons: String,
        id: String,
        payment: String,
        lat: Double,
        lng: Double,
        driverArrivalDate: String
    )
}