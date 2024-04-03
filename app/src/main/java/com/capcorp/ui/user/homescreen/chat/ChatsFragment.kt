package com.capcorp.ui.user.homescreen.chat


import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.capcorp.R
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.chat.search_chat.SearchUserActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.chats.ChatListing
import com.capcorp.webservice.models.chats.ChatMessageListing
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_chats.*


const val RQ_CODE_CHAT = 100

class ChatsFragment : Fragment(), ChatContract.View {
    override fun chatSearchSuccess(chatList: ArrayList<ChatListing>?) {
    }

    private val presenter = ChatPresenter()

    private val chatListing: ArrayList<ChatListing>? = ArrayList()

    private lateinit var adapter: ChatListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.attachView(this)
        AppSocket.get().init(activity)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = ChatListAdapter(recyclerView.context, chatListing, this)
        recyclerView.adapter = adapter
        progressbar.visibility = View.GONE
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary, R.color.app_green)
        chatLogsApiCall()
        setListeners()
        activity?.registerReceiver(
            broadcastReceiver, IntentFilter(
                ConnectivityManager
                    .CONNECTIVITY_ACTION
            )
        )
        iv_search.setOnClickListener {
            val intent = Intent(context, SearchUserActivity::class.java)
            startActivity(intent)
        }
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


    override fun onDestroyView() {
        super.onDestroyView()
        presenter.detachView()
        activity?.unregisterReceiver(broadcastReceiver)
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

    private fun setListeners() {
        swipeRefresh.setOnRefreshListener {
            chatLogsApiCall()
        }
    }

    private fun chatLogsApiCall() {
        if (CheckNetworkConnection.isOnline(activity)) {
            presenter.getChatLogsApiCall(getAuthAccessToken(activity), PAGE_LIMIT, 0)
        } else {
            CheckNetworkConnection.showNetworkError(rootView)
            progressbar.visibility = View.GONE
        }
    }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            chatLogsApiCall()
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
            chatLogsApiCall()
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

    override fun chatLogsApiSuccess(chatList: ArrayList<ChatListing>?) {
        swipeRefresh.isRefreshing = false
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
        rootView.showSWWerror()
        swipeRefresh.isRefreshing = false
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        swipeRefresh.isRefreshing = false
        if (code == 401) {
            val dialog = Dialog(requireActivity(),R.style.DialogStyle)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.alert_dialog)
            val tvDescription = dialog.findViewById(R.id.tvDescription) as TextView
            val dialogButton: TextView = dialog.findViewById(R.id.btnContinue) as TextView
            val tvTitle = dialog.findViewById(R.id.tvTitle) as TextView
            tvTitle.text = getString(R.string.error)
            tvDescription.text = getString(R.string.sorry_account_have_been_logged)
            dialogButton.text = getString(R.string.ok)
            dialogButton.setOnClickListener {
                dialog.dismiss()
                SharedPrefs.with(activity).remove(ACCESS_TOKEN)
                activity?.finishAffinity()
                startActivity(Intent(activity, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            errorBody?.let { rootView.showSnack(it) }
        }
    }

    override fun validationsFailure(type: String?) {

    }


}
