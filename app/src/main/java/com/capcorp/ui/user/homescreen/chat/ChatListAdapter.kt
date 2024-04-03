package com.capcorp.ui.user.homescreen.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.home.ImageViewerActivity
import com.capcorp.ui.user.homescreen.chat.chatmessage.ChatActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.UserId
import com.capcorp.webservice.models.chats.ChatListing
import kotlinx.android.synthetic.main.item_chat_list.view.*
import java.util.*

class ChatListAdapter(
    private var context: Context,
    private val chatListing: ArrayList<ChatListing>?,
    private val chatsFragment: ChatsFragment
) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.item_chat_list, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return chatListing?.size ?: 0
    }

    override fun onBindViewHolder(holder: ChatListAdapter.ViewHolder, position: Int) {
        holder.bind(chatListing?.get(position))
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra(USER_ID, chatListing?.get(adapterPosition)?.senderId?._id)
                intent.putExtra(USER_NAME, chatListing?.get(adapterPosition)?.senderId?.fullName)
                intent.putExtra(
                    PROFILE_PIC_URL,
                    chatListing?.get(adapterPosition)?.senderId?.profilePicURL?.original
                )
                chatsFragment.startActivityForResult(intent, RQ_CODE_CHAT)
                chatListing?.get(adapterPosition)?.unDeliverCount = -1
                notifyDataSetChanged()
            }

            itemView.ivProfilePic.setOnClickListener {

                val intent = Intent(context, ImageViewerActivity::class.java)
                intent.putExtra(
                    PROFILE_PIC_URL,
                    chatListing?.get(adapterPosition)?.senderId?.profilePicURL?.original.toString()
                )
                context.startActivity(intent)
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(chat: ChatListing?) = with(itemView) {
            val isMyMessage: Boolean = chat?.senderId?._id == SharedPrefs.with(context)
                .getObject(USER_DATA, UserId::class.java)._id
            tvName.text = chat?.senderId?.fullName
            Glide.with(context).load(chat?.senderId?.profilePicURL?.original)
                .apply(RequestOptions().circleCrop())
                .apply(RequestOptions().placeholder(R.drawable.profile_pic_placeholder))
                .into(itemView.ivProfilePic)
            if (chat?.chatType == ChatType.IMAGE) {
                if (isMyMessage) {
                    tvMessage.text =
                        """${context.getString(R.string.you)} ${context.getString(R.string.sent_an_image)}"""
                } else {
                    tvMessage.text =
                        """${chat.senderId?.fullName} ${context.getString(R.string.sent_an_image)}"""
                }
            } else {
                tvMessage.text = chat?.text
            }
            val calendar = Calendar.getInstance()
            chat?.sentAt?.let { calendar.timeInMillis = it }
            val dateString: String
            val time = getFormatFromDate(calendar.time, "h:mm a")
            dateString = when {
                DateUtils.isToday(calendar.timeInMillis) -> String.format("%s", time)
                isYesterday(calendar) -> String.format("%s", context.getString(R.string.yesterday))
                existsInWeek(calendar) -> getFormatFromDate(calendar.time, "EEE")
                else -> getFormatFromDate(calendar.time, "MMM dd")
            }
            tvTime.text = dateString
            if (chat?.unDeliverCount ?: 0 > 0) {
                tvUnreadCount.visibility = View.VISIBLE
                if (chat?.unDeliverCount ?: 0 > 99) {
                    tvUnreadCount.text = context.getString(R.string.nintynine_plus)
                } else {
                    tvUnreadCount.text = chat?.unDeliverCount.toString()
                }
            } else {
                tvUnreadCount.visibility = View.GONE
            }
        }
    }
}