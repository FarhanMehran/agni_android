package com.agnidating.agni.ui.activities.notifications

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatCallback
import com.agnidating.agni.R
import com.agnidating.agni.base.BaseAdapter
import com.agnidating.agni.databinding.ItemNotificationsBinding
import com.agnidating.agni.model.notification.Data
import com.agnidating.agni.ui.fragment.youGotMatch.YouGotMatch

/**
 * Create by AJAY ASIJA on 04/11/2022
 */
class NotificationAdapter(val data: List<Data>,val callback: ()->Unit) :BaseAdapter<ItemNotificationsBinding>(R.layout.item_notifications) {

    override fun onBindViewHolder(holder: MyViewHolder<ItemNotificationsBinding>, position: Int) {
        val item=data[position]
        holder.binding.item=item
        if (item.notificationType=="2"){
            holder.binding.civProfile1.setImageResource(R.drawable.double_heart)
        }else{
            holder.binding.civProfile1.setImageResource(R.drawable.heart_single)
        }
        holder.binding.root.setOnClickListener {
           if (item.notificationType=="2"){
               val youGotMatch = YouGotMatch()
               youGotMatch.show((requireContext() as AppCompatActivity).supportFragmentManager, "")
               youGotMatch.data=item
               youGotMatch.callBack = {
                   youGotMatch.dismiss()
               }
           }
            else{
              callback.invoke()
           }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}