package com.capcorp.ui.user.homescreen.chat.search_chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.user.homescreen.chat.ChatContract
import com.capcorp.ui.user.homescreen.chat.ChatPresenter
import com.capcorp.ui.user.homescreen.chat.RQ_CODE_CHAT
import com.capcorp.utils.*
import com.capcorp.webservice.models.chats.ChatListing
import com.capcorp.webservice.models.chats.ChatMessageListing
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_search_user.*
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.fragment_chats.progressbar
import kotlinx.android.synthetic.main.fragment_chats.recyclerView
import kotlinx.android.synthetic.main.fragment_chats.tvEmptyMessage

class SearchUserActivity : BaseActivity(), ChatContract.View {

    private val presenter = ChatPresenter()

    private val chatListing: ArrayList<ChatListing>? = ArrayList()

    private lateinit var adapter: SearchUserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_user)
        presenter.attachView(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = SearchUserAdapter(recyclerView.context, chatListing, this)
        recyclerView.adapter = adapter
        progressbar.visibility = View.GONE
        //chatLogsApiCall()
        setListener(this)

    }

    fun setListener(context: Context) {
        edt_user.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0?.length ?: 0 > 0) {
                    if (CheckNetworkConnection.isOnline(context)) {
                        presenter.getSearchUser(
                            getAuthAccessToken(context),
                            PAGE_LIMIT,
                            0,
                            edt_user.text.toString()
                        )
                    } else {
                        CheckNetworkConnection.showNetworkError(rootView)
                        progressbar.visibility = View.GONE
                    }
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        AppSocket.get().addOnMessageReceiver(messageReceiver)
    }

    override fun onPause() {
        super.onPause()
        AppSocket.get().removeOnMessageReceiver(messageReceiver)
        AppSocket.get().disconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        AppSocket.get().disconnect()
        presenter.detachView()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RQ_CODE_CHAT && resultCode == Activity.RESULT_OK && data != null) {
            val lastMsgData =
                Gson().fromJson(data.getStringExtra(LAST_MESSAGE), ChatMessageListing::class.java)
            val userId = data.getStringExtra(USER_ID)
            refreshChatLogs(lastMsgData, userId)
        }
    }

    private fun chatLogsApiCall() {
        if (CheckNetworkConnection.isOnline(this)) {
            presenter.getChatLogsApiCall(getAuthAccessToken(this), PAGE_LIMIT, 0)
        } else {
            CheckNetworkConnection.showNetworkError(rootView)
            progressbar.visibility = View.GONE
        }
    }


    private val messageReceiver = AppSocket.OnMessageReceiver { message ->
        refreshChatLogs(message, message.senderId, 1)
        message.text?.let { Log.e("message_received", it) }
    }

    private fun refreshChatLogs(
        message: ChatMessageListing,
        userId: String?,
        unDeliveredCount: Int = 0
    ) {
        val index = chatListing?.indexOf(chatListing.find {
            it.senderId?._id == userId
        })
        if (index == null || index == -1) {
            presenter.getSearchUser(
                getAuthAccessToken(this),
                PAGE_LIMIT,
                0,
                edt_user.text.toString()
            )
        } else {
            chatListing?.get(index)?.messageId = message._id
            chatListing?.get(index)?.text = message.text
            chatListing?.get(index)?.chatImage = message.chatImage
            chatListing?.get(index)?.sentAt = message.sentAt
            chatListing?.get(index)?.unDeliverCount =
                chatListing?.get(index)?.unDeliverCount?.plus(unDeliveredCount)
            chatListing?.sortByDescending {
                it.sentAt
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun chatSearchSuccess(chatList: ArrayList<ChatListing>?) {
        chatListing?.clear()
        chatList?.let { chatListing?.addAll(it) }
        progressbar.visibility = View.GONE
        if (chatList?.size == 0) {
            tvEmptyMessage.visibility = View.VISIBLE
        } else {
            tvEmptyMessage.visibility = View.GONE
        }
        adapter.notifyDataSetChanged()
    }

    override fun chatLogsApiSuccess(chatList: ArrayList<ChatListing>?) {
        chatListing?.clear()
        chatList?.let { chatListing?.addAll(it) }
        progressbar.visibility = View.GONE
        if (chatList?.size == 0) {
            tvEmptyMessage.visibility = View.VISIBLE
        } else {
            tvEmptyMessage.visibility = View.GONE
        }
        adapter.notifyDataSetChanged()
    }

    override fun showLoader(isLoading: Boolean) {

    }

    override fun apiFailure() {
        view_search.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        errorBody?.let { view_search.showSnack(it) }
    }

    override fun validationsFailure(type: String?) {

    }
}
