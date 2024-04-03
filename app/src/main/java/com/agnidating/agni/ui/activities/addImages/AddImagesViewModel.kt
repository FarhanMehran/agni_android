package com.agnidating.agni.ui.activities.addImages

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.base.BaseViewModel
import com.agnidating.agni.model.LoginResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class AddImagesViewModel @Inject constructor(private val apiService: ApiService):BaseViewModel() {
    val updateLiveData=MutableLiveData<ResultWrapper<LoginResponse>>()

    fun updateImage(list:ArrayList<MultipartBody.Part>){
        updateLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.updateImages(list) }
            updateLiveData.postValue(response)
        }
    }
}