package com.agnidating.agni.network

import com.agnidating.agni.model.BaseResponse


sealed class ResultWrapper<T> {

    class Success<T>(val data: T) : ResultWrapper<T>()

    class Error<T>(val message: String?,val error: BaseResponse? = null) : ResultWrapper<T>()

    class Loading<T> : ResultWrapper<T>()

}