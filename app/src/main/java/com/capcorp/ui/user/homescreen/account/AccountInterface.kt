package com.capcorp.ui.user.homescreen.account

interface AccountInterface {
    fun switchChange(isNotificationOn: Boolean)
    fun changeLanguage(language: String)
    fun doLogout()
}