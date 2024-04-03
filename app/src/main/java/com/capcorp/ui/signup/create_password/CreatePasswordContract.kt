package com.capcorp.ui.signup.phone_verification

import com.capcorp.utils.BasePresenter
import com.capcorp.utils.BaseView
import com.capcorp.webservice.models.SignUpModelRequest
import com.capcorp.webservice.models.SignupModel
import com.capcorp.webservice.models.VerifyDocumentRequestModel
import okhttp3.RequestBody

class CreatePasswordContract {


    interface View : BaseView {
        fun onSignupSuccess(data: SignupModel?)
        fun onTravellerSignUpSuccess(data: SignupModel?)
        fun onDocumentUploadedSuccess(data: SignupModel?)
    }

    interface Presenter : BasePresenter<View> {
        fun signupApiCall(map: Map<String, String>)
        fun travellerSignup(signUpModelRequest: SignUpModelRequest)


        fun shopperSignup(
            countryCode: RequestBody,
            fullnumber: RequestBody,
            firstname: RequestBody,
            lastname: RequestBody,
            email: RequestBody,
            lat: RequestBody,
            lng: RequestBody,
            default_lat: RequestBody,
            default_lng: RequestBody,
            password: RequestBody,
            deviceType: RequestBody,
            usertype: RequestBody,
            dateOfBirth: RequestBody,
            deviceToken: RequestBody,
            country: RequestBody,
            languageId: RequestBody,
            countryIso: RequestBody
        )

        fun verifyDocuments(
            authorization: String,
            verifyDocumentRequestModel: VerifyDocumentRequestModel
        )
    }
}