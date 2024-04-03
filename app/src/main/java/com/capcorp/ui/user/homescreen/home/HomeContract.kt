package com.capcorp.ui.user.homescreen.home

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.KnowMoreResponse
import com.capcorp.webservice.models.home.Data
import com.capcorp.webservice.models.product_information.ProductInformation
import okhttp3.RequestBody

class HomeContract {
    interface View : BaseView {
        fun getHomeDetailsSuccess(data: Data)
        fun getKnowMoreSuccess(data: KnowMoreResponse.Data)
        fun getProductInformationSuccess(data: ProductInformation)
    }

    interface Presenter : BasePresenter<View> {
        fun getHomeDetails()
        fun getKnowMore()
        fun getProductInformation(token: String, url: RequestBody, content: RequestBody)
    }
}