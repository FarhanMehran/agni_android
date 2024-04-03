package com.agnidating.agni.ui.activities.writeBio

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.base.BaseViewModel
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BioViewModel @Inject constructor(private val apiService: ApiService):BaseViewModel() {
    val updateLiveData=MutableLiveData<ResultWrapper<BaseResponse>>()

    fun updateBio(bio:String){
        updateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.updateBio(bio) }
            updateLiveData.postValue(response)
        }
    }
}