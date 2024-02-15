package com.agnidating.agni.ui.fragment.home

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.agnidating.agni.model.MessageEvent
import com.agnidating.agni.model.home.User
import com.agnidating.agni.network.ApiService
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 04/12/2022
 */
class HomeDataSource @Inject constructor(
    private val apiService: ApiService,
    private val flowers: MutableLiveData<Int>,
    private val unreadNotification: MutableLiveData<Int>,
) : PagingSource<Int, User>() {

    private val LIMIT = 20

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, User> {
        return try {
            val offset = params.key ?: 0
            val response = apiService.getProfilesList(offset, LIMIT)
            if (response.body()?.status == 401) {
                EventBus.getDefault().post(MessageEvent(true))
            }
            if (offset == 0) {
                flowers.postValue(response.body()!!.flowerData.totalFlowers.toInt())
                unreadNotification.postValue(response.body()!!.unreadNotification)
            }
            LoadResult.Page(
                data = response.body()!!.data,
                prevKey = null,
                nextKey = if (response.body()!!.data.size < LIMIT) null else offset + LIMIT
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int {
        return 0
    }
}