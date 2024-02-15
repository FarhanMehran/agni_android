package com.agnidating.agni.ui.fragment.editProfile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.base.BaseViewModel
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.DeleteResponse
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(private val apiService: ApiService):BaseViewModel() {
    val editLiveData=MutableLiveData<ResultWrapper<BaseResponse>>()
    val deleteResponse=MutableLiveData<ResultWrapper<DeleteResponse>>()

    fun editProfile(list:ArrayList<MultipartBody.Part>){
        editLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.editProfile(list) }
            editLiveData.postValue(response)
        }
    }
    fun deleteImage(imgId:String,deletePos:Int){
        deleteResponse.postValue(ResultWrapper.Loading())
        viewModelScope.launch(Dispatchers.IO) {
            val response= safeApiCall { apiService.deleteImg(imgId) }
            if (response is  ResultWrapper.Success){
                response.data.position=deletePos
            }
            deleteResponse.postValue(response)
        }
    }
}