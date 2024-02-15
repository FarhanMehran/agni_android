package com.agnidating.agni.ui.fragment.message

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agnidating.agni.BuildConfig
import com.agnidating.agni.model.recent.RecentChatResponse
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.logs
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import javax.inject.Inject

/**
 * Create by AJAY ASIJA on 05/10/2022
 */
@HiltViewModel
class MessageViewModel @Inject constructor(private val sharedPrefs: SharedPrefs):ViewModel() {

    var socketClient: WebSocketClient?=null
    val recentChat=MutableLiveData<RecentChatResponse>()

    fun connectSocket(){
        val uri= URI.create("${BuildConfig.SOCKET_URL}token=${sharedPrefs.getMessageId()}&room=&userID=${sharedPrefs.getString(CommonKeys.USER_ID)}")
        uri.toString().logs()

        socketClient=object : WebSocketClient(uri){
            override fun onOpen(handshakedata: ServerHandshake?) {
                "connected".logs()
                getRecentChats()
            }

            override fun onMessage(message: String?) {
                message?.logs()
                try {
                    val response= Gson().fromJson(message, RecentChatResponse::class.java)
                    recentChat.postValue(response)
                } catch (e: Exception) {
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
    fun getRecentChats( ){
        val recentChat= com.agnidating.agni.utils.getRecentChats(sharedPrefs.getString(CommonKeys.USER_ID).toString())
        recentChat.toString().logs()
        socketClient?.send(recentChat.toString())
    }
}