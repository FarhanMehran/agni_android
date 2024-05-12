package com.agnidating.agni.ui.fragment.report

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.ArrayList
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 04/12/2022
 */
@HiltViewModel
class ReportBlockViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    var baseLiveData = MutableLiveData<ResultWrapper<BaseResponse>>()
    var blockLiveData = MutableLiveData<ResultWrapper<BaseResponse>>()

    fun reportUser(
        map: HashMap<String, RequestBody>,
        images: ArrayList<MultipartBody.Part>)
    {
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.reportUser(map,images) }
            baseLiveData.postValue(response)
        }
    }
    fun blockUser(map: HashMap<String,String>){
        blockLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.blockUser(map) }
            blockLiveData.postValue(response)
        }
    }
    fun unMatch(map: HashMap<String,String>){
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.unMatch(map) }
            baseLiveData.postValue(response)
        }
    }


}