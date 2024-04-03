package com.capcorp.ui.user.homescreen.chat.chatmessage

import android.app.Activity
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.base.BaseActivity
import com.capcorp.ui.driver.homescreen.home.ImageViewerActivity
import com.capcorp.ui.splash.SplashActivity
import com.capcorp.ui.user.homescreen.HomeActivity
import com.capcorp.utils.*
import com.capcorp.utils.bottomsheet.BottomSheetMedia
import com.capcorp.webservice.models.UserId
import com.capcorp.webservice.models.chats.ChatDelivery
import com.capcorp.webservice.models.chats.ChatMessageListing
import com.capcorp.webservice.models.chats.MediaUpload
import com.google.gson.Gson
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_chat.*
import java.io.File
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class ChatActivity : BaseActivity(), ChatMessageContract.View,
    AppSocket.OnMessageReceiver,
    AppSocket.OnDeliverReceiver,
    AppSocket.OnReadReceiver,
    AppSocket.OnSentMessageReceiver,
    Utility.PassValues, AppSocket.ConnectionListener {

    private val chatMessages = ArrayList<ChatMessageListing>()
    private val presenter = ChatMessagesPresenter()
    private lateinit var adapter: ChatAdapter
    private lateinit var userId: String
    private lateinit var otherUserId: String
    private var mediaType: String = ""
    private lateinit var utility: Utility
    private var callingApi = false
    private var skip = 0
    private var msg: String = ""
    private var socketReconnecting = false
    private var dialogAlert: AlertDialog? = null
    private lateinit var layoutManager: WrapContentLinearLayoutManager
    private var managePermissions: ManagePermissions? = null
    private var imageurl = ""

    companion object {
        var onlineStatus = false
        var receiverId: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        presenter.attachView(this)
        managePermissions = ManagePermissions(this)

        layoutManager = WrapContentLinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        adapter = ChatAdapter(this, chatMessages)
        recyclerView.adapter = adapter
        userId = SharedPrefs.with(this).getObject(USER_DATA, UserId::class.java)._id
        otherUserId = intent.getStringExtra(USER_ID).toString()
        setListeners()
        if (intent.hasExtra(NOTIFICATION)) {
            receiverId = intent.getStringExtra("receiverId").toString()
            onlineStatus = true
            tvTitle.text = intent.getStringExtra("fullname")

            if (intent.hasExtra(PROFILE_PIC_URL)) {
                imageurl = intent.getStringExtra(PROFILE_PIC_URL).toString()
                Glide.with(this).load(imageurl)
                    .apply(
                        RequestOptions().circleCrop()
                            .placeholder(R.drawable.profile_pic_placeholder)
                    ).into(ivProfilePic)
            }

            callingApi = true
            presenter.getChatMessagesApiCall(
                getAuthAccessToken(this),
                "",
                receiverId,
                PAGE_LIMIT,
                skip,
                "BEFORE"
            )
        } else {
            receiverId = otherUserId
            onlineStatus = true
            setNameImage()
            if (CheckNetworkConnection.isOnline(this)) {
                getChatApiCall()
            } else {
                CheckNetworkConnection.showNetworkError(rootView)
            }
        }
        setToolbar()
        AppSocket.get().connect()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }


    override fun onBackPressed() {
        if (intent.hasExtra(NOTIFICATION)) {
            if (SharedPrefs.with(this).getString(USER_TYPE, "") == UserType.USER)
                startActivity(Intent(this, HomeActivity::class.java))
            else
                startActivity(
                    Intent(
                        this,
                        com.capcorp.ui.driver.homescreen.HomeActivity::class.java
                    )
                )
        } else {
            if (chatMessages.isNotEmpty()) {
                var x = chatMessages.size - 1
                while (x > 0) {
                    if ((chatMessages[x].senderId == userId && chatMessages[x].isSent == true) || chatMessages[x].senderId == otherUserId) {
                        val intent = Intent()
                        intent.putExtra(LAST_MESSAGE, Gson().toJson(chatMessages[x]))
                        intent.putExtra(USER_ID, otherUserId)
                        setResult(Activity.RESULT_OK, intent)
                        break
                    }
                    x--
                }
            }
            finish()
        }
    }

    private fun isValidPhoneNumber(phone: String): Boolean {
        val pattern: Pattern = Pattern.compile("\\d{6}")
        val matcher: Matcher = pattern.matcher(phone)
        return matcher.find()
    }


    private fun setToolbar() {
        toolbar.setNavigationIcon(R.drawable.ic_back_btn)
    }

    private fun setNameImage() {
        tvTitle.text = intent.getStringExtra(USER_NAME)
        if (intent.hasExtra(PROFILE_PIC_URL)) {
            if (intent.getStringExtra(PROFILE_PIC_URL) != null) {
                imageurl = intent.getStringExtra(PROFILE_PIC_URL).toString()
                Glide.with(this).load(imageurl)
                    .apply(
                        RequestOptions().circleCrop()
                            .placeholder(R.drawable.profile_pic_placeholder)
                    ).into(ivProfilePic)
            }
        }

    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra(NOTIFICATION)) {
            receiverId = intent.getStringExtra("receiverId").toString()
            onlineStatus = true
        } else {
            receiverId = otherUserId
            onlineStatus = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
        AppSocket.get().removeOnMessageReceiver(this)
        unregisterReceiver(broadcastReceiver)
        AppSocket.get().disconnect()
        receiverId = ""
        onlineStatus = false
    }

    private fun setListeners() {
        ivDown.setOnClickListener { recyclerView.scrollToPosition(chatMessages.size - 1) }
        AppSocket.get().addConnectionListener(this)
        AppSocket.get().addOnMessageReceiver(this)
        AppSocket.get().onReadMessageEvent(this)

        li_profile.setOnClickListener {
            val intent = Intent(applicationContext, ImageViewerActivity::class.java)
            intent.putExtra(PROFILE_PIC_URL, imageurl)
            startActivity(intent)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
        ivSend.setOnClickListener {
            msg = etMessage.text.toString().trim()
            if (msg.length == 0) {
                Toast.makeText(this, getString(R.string.enter_message), Toast.LENGTH_SHORT).show()
            } else if (msg.contains("@")) {
                Toast.makeText(this, getString(R.string.dont_send_email), Toast.LENGTH_SHORT).show()
            } else if (isValidPhoneNumber(msg) == true) {
                Toast.makeText(this, getString(R.string.phone_number_cant_send), Toast.LENGTH_SHORT)
                    .show()
            } else {
                /*var p = Pattern.compile("\\d+");
                var m = p.matcher(msg);
                while (m.find()) {
                    if(m.group().length ==8){
                        Toast.makeText(this, "Phone number cannot be shared due to privacy reasons.,Toast.LENGTH_SHORT", Toast.LENGTH_SHORT).show()
                    }else{

                    }
                }

*/              etMessage.setText("")
                var chatId = ""
                if (chatMessages.size > 0) {
                    chatId = chatMessages[0]._id!!
                }
                if (intent.hasExtra(NOTIFICATION)) {
                    val message = ChatMessageListing(
                        chatId,
                        Calendar.getInstance().timeInMillis.toString(),
                        intent.getStringExtra("receiverId"),
                        userId,
                        msg,
                        null,
                        Calendar.getInstance().timeInMillis,
                        ChatType.TEXT,
                        false,
                        false,
                        false
                    )
                    message.let { chatMessages.add(it) }
                    adapter.notifyItemInserted(chatMessages.size - 1)
                    recyclerView.scrollToPosition(chatMessages.size - 1)
                    sendMessage(message)
                } else {
                    val message = ChatMessageListing(
                        chatId,
                        Calendar.getInstance().timeInMillis.toString(),
                        otherUserId,
                        userId,
                        msg,
                        null,
                        Calendar.getInstance().timeInMillis,
                        ChatType.TEXT,
                        false,
                        false,
                        false
                    )
                    message.let { chatMessages.add(it) }
                    adapter.notifyItemInserted(chatMessages.size - 1)
                    recyclerView.scrollToPosition(chatMessages.size - 1)
                    sendMessage(message)
                }


                /* for (i in 0..msg.length) {
                     var letter : Char
                     letter = msg.(0);
                      if (msg.matches("[0-9]+")) {
                          numberCount++
                      } else {
                          if (numberCount < 7) {
                              numberCount = 0
                          }
                      }
                  }
                  if (numberCount > 7) {
                      Toast.makeText(this, "Phone number cannot be shared due to privacy reasons.,Toast.LENGTH_SHORT", Toast.LENGTH_SHORT).show()
                  }else{
                      etMessage.setText("")
                      var chatId = ""
                      if (chatMessages.size > 0) {
                          chatId = chatMessages[0]._id!!
                      }
                      val message = ChatMessageListing(chatId, Calendar.getInstance().timeInMillis.toString(), otherUserId, userId, msg, null, Calendar.getInstance().timeInMillis, ChatType.TEXT, false, false, false)
                      message.let { chatMessages.add(it) }
                      adapter.notifyItemInserted(chatMessages.size - 1)
                      recyclerView.scrollToPosition(chatMessages.size - 1)
                      sendMessage(message)
                  }*/
                /*var p = Pattern.compile("(\\([0-9]{2}\\)|[0-9]{2})[ ][0-9]{3}[ ][0-9]{2,2}[ ][0-9]{2} ");
                var m = p.matcher(msg);
                if (m.find()) {
                    System.out.println("............" + m.group(0));
                }else{
                    etMessage.setText("")
                    var chatId = ""
                    if (chatMessages.size > 0) {
                        chatId = chatMessages[0]._id!!
                    }
                    val message = ChatMessageListing(chatId, Calendar.getInstance().timeInMillis.toString(), otherUserId, userId, msg, null, Calendar.getInstance().timeInMillis, ChatType.TEXT, false, false, false)
                    message.let { chatMessages.add(it) }
                    adapter.notifyItemInserted(chatMessages.size - 1)
                    recyclerView.scrollToPosition(chatMessages.size - 1)
                    sendMessage(message)
                }
*/

                /* etMessage.setText("")
                 var chatId = ""
                 if (chatMessages.size > 0) {
                     chatId = chatMessages[0]._id!!
                 }
                 val message = ChatMessageListing(chatId, Calendar.getInstance().timeInMillis.toString(), otherUserId, userId, msg, null, Calendar.getInstance().timeInMillis, ChatType.TEXT, false, false, false)
                 message.let { chatMessages.add(it) }
                 adapter.notifyItemInserted(chatMessages.size - 1)
                 recyclerView.scrollToPosition(chatMessages.size - 1)
                 sendMessage(message)*/

            }

        }
        etMessage.addTextChangedListener(textChangeListener)
        ivAdd.setOnClickListener {
            if (CheckNetworkConnection.isOnline(this)) {
                permissionSafetyMethod()
            } else {
                CheckNetworkConnection.showNetworkError(rootView)
            }
        }
        recyclerView.addOnScrollListener(onScrollListener)
        view_message_filtered.setOnClickListener {
            openMessageFilterPopup(this)
        }
    }

    fun getPhoneNumber(): String? {
        val temp = msg.toCharArray()
        var value: String? = ""
        var licz = 0

        for (i in temp.indices) {
            if (licz < 9) {
                if (Character.toString(temp[i]).matches("[0-9]".toRegex())) {
                    value += Character.toString(temp[i])
                    licz++
                } else if (Character.toString(temp[i]).matches("\u0020|\\-|\\(|\\)".toRegex())) {

                } else {
                    value = ""
                    licz = 0
                }
            }
        }

        if (value!!.length != 9) {
            value = null
        } else {
            value = "tel:" + value.trim { it <= ' ' }
        }

        return value
    }


    private var onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (chatMessages.isNotEmpty() && layoutManager.findFirstVisibleItemPosition() == 0 && !callingApi) {
                if (intent.hasExtra(NOTIFICATION)) {
                    callingApi = true
                    intent.getStringExtra("receiverId")?.let {
                        presenter.getChatMessagesApiCall(
                            getAuthAccessToken(context = this@ChatActivity), "",
                            it, PAGE_LIMIT, skip, "BEFORE"
                        )
                    }
                } else {
                    getChatApiCall(chatMessages.first()._id ?: "")

                }
            }
            if ((recyclerView.layoutManager as LinearLayoutManager)
                    .findLastCompletelyVisibleItemPosition() > chatMessages.size - 4
            ) {
                ivDown.visibility = View.GONE
                ivDown.setBackgroundResource(R.drawable.shape_scroll_bottom_chat)
            } else {
                ivDown.visibility = View.VISIBLE
            }

        }
    }

    private val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {

        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            if (s?.trim()?.isEmpty() == true) {
                ivSend.setImageResource(R.drawable.ic_send)
                ivSend.isEnabled = false
            } else {
                ivSend.setImageResource(R.drawable.ic_send_b)
                ivSend.isEnabled = true
            }
        }

    }

    private fun getChatApiCall(
        messageId: String = "",
        skip: Int = 0,
        messageOrder: String = MessageOrder.BEFORE
    ) {
        callingApi = true
        presenter.getChatMessagesApiCall(
            getAuthAccessToken(this),
            messageId,
            otherUserId,
            PAGE_LIMIT,
            skip,
            messageOrder
        )
    }

    fun sendMessage(message: ChatMessageListing?) {
        if (AppSocket.get().isConnected) {
            AppSocket.get().sendMessage(message) { message ->
                val index = chatMessages.indexOf(chatMessages.find {
                    it.messageId == message?.messageId
                })
                chatMessages[index].isSent = true
                adapter.notifyItemChanged(index, adapter.CHANGE_SENT_STATUS)
                Log.e("got_ack", message.toString())

                //
            }
        } else {
            CheckNetworkConnection.showNetworkError(rootView)
            setFailedMessages()
        }
    }

    override fun chatMessagesApiSuccess(
        chatList: ArrayList<ChatMessageListing>?,
        messageOrder: String
    ) {

        Thread(Runnable {
            callingApi = false
            if (messageOrder == MessageOrder.BEFORE) {
                if (chatMessages.isEmpty()) {
                    chatList?.let { chatMessages.addAll(it) }
                    runOnUiThread { adapter.notifyDataSetChanged() }
                } else {
                    chatList?.let { chatMessages.addAll(0, it) }
                    val tempChat = ArrayList<ChatMessageListing>()
                    tempChat.addAll(chatMessages.distinctBy { it.messageId })
                    tempChat.sortBy { it.sentAt }
                    chatMessages.clear()
                    chatMessages.addAll(tempChat)
                    runOnUiThread {
                        chatList?.size?.let { adapter.notifyItemRangeInserted(0, it) }
                        adapter.notifyItemChanged(chatList?.size ?: 0)
                    }
                }
            } else {
                chatList?.let { chatMessages.addAll(it) }
                val tempChat = ArrayList<ChatMessageListing>()
                tempChat.addAll(chatMessages.distinctBy { it.messageId })
                tempChat.sortBy { it.sentAt }
                runOnUiThread {
                    chatMessages.clear()
                    chatMessages.addAll(tempChat)
                    adapter.notifyDataSetChanged()
                    if (messageOrder == MessageOrder.AFTER) {
                        scrollToBottom()
                    }
                }
            }

            makeReadedMessages()
        }).start()


    }

    fun makeReadedMessages() {
        for (k in chatMessages.indices) {
            if (!chatMessages[k].isDeliver || !chatMessages[k].isRead) {
                val delivery = ChatDelivery(
                    chatMessages[k]._id,
                    chatMessages[k].senderId,
                    chatMessages[k].receiverId
                )
                AppSocket.get().readMessage(delivery)
            }
        }
    }

    override fun showLoader(isLoading: Boolean) {
        progressbar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    override fun apiFailure() {
        callingApi = false
        rootView.showSWWerror()
    }

    override fun handleApiError(code: Int?, errorBody: String?) {
        callingApi = false
        if (code == 401) {
            val dialog = Dialog(this,R.style.DialogStyle)
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
                SharedPrefs.with(this).remove(ACCESS_TOKEN)
                finishAffinity()
                startActivity(Intent(this, SplashActivity::class.java))
            }
            dialog.show()
        } else {
            errorBody?.let { rootView.showSnack(it) }
        }
    }

    override fun validationsFailure(type: String?) {

    }

    override fun onConnectionStatusChanged(status: String?) {
        when (status) {
            Socket.EVENT_CONNECT -> {
                if (socketReconnecting) {
                    socketReconnecting = false
                    var x = chatMessages.size - 1
                    while (x > 0) {
                        if (chatMessages[x].isSent == true) {
                            getChatApiCall(
                                messageId = chatMessages[x]._id.toString(),
                                messageOrder = MessageOrder.AFTER
                            )
                            break
                        }
                        x--
                    }
                }
            }
            Socket.EVENT_DISCONNECT -> {
                socketReconnecting = true
                setFailedMessages()
            }
            Socket.EVENT_ERROR -> {

            }
            else -> {

            }
        }
    }


    private fun setFailedMessages() {
        for (i in 0 until chatMessages.size) {
            if (chatMessages[i].isSent == false) {
                chatMessages[i].isFailed = true
                adapter.notifyItemChanged(i, adapter.CHANGE_SENT_STATUS)
            }
        }
    }

    override fun onMessageReceive(message: ChatMessageListing?) {
        ivDown.setBackgroundResource(R.drawable.shape_scroll_bottom_chat_blue)
        val index = chatMessages.indexOfFirst { message?.messageId == it.messageId }
        if (index == -1) {
            message?.let { chatMessages.add(it) }
            adapter.notifyItemChanged(chatMessages.size - 1)
            scrollToBottom()


            val delivery = ChatDelivery(message?._id, message?.senderId, message?.receiverId)
            //AppSocket.get().deliveryMessage(delivery, this)
            AppSocket.get().readMessage(delivery)
        }

    }

    override fun onSentMessageReceive(message: ChatMessageListing?) {
        ivDown.setBackgroundResource(R.drawable.shape_scroll_bottom_chat_blue)
        val index = chatMessages.indexOfFirst { message?.messageId == it.messageId }
        if (index == -1) {
            message?.let { chatMessages.add(it) }
            adapter.notifyItemChanged(chatMessages.size - 1)
            scrollToBottom()
        }
    }


    override fun onDeliverReceive(message: ChatMessageListing?) {
        if (message?.isDeliver == true) {
            for (i in chatMessages.indices) {
                val chatMessageListing = chatMessages[i]
                if (message.isDeliver == true && chatMessages.get(i).isDeliver == false) {
                    chatMessageListing.isDeliver = message.isDeliver
                }
                chatMessages.set(i, chatMessageListing)
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun onReadReceive(message: ChatMessageListing?) {
        if (message?.isRead == true) {
            for (i in chatMessages.indices) {
                val chatMessageListing = chatMessages[i]
                if (message.isRead == true && chatMessages.get(i).isRead == false) {
                    chatMessageListing.isRead = message.isRead
                    chatMessageListing.isDeliver = message.isDeliver
                }
                chatMessages.set(i, chatMessageListing)
            }
            adapter.notifyDataSetChanged()
            scrollToBottom()

        }
    }

    private fun scrollToBottom() {
        if ((recyclerView.layoutManager as LinearLayoutManager)
                .findLastCompletelyVisibleItemPosition() > chatMessages.size - 4
        ) {
            recyclerView.scrollToPosition(chatMessages.size - 1)
        }
    }

    private fun mediaOption() {
        val bottomSheetMedia = BottomSheetMedia()
        bottomSheetMedia.setOnCameraListener(View.OnClickListener {
            openCamera()
            bottomSheetMedia.dismiss()
        })

        bottomSheetMedia.setOnGalleryListener(View.OnClickListener {
            openGallery()
            bottomSheetMedia.dismiss()

        })

        bottomSheetMedia.setOnCancelListener(View.OnClickListener {
            bottomSheetMedia.dismiss()
        })
        bottomSheetMedia.show(supportFragmentManager, "camera")
    }

    private fun openGallery() {
        mediaType = UtilityConstants.GALLERY
        utility = Utility(this, UtilityConstants.GALLERY)
        utility.selectImage()
    }

    private fun openCamera() {
        mediaType = UtilityConstants.CAMERA
        utility = Utility(this, UtilityConstants.CAMERA)
        utility.selectImage()
    }


    override fun passImageURI(file: File?, photoURI: Uri?) {
        val mediaToUpload = MediaUpload(MediaUploadStatus.NOT_UPLOADED, -1, file)
        val message = ChatMessageListing(
            "",
            getUniqueId(),
            otherUserId,
            userId,
            "",
            null,
            Calendar.getInstance().timeInMillis,
            ChatType.IMAGE,
            false,
            false,
            false,
            false,
            mediaToUpload
        )
        chatMessages.add(message)
        adapter.notifyItemInserted(chatMessages.size - 1)
        recyclerView.scrollToPosition(chatMessages.size - 1)
    }

    private fun permissionSafetyMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (managePermissions!!.checkPermissions()) mediaOption()
        } else mediaOption()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (utility != null) {
            utility.onActivityResult(requestCode, resultCode, data)
        }
    }


    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

        }
    }

    fun openMessageFilterPopup(context: Context) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        val alertLayout = inflater.inflate(R.layout.layout_message_filtered, null)
        val ok = alertLayout.findViewById(R.id.tv_ok) as TextView
        ok.setOnClickListener {
            dialogAlert?.dismiss()
        }
        builder.setView(alertLayout)
        builder.setCancelable(true)
        dialogAlert = builder.create()
        dialogAlert?.setCanceledOnTouchOutside(true)
        dialogAlert?.window?.setGravity(Gravity.CENTER)
        dialogAlert!!.show()
    }
}
