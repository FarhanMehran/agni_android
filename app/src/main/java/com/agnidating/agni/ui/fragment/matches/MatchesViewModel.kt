package com.agnidating.agni.ui.fragment.matches

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.home.User
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 04/12/2022
 */
@HiltViewModel
class MatchesViewModel @Inject constructor(private val apiService: ApiService) : ViewModel() {

    var baseLiveData = MutableLiveData<ResultWrapper<BaseResponse>>()
    private val modifier = MutableStateFlow<List<User>>(emptyList())

    var matches:Flow<PagingData<User>>? =null
    var matchesDataStore:MatchesDataStore?=null
    val homeList=matches?.asLiveData()

    var matchedUser:Flow<PagingData<User>>? =null

    val matchedList=matchedUser?.asLiveData()

    fun updateList(){
        if(matchesDataStore!=null){
            matchesDataStore?.invalidate()
        }else{
            matches=Pager(
                PagingConfig(pageSize = 20)
            ) {
                MatchesDataStore(apiService).also { matchesDataStore=it }
            }.flow.cachedIn(viewModelScope)
        }
    }
    fun updateMatchedList(){
        matchedUser=Pager(
            PagingConfig(pageSize = 20)
        ) {
            MatchedListDataStore(apiService)
        }.flow.cachedIn(viewModelScope)
    }

    private fun applyEvents(m: PagingData<User>, mod: User):PagingData<User> {
        return m.filter { mod.id!=it.id }
    }

    fun onAcceptReject(map: HashMap<String, String>,user: User) {
        baseLiveData.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.acceptReject(map) }
            baseLiveData.postValue(response)
            if (response is ResultWrapper.Success){
                matchesDataStore?.invalidate()
            }
        }
    }


}