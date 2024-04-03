package com.capcorp.ui.driver.stripe

data class ConnectStripeOnBoarding(
    val `data`: Data,
    val message: String,
    val statusCode: Int
){
    data class Data(
        val alreadyRegistegitred: Boolean = false,
        val created: Int,
        val stripeId: String,
        val expires_at: Int,
        val `object`: String,
        val url: String?
    )
}
