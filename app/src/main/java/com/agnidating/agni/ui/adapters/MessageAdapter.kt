package com.agnidating.agni.ui.adapters



import com.agnidating.agni.R
import com.agnidating.agni.base.BaseAdapter
import com.agnidating.agni.databinding.ItemMessageBinding
import com.agnidating.agni.model.recent.Data
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.load
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import com.agnidating.agni.utils.to12Time
import com.agnidating.agni.utils.toTime
import javax.inject.Inject


class MessageAdapter @Inject constructor(val list: ArrayList<Data>, private val sharedPrefs: SharedPrefs, private val onSelect: (Data) -> Unit) :
    BaseAdapter<ItemMessageBinding>(R.layout.item_message) {

    override fun onBindViewHolder(holder: MyViewHolder<ItemMessageBinding>, position: Int) {
        val item=list[position]

        holder.binding.item=item
        holder.binding.tvTime.text=item.createdOn.to12Time()
        if (sharedPrefs.getString(CommonKeys.USER_ID)==item.senderId){
            holder.binding.tvVendorPlanName.text=  if (item.receiverSuspendedStatus=="1" ) "Agni User" else item.receiverName
            if (item.receiverSuspendedStatus!="1")
                holder.binding.civProfile.load(item.receiverProfilePicture,CommonKeys.IMAGE_ThUMBNAIL_BASE_URL)
        }else{
            holder.binding.tvVendorPlanName.text= if (item.receiverSuspendedStatus=="1" ) "Agni User" else item.senderName
            if (item.receiverSuspendedStatus!="1")
                holder.binding.civProfile.load(item.senderProfilePicture,CommonKeys.IMAGE_ThUMBNAIL_BASE_URL)
        }
        holder.binding.root.setOnClickListener {
            onSelect.invoke(item)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}


