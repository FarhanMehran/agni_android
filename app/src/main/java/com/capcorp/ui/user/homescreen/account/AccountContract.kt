package com.capcorp.ui.user.homescreen.account

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.Faq
import com.capcorp.webservice.models.Reason
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.SupportRequest


class AccountContract {
    interface View : BaseView {
        fun switchUserSuccess(signupModel: SignupModel)
        fun onEditProfileApiSuccess(data: SignupModel?)
        fun onFaqSuccess(data: List<Faq>)
        fun logoutSuccess()
        fun supportSuccess()
        fun reasonSuccess(data: List<Reason>)
        fun blockPush(signupModel: SignupModel)
        fun languageSuccess()
    }

    interface Presenter : BasePresenter<View> {
        fun switchUser(accessToken: String, type: String)
        fun editProfileApiCall(authToken: String, map: Map<String, String>)
        fun logout(accessToken: String)
        fun faq()
        fun supportRequest(accessToken: String, supportRequest: SupportRequest)
        fun reasonApi(accessToken: String, type: String)
        fun blockPush(accessToken: String, updateStatus: String)
        fun languageChangeApi(accessToken: String, type: Int)
    }
}