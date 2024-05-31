package com.agnidating.agni.ui.fragment.message

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agnidating.agni.BuildConfig
import com.agnidating.agni.model.recent.RecentChatResponse
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.logs
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.google.gson.Gson
import com.google.gson.JsonObject
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



        val userIdString = sharedPrefs.getString(CommonKeys.USER_ID)
        val receiverIdString = sharedPrefs.getString(CommonKeys.RECEIVER_ID)

        val userId = userIdString?.takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0
        val receiverId = receiverIdString?.takeIf { it.isNotEmpty() }?.toIntOrNull() ?: 0

        val roomName = if (userId < receiverId) {
            receiverId - userId
        } else {
            userId - receiverId
        }


        Log.d("room_name",roomName.toString())
        Log.d("userId",userIdString.toString())
        Log.d("receiverId",receiverIdString.toString())


        val uri= URI.create("${BuildConfig.SOCKET_URL}token=${sharedPrefs.getMessageId()}&room=&userID=${sharedPrefs.getString(CommonKeys.USER_ID)}")
//        val uri= URI.create("ws://77.37.45.86:8076?token=2d5a7dbeca&room=7-8&userID=7")

        Log.d("http..............",uri.toString())

        uri.toString().logs()

        socketClient=object : WebSocketClient(uri){
            override fun onOpen(handshakedata: ServerHandshake?) {
                "connected".logs()

                Log.d("onopen","called")

                getRecentChats()
            }

            override fun onMessage(message: String?) {
                message?.logs()
                Log.d("onMessage","called")
                try {
                    val response= Gson().fromJson(message, RecentChatResponse::class.java)
                    recentChat.postValue(response)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                reason?.logs()
                Log.d("onCloase","called")
            }

            override fun onError(ex: Exception?) {
                ex?.message?.logs()
                Log.d("onError","called")
            }

        }
        socketClient?.connect()
    }
    fun getRecentChats( ){
        val recentChat= com.agnidating.agni.utils.getRecentChats(sharedPrefs.getString(CommonKeys.USER_ID).toString())
        recentChat.toString().logs()



        Log.d("getChats","called")
        Log.d("recentChats",recentChat.toString())

        val message= JsonObject()
        message.addProperty("serviceType", "RecentChat")
        message.addProperty("userID", "7").toString()

        socketClient?.send(recentChat.toString())
//        socketClient?.send(message.toString())
    }
}