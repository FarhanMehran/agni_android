package com.agnidating.agni.ui.adapters

import android.annotation.SuppressLint
import com.agnidating.agni.R
import com.agnidating.agni.base.BaseAdapter
import com.agnidating.agni.databinding.ItemChatBinding
import com.agnidating.agni.model.DataX
import com.agnidating.agni.utils.*
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.bumptech.glide.Glide
import javax.inject.Inject

/**
 * Created by AJAY ASIJA on 04/12/2022
 */
class ChatAdapter @Inject constructor(
    private val chats: ArrayList<DataX>,
    private val sharedPrefs: SharedPrefs,
    private val suspended:Boolean=false
) : BaseAdapter<ItemChatBinding>(R.layout.item_chat) {
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder<ItemChatBinding>, position: Int) {
        holder.binding.tvDate.text = chats[position].createdOn.toMessageDate()
        val item = chats[position]
        if (position == 0) {
            holder.binding.tvDate.visible()
        } else {
            if (isDateEquals(chats[position].createdOn, chats[position - 1].createdOn).not()) {
                holder.binding.tvDate.visible()
            } else {
                holder.binding.tvDate.gone()
            }
        }
        if (chats[position].senderId == sharedPrefs.getString(CommonKeys.USER_ID).toString()) {
            holder.binding.consRight.visible()
            holder.binding.consLeft.gone()
            if (chats[position].messageType == "Flower") {
                holder.binding.flowerSent.visible()
                holder.binding.tvRightChat.visible()
                holder.binding.tvRightChat.text=item.message
                if (item.flowerCount == "1") {
                    Glide.with(requireContext()).load(R.drawable.ic_rose)
                        .into(holder.binding.flowerSent)
                } else {
                    Glide.with(requireContext()).load(R.drawable.flower)
                        .into(holder.binding.flowerSent)
                }
            } else {
                holder.binding.tvRightChat.text = chats[position].message
                holder.binding.tvRightChat.visible()
                holder.binding.flowerSent.gone()
            }
            holder.binding.tvTimeSent.text = chats[position].createdOn.to12Time()
        } else {
            if (suspended.not()){
                holder.binding.civProfile.load(
                    chats[position].senderProfilePicture,
                    CommonKeys.IMAGE_BASE_URL)
            }
            holder.binding.consRight.gone()
            holder.binding.consLeft.visible()
            if (chats[position].messageType == "Flower") {
                holder.binding.tvLeftChat.visible()
                holder.binding.flowerReceived.visible()
                holder.binding.tvLeftChat.text=item.message
                if (item.flowerCount == "1") {
                    Glide.with(requireContext()).load(R.drawable.ic_rose)
                        .into(holder.binding.flowerReceived)
                } else {
                    Glide.with(requireContext()).load(R.drawable.flower)
                        .into(holder.binding.flowerReceived)
                }
            } else {
                holder.binding.tvLeftChat.visible()
                holder.binding.flowerReceived.gone()
                holder.binding.tvLeftChat.text = chats[position].message
            }
            holder.binding.tvTimeReceived.text = chats[position].createdOn.to12Time()
        }

    }

    override fun getItemCount(): Int {
        return chats.size
    }
}