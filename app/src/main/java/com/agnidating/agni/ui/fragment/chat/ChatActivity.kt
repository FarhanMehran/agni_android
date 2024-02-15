package com.agnidating.agni.ui.fragment.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.navigation.Navigation.findNavController
import com.agnidating.agni.R
import com.agnidating.agni.base.ScopedActivity
import com.agnidating.agni.databinding.FragmentChatBinding
import com.agnidating.agni.model.DataX
import com.agnidating.agni.model.home.User
import com.agnidating.agni.network.ResultWrapper
import com.agnidating.agni.ui.activities.dashboard.DashboardActivity
import com.agnidating.agni.ui.adapters.ChatAdapter
import com.agnidating.agni.ui.fragment.userDetails.UserDetailsDialogFragment
import com.agnidating.agni.utils.*
import com.agnidating.agni.utils.custom_view.UnblockDialog
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatActivity() : ScopedActivity() {

    private var senderId: Int? = 0
    private var receiverId: Int? = 0
    private var fromNotification = false
    private var suspended = false
    private var isBlocked = false
    var receiver: User? = null
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var binding: FragmentChatBinding
    private var chats = ArrayList<DataX>()

    @Inject
    lateinit var sharedPrefs: SharedPrefs
    private val viewModel: ChatViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadData()
        setupRecyclerView()
        bindObserver()
        bindListeners()
    }

    private fun loadData() {
        senderId = sharedPrefs.getString(CommonKeys.USER_ID)!!.toInt()
        receiverId = intent.getIntExtra(CommonKeys.RECEIVER_ID, 0)
        suspended = intent.getBooleanExtra(CommonKeys.SUSPENDED, false)
        fromNotification = intent.getBooleanExtra(CommonKeys.FROM_NOTIFICATION, false)
        getPreviousMessages()
        viewModel.connectSocket(receiverId!!, senderId!!)
    }

    /**
     *  hit api to get previous messages
     */
    private fun getPreviousMessages() {
        viewModel.getPreviousMessages(receiverId!!)
    }

    /**
     * bind live data observers
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun bindObserver() {
        viewModel.messageReceived.observe(this) {
            chats.add(it)
            chatAdapter.notifyItemInserted(chats.size - 1)
            binding.rvChat.scrollToPosition(chats.size - 1)
        }

        viewModel.recentMessages.observe(this)
        {
            when (it) {
                is ResultWrapper.Loading -> {
                    showProgress()
                }
                is ResultWrapper.Success -> {
                    hideProgress()
                    this.receiver = it.data.receiver
                    isBlocked=it.data.blockedBy!!.isNotEmpty()
                    setUserDetails(it.data.receiver, it.data.blockedBy)
                    if (it.data.data.isNullOrEmpty().not()) {
                        binding.rlChat.visible()
                        chats.clear()
                        chatAdapter.notifyDataSetChanged()
                        chats.addAll(it.data.data)
                        chatAdapter.notifyItemRangeInserted(0, chats.size - 1)
                        binding.rvChat.scrollToPosition(chats.size - 1)
                        if (chats.isNotEmpty()) {
                            viewModel.readMessage(senderId!!.toInt(), receiverId!!)
                        }
                    }
                }
                is ResultWrapper.Error -> {
                    hideProgress()
                    //showToast(it.error?.message.toString())
                }
            }
        }

        viewModel.clearChat.observe(this)
        {
            if (it) {
                findNavController(this, R.id.navHostView).popBackStack()
            }
        }

        viewModel.unblockResponse.observe(this) {
            when (it) {
                is ResultWrapper.Error -> {
                    hideProgress()
                    showToast(it.error?.message.toString())
                }
                is ResultWrapper.Loading -> {
                    showProgress()
                }
                is ResultWrapper.Success -> {
                    hideProgress()
                    isBlocked=false
                    setUserDetails(receiver!!,"")
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setUserDetails(data: User, blockedBy: String?) {
        if (blockedBy.isNullOrEmpty().not()) {
            binding.consSendMessage.visibility= View.INVISIBLE
            binding.cvBlock.visibility= View.VISIBLE
            binding.tvBlockMessage.text = if (data.id == blockedBy) {
                binding.btnUnblock.gone()
                "${data.name} has blocked you"
            } else {
                "You've blocked ${data.name}"
            }
        }
        else{
            binding.consSendMessage.visibility= View.VISIBLE
            binding.cvBlock.visibility= View.INVISIBLE
        }
        if (suspended.not()) {
            binding.civProfile.load(
                if (data.profileImg.isNullOrEmpty().not()) data.profileImg!![0].profile else "",
                CommonKeys.IMAGE_BASE_URL
            )
            binding.tvName.text = data.name
            binding.tvBio.text = data.address
        } else {
            binding.tvName.text = "Agni User"
            binding.tvBio.text = data.address
        }
    }

    /**
     * bind listeners [bindListeners]
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun bindListeners() {
        binding.tvSend.setOnClickListener {
            if (binding.etPhone.text.toString().trim().isNotEmpty()) {
                viewModel.sendMessage(binding.etPhone.text.toString(), senderId!!, receiverId!!)
                chats.add(
                    createSendMessageObject(
                        binding.etPhone.text.toString(),
                        sharedPrefs
                    )
                )
                chatAdapter.notifyItemInserted(chats.size - 1)
                binding.rvChat.scrollToPosition(chats.size - 1)
                binding.etPhone.setText("")
            } else {
                showToast("Message should not be empty")
            }
        }

        if (suspended.not()) {
            binding.civProfile.setOnClickListener {
                showUserInfo()
            }
            binding.tvName.setOnClickListener {
                showUserInfo()
            }
            binding.tvBio.setOnClickListener {
                showUserInfo()
                /* val action =
                     ChatFragmentDirections.actionChatFragmentToUserDetailsDialogFragment(args.receiverId.toString())
                 findNavController(this, R.id.navHostView).navigate(action)*/
            }

        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.ivMenu.setOnClickListener {
            errorDialog("Are you sure want to delete all messages?", true) {
                viewModel.clearChat(senderId!!, receiverId!!)
            }
        }
        binding.btnUnblock.setOnClickListener {
            val unblockDialog= UnblockDialog{
                if (it) viewModel.unblockUser(0,receiver?.id.toString())
            }
            unblockDialog.user=receiver
            unblockDialog.show(supportFragmentManager, "")
        }
    }

    private fun showUserInfo() {
       if (isBlocked.not()){

           val userInfoDialogFragment = UserDetailsDialogFragment()
           userInfoDialogFragment.id =  receiverId.toString()
           userInfoDialogFragment.hideMessage=true
           userInfoDialogFragment.matchStatus=true
           userInfoDialogFragment.callBack={
               if (it){
                   setUserDetails(receiver!!,sharedPrefs.getString(CommonKeys.USER_ID))
                   isBlocked=true
               }
           }
           userInfoDialogFragment.show(supportFragmentManager, "")
         /*  userInfoDialogFragment.show(supportFragmentManager, "")
           val userInfoDialogFragment = UserDetailsDialogFragment()
           userInfoDialogFragment.id = receiverId.toString()
           userInfoDialogFragment.callBack={
               if (it){
                   setUserDetails(receiver!!,sharedPrefs.getString(CommonKeys.USER_ID))
                   isBlocked=true
               }
           }
           userInfoDialogFragment.show(supportFragmentManager, "")*/
       }
    }

    /**
     * initialize recyclerview [setupRecyclerView]
     */

    private fun setupRecyclerView() {
        chatAdapter = ChatAdapter(chats, sharedPrefs, suspended)
        binding.rvChat.adapter = chatAdapter
    }

    override fun onBackPressed() {
        if (fromNotification) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finishAffinity()
        } else {
            super.onBackPressed()
        }
    }
}