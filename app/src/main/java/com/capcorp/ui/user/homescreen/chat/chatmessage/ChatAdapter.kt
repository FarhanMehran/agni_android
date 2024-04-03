package com.capcorp.ui.user.homescreen.chat.chatmessage

import android.content.Context
import android.content.Intent
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.bumptech.glide.Glide
import com.capcorp.R
import com.capcorp.ui.driver.homescreen.home.ImageViewerActivity
import com.capcorp.utils.*
import com.capcorp.webservice.models.ProfilePicUr
import com.capcorp.webservice.models.UserId
import com.capcorp.webservice.models.chats.ChatMessageListing
import kotlinx.android.synthetic.main.item_chat_media.view.*
import kotlinx.android.synthetic.main.item_chat_media_others.view.*
import kotlinx.android.synthetic.main.item_chat_others.view.*
import kotlinx.android.synthetic.main.item_direct_chat.view.*
import java.io.File
import java.util.*

class ChatAdapter(
    private val context: Context?,
    private val chatListing: ArrayList<ChatMessageListing>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TEXT = 1

    private val ITEM_TEXT_OTHERS = 2

    private val ITEM_MEDIA = 3

    private val ITEM_MEDIA_OTHERS = 4

    private val amazonMobile = context?.let { AwsMobile(it, "") }

    val CHANGE_SENT_STATUS = "change_sent_status"

    val CHANGE_MEDIA_UPLOAD_STATUS = "change_media_upload_status"

    override fun getItemViewType(position: Int): Int {
        return if (chatListing[position].senderId == SharedPrefs.with(context).getObject(
                USER_DATA,
                UserId::class.java
            )._id
        ) {
            if (chatListing[position].chatType == ChatType.TEXT) {
                ITEM_TEXT
            } else {
                ITEM_MEDIA
            }
        } else {
            if (chatListing[position].chatType == ChatType.TEXT) {
                ITEM_TEXT_OTHERS
            } else {
                ITEM_MEDIA_OTHERS
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TEXT -> TextViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_direct_chat, parent, false)
            )
            ITEM_TEXT_OTHERS -> TextOthersViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_chat_others, parent, false)
            )
            ITEM_MEDIA -> MediaViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_chat_media, parent, false)
            )
            ITEM_MEDIA_OTHERS -> MediaOthersViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_chat_media_others, parent, false)
            )
            else -> TextViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_direct_chat, parent, false)
            )

        }
    }

    override fun getItemCount(): Int {
        return chatListing.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        var showDateHeader = false
//        if (position == 0) {
//            showDateHeader = true
//        } else {
//            val cal1 = Calendar.get()
//            cal1.timeInMillis = chatListing[position-1].sentAt ?: 0
//            val cal2 = Calendar.get()
//            cal2.timeInMillis = chatListing[position].sentAt ?: 0
//            showDateHeader = !(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR))
//        }
//        when (holder) {
//            is TextViewHolder -> holder.bind(chatListing[position], false, showDateHeader)
//            is TextOthersViewHolder -> holder.bind(chatListing[position])
//            is MediaViewHolder -> holder.bind(chatListing[position], false, showDateHeader)
//            is MediaOthersViewHolder -> holder.bind(chatListing[position])
//        }

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val showDateHeader: Boolean
        if (position == 0) {
            showDateHeader = true
        } else {
            val cal1 = Calendar.getInstance()
            cal1.timeInMillis = chatListing[position - 1].sentAt ?: 0
            val cal2 = Calendar.getInstance()
            cal2.timeInMillis = chatListing[position].sentAt ?: 0
            showDateHeader =
                !(cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(
                    Calendar.DAY_OF_YEAR
                ))
        }
        var payload = ""
        if (payloads.isNotEmpty()) {
            payload = payloads[0] as String
        }
        when (holder) {
            is TextViewHolder -> holder.bind(chatListing[position], payload, showDateHeader)
            is TextOthersViewHolder -> holder.bind(chatListing[position], showDateHeader)
            is MediaViewHolder -> holder.bind(chatListing[position], payload, showDateHeader)
            is MediaOthersViewHolder -> holder.bind(chatListing[position], showDateHeader)
        }
    }


    inner class TextViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val clickListener = View.OnClickListener {
            if (chatListing[adapterPosition].isFailed == true) {
                chatListing[adapterPosition].isFailed = false
                (context as ChatActivity).sendMessage(chatListing[adapterPosition])
                notifyItemChanged(adapterPosition, CHANGE_SENT_STATUS)
            }
        }

        init {
            itemView.tvTime.setOnClickListener(clickListener)
            itemView.ivTick.setOnClickListener(clickListener)
        }


        fun bind(chat: ChatMessageListing, payload: String, showDateHeader: Boolean) {
            if (payload.isEmpty()) {
                itemView.tvMessage.text = chat.text
                if (showDateHeader) {
                    itemView.tvDateHeader.text = chat.sentAt?.let { getDateHeader(it) }
                    itemView.tvDateHeader.visibility = View.VISIBLE
                } else {
                    itemView.tvDateHeader.visibility = View.GONE
                }
                setSentStatus(chat)
            } else {
                setSentStatus(chat)
            }
        }

        private fun setSentStatus(chat: ChatMessageListing) {
            when {
                chat.isFailed == true -> {
                    itemView.ivTick.setImageResource(R.drawable.ic_error_outline)
                    itemView.tvTime.text = context?.getString(R.string.retry)
                    context?.let { ContextCompat.getColor(it, R.color.error) }
                        ?.let { itemView.tvTime.setTextColor(it) }
                }
                chat.isRead == true -> {
                    itemView.ivTick.setImageResource(R.drawable.ic_check_read)
                    itemView.tvTime.text = chat.sentAt?.let {
                        DateUtils.formatDateTime(
                            context,
                            it,
                            DateUtils.FORMAT_SHOW_TIME
                        )
                    }
                    context?.let { ContextCompat.getColor(it, R.color.grey_error) }
                        ?.let { itemView.tvTime.setTextColor(it) }
                }
                chat.isDeliver == true -> {
                    itemView.ivTick.setImageResource(R.drawable.ic_check_deliver)
                    itemView.tvTime.text = chat.sentAt?.let {
                        DateUtils.formatDateTime(
                            context,
                            it,
                            DateUtils.FORMAT_SHOW_TIME
                        )
                    }
                    context?.let { ContextCompat.getColor(it, R.color.grey_error) }
                        ?.let { itemView.tvTime.setTextColor(it) }
                }
                chat.isSent == true -> {
                    itemView.ivTick.setImageResource(R.drawable.ic_sent)
                    itemView.tvTime.text = chat.sentAt?.let {
                        DateUtils.formatDateTime(
                            context,
                            it,
                            DateUtils.FORMAT_SHOW_TIME
                        )
                    }
                    context?.let { ContextCompat.getColor(it, R.color.grey_error) }
                        ?.let { itemView.tvTime.setTextColor(it) }
                }

                else -> {
                    itemView.ivTick.setImageResource(R.drawable.ic_wait)
                    itemView.tvTime.text = chat.sentAt?.let {
                        DateUtils.formatDateTime(
                            context,
                            it,
                            DateUtils.FORMAT_SHOW_TIME
                        )
                    }
                    context?.let { ContextCompat.getColor(it, R.color.grey_error) }
                        ?.let { itemView.tvTime.setTextColor(it) }
                }
            }
        }
    }


    inner class TextOthersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chat: ChatMessageListing, showDateHeader: Boolean) {
            itemView.tvTimeOthers.text = chat.sentAt?.let {
                DateUtils.formatDateTime(
                    context,
                    it,
                    DateUtils.FORMAT_SHOW_TIME
                )
            }
            itemView.tvMessageOthers.text = chat.text
            if (showDateHeader) {
                itemView.tvDateHeaderOthers.text = chat.sentAt?.let { getDateHeader(it) }
                itemView.tvDateHeaderOthers.visibility = View.VISIBLE
            } else {
                itemView.tvDateHeaderOthers.visibility = View.GONE
            }
        }
    }

    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.ivCancel.setOnClickListener {
                if (chatListing[adapterPosition].mediaToUpload?.mediaUploadStatus == MediaUploadStatus.CANCELED) {
                    chatListing[adapterPosition].mediaToUpload?.mediaUploadStatus =
                        MediaUploadStatus.NOT_UPLOADED
                } else {
                    amazonMobile?.cancelUploading(chatListing[adapterPosition].mediaToUpload?.transferId)
                    chatListing[adapterPosition].mediaToUpload?.mediaUploadStatus =
                        MediaUploadStatus.CANCELED
                }
                notifyItemChanged(adapterPosition, CHANGE_MEDIA_UPLOAD_STATUS)
            }
        }

        fun bind(chat: ChatMessageListing, payload: String, showDateHeader: Boolean) {
            if (payload.isEmpty()) {
                itemView.tvTimeMedia.text = chat.sentAt?.let {
                    DateUtils.formatDateTime(
                        context,
                        it,
                        DateUtils.FORMAT_SHOW_TIME
                    )
                }
                if (showDateHeader) {
                    itemView.tvDateHeaderMedia.text = chat.sentAt?.let { getDateHeader(it) }
                    itemView.tvDateHeaderMedia.visibility = View.VISIBLE


                } else {
                    itemView.tvDateHeaderMedia.visibility = View.GONE
                }



                setMediaUploadStatus(chat)
                setSentStatus(chat)
            } else {
                if (payload == CHANGE_SENT_STATUS) {
                    setSentStatus(chat)
                } else if (payload == CHANGE_MEDIA_UPLOAD_STATUS) {
                    setMediaUploadStatus(chat)
                }
            }
        }

        private fun setMediaUploadStatus(chat: ChatMessageListing) {
            if (chat.mediaToUpload == null) {
                context?.let {
                    Glide.with(it).load(chat.chatImage?.original).into(itemView.imageView)
                }
                itemView.ivCancel.visibility = View.GONE
                itemView.progressBar.visibility = View.GONE

                itemView.imageView.setOnClickListener {
                    val intent = Intent(context, ImageViewerActivity::class.java)
                    intent.putExtra(
                        PROFILE_PIC_URL,
                        chatListing[adapterPosition].chatImage?.original.toString()
                    )
                    context?.startActivity(intent)
                }

            } else {
                context?.let {
                    Glide.with(it).load(chat.mediaToUpload?.file).into(itemView.imageView)

                    itemView.imageView.setOnClickListener {
                        val intent = Intent(context, ImageViewerActivity::class.java)
                        intent.putExtra(PROFILE_PIC_URL, chat.mediaToUpload?.file.toString())
                        intent.putExtra(IS_FILE_IMAGE, true)
                        context.startActivity(intent)
                    }
                }
                when (chat.mediaToUpload?.mediaUploadStatus) {
                    MediaUploadStatus.NOT_UPLOADED -> {
                        itemView.ivCancel.visibility = View.VISIBLE
                        itemView.progressBar.visibility = View.VISIBLE
                        itemView.ivCancel.setImageResource(R.drawable.ic_media_stop)
                        chat.mediaToUpload?.file?.let { uploadMedia(it, adapterPosition) }
                    }
                    MediaUploadStatus.UPLOADING -> {
                        itemView.progressBar.visibility = View.VISIBLE
                        itemView.ivCancel.visibility = View.VISIBLE
                        itemView.ivCancel.setImageResource(R.drawable.ic_media_stop)
                    }
                    MediaUploadStatus.UPLOADED -> {
                        itemView.ivCancel.visibility = View.GONE
                        itemView.progressBar.visibility = View.GONE
                        if (chat.isSent == false) {
                            (context as ChatActivity).sendMessage(chat)
                        }
                    }
                    MediaUploadStatus.CANCELED -> {
                        itemView.ivCancel.visibility = View.VISIBLE
                        itemView.progressBar.visibility = View.GONE
                        itemView.ivCancel.setImageResource(R.drawable.ic_media_retry)
                    }
                }
            }
        }

        private fun setSentStatus(chat: ChatMessageListing) {
            when {
                chat.isFailed == true -> {
                    itemView.ivTickMedia.setImageResource(R.drawable.ic_error_outline)
                    itemView.tvTimeMedia.text = context?.getString(R.string.retry)
                    context?.let { ContextCompat.getColor(it, R.color.error) }
                        ?.let { itemView.tvTime?.setTextColor(it) }
                }
                chat.isSent == true -> {
                    itemView.ivTickMedia.setImageResource(R.drawable.ic_sent)
                    itemView.tvTimeMedia.text = chat.sentAt?.let {
                        DateUtils.formatDateTime(
                            context,
                            it,
                            DateUtils.FORMAT_SHOW_TIME
                        )
                    }
                    context?.let { ContextCompat.getColor(it, R.color.grey_error) }
                        ?.let { itemView.tvTimeMedia?.setTextColor(it) }
                }
                else -> {
                    itemView.ivTickMedia.setImageResource(R.drawable.ic_wait)
                    itemView.tvTimeMedia.text = chat.sentAt?.let {
                        DateUtils.formatDateTime(
                            context,
                            it,
                            DateUtils.FORMAT_SHOW_TIME
                        )
                    }
                    context?.let { ContextCompat.getColor(it, R.color.grey_error) }
                        ?.let { itemView.tvTimeMedia?.setTextColor(it) }
                }
            }
        }

        private fun uploadMedia(file: File, position: Int) {
            chatListing[adapterPosition].mediaToUpload?.mediaUploadStatus =
                MediaUploadStatus.UPLOADING
            /*val key = System.currentTimeMillis().toString()
            val preGeneratedUrl = S3_BUCKET + System.currentTimeMillis().toString()
            val transferId = context?.let {
                AwsMobile(it,"").uploadFile(key, file, object : TransferListener {
                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

                    }

                    override fun onStateChanged(id: Int, state: TransferState?) {
                        when (state) {
                            TransferState.COMPLETED -> {
                                val chatImage = ProfilePicUrl(preGeneratedUrl, preGeneratedUrl)
                                chatListing[position].chatImage = chatImage
                                chatListing[position].mediaToUpload?.mediaUploadStatus = MediaUploadStatus.UPLOADED
                                chatListing[position].isFailed = false
                                notifyItemChanged(position, CHANGE_MEDIA_UPLOAD_STATUS)
                            }

                            TransferState.FAILED -> {
                                chatListing[position].mediaToUpload?.mediaUploadStatus = MediaUploadStatus.CANCELED
                                notifyItemChanged(position, CHANGE_MEDIA_UPLOAD_STATUS)
                            }

                            TransferState.CANCELED -> {
                                chatListing[position].mediaToUpload?.mediaUploadStatus = MediaUploadStatus.CANCELED
                                notifyItemChanged(position, CHANGE_MEDIA_UPLOAD_STATUS)
                            }
                        }
                    }

                    override fun onError(id: Int, ex: Exception?) {
                        chatListing[position].mediaToUpload?.mediaUploadStatus = MediaUploadStatus.CANCELED
                        notifyItemChanged(position, CHANGE_MEDIA_UPLOAD_STATUS)
                    }
                })
            }*/
            //val atIndex = mImageList.size - 1
            val key = System.currentTimeMillis().toString()
            val preGeneratedUrl = "$S3_BUCKET$key"
            val amazonMobile = AwsMobile(context!!, key)
            amazonMobile.uploadFile(key, file, object : TransferListener {
                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {

                }

                override fun onStateChanged(id: Int, state: TransferState?) {
                    when (state) {
                        TransferState.COMPLETED -> {
                            val chatImage = ProfilePicUr(preGeneratedUrl, preGeneratedUrl)
                            chatListing[position].chatImage = chatImage
                            chatListing[position].mediaToUpload?.mediaUploadStatus =
                                MediaUploadStatus.UPLOADED
                            chatListing[position].isFailed = false
                            notifyItemChanged(position, CHANGE_MEDIA_UPLOAD_STATUS)
                            Thread(Runnable {
                                run {
                                    amazonMobile.getS3ClientInstance().setObjectAcl(
                                        "h2d-dev",
                                        key,
                                        CannedAccessControlList.PublicRead
                                    )
                                }
                            }).start()
                        }

                        TransferState.FAILED -> {
                            chatListing[position].mediaToUpload?.mediaUploadStatus =
                                MediaUploadStatus.CANCELED
                            notifyItemChanged(position, CHANGE_MEDIA_UPLOAD_STATUS)
                        }

                        TransferState.CANCELED -> {
                            chatListing[position].mediaToUpload?.mediaUploadStatus =
                                MediaUploadStatus.CANCELED
                            notifyItemChanged(position, CHANGE_MEDIA_UPLOAD_STATUS)
                        }

                        else -> {

                        }
                    }
                }

                override fun onError(id: Int, ex: Exception?) {
                    chatListing[position].mediaToUpload?.mediaUploadStatus =
                        MediaUploadStatus.CANCELED
                    notifyItemChanged(position, CHANGE_MEDIA_UPLOAD_STATUS)
                }
            })
            //chatListing[position].mediaToUpload?.transferId = transferId
        }
    }

    inner class MediaOthersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chat: ChatMessageListing, showDateHeader: Boolean) {
            context?.let { Glide.with(it).load(chat.chatImage?.original) }
            itemView.tvTimeMediaOther.text = chat.sentAt?.let {
                DateUtils.formatDateTime(
                    context,
                    it,
                    DateUtils.FORMAT_SHOW_TIME
                )
            }
            if (showDateHeader) {
                itemView.tvDateHeaderMediaOther.text = chat.sentAt?.let { getDateHeader(it) }
                itemView.tvDateHeaderMediaOther.visibility = View.VISIBLE
            } else {
                itemView.tvDateHeaderMediaOther.visibility = View.GONE
            }
            context?.let {
                Glide.with(it).load(chat.chatImage?.original).into(itemView.imageViewOther)
            }
        }
    }

    private fun getDateHeader(millis: Long): String? {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        val dateString: String?
        dateString = when {
            DateUtils.isToday(calendar.timeInMillis) -> context?.getString(R.string.today)
            isYesterday(calendar) -> String.format("%s", context?.getString(R.string.yesterday))
            existsInWeek(calendar) -> getFormatFromDate(calendar.time, "EEEE")
            else -> getFormatFromDate(calendar.time, "MMM dd")
        }
        return dateString
    }

}