package com.capcorp.ui.user.homescreen.detail_offer_accepted.shop

interface Tracking {
    fun callDriveStatusAPI(status: String)
    fun openCameraOrGallery(isFrom: String)
    fun enterCodeDialog()
}