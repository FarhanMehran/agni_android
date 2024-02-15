package com.agnidating.agni.ui.activities.notifications

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.base.BaseViewModel
import com.agnidating.agni.model.notification.NotificationsResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 04/28/2022
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(private val apiService: ApiService):BaseViewModel() {
    val notificationLiveData=MutableLiveData<ResultWrapper<NotificationsResponse>>()

    fun getNotifications(){
        notificationLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.getNotifications() }
            notificationLiveData.postValue(response)
        }
    }
}