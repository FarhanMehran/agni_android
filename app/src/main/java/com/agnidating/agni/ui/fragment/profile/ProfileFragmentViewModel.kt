package com.agnidating.agni.ui.fragment.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.model.profile.ProfileResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor( private val apiService: ApiService
) : ViewModel() {

    val profileLiveData=MutableLiveData<ResultWrapper<ProfileResponse>>()


    fun getProfile(){
        profileLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.getProfile() }
            profileLiveData.postValue(response)
        }
    }
}