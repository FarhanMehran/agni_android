package com.capcorp.ui.settings.profile.otheruserProfile

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.SignupModel

class OtherUserProfileContract {
    interface View : BaseView {
        fun onOtherUserProfile(data: SignupModel?)
    }

    interface Presenter : BasePresenter<View> {
        fun onOtherUserProfile(authToken: String, user_id: String, type: String)
    }
}