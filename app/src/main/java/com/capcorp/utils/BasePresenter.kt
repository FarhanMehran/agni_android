package com.capcorp.utils

interface BasePresenter<in V : BaseView> {
    fun attachView(view: V)
    fun detachView()
}