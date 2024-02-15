package com.agnidating.agni.ui.fragment.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.profile.ProfileResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsFragmentViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    val baseLiveData= MutableLiveData<ResultWrapper<BaseResponse>>()
    val hideProfileLiveData= MutableLiveData<ResultWrapper<BaseResponse>>()
    val profileLiveData=MutableLiveData<ResultWrapper<ProfileResponse>>()

    fun logout(){
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.logout() }
            baseLiveData.postValue(response)
        }
    }
    fun updateInterestedLocation(map:HashMap<String,String>){
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.updateInterestedLocation(map) }
            baseLiveData.postValue(response)
        }
    }
    fun updateInterestedAgeRange(map:HashMap<String,String>){
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.updateInterestedAgeRange(map) }
            baseLiveData.postValue(response)
        }
    }
    fun updateInterest(gender:String){
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.updateInterest(gender) }
            baseLiveData.postValue(response)
        }
    }
    fun updateDistanceRange(map:HashMap<String,String>){
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.updateDistanceRange(map) }
            baseLiveData.postValue(response)
        }
    }
    fun contactUs(map:HashMap<String,String>){
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.contactUs(map) }
            baseLiveData.postValue(response)
        }
    }

    fun getProfile(){
        profileLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.getProfile() }
            profileLiveData.postValue(response)
        }
    }
    fun hideProfile(status:String){
        hideProfileLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.hideProfile(status) }
            hideProfileLiveData.postValue(response)
        }
    }
    fun deleteAccount(){
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.deleteAccount() }
            baseLiveData.postValue(response)
        }
    }
}