package com.agnidating.agni.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Looper
import android.util.Log
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.MessageEvent
import com.agnidating.agni.network.ResultWrapper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import org.greenrobot.eventbus.EventBus
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Suppress("DEPRECATION")
fun Context.isOnline(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val capabilities: NetworkCapabilities?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
    } else {
        return connectivityManager.activeNetworkInfo?.isConnected!!
    }
    when {
        capabilities==null->return false
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
            return true
        }
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
            return true
        }
        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
            return true
        }
    }
    return false
}

/**
 * Safe call api and handle api response
 */
suspend fun <T : BaseResponse> safeApiCall(apiCall: suspend () -> Response<T>): ResultWrapper<T> {

    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall.invoke()
            if (response.isSuccessful) {
                // api call successful check if there is error or not
                if (response.body()?.status == 1) //no error
                    ResultWrapper.Success(response.body()!!)
                else {
                    if (response.body()?.status==401){
                        EventBus.getDefault().post(MessageEvent(true))
                        ResultWrapper.Error(response.message(), response.body())
                    }
                    else{
                        val errorResponse = response.body() //there is some error in api
                        ResultWrapper.Error(response.message(), response.body())
                    }
                }
            } else {
                if (response.code() == 500) {
                    val error = BaseResponse()
                    error.status = 0
                    error.message = "Some thing went wrong"
                    ResultWrapper.Error(error.message, error)
                } else {
                    ResultWrapper.Error(response.message(), convertErrorBody(response.errorBody()))
                }
            }
        } catch (throwable: Throwable) {
            //exception
            val error = BaseResponse()
            error.status = 0
            when (throwable) {
                is UnknownHostException -> {
                    error.message = "Internet not connected please connect to internet"
                }
                is SocketTimeoutException -> {
                    error.message = "Unable to connect please try again"
                }
                else-> error.message = throwable.localizedMessage
            }
            ResultWrapper.Error(throwable.localizedMessage, error)
        }
    }
}

/**
 * convert error body to base response
 */
private fun convertErrorBody(errorBody: ResponseBody?): BaseResponse? {
    return try {
        Gson().fromJson(errorBody!!.string(), BaseResponse::class.java)
    } catch (exception: Exception) {
        val error = BaseResponse()
        error.status = 0
        error.message = exception.localizedMessage
        error
    }
}