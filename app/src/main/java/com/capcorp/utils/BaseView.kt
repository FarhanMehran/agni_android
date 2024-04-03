package com.capcorp.utils

interface BaseView {
    fun showLoader(isLoading: Boolean)
    fun apiFailure()
    fun handleApiError(code: Int?, errorBody: String?)
    fun validationsFailure(type: String?)

    fun showMessage(message: String): String {
        return message
    }

}