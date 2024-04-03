package com.capcorp.ui.user.homescreen.home.ship_here2dare.location_details.fragment.review

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.H2dFeeResponse
import com.capcorp.webservice.models.request_model.ShipDataRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody


class ReviewContract {

    interface View : BaseView {
        fun apiSuccess()
        fun getH2DFeeSucess(h2dFee: H2dFeeResponse?)
    }

    interface Presenter : BasePresenter<View> {
        fun shipRequestApiCall(
            auth_key: String,
            type: RequestBody,
            description: RequestBody,
            pickUpLocation: RequestBody,
            dropDownLocation: RequestBody,
            pickUpAddress: RequestBody,
            dropDownAddress: RequestBody,
            itemUrl: RequestBody,
            itemPrice: RequestBody,
            itemQuantity: RequestBody,
            payment: RequestBody,
            pickUpdate: RequestBody,
            image: List<MultipartBody.Part>
        )

        fun shipRequestCall(auth_key: String, shipDataRequest: ShipDataRequest)

        fun getH2dFee(auth_key: String)

    }
}