package com.agnidating.agni.ui.fragment.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agnidating.agni.BuildConfig
import com.agnidating.agni.model.BaseResponse
import com.agnidating.agni.model.DataX
import com.agnidating.agni.model.UnblockResponse
import com.agnidating.agni.model.UserMessage
import com.agnidating.agni.model.user_details.UserDetails
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.utils.*
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.lang.Exception
import java.net.URI
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 05/06/2022
 */
@HiltViewModel
class ChatViewModel @Inject constructor(val sharedPrefs: SharedPrefs, val apiService: ApiService):ViewModel() {
    var socketClient:WebSocketClient?=null
    var messageReceived=MutableLiveData<DataX>()
    val recentMessages=MutableLiveData<ResultWrapper<UserMessage>>()
    val userDetails=MutableLiveData<ResultWrapper<UserDetails>>()
    val clearChat=MutableLiveData<Boolean>()
    val unblockResponse= MutableLiveData<ResultWrapper<UnblockResponse>>()


    fun connectSocket(id:Int,userId:Int){
        val uri= URI.create("${BuildConfig.SOCKET_URL}token=${sharedPrefs.getMessageId()}&room=${getRoomId(id,userId)}&userID=$userId")
        uri.toString().logs()
        socketClient=object : WebSocketClient(uri){
            override fun onOpen(handshakedata: ServerHandshake?) {
                "connected".logs()
            }

            override fun onMessage(message: String?) {
                message?.logs()
                try {
                    val response= Gson().fromJson(message,UserMessage::class.java)
                    messageReceived.postValue(response.data[0])
                } catch (e: Exception) {
                    val json=JSONObject(message)
                    clearChat.postValue(json.getString("type")=="clearChat")
                    e.printStackTrace()
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                reason?.logs()
            }

            override fun onError(ex: Exception?) {
                ex?.message?.logs()
            }

        }
        socketClient?.connect()
    }

    fun getPreviousMessages(receiverId: Int){
        recentMessages.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.getRecentChats(receiverId.toString()) }
            recentMessages.postValue(response)
        }
    }
    fun readMessage(senderId:Int,receiverId:Int){
        val message= getReadMessagesJson(senderId,receiverId)
        message.logs()
        if (socketClient?.isOpen==true){
            try {
                socketClient?.send(message)
            } catch (e: Exception) {
                e.message?.logs()
            }
        }
    }

    fun sendMessage(msg:String,senderId:Int,receiverId:Int){
        val message=getMessageJson(msg,senderId,receiverId)
        message.logs()
        if (socketClient?.isOpen==true){
            try {
                socketClient?.send(message)
            } catch (e: Exception) {
                e.message?.logs()
            }
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

  /*  fun getUserDetals(id:String){
        userDetails.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.getUserInfo(id) }
            userDetails.postValue(response)
        }
    }*/

    fun clearChat(senderId:Int,receiverId:Int){
        val json= getClearChatJson(senderId, receiverId)
        json.logs()
        if (socketClient?.isOpen==true){
            try {
                socketClient?.send(json)
            } catch (e: Exception) {
                e.message?.logs()
            }
        }
        /*clearChat.postValue(ResultWrapper.Loading())
        viewModelScope.launch {
            val response= safeApiCall { apiService.clearChat(map) }
            clearChat.postValue(response)
        }*/
    }

    override fun onCleared() {
        super.onCleared()
        socketClient?.close()
    }
}