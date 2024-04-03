package com.agnidating.agni.ui.fragment.blocked_user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.UnblockResponse
import com.agnidating.agni.model.blockUser.BlockedResponse
import com.agnidating.agni.model.blockUser.BlockedUser
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.Field
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 07/11/2022
 */
@HiltViewModel
class BlockUserViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    val blockedResponse= MutableLiveData<ResultWrapper<BlockedResponse>>()
    val unblockResponse= MutableLiveData<ResultWrapper<UnblockResponse>>()

    fun getBlockedUsers(){
        blockedResponse.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.getBlockedUsers() }
            blockedResponse.postValue(response)
        }
    }

    fun unblockUser(position: Int, id: String){
        unblockResponse.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.unblockUser(id) }
            if (response is ResultWrapper.Success){
                response.data.pos=position
            }
            unblockResponse.postValue(response)
        }
    }
}