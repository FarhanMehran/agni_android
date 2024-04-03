package com.agnidating.agni.ui.fragment.matches

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.agnidating.agni.model.home.User
import com.agnidating.agni.network.ApiService
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 04/12/2022
 */
class MatchedListDataStore @Inject constructor(private val apiService: ApiService) :PagingSource<Int, User>() {

    private val LIMIT=10

    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, User> {
        return try {
            val offset = params.key ?: 0
            val response = apiService.getMatchedRequest(offset,LIMIT)
            LoadResult.Page(
                data = response.body()!!.data,
                prevKey = null,
                nextKey = if (response.body()!!.data.size<LIMIT) null else offset+LIMIT
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {

        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}