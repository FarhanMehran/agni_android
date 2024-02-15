package com.agnidating.agni.ui.adapters



import com.agnidating.agni.R
import com.agnidating.agni.base.BaseAdapter
import com.agnidating.agni.databinding.ItemBlockedUserBinding
import com.agnidating.agni.model.blockUser.BlockedUser
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.load
import javax.inject.Inject


class BlockUserAdapter @Inject constructor(val list: ArrayList<BlockedUser>, private val onSelect: (Int,BlockedUser) -> Unit) :
    BaseAdapter<ItemBlockedUserBinding>(R.layout.item_blocked_user) {

    override fun onBindViewHolder(holder: MyViewHolder<ItemBlockedUserBinding>, position: Int) {
        val item=list[position]
        holder.binding.item=item
        holder.binding.civProfile.load(item.profilePicture,CommonKeys.IMAGE_BASE_URL)

        holder.binding.tvUnblock.setOnClickListener{
            onSelect.invoke(position,item)
        }
    }
    fun removeAt(position: Int){
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun isEmpty(): Boolean {
        return list.isEmpty()
    }

    override fun getItemCount(): Int {
        return list.size
    }


}


