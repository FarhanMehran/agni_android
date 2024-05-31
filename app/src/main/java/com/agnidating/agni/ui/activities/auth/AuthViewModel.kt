package com.agnidating.agni.ui.activities.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.R
import com.agnidating.agni.base.BaseViewModel
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.Data
import com.agnidating.agni.model.LoginResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(val apiService: ApiService):BaseViewModel() {
    val registerLiveData=MutableLiveData<ResultWrapper<LoginResponse>>()
    val phoneUpdateLiveData=MutableLiveData<ResultWrapper<BaseResponse>>()
    val verifyUpdateLiveData=MutableLiveData<ResultWrapper<BaseResponse>>()
    val resendLiveData=MutableLiveData<ResultWrapper<BaseResponse>>()

    fun register(map:HashMap<String,String>){
        registerLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { if(map["status"]=="1") apiService.newRegister_(map) else  apiService.register_(map)}
            registerLiveData.postValue(response)
        }
    }
    fun registerResend(map:HashMap<String,String>){
        resendLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.registerResend(map)}
            resendLiveData.postValue(response)
        }
    }
    fun updatePhone(map:HashMap<String,String>){
        phoneUpdateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.updatePhone(map) }
            phoneUpdateLiveData.postValue(response)
        }
    }
    fun verifyOtp(map: HashMap<String, String>){
        registerLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.verifyOtp(map) }
            registerLiveData.postValue(response)
        }
    }
    fun resendOtp(map: HashMap<String, String>){
        resendLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.resendOtp(map) }
            resendLiveData.postValue(response)
        }
    }
    fun verifyPhoneUpdate(map: HashMap<String, String>){
        verifyUpdateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.verifyPhoneUpdate(map) }
            verifyUpdateLiveData.postValue(response)
        }
    }

    fun getDestination(data: Data): Int? {
        return when(data.isComplete) {
            0 -> R.id.navYourName
            1 -> R.id.navLocation
            2 -> R.id.navBirthday
            3 -> R.id.gender
            4 -> R.id.showMe
            5-> R.id.nav_education
            6-> R.id.nav_religion
            7-> R.id.nav_community
            10->null
            else->  0
        }
    }
}