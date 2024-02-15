package com.agnidating.agni.ui.adapters

import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.DiffUtil
import com.agnidating.agni.R
import com.agnidating.agni.base.BasePagingAdapter
import com.agnidating.agni.databinding.ItemHomeCardsBinding
import com.agnidating.agni.model.home.User
import com.agnidating.agni.utils.CommonKeys.OPTION
import com.agnidating.agni.utils.CommonKeys.SEND_REQUEST
import com.agnidating.agni.utils.CommonKeys.FLOWER
import com.agnidating.agni.utils.CommonKeys.USER_DETAILS
import com.agnidating.agni.utils.toast


class HomeCardsAdapter(val onUserClick:(User,Int)->Unit) :
    BasePagingAdapter<User, ItemHomeCardsBinding>(R.layout.item_home_cards, UserComparator) {
    var msgView: AppCompatEditText? = null

    override fun onBindViewHolder(holder: MyViewHolder<ItemHomeCardsBinding>, position: Int) {
        holder.binding.item = getItem(position)
        holder.binding.root.setOnClickListener {
            onUserClick.invoke(getItem(position)!!,USER_DETAILS)
        }
        holder.binding.ivHeart.setOnClickListener {
           if (holder.binding.etTypeSomeThing.text.toString().trim().isEmpty()){
               "Type a message in message box".toast(requireContext())
           }else{
               msgView = holder.binding.etTypeSomeThing
               onUserClick.invoke(getItem(position)!!,SEND_REQUEST)
           }
        }
        holder.binding.ivShare.setOnClickListener {
            onUserClick.invoke(getItem(position)!!,FLOWER)
        }
        holder.binding.ivMenu.setOnClickListener {
            onUserClick.invoke(getItem(position)!!,OPTION)
        }
    }
    fun getUser(position:Int): User? {
        return getItem(position)
    }
    object UserComparator : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}


